package nl.tue.twimu.ir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import nl.tue.twimu.model.Artist;
import nl.tue.twimu.model.Tweet;
import nl.tue.twimu.model.TweetsDb;
import nl.tue.twimu.ir.util.Stemmer;
import pitt.search.lucene.PorterAnalyzer;

/**
 * @author cristiprg
 */
public class TFIDFMatrix implements Serializable{
	private ArrayList<String> artists;
	private ArrayList<String> terms;
	private ArrayList<ArrayList<Double>> matrix;
	private boolean doStemming = true;
	public static final String fileName = "tfidf.gz";
	
	
	public TFIDFMatrix(){
		artists = new ArrayList<String>();
		terms = new ArrayList<String>();
		matrix = new ArrayList<ArrayList<Double>>();
	}
	
	public static TFIDFMatrix loadFromCache() throws FileNotFoundException, IOException, ClassNotFoundException{
		File f = new File(fileName);
		ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(f)));
		TFIDFMatrix mat = (TFIDFMatrix)ois.readObject();
		ois.close();
		return mat;
	}
	
	// overwrites old save, save to static file
	public void save() throws FileNotFoundException, IOException {
		File f = new File(fileName);
		ObjectOutputStream ous = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(f)));
		ous.writeObject(this);
		ous.close();
	}

	/*
	 * This is repsonsible for counting TF and adding the terms into the right place
	 * TODO: add IDF
	 */
	public void addAArtist(Artist artist) {
		artists.add(artist.getHandle());

		// for each tweet, get the text
		StringBuilder stringBuilder = new StringBuilder();
		for (Tweet tweet : artist.getTweets()) {
			stringBuilder.append(tweet.text + " ");
		}

		// now compute for each term
		// TODO: replace by Lecene tokenizer
		// http://stackoverflow.com/questions/21771566/calculating-frequency-of-each-word-in-a-sentence-in-java
		String allTweets = stringBuilder.toString();
		Map<String, Integer> map = new HashMap<>();
		for (String w : allTweets.split("\\s")) {
			// filter out zero-length strings, 
			if (w.length() == 0) continue;
			
			w = termPreProcessing(w);

			Integer n = map.get(w);
			n = (n == null) ? 1 : ++n;
			map.put(w, n);
		}
	 
		int a = 0;
		a = 2;
		
		// add one entry for each term vector - update length
		for(int i = 0; i < terms.size(); ++i){
			matrix.get(i).add((double) 0);
		}
		
		// add term into the right place 
		for (String term : map.keySet()){
			// get the index of the term
			int index = findIndexOf(term);
			
			if (index != -1){
				// if found, add the frequency of the term - just update the last item
				matrix.get(index).set(artists.size()-1, Double.valueOf(map.get(term)));
			}
			else{
				// if not found, 1) add the new term to the list of terms and 2) create a new entry in the 
				// matrix, fill it with zero's everything but the current artist
				terms.add(term);
				ArrayList<Double> newTermVector = new ArrayList<Double>(Collections.nCopies(artists.size(), 0.0));
				newTermVector.set(artists.size()-1, Double.valueOf(map.get(term)));
				matrix.add(newTermVector);
			}
		}
	}
	
	public void addTerm(String term){
		terms.add(term);
	}
	
	public ArrayList<String> getArtists() {
		return artists;
	}

	public ArrayList<String> getTerms() {
		return terms;
	}
	
	public ArrayList<ArrayList<Double>> getMatrix(){
		return matrix;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("\t");
		for (int j = 0; j < artists.size(); j++)
			str.append(artists.get(j) + "\t               ");

		for (int i = 0; i < terms.size(); i++)
			for (int j = 0; j < artists.size(); j++) {
				if (j == 0)
					str.append("\n" + terms.get(i) + "\t");
				str.append(matrix.get(i).get(j) + "\t");
			}
		return str.toString();
	}

	
	public boolean isDoStemming() {
		return doStemming;
	}

	public void setDoStemming(boolean doStemming) {
		this.doStemming = doStemming;
	}

	/*
	 * TODO: veeeery slow - change this
	 */
	private int findIndexOf(String term) {
		for(int i = 0; i < terms.size(); ++i){
			if (terms.get(i).equals(term)){
				return i;
			}
		}
			
		return -1;
	}

	private String termPreProcessing(String term){
		// to lower case
		term = term.toLowerCase();
		
		// remove trailing and leading non-aplhanumeric
		// TODO: except the # character? to retain hashtags - (^[^a-z0-9#:]*|[^a-z0-9]*$)
		term = term.replaceAll("(^[^a-z0-9]*|[^a-z0-9]*$)", "");
		
		
		// perform stemming on non-hashtags
		Stemmer stemmer = new Stemmer();
		if(doStemming && term.length() >0 && term.getBytes()[0] != '#'){
			//w = porterAnalyzer.stemQuery(w);
			stemmer.add(term.toCharArray(), term.toCharArray().length);
			stemmer.stem();
			term = stemmer.toString();				
		}
		
		return term;
	}
	
}
