package nl.tue.twimu.ir;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import nl.tue.twimu.model.Artist;
import nl.tue.twimu.model.TweetsDb;
import nl.tue.twimu.pagerank.PageRankBuilder;

/**
 * @author cristiprg
 * Indexing class, populates the TweetsDB with tweets/artists and computes the tf.idf matrix.
 */
public class Indexer {
	final static Logger logger = Logger.getLogger(Indexer.class);

	private TweetsDb db;
	private TFIDFMatrix matrix;

	private PageRankBuilder pageRankBuilder;

	/**
	 * New db and matrix;
	 */
	public Indexer(){
		matrix = new TFIDFMatrix();

		// either populate the DB, or load it
		try {
			logger.info("Loading objects from cache. Please be patient ...");

			db = TweetsDb.loadFromCache();
			matrix = TFIDFMatrix.loadFromCache();
		}
		catch (ClassNotFoundException | IOException e) {
			logger.warn("Could not load DB from cache. Repopulating DB and computing tf.idf matrix ...");
			populateDB(TweetsLoader.DEF_PATH);
			computeTFIDFMatrix();
		}
	}

	/**
	 * Get all the indexed artists
	 * @return the list of artists as strings
	 */
	public ArrayList<String> getArtists(){
		return matrix.getArtists();
	}

	/**
	 * Get all the indexed terms
	 * @return the list of indexed term as strings
	 */
	public ArrayList<String> getTerms(){
		return matrix.getTerms();
	}

	/**
	 * Just a getter, nothing fancy.
	 * @return the whole TF IDF matrix associated to this indexer.
	 */
	public TFIDFMatrix getMatrix(){
		return matrix;
	}

	/**
	 * Adds each artist in the DB to the matrix which indexes each term found in his tweets. See TFIDFMatrix.addArtist for details.
	 */
	private void computeTFIDFMatrix() {

		boolean DEBUG = false;
		int count = 0;

		// each artist is a document here
		for (Artist artist : db.getArtists().values()){
			logger.info("Adding artist to tf.idf matrix:" + artist.getHandle() );

			matrix.addAArtist(artist);

			// keep matrix small if debugging
			if (DEBUG && ++count > 10){
				break;
			}
		}

		// set the page rank
		pageRankBuilder = new PageRankBuilder(db);
		matrix.setPageRank(pageRankBuilder.getPageRankArray());

		try {
			matrix.save();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//System.out.println(matrix.toString());
	}


	/**
	 * Looks for each json file in dir. For now, each json is an artist with tweets.
	 */
	private void populateDB(String path){
		db = TweetsLoader.loadTweets(path);
	}
}
