package nl.tue.twimu.ml;

import java.util.ArrayList;

import org.apache.lucene.index.Term;

import nl.tue.twimu.ir.TFIDFMatrix;
import nl.tue.twimu.ir.TermPreprocessor;

public class Rocchio {
	private TFIDFMatrix tf;
	private double[] query;
	private int[] relevance;
	private int relevantArtists, irrelevantArtists;
	private ArrayList<String> artists;

	public final static double ALPHA = 1.0, BETA = 0.75, GAMMA = 0.15;
	public final static double DEF_SCORE = 5.0;

	public Rocchio(TFIDFMatrix tf, double[] query, int[] relevance) {
		this.tf = tf;
		this.artists = tf.getArtists();
		this.relevance = relevance;
		this.query = query;
		countRelevant();
	}
	
	public static double[] queryValues(String query, TFIDFMatrix mx) {
		String[] words = query.replace(',', ' ').split(" ");
		double[] values = new double[mx.getTerms().size()];
		for (String w : words){
			w = TermPreprocessor.termPreProcessing(w);
			values[mx.getTerms().indexOf(w)] = DEF_SCORE;
		}
			
		return values;
	}
	
	private void countRelevant() {
		relevantArtists = 0;
		irrelevantArtists = 0;
		for (int i = 0; i < relevance.length; i++) {
			if (relevance[i] == 1)
				relevantArtists++;
			if (relevance[i] == -1)
				irrelevantArtists++;
		}
	}

	public double[] getNewQuery() {
		double[] nQuery = new double[query.length];
		
		for(int i = 0; i < artists.size(); i++) {
			nQuery[i] = ALPHA*query[i] 
					+ ((relevantArtists != 0) ? (BETA/relevantArtists)*getScore(i, true) : 0)
					- ((irrelevantArtists != 0) ? (GAMMA/irrelevantArtists)*getScore(i, false) : 0);
		}
		return nQuery;
	}

	private double getScore(int term, boolean relevant) {
		double score = 0;
		int rel = relevant ? 1 : -1;
		for (int i = 0; i < relevance.length; i++)
			if (relevance[i] == rel)
				score += tf.getItem(term, i);
		return score;
	}
}
