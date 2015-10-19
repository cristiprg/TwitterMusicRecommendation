package nl.tue.twimu.ir;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;

import nl.tue.twimu.model.TweetsDb;
import nl.tue.twimu.ir.util.TwitterJSONReader;
import nl.tue.twimu.model.Artist;
import nl.tue.twimu.model.Tweet;

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
		db = new TweetsDb();
		matrix = new TFIDFMatrix();
		
		// either populate the DB, or load it
		try {
			db = TweetsDb.loadFromCache();
		} catch (ClassNotFoundException | IOException e) {
			logger.warn("Could not load DB from cache. Repopulating DB and computing tf.idf matrix ...");
			populateDB(new File("D:\\Scoala\\WIR&DM\\artisttweets"));
			//computeTFIDFMatrix();
		}
		
		computeTFIDFMatrix();

	}
	
	public TFIDFMatrix getMatrix(){
		return matrix;
	}
	
	private void computeTFIDFMatrix() {
		
		boolean DEBUG = true;
		int count = 0;
		
		// each artist is a document here				
		for (Artist artist : db.getArtists().values()){
			logger.info("Adding artist to tf.idf matrix:" + artist.getHandle() );
			
			matrix.addAArtist(artist);
			
			if (DEBUG && ++count > 10){
				break;
			}
		}
		
		//System.out.println(matrix.toString());
	}


	/**
	 * Looks for each json file in dir. For now, each json is an artist with tweets. 
	 */
	private void populateDB(File dir){
		
		// Get all the files from dir
		ArrayList<File> files = new ArrayList<>();
        files.addAll(FileUtils.listFiles(dir, new SuffixFileFilter(".json"), TrueFileFilter.INSTANCE));
        
        // Prepare regex to identify the ID
        Pattern p = Pattern.compile("[0-9]+");
        Matcher m = null;
        
        // each file corresponds to an artist. Add them and the tweets in DB
        for(File f : files){        
        	m = p.matcher(f.getName());
        	
        	
        	if (!m.find()){
        		// skip if didn't find the expected ID
        		logger.warn("This filename is supposed to contain a twitter ID: " + f.getName());
        		continue;
        	}             
        	
			try {
				// Artist's twitterID and handle
				Long twitterid = Long.valueOf(m.group());
				String handle = TwitterJSONReader.getTwitterHandle(f.getAbsolutePath());
				db.addArtist(new Artist(twitterid, handle));
				
				// Artist's tweets			
				db.addTweets(twitterid, TwitterJSONReader.getTweets(f.getAbsolutePath()));					
			} catch (IOException e) {
				logger.error("Could not retrieve twitter handle from " + f.getAbsolutePath());
				e.printStackTrace();				
			}			        
        }
        
        try {
			db.save();
		} catch (IOException e) {
			logger.error("Could not save DB!");
			e.printStackTrace();
		}
	}
}
