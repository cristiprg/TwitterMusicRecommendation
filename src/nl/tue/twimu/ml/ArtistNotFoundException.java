package nl.tue.twimu.ml;

public class ArtistNotFoundException extends Exception {
	
	private String artist;
	
	public ArtistNotFoundException(String msg){
		this(msg, "");
	}
	
	public ArtistNotFoundException(String msg, String artist){
		super(msg);
		setArtist(artist);
	}
	
	public void setArtist(String artist){
		this.artist = artist;
	}
	
	public String getArtist(){
		return artist;
	}
}
