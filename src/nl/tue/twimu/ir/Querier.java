package nl.tue.twimu.ir;

import java.util.ArrayList;
import java.util.List;

public class Querier {
	private List<String> artists;
	private List<String> terms;
	// private List<ArrayList<Double>> matrix; // [i:term][j:artist]
	public TFIDFMatrix matrix;
	
	private int pageRankType = TFIDFMatrix.USE_PAGE_RANK;

	public final static int NUM_RESULTS = 3;
	public static boolean detectedArtistQuery = false; 
	
	public final static double DEF_SCORE = 5.0;	

	public Querier(List<String> artists, List<String> terms, TFIDFMatrix matrix, int pageRankType) {
		this.artists = artists;
		this.terms = terms;
		this.matrix = matrix;
		this.pageRankType = pageRankType;
	}

	public Querier(Indexer indexer, int pageRankType) {
		this(indexer.getArtists(), indexer.getTerms(), indexer.getMatrix(), pageRankType);
	}

	@Deprecated
	public List<String> search(String query) {
		if (query.startsWith("@"))
			return searchByArtist(query);
		return searchByTerms(query);	
		
	}
	
	/**
	 * By default, we search NOT by artist
	 * @param query
	 * @return
	 */
	public List<String> search(double[] query){
		return search(query, false);
	}

	public List<String> search(double[] query, boolean byArtists) {
		ArrayList<Double> similarities = new ArrayList<Double>();

		for (int j = 0; j < artists.size(); j++)
			similarities.add(similarity(query, j));

		/*
		 * If we search by artist, remove the first resulted-item, since it is the same as the search query 
		 */
		List<String> results = getTop(similarities);
		if(byArtists){
			results.remove(0); 
		}
		
		return results;
	}

	private Double similarity(double[] query, int artist2) {
		// dot product of both artists
		double dot = 0;
		for (int i = 0; i < terms.size(); i++) {
			dot += query[i] * matrix.getItem(i, artist2, pageRankType);
		}

		// euclidean lengths
		double euc1 = 0, euc2 = 0, euc;
		for (int i = 0; i < terms.size(); i++) {
			euc1 += Math.pow(query[i], 2);
			euc2 += Math.pow(matrix.getItem(i, artist2, pageRankType), 2);
		}
		euc = Math.sqrt(euc1) * Math.sqrt(euc2);

		return dot / euc;
	}

	@Deprecated
	private List<String> searchByTerms(String query) {
		List<String> words = fixQuery(query);
		System.out.println("Searching for " + words);
		ArrayList<Double> scores = new ArrayList<Double>();
		double score;
		for (int j = 0; j < artists.size(); j++) {
			score = 0;
			for (String t : words) {
				// score+=matrix.get(terms.indexOf(t)).get(j);
				score += matrix.getItem(terms.indexOf(t), j, pageRankType);
			}
			scores.add(score);
		}
		return getTop(scores);
	}

	private List<String> fixQuery(String query) {
		String[] words = query.replace(',', ' ').split(" ");
		List<String> ret = new ArrayList<String>();
		for (String t : words) {
			t = TermPreprocessor.termPreProcessing(t);
			if (terms.contains(t))
				ret.add(t);
		}

		return ret;
	}

	@Deprecated
	private List<String> searchByArtist(String artist) {
		ArrayList<Double> similarities = new ArrayList<Double>();
		int idxArt = artists.indexOf(artist);

		if (idxArt == -1)
			return new ArrayList<String>();

		for (int j = 0; j < artists.size(); j++)
			if (j == idxArt)
				similarities.add(-1.0);
			else
				similarities.add(similarity(idxArt, j));

		// printList(similarities);
		return getTop(similarities);
	}

	private List<String> getTop(ArrayList<Double> similarities) {
		ArrayList<String> top = new ArrayList<String>();
		int counter = 0;
		do {
			double max = -1.0;
			int idx = -1;
			for (int i = 0; i < similarities.size(); i++)
				if (similarities.get(i) > max) {
					max = similarities.get(i);
					idx = i;
				}
			top.add(artists.get(idx));
			counter++;
			similarities.set(idx, -1.0);
		} while (counter < NUM_RESULTS + 1);
		return top;
	}

	/**
	 * Computes the cosine similarity of two artists given their twitter
	 * handles.
	 * 
	 * @param artist1
	 *            Handle of artist 1
	 * @param artist2
	 *            Handle of artist 2
	 * @return cosine similarity
	 */
	private Double similarity(int artist1, int artist2) {
		// dot product of both artists
		double dot = 0;
		for (int i = 0; i < terms.size(); i++) {
			// dot += matrix.get(i).get(artist1)*matrix.get(i).get(artist2);
			dot += matrix.getItem(i, artist1, pageRankType) * matrix.getItem(i, artist2, pageRankType);
		}

		// euclidean lengths
		double euc1 = 0, euc2 = 0, euc;
		for (int i = 0; i < terms.size(); i++) {
			// euc1 += Math.pow(matrix.get(i).get(artist1), 2);
			// euc2 += Math.pow(matrix.get(i).get(artist2), 2);

			euc1 += Math.pow(matrix.getItem(i, artist1, pageRankType), 2);
			euc2 += Math.pow(matrix.getItem(i, artist2, pageRankType), 2);
		}
		euc = Math.sqrt(euc1) * Math.sqrt(euc2);

		return dot / euc;
	}
	
	public static double[] queryValues(String query, TFIDFMatrix mx, int pageRankType) throws Exception {
		if (query.startsWith("@")){
			return queryValuesArtist(query, mx, pageRankType);
		} else {
			return queryValuesTerms(query, mx, pageRankType);
		}
	}
	
	private static double[] queryValuesArtist(String query, TFIDFMatrix mx, int pageRankType) throws Exception {
		detectedArtistQuery = true;
		query = query.replaceFirst("@", "");
		double[] values = new double[mx.getTerms().size()];
		
		//TODO: check if artist doesn't exist
		int idx = mx.getArtists().indexOf(query);
		if (idx == -1){
			throw new Exception("Artist not found: " + query);
		}
		
		for(int i = 0; i<mx.getTerms().size(); i++) {
			values[i] = mx.getItem(i, idx, pageRankType);
		}
				
		return values;
	}

	private static double[] queryValuesTerms(String query, TFIDFMatrix mx, int pageRankType) {
		detectedArtistQuery = false;
		String[] words = query.replace(',', ' ').split(" ");
		double[] values = new double[mx.getTerms().size()];
		for (String w : words){
			w = TermPreprocessor.termPreProcessing(w);
			int index = mx.getTerms().indexOf(w);
			if(index==-1)
				return null;
			values[index] = DEF_SCORE;
			
		}		
		return values;
	}
	
	//You can simply print the list. It has a toString
	/*private void printList(List<Double> ls) {
		for (Double d : ls)
			System.out.print(d + "||");
		System.out.println();
	}*/
}
