package nl.tue.twimu.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TweetsDb implements Serializable {
	//needed for serialization
	private static final long serialVersionUID = 1L;
	public static final String fileName = "tw.db.gz";
	
	//list of all artists
	public TreeMap<String, Artist> artists;
	//the two kinds of caches for the popular querys
	protected TreeMap<String, Integer> idCache;
	protected TreeMap<String, Integer> hashCache;
	
	//save to cache in gzip-compressed version
	public static TweetsDb loadFromCache() throws FileNotFoundException, IOException, ClassNotFoundException{
		File f = new File(fileName);
		ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(f)));
		TweetsDb db = (TweetsDb)ois.readObject();
		ois.close();
		return db;
	}
	
	//constructor, only use for first generation
	public TweetsDb() {
		artists = new TreeMap<>();
	}
	
	public TreeMap<String, Artist> getArtists() {
		return artists;
	}
	
	//overwrites old save, save to static file
	public void save() throws FileNotFoundException, IOException{
		File f = new File(fileName);
		ObjectOutputStream ous = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(f)));
		ous.writeObject(this);
		ous.close();
	}

	//add a new artist, addArtist(new Artist
	public void addArtist(Artist artist) {
		artists.put(artist.getTwitterid(), artist);
	}
	
	//add a tweet from twitterhandle with text and timestamp
	//addTweet("justinbieber", new Tweet("YOLO", new Date()));
	public void addTweet(String twitterHandle, Tweet t) {
		artists.get(twitterHandle).tweets.add(t);
	}
	
	//counts for mentioned hashtags/twitter handles
	private TreeMap<String, Integer> getTotalCountsFor(char type){
		TreeMap<String, Integer> t = new TreeMap<>();
		LinkedList<String> l=null;
		for(Artist a:artists.values()) {
			if(type=='#') {
				l=a.getAllMentionedHashtags();
			} else if (type=='@') {
				l=a.getAllMentionedTwitterIds();
			}
			for(String e:l) {
				if(t.containsKey(e)) {
					t.put(e, t.get(e)+1); //increment the counter by 1				
				} else {	
					t.put(e, 1);
				}
			}
		}
		return t;
	}

	public TreeMap<String, Integer> getTotalCountsHashtags() {
		if(hashCache==null) 
			hashCache=getTotalCountsFor('#');
		return hashCache;
	}
	
	public TreeMap<String, Integer> getTotalCountsTwitterIds() {
		if(idCache==null)
			idCache=getTotalCountsFor('@');
		return idCache;
	}
	

	//print a number of artists and tweets per artists, for debugging
	public void printFirstArists(int artistsMax, int tweetsPerArtistMax) {
		int i=0;
		int j=0;
		for(Artist a:this.artists.values()){
			System.out.println(a);
			j=0;
			for(Tweet t:a.getTweets()) {
				System.out.println("\t"+t);
				if(++j>=artistsMax) break;
			}
			if(++i>=artistsMax) break;
		}
	}



	@Deprecated //see new method with same name, just to be compatible with v0
	public void addTweet(String twitterHandle, String text, Date timestamp) {
		artists.get(twitterHandle).tweets.add(new Tweet(text, timestamp));
	}
}
