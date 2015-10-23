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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

import nl.tue.twimu.model.Artist;

/**
 * @author cristiprg
 * Implementation of tf idf matrix. Here you have a lot of useful information: all the indexed artists,
 * terms (strings only so far), the matrix of term frequencies, the array with document frequencies of terms.
 * The values are retrieved via the getItem method.
 *
 * Unfortunately, it does a little more than that: it contains also the page rank values of artists
 * (computed in the nl.tue.twimu.ragerank package) and uses them to modify the tf.idf values based on the
 * three policies NO_PAGE_RANK, USE_PAGE_RANK or USE_INVERTED_PAGE_RANK
 */
public class TFIDFMatrix implements Serializable{
	private static final long serialVersionUID = 1L;	

	final static Logger logger = Logger.getLogger(TFIDFMatrix.class);


	private ArrayList<String> artists;
	private ArrayList<String> terms;
	private ArrayList<Integer> df; // df[i] = document frequency of term i
	public static final String fileName = "tfidf.db.gz";
	//220mb memory, r u kiddin' me //public ArrayList<ArrayList<Integer>> matrix;
	public TreeMap<Integer, Integer> h;

	//TODO: ENUMS?
	/**
	 * PageRank will not influence the returned tf.idf values.
	 */
	public final static int NO_PAGE_RANK = 0;

	/**
	 * PageRank will proportionally influence the tf.idf values, i.e.
	 * will increase the value if the artist has a high rank.
	 * Useful for retrieving most mentioned/popular artist.
	 */
	public final static int USE_PAGE_RANK = 1;

	/**
	 * PageRank will inverse-proportionally influence the tf.idf values, i.e.
	 * will decrease the value if the artist has a high rank.
	 * Useful for retrieving more "underground" stuff rather than popular.
	 */
	public final static int USE_INVERTED_PAGE_RANK = 2;

	private ArrayList<Double> pageRank;
	

	public static final int SHIFT = 19;
	
	public void put(int i, int j, int val) {
		if (val>0) {
			//System.out.println(i+" "+j);
			h.put(i+j<<SHIFT, val);
		}
	}
	
	public int get(int i, int j) {
		Integer val = h.get(i+j<<SHIFT);
		if(val==null)
			val = 0;
		return val;
	}

	public TFIDFMatrix(){
		artists = new ArrayList<String>();
		terms = new ArrayList<String>();
		h = new TreeMap<>();
		df = new ArrayList<Integer>();
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

	/**
	 * This will index the terms found in an artist's tweets (retrieved from the DB) - note that all the
	 * tweets are considered, not individual tweets.
	 * It adds a new column for the artist, counts the TF of the terms and adds them to the matrix.
	 * This technique is known to be slow, but hey, it works :P
	 */
	public void addAArtist(Artist artist) {
		artists.add(artist.getHandle());

		// now compute for each term
		// TODO: replace by Lecene tokenizer
		// http://stackoverflow.com/questions/21771566/calculating-frequency-of-each-word-in-a-sentence-in-java
		String allTweets = artist.getTweetsText();

		Map<String, Integer> map = new HashMap<>();
		for (String w : allTweets.split("\\s")) {
			// filter out zero-length strings,
			if (w.length() == 0) continue;

			w = termPreProcessing(w);

			Integer n = map.get(w);
			n = n == null ? 1 : ++n;
			map.put(w, n);
		}

		// add term into the right place
		int size = 0;
		for (String term : map.keySet()){
			// get the index of the term
			int index = findIndexOf(term);

			if (index != -1){
				// if found, add the frequency of the term - just update the last item
				// matrix.get(index).set(artists.size()-1, map.get(term));
				put(index, size, map.get(term));

				// increment the document frequency of the term
				df.set(index, df.get(index)+1);
			}
			else{
				// if not found, 1) add the new term to the list of terms and 2) create a new entry in the
				// matrix, fill it with zero's everything but the current artist+
				index = terms.size();
				terms.add(term);
				put(index, size, get(index,size)+1);

				// also, add a new entry in the document frequency
				df.add(1);
			}
			size++;
		}
	}

	/**
	 * @return the list of indexed artists as strings.
	 */
	public ArrayList<String> getArtists() {
		return artists;
	}

	/**
	 * @return list of indexed terms as strings
	 */
	public ArrayList<String> getTerms() {
		return terms;
	}

	/**
	 * Returns an item in the tf.idf matrix.
	 * @param i Index of term
	 * @param j Index of artist
	 * @return corresponding tf.idf value
	 */
	private Double getItem(int i, int j){
		//this is not C!
		// preconditions: i and j within bounds
		//if ((i >= 0 && i < df.size() && j >= 0 && j < artists.size())) 			return 0.0;
		int N = artists.size();
		int docFreq = df.get(i);
		double a=0;
		
		try {
			// tf * log (N/df)
			a = h.get(i+j<<SHIFT) * Math.log((double)N / docFreq);
		} catch (Exception e) { //null, indexoutofbounds, divide by zero
			return 0.0;
		}

		/*System.out.println("term = " + terms.get(i));
		System.out.println("N = " + N);
		System.out.println("docFreq = " + docFreq);
		if (docFreq == 0 ) //this is still not c
			return 0.0;*/
		return a;
	}

	/**
	 * Returns an item in the tf.idf matrix multiplied by the page rank
	 * @param i Index of term
	 * @param j Index of artist
	 * @param pageRankType what type of page rank you want (check docs for individual options):
	 * 			*) NO_PAGE_RANK - simply, not use it, just return tf.idf
	 * 			*) USE_PAGE_RANK - get the tf.idf + page_rank of artist
	 * 			*) USE_INVERTED_PAGE_RANK - get the tf.idf + (1-page_rank) - useful for least popular artists, underground music
	 * @return
	 */
	public Double getItem(int i, int j, int pageRankType){
		//System.out.println("page rank " + artists.get(j) + " = " + pageRank.get(j));
		switch (pageRankType) {
		case NO_PAGE_RANK:
			return getItem(i, j);
		case USE_PAGE_RANK:
			return getItem(i, j) + pageRank.get(j);
		case USE_INVERTED_PAGE_RANK:
			return getItem(i, j) + 1 - pageRank.get(j);
		default:
			logger.error("Unknown page rank type " + pageRankType + ". Using default USE_PAGE_RANK.");
			return getItem(i, j) + pageRank.get(j);
		}
	}

	public void setPageRank(ArrayList<Double> pageRank) {
		this.pageRank = pageRank;
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
				str.append(getItem(i, j) + "\t");
			}
		return str.toString();
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
		return TermPreprocessor.termPreProcessing(term);
	}
}
