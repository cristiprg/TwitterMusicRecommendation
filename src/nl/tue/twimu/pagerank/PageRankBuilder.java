package nl.tue.twimu.pagerank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer.ReuseStrategy;

import nl.tue.twimu.ir.Indexer;
import nl.tue.twimu.model.Artist;
import nl.tue.twimu.model.TweetsDb;

public class PageRankBuilder {	
	final static Logger logger = Logger.getLogger(PageRankBuilder.class);
	
	private ProbabilityMatrix matrix;
	private ProbabilityVector pageRank;
	private ArrayList<String> artists;
	private TweetsDb db = null;
	private int size = 0;
	private double lamda = 0.1; // teleportation rate
	
	
	private ArrayList<Artist> rankedArtists;
	private ArrayList<Double> pageRankArray; // useful for influencing the tf idf values
	
	public PageRankBuilder(TweetsDb db){
		this.db = db;
		size = db.getArtists().size();
		artists = new ArrayList<>();
		// create new instance of probability with size the number of artists 
		matrix = new ProbabilityMatrix(size);
		
		// init the result page rank
		pageRank = new ProbabilityVector(size);
		
		// count links
		initProbabilityMatrix();
		
		if (matrix.sanityCheck()){
			logger.info("Everything fine!");
		}
		else{
			logger.error("ERROR computing page rank!");
		}
		
		// consider the teleportation rate
		computeProbabilityMatrix();

		if (matrix.sanityCheck()){
			logger.info("Everything fine!");
		}
		else{
			logger.error("ERROR computing page rank!");
		}

		computePageRank();
		
		// get all the artists and then sort them
		pageRankArray = pageRank.toArrayList();
		rankedArtists = new ArrayList<Artist>();
		for(Artist a : db.getArtists().values()){
			rankedArtists.add(a);
		}
		
		Collections.sort(rankedArtists, new ArtistComparator());	
		Collections.sort(pageRankArray, Collections.reverseOrder());
	}
	
	public ArrayList<Artist> getRankedArtists(){
		return rankedArtists;
	}
	
	private void computePageRank() {
		// begin with prob vector 1,0,0,0...
		pageRank.set(0, 1.0);
		
		// iterative method
		for(int i = 0; i < 100; ++i){
			pageRankIteration();			
			
			if (pageRank.sanityCheck()){
				logger.info("Everything fine!");
			}
			else{
				logger.error("ERROR computing page rank in interation " + i + "!");
			}
		}
	}
	
	private void pageRankIteration(){
		pageRank = multiply(pageRank, matrix);
	}

	private ProbabilityVector multiply(ProbabilityVector probVector, ProbabilityMatrix probMatrix) {
		double value = 0;
		ProbabilityVector result = new ProbabilityVector(size);
		for (int i = 0; i < size; ++i){
			value = 0;
			for (int k = 0; k < size; ++k){
				value += probVector.get(k) * probMatrix.get(k, i); 				
			}
			result.set(i, value);
		}
		
		return result;
	}

	/**
	 * Based on the link frequencies, it computes the probability vectors using lamda as teleportation rate
	 */
	private void computeProbabilityMatrix() {
		double value = 0;
		for (int i = 0; i < size; ++i)
			
			if (!matrix.isZero(i)) {
				for (int j = 0; j < size; ++j) {
					value = lamda / size + (1 - lamda) * matrix.get(i, j);
					matrix.set(i, j, value);
				}
			} 
			else {
				for (int j = 0; j < size; ++j) {
					value = 1.0 / size;
					matrix.set(i, j, value);
				}
			}
	}

	/**
	 * Computes the probability of navigating from one node to another without
	 * teleportation rate.
	 */
	private void initProbabilityMatrix(){
		// find the twitter handle of each artist
		for (Artist artist : db.getArtists().values()){
			artists.add(artist.getHandle());
		}
		
		int currentArtistIndex = 0;
		double value = 0;
		for (Artist artist : db.getArtists().values()){
			ArrayList<String> mentions = null; 
			
			// get all the tweets
			String text = artist.getTweetsText();
			
			// retain only the "mentions" with @'s
			mentions = getMatches(text);
			
			// for each match, we check if found an artist in our db, increment when found
			int nrMentions = 0;
			for(String mention : mentions){				
				int idx = artists.indexOf(mention);
				
				if (idx != -1){
					// yay, artist found
					++nrMentions;
					matrix.increment(currentArtistIndex, idx);
				}
			}
			
			// divide by nr mentionings => we get probability vectors
			if (nrMentions > 0){
				for (int idx = 0; idx < size; ++idx){
					value = matrix.get(currentArtistIndex, idx);
					matrix.set(currentArtistIndex, idx, value / nrMentions);
				}
			}
			
			++currentArtistIndex;
		}
	}
	

	public ArrayList<Double> getPageRankArray() {
		return pageRankArray;
	}

		
	private ArrayList<String> getMatches(String text){
		Pattern pattern = Pattern.compile("@\\S*");
		Matcher matcher = pattern.matcher(text);
		
		String match = null;
		ArrayList<String> result = new ArrayList<>();
		
		while(matcher.find()){
			match = matcher.group(0);
			match = match.replaceFirst("@", "");
			result.add(match);			
		}
				
		return result;
	}
	
	/**
	 * 
	 * @author cristiprg
	 * Compares two artists based on their Page Rank. Assumess a lot of preconditions...
	 * Especially, that the page rank has been computed
	 */
	class ArtistComparator implements Comparator<Artist>{
		@Override
		public int compare(Artist o1, Artist o2) {				
			return -1 * pageRank.get(artists.indexOf(o1.getHandle()))
					.compareTo(pageRank.get(artists.indexOf(o2.getHandle())));
		}		
	}
}
