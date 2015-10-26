package nl.tue.twimu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tweet implements Serializable{

	//needed for serialization
	private static final long serialVersionUID = 1L;
	
	//search patterns for words that start with #/@ and then only consist of a-z; A-Z, 0-9
	private static final Pattern hashtagP = Pattern.compile("#[a-zA-Z0-9_]+");
	private static final Pattern twitterIdP = Pattern.compile("@[a-zA-Z0-9_]+");
	
	
	private String text;
	private Date timestamp;
	
	//constructor
	public Tweet(String text, Date timestamp) {
		super();
		this.text = text;
		this.timestamp = timestamp;
	}
	
	//why do we need the timestamp? //it's just 4 bytes and we could use it later
	public Tweet(String text){
		this(text, new Date());
	}
	
	public String getText() {
		return text;
	}
	
	//internal method, do not care
	private List<String> getMatches(Pattern p) {
		LinkedList<String> l = new LinkedList<>();
		Matcher m = p.matcher(text);
		while(m.find())
			l.add(m.group().substring(1)); //without first sign like @ or #
		return l;
	}

	//not cached!
	public List<String> getHashtags(){
		return getMatches(hashtagP);
	}

	//not cached!
	public List<String> getTwitterIds(){
		return getMatches(twitterIdP);
	}
	
	@Override
	public String toString() {
		return text; //for later: probably add timestamp
	}
}
