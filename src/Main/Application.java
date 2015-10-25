package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import nl.tue.twimu.ir.Indexer;
import nl.tue.twimu.ir.Querier;
import nl.tue.twimu.ir.TFIDFMatrix;
import nl.tue.twimu.ml.Rocchio;

public class Application {

	private final static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	Querier querier;
	Indexer indexer;
	TFIDFMatrix mx;
	int pageRankType;
	
	public static void main(String[] args) {
		Application app = new Application();
		
		
		System.out.println("First, input a query as either a set of terms or a twitter handle (don't"
				+ "forget the @!)\n" + "The system is not robust (not anymore, muahaha)! Please don't input non existent twitter handles!");
		
		while(app.runQuery()){
			;
		}
	}
	
	public Application() {
		BasicConfigurator.configure();
		pageRankType = TFIDFMatrix.USE_INVERTED_PAGE_RANK; // let user decide for this value
		
		indexer = new Indexer();
		querier = new Querier(indexer, pageRankType);
		mx = indexer.getMatrix();
		
		/* testing code
		TweetsDb db = indexer.db;
		for(Artist a:db.artists.values()) {
			Style s = a.getStyle();
			s.addWeightedFromText(a.getDescription(), 10.0);
			s.addWeightedFromList(a.getAllMentionedHashtags(), 5.0);
		}*/
	}
	
	private boolean runQuery(){		
		String consoleInp = null;
		List<String> results = null;
		
		double[] query = null;
		try {
			consoleInp = br.readLine();
			query = Querier.queryValues(consoleInp, mx, pageRankType);			
			results = querier.search(query, Querier.detectedArtistQuery);
			
			System.out.println("Results: ");
			showResults(results);
		}
		catch (IOException e) {
			System.err.println("Error reading query from console");
			e.printStackTrace();
			return true;
		}	 catch (Exception e) {
			System.out.println("No artist found.");
			e.printStackTrace();
			return true;
		}	
		
		boolean continu = continu();
		while (continu) {
			int[] relevance = getRelevance(mx, results);
			System.out.println("calculating new results...");
			Rocchio r = new Rocchio(mx, query, relevance);
			query = r.getNewQuery();
			results = querier.search(query, Querier.detectedArtistQuery);
			System.out.println("Results: ");
			showResults(results);
			continu = continu();
		}	
		
		return false;
	}
	
	/**
	 * Prints the list with the results
	 * @param results
	 */
	private static void showResults(List<String> results) {
		int counter = 1;
		for (String s : results)
			System.out.println((counter++) + ") " + s);

	}
	
	private static int[] getRelevance(TFIDFMatrix mx, List<String> results) {
		int[] relevance = new int[mx.getArtists().size()];
		try {
			String consoleInp;
			for (int i = 0; i < results.size(); i++) {
				System.out.println("Was result number " + (i+1) + " relevant? Y/n");
				consoleInp = br.readLine().toLowerCase();
				if (consoleInp.equals("y"))
					relevance[mx.getArtists().indexOf(results.get(i))] = 1;
				else
					relevance[mx.getArtists().indexOf(results.get(i))] = -1;
			}
		} catch (IOException e) {
			System.err.println("Error reading answer from console");
			e.printStackTrace();
		}
		return relevance;
	}

	private static boolean continu() {
		try {
			System.out.println("Would you like to refine your query? Y/n");
			String consoleInp = br.readLine().toLowerCase();
			if (consoleInp.equals("y"))
				return true;
		} catch (IOException e) {
			System.err.println("Error reading answer from console");
			e.printStackTrace();
		}
		return false;
	}

}
