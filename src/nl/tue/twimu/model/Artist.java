package nl.tue.twimu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class Artist implements Serializable {
	// needed for serialization
	private static final long serialVersionUID = 1L;

	
	private Long twitterid; // just a big integer
	private String handle; // @justinbieber
	public String name; // Justin Bieber
	private String description = null;
	public ArrayList<Tweet> tweets = new ArrayList<>();
	private Style style = new Style();

	public LinkedList<String> idCache;
	public LinkedList<String> hashCache;


	public Artist(Long twitterid, String handle, String name) {
		super();
		this.twitterid = twitterid;
		this.handle = handle;
		this.name = name;
	}

	public Artist(Long twitterid, String handle, String name, String description) {
		this(twitterid, handle, name);
		if(description==null) 
			description="";
		this.description = description;
	}

	public String getHandle() {
		return handle;
	}

	public Long getTwitterid() {
		return twitterid;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public Style getStyle() {
		return style;
	}
	
	public String getTweetsText() {
		// for each tweet, get the text
		StringBuilder stringBuilder = new StringBuilder();
		for (Tweet tweet : getTweets()) {
			stringBuilder.append(tweet.getText() + " ");
		}
		return stringBuilder.toString();
	}

	public LinkedList<String> getAllMentionedTwitterIds() {
		if (idCache == null) {
			idCache = new LinkedList<>();
			for (Tweet t : tweets) {
				idCache.addAll(t.getTwitterIds());
			}
		}
		return idCache;
	}

	public LinkedList<String> getAllMentionedHashtags() {
		if (hashCache == null) {
			hashCache = new LinkedList<>();
			for (Tweet t : tweets) {
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
		return "Name: " + name + "||" + "Username: " + handle + "||" + "Hashtags:" + getAllMentionedHashtags()
				+ "||" + "TwitterIds:" + getAllMentionedTwitterIds();
	}
	

}
