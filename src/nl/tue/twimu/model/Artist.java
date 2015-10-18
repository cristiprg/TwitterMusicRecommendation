package nl.tue.twimu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class Artist implements Serializable{
	//needed for serialization
	private static final long serialVersionUID = 1L;
	
	//public String twitterid; //justinbieber		//without @ in front
	public Long twitterid; // just a big integer
	public String handle;   //Justin Bieber
	public ArrayList<Tweet> tweets = new ArrayList<>();
	
	public LinkedList<String> idCache;
	public LinkedList<String> hashCache;
	
	public Artist(Long twitterid, String handle) {
		super();
		this.twitterid = twitterid;
		this.handle = handle;
	}
	
	public String getHandle() {
		return handle;
	}
	
	public Long getTwitterid() {
		return twitterid;
	}
	
	public LinkedList<String> getAllMentionedTwitterIds(){
		if(idCache==null) {
			idCache = new LinkedList<>();
			for(Tweet t:tweets) {
				idCache.addAll(t.getTwitterIds());
			}
		}
		return idCache;
	}
	
	public LinkedList<String> getAllMentionedHashtags(){
		if(hashCache==null) {
			hashCache = new LinkedList<>();
			for(Tweet t:tweets) {
				hashCache.addAll(t.getHashtags());
			}
		}
		return hashCache;
	}
	
	public ArrayList<Tweet> getTweets() {
		return tweets;
	}
	
	@Override
	public String toString() {
		return "Userid: "+twitterid+"\t"+
	"Username: "+handle+"\t"+
	"Hashtags:"+getAllMentionedHashtags()+"\t"+
	"TwitterIds:"+getAllMentionedTwitterIds();
	}

}
