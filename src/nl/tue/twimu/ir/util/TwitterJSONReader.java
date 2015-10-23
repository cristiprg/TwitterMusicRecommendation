package nl.tue.twimu.ir.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import nl.tue.twimu.model.Artist;
import nl.tue.twimu.model.Tweet;

/**
 * Created by cristiprg on 10/18/2015. Helper class that reads stuff from
 * Twitter JSON files.
 */
public class TwitterJSONReader {

	// prevents unnecessarily re-reading files
	private static String textFileCache = "";
	private static String lastFileAccessed = "";

	/**
	 * Gets the tweets text from a twitter __ARRAY__ json file.
	 * 
	 * @param
	 * @return
	 * @throws ParseException 
	 */
	public static ArrayList<String> getTweetsText(File file) throws IOException, ParseException {

		JSONArray tweets = openJason(file);
		ArrayList<String> result = new ArrayList<>();
		for (int i = 0; i < tweets.size(); i++) {
			JSONObject tweet = (JSONObject) tweets.get(i);
			String text = (String) tweet.get("text");
			result.add(text);
		}
		return result;
	}

	/**
	 * Gets the tweets from a twitter __ARRAY__ json file.
	 * 
	 * @param
	 * @return
	 * @throws ParseException 
	 */
	public static ArrayList<Tweet> getTweets(File file) throws IOException, ParseException {

		JSONArray tweets = openJason(file);
		ArrayList<Tweet> result = new ArrayList<>();
		for (int i = 0; i < tweets.size(); i++) {
			JSONObject tweet = (JSONObject) tweets.get(i);
			String text = (String) tweet.get("text");
			result.add(new Tweet(text));
		}
		return result;
	}

	/**
	 * Gets the hashtags from a twitter __ARRAY__ json file.
	 * 
	 * @param pathToJSONTweet
	 * @return
	 * @throws IOException
	 * @throws ParseException 
	 */
	public static ArrayList<String> getHashTags(File file) throws IOException, ParseException {
		JSONArray tweets = openJason(file);
		ArrayList<String> result = new ArrayList<>();
		for (int i = 0; i < tweets.size(); i++) {
			JSONObject tweet = (JSONObject) tweets.get(i);
			JSONObject entities = (JSONObject) tweet.get("entities");
			JSONArray hashtags = (JSONArray) entities.get("hashtags");
			for (int j = 0; j < hashtags.size(); ++j) {
				String hashtag = (String) ((JSONObject) hashtags.get(j)).get("text");
				result.add(hashtag);
			}
		}
		return result;
	}

	/**
	 * Gets the Artist from a twitter __ARRAY__ json file.
	 * 
	 * @param pathToJSONTweet
	 * @return
	 * @throws IOException
	 * @throws ParseException 
	 */
	public static Artist getTweetsAuthor(File file) throws IOException, ParseException {
		JSONArray tweets = openJason(file);
		JSONObject job = (JSONObject) ((JSONObject) tweets.get(0)).get("user");
		return new Artist(Long.parseLong(job.get("id").toString()), (String) job.get("screen_name"), (String) job.get("name"), (String) job.get("description"));
	}

	static JSONArray openJason(File file) throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		JSONArray a=null;
		try {
			a = (JSONArray) parser.parse(new FileReader(file));
		} catch (Exception e) {
			System.err.println("Problem in file: "+file);
			e.printStackTrace();
		}
		return a;
	}
	/**
	 * Extracts the text out of a file.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	static String readFile(String path) throws IOException {
		if (!lastFileAccessed.equals(path)) {
			// we're accessing a new file, so re-read it
			byte[] encoded = Files.readAllBytes(Paths.get(path));
			textFileCache = new String(encoded);
			lastFileAccessed = path;
		}

		return textFileCache;
	}
}
