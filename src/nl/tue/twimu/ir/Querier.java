package nl.tue.twimu.ir;

import java.util.ArrayList;
import java.util.List;

public class Querier {
	private List<String> artists;
	private List<String> terms;
	private List<ArrayList<Double>> matrix; // [i:term][j:artist]

	public final static int NUM_RESULTS = 3;

	public Querier(List<String> artists, List<String> terms, List<ArrayList<Double>> matrix) {
		this.artists = artists;
		this.terms = terms;
		this.matrix = matrix;
	}

	public List<String> search(String query) {
		if(query.startsWith("@"))
			return searchByArtist(query);
		return searchByTerms(query);
	}
	
	private List<String> searchByTerms(String query) {
		List<String> words = fixQuery(query);
		ArrayList<Double> scores = new ArrayList<Double>();
		double score;
		for (int j = 0; j < artists.size(); j++) {
			score = 0;
			for (String t : words) 
				score+=matrix.get(terms.indexOf(t)).get(j);
			scores.add(score);
		}
		printList(scores);
		return getTop(scores);
	}

	private List<String> fixQuery(String query) {
		String[] words = query.replace(',', ' ').split(" ");
		List<String> ret = new ArrayList<String>();
		for (String t : words) 
			if (terms.contains(t))
				ret.add(t);
		
		return ret;
	}

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
		
		//printList(similarities);
		return getTop(similarities);
	}
	
	private List<String> getTop(ArrayList<Double> similarities) {
		ArrayList<String> top = new ArrayList<String>();
		int counter = 0;
		do {
			double max = -1.0; int idx = -1;
			for (int i = 0; i<similarities.size(); i++) 
				if (similarities.get(i) > max) {
					max = similarities.get(i);
					idx = i;
				}
			top.add(artists.get(idx));
			counter++;
			similarities.set(idx, -1.0);
		} while (counter < NUM_RESULTS);
		return top;
	}

	/**
	 * Computes the cosine similarity of two artists given their twitter handles.
	 * @param artist1 Handle of artist 1
	 * @param artist2 Handle of artist 2
	 * @return cosine similarity
	 */
	private Double similarity(int artist1, int artist2) {
		//dot product of both artists
		double dot = 0;
		for (int i = 0; i<terms.size(); i++)
			dot += matrix.get(i).get(artist1)*matrix.get(i).get(artist2);
		
		//euclidean lengths
		double euc1 = 0, euc2 = 0, euc;
		for (int i = 0; i<terms.size(); i++) {
			euc1 += Math.pow(matrix.get(i).get(artist1), 2);
			euc2 += Math.pow(matrix.get(i).get(artist2), 2);
		}
		euc = Math.sqrt(euc1)*Math.sqrt(euc2);
		
		return dot/euc;
	}

	private void printList(List<Double> ls) {
		for (Double d:ls)
			System.out.print(d+"||");
		System.out.println();
	}
}
