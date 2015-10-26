package nl.tue.twimu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Style implements Serializable {	
	private static final long serialVersionUID = 1L;
	
	private static final String stylesListStr = "Blues;Classic Rock;Country;Dance;Disco;Funk;Grunge;Hip-Hop;Jazz;Metal;New Age;Oldies;Other;Pop;R&B;Rap;Reggae;Rock;Techno"
			+ ";Industrial;Alternative;Ska;Death Metal;Pranks;Soundtrack;Euro-Techno;Ambient;Trip-Hop;Vocal;Jazz+Funk;Fusion;Trance;Classical;Instrumental;Acid;House"
			+ ";Game;Sound Clip;Gospel;Noise;AlternRock;Bass;Soul;Punk;Space;Meditative;Instrumental Pop;Instrumental Rock;Ethnic;Gothic;Darkwave;Techno-Industrial"
			+ ";Electronic;Pop-Folk;Eurodance;Dream;Southern Rock;Comedy;Cult;Gangsta;Top 40;Christian Rap;Pop/Funk;Jungle;Native American;Cabaret;New Wave"
			+ ";Psychadelic;Rave;Showtunes;Trailer;Lo-Fi;Tribal;Acid Punk;Acid Jazz;Polka;Retro;Musical;Rock & Roll;Hard Rock;Folk;Folk-Rock;National Folk;Swing"
			+ ";Fast Fusion;Bebob;Latin;Revival;Celtic;Bluegrass;Avantgarde;Gothic Rock;Progressive Rock;Psychedelic Rock;Symphonic Rock;Slow Rock;Big Band;Chorus"
			+ ";Easy Listening;Acoustic;Humour;Speech;Chanson;Opera;Chamber Music;Sonata;Symphony;Booty Bass;Primus;Porn Groove;Satire;Slow Jam;Club;Tango;Samba"
			+ ";Folklore;Ballad;Power Ballad;Rhythmic Soul;Freestyle;Duet;Punk Rock;Drum Solo;A capella;Euro-House;Dance Hall";
	public static final String[] styles;
	public static final List<String> stylesList;
	
	//at starting the program
	static{
		styles = stylesListStr.toLowerCase().replace(" ", "").replace("-", "").split(";");
		Arrays.sort(styles); //so you can binary-search them
		stylesList = Arrays.asList(styles);
	}
	
	private double[] stylevector;
	
	public Style() {
		stylevector = new double[styles.length]; //will already be initialized with 0
	}
	
	public double[] getStylevector() {
		return stylevector;
	}
	
	public String getFirst() {
		double max = 0.0;
		int maxPos = -1;
		for(int i=0;i<stylevector.length;i++) {
			if(stylevector[i]>max) {
				max = stylevector[i];
				maxPos = i;
			}
		}
		if(maxPos==-1) 
			return "none";
		return styles[maxPos];
	}

	public void addWeightedFromText(String text, double weight) {
		for(int i=0;i<styles.length;i++) {
			if(text.contains(styles[i]))
				stylevector[i]+=weight;
		}
	}
	
	//could also be done considering TF-idf matrix or during initilaizing
	public void addWeightedFromTextList(ArrayList<Tweet> arrayList, double weight) {
		for (Tweet t : arrayList) {
			addWeightedFromText(t.getText(), weight);
		}
	}

	public void addWeightedFromList(LinkedList<String> allMentionedHashtags, double weight) {
		List<String> sl = new ArrayList<>(Arrays.asList(styles)); //must be a mutable list, otherwise retainall crashes
		sl.retainAll(allMentionedHashtags);
		for(String s:sl) {
			int pos = stylesList.indexOf(s);
			stylevector[pos]+=weight;
		}
	}

	public void addStyle(Style style) {
		for (int i=0;i<stylevector.length;i++) {
			this.stylevector[i]+=style.stylevector[i];
		}
		
	}

}
