package nl.tue.twimu.ir;

import java.util.ArrayList;
import java.util.List;

public class Querier {
	private List<String> artists;
	private List<String> terms;
	// private List<ArrayList<Double>> matrix; // [i:term][j:artist]
	private TFIDFMatrix matrix;

	public final static int NUM_RESULTS = 3;

	public Querier(List<String> artists, List<String> terms, TFIDFMatrix matrix) {
		this.artists = artists;
		this.terms = terms;
		this.matrix = matrix;
	}

	public Querier(Indexer indexer) {
		this(indexer.getArtists(), indexer.getTerms(), indexer.getMatrix());
	}

	public List<String> search(String query) {
		if (query.startsWith("@"))
			return searchByArtist(query);
		return searchByTerms(query);
	}

	public List<String> search(double[] query) {
		ArrayList<Double> similarities = new ArrayList<Double>();

		for (int j = 0; j < artists.size(); j++)
			similarities.add(similarity(query, j));

		return getTop(similarities);
	}

	private Double similarity(double[] query, int artist2) {
		// dot product of both artists
		double dot = 0;
		for (int i = 0; i < terms.size(); i++) {
			dot += query[i] * matrix.getItem(i, artist2);
		}

		// euclidean lengths
		double euc1 = 0, euc2 = 0, euc;
		for (int i = 0; i < terms.size(); i++) {
			euc1 += Math.pow(query[i], 2);
			euc2 += Math.pow(matrix.getItem(i, artist2), 2);
		}
		euc = Math.sqrt(euc1) * Math.sqrt(euc2);

		return dot / euc;
	}

	private List<String> searchByTerms(String query) {
		List<String> words = fixQuery(query);
		System.out.println("Searching for " + words);
		ArrayList<Double> scores = new ArrayList<Double>();
		double score;
		for (int j = 0; j < artists.size(); j++) {
			score = 0;
			for (String t : words) {
				// score+=matrix.get(terms.indexOf(t)).get(j);
				score += matrix.getItem(terms.indexOf(t), j);
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
		} while (counter < NUM_RESULTS);
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
			dot += matrix.getItem(i, artist1) * matrix.getItem(i, artist2);
		}

		// euclidean lengths
		double euc1 = 0, euc2 = 0, euc;
		for (int i = 0; i < terms.size(); i++) {
			// euc1 += Math.pow(matrix.get(i).get(artist1), 2);
			// euc2 += Math.pow(matrix.get(i).get(artist2), 2);

			euc1 += Math.pow(matrix.getItem(i, artist1), 2);
			euc2 += Math.pow(matrix.getItem(i, artist2), 2);
		}
		euc = Math.sqrt(euc1) * Math.sqrt(euc2);

		return dot / euc;
	}

	private void printList(List<Double> ls) {
		for (Double d : ls)
			System.out.print(d + "||");
		System.out.println();
	}
}
