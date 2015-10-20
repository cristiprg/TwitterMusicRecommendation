package nl.tue.twimu.ir;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import nl.tue.twimu.model.TweetsDb;
import nl.tue.twimu.model.Artist;

/**
 * @author cristiprg
 * Indexing class, populates the TweetsDB with tweets/artists and computes the tf.idf matrix.
 */
public class Indexer {
	final static Logger logger = Logger.getLogger(Indexer.class);
	
	private TweetsDb db;
	private TFIDFMatrix matrix;
	
	/**
	 * New db and matrix;
	 */
	public Indexer(){
		matrix = new TFIDFMatrix();
		
		// either populate the DB, or load it
		try {
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
	 * @return
	 */
	public ArrayList<String> getArtists(){
		return matrix.getArtists();
	}
	
	/**
	 * Get all the indexed terms
	 * @return
	 */
	public ArrayList<String> getTerms(){
		return matrix.getTerms();
	}
	
	public TFIDFMatrix getMatrix(){
		return matrix;
	}
	
	private void computeTFIDFMatrix() {
		
		boolean DEBUG = false;
		int count = 0;
		
		// each artist is a document here				
		for (Artist artist : db.getArtists().values()){
			logger.info("Adding artist to tf.idf matrix:" + artist.getHandle() );
			
			matrix.addAArtist(artist);
			
			if (DEBUG && ++count > 10){
				break;
			}
		}

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
