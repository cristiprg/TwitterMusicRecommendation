package nl.tue.twimu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import nl.tue.twimu.model.Artist;
import nl.tue.twimu.model.Tweet;
import nl.tue.twimu.model.TweetsDb;

//FOR TESTING ONLY; DO NOT CHANGE/DEVELOP FURTHER UNLESS REALLY NEEDED

public class RandomTweetsDb extends TweetsDb {

	//serialization
	private static final long serialVersionUID = 1L;
	public static final int printUserNumber = 5;
	public static final int printTweetsPerUserNumber = 5;

	public static final int usersNumber = 100;
	public static final int tweetsNumber = 10000;
	
	public static final int seed = 1; //random seed, so every time the same randomdb is generated
	public static final int dateRange = 100000; //ms to the past
	public static final int tweetWords = 5; //words per tweet, for example 
	

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		TweetsDb tweetsDb;
		//create new random if nothing exists
		if(!new File(TweetsDb.fileName).exists()) {
			tweetsDb = new RandomTweetsDb();
			((RandomTweetsDb)tweetsDb).initRandom(usersNumber,tweetsNumber); 
		}else {
			tweetsDb = TweetsDb.loadFromCache();
		}
		tweetsDb.printFirstArists(printUserNumber,printTweetsPerUserNumber); 
		//System.out.println(tweetsDb.getTotalCountsHashtags());
		//System.out.println(tweetsDb.getTotalCountsTwitterIds());
		tweetsDb.save();
	}
	
	private Random r;
	
	public RandomTweetsDb() {
	}

	public void initRandom(int numberUsers, int numberTweets) {
		int i;
		r = new Random(seed); //with seed, so reproducable
		//add 100 artists, artist0, artist1... artist99
		for(i=0;i<numberUsers;i++)
			// asdf asdf
			addArtist(new Artist(new Long(i),"artist"+i, "Artist number "+i));
		
		//add 10000 random tweets from 10000 random artists with dates in the past
		for(i=0;i<numberTweets;i++) {
			// asdf asdf
			addTweet(new Long(r.nextInt(numberUsers)), new Tweet(generateRandomText(tweetWords), new Date(-r.nextInt(dateRange))));
		}
	}

	//the int says how many elements per tweet
	private String generateRandomText(int randomelements) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<randomelements; i++) {
			int rnd = r.nextInt(10);
			if (rnd == 0) { //append hashtag
				sb.append("#").append(newRandomFourletter());
			} else if(rnd == 1) { //append mention of unknown user
				sb.append("@").append(newRandomFourletter());
			} else if(rnd == 2) {
				sb.append("@artist").append(r.nextInt(100));
			} else { //70% chance of text
				sb.append(newRandomFourletter()).append(newRandomFourletter());
			}
			if(i!=(randomelements-1))
				sb.append(" ");
		}
		return sb.toString();
	}
	
	//generate a int between 2^20, get 5-bit-tuples and generate a letter from 0-9a-u. can be less letters too, if the first bits are all 0, chance 1:2^5
	private String newRandomFourletter() {
		return Integer.toString(Math.abs(r.nextInt(1<<5*4)), 32);
	}
	
}
