package nl.tue.twimu.ir;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;

import nl.tue.twimu.ir.util.TwitterJSONReader;
import nl.tue.twimu.model.Artist;
import nl.tue.twimu.model.Tweet;
import nl.tue.twimu.model.TweetsDb;

public class TweetsLoader {
	final static Logger logger = Logger.getLogger(TweetsLoader.class);
	public final static String DEF_PATH = "d:\\Scoala\\WIR&DM\\artisttweets\\";

	/*public static void main(String[] args) {
		loadTweets(DEF_PATH);
	}*/

	public static TweetsDb loadTweets(String path) {
		System.out.println("New Tweet Database is being created");
		System.out.println("Reading tweets by artistis");
		TweetsDb db = new TweetsDb();
		// Get all the files from path
		ArrayList<File> files = new ArrayList<>();
		files.addAll(FileUtils.listFiles(new File(path), new SuffixFileFilter(".json"), TrueFileFilter.INSTANCE));

		for (File f : files) {
			try {
				Artist artist = TwitterJSONReader.getTweetsAuthor(f);
				db.addArtist(artist);
				//logger.info("Loading artist: " + artist.getName());
				ArrayList<Tweet> tweets = TwitterJSONReader.getTweets(f);
				db.addTweets(artist.getTwitterid(), tweets);
			} catch (IOException | ParseException e) {
				System.err.println("Error reading file " + f.getName());
				e.printStackTrace();
			}
		}
		try {
			db.save();
			logger.info("Database created and saved!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return db;
	}

}
