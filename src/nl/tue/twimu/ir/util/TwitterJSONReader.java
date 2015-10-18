package nl.tue.twimu.ir.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.json.*;

import nl.tue.twimu.model.Tweet;
import nl.tue.twimu.model.TweetsDb;

/**
 * Created by cristiprg on 10/18/2015.
 * Helper class that reads stuff from Twitter JSON files.
 */
public class TwitterJSONReader {

	// prevents unnecessarily re-reading files
	private static String textFileCache = ""; 
	private static String lastFileAccessed = "";
	
    /**
     * Gets the tweets text from a twitter __ARRAY__ json file.
     * @param
     * @return
     */
    public static ArrayList<String> getTweetsText(String pathToJSONTweet) throws IOException{

        JSONArray array =  new JSONArray(readFile(pathToJSONTweet));
        ArrayList<String> result = new ArrayList<>();

        for(int i = 0; i < array.length(); i++){
            String tweetText = array.getJSONObject(i).getString("text");
            result.add(tweetText);
        }

        return result;
    }

    /**
     * Gets the tweets from a twitter __ARRAY__ json file.
     * @param
     * @return
     */
    public static ArrayList<Tweet> getTweets(String pathToJSONTweet) throws IOException{

        JSONArray array =  new JSONArray(readFile(pathToJSONTweet));
        ArrayList<Tweet> result = new ArrayList<>();

        for(int i = 0; i < array.length(); i++){
            String tweetText = array.getJSONObject(i).getString("text");
            result.add(new Tweet(tweetText));
        }
        return result;
    }

    
    
    /**
     * Gets the hashtags from a twitter __ARRAY__ json file.
     * @param pathToJSONTweet
     * @return
     * @throws IOException
     */
    public static ArrayList<String> getHashTags(String pathToJSONTweet) throws IOException{

        JSONArray array =  new JSONArray(readFile(pathToJSONTweet));
        ArrayList<String> result = new ArrayList<>();
        
        for(int i = 0; i < array.length(); i++){
            JSONArray entities = array.getJSONObject(i).getJSONObject("entities").getJSONArray("hashtags");
            for(int j = 0; j < entities.length(); ++j){
                String hashtag = entities.getJSONObject(j).getString("text");
                result.add(hashtag);
            }
        }
        return result;
    }
    
    /**
     * Gets the twitter handle/name from a twitter __ARRAY__ json file.
     * @param pathToJSONTweet
     * @return
     * @throws IOException
     */
    public static String getTwitterHandle(String pathToJSONTweet) throws IOException{

        JSONArray array =  new JSONArray(readFile(pathToJSONTweet));
        
        //for(int i = 0; i < array.length(); i++){
            String name = array.getJSONObject(0).getJSONObject("user").getString("name");
           // for(int j = 0; j < entities.length(); ++j){
           //     String hashtag = entities.getJSONObject(j).getString("text");
           //     result.add(hashtag);
           // }
        //}
        return name;
    }

    /**
     * Extracts the text out of a file.
     * @param path
     * @return
     * @throws IOException
     */
    static String readFile(String path) throws IOException
    {
    	if (!lastFileAccessed.equals(path)){
    		// we're accessing a new file, so re-read it
    		byte[] encoded = Files.readAllBytes(Paths.get(path));    		
    		textFileCache = new String(encoded);
    		lastFileAccessed = path;
    	}
    	
    	return textFileCache;
    }
}
