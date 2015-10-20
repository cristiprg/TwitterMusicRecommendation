package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import nl.tue.twimu.ir.Indexer;
import nl.tue.twimu.ir.Querier;
import nl.tue.twimu.ir.TFIDFMatrix;
import nl.tue.twimu.ml.Rocchio;

public class Application {

	private final static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	Querier querier;
	Indexer indexer;
	TFIDFMatrix mx;
	
	public Application() {
		indexer = new Indexer();
		querier = new Querier(indexer);
		mx = indexer.getMatrix();
	
	}
	
	public static void main(String[] args) {
		Application app = new Application();
		app.run();		
	}
	
	private void run(){
		System.out.println("First, input a query as either a set of terms or a twitter handle (don't"
				+ "forget the @!)\n" + "The system is not robust (not anymore, muahaha)! Please don't input non existent twitter handles!");
		
		String consoleInp = null;
		List<String> results = null;
		try {
			consoleInp = br.readLine();
			results = querier.search(consoleInp);
			System.out.println("Results: ");
			showResults(results);
		} catch (IOException e) {
			System.err.println("Error reading query from console");
			e.printStackTrace();
		}
		double[] query = Rocchio.queryValues(consoleInp, mx);
		
		boolean continu = continu();
		while (continu) {
			int[] relevance = getRelevance(mx, results);
			System.out.println("calculating new results...");
			Rocchio r = new Rocchio(mx, query, relevance);
			query = r.getNewQuery();
			results = querier.search(query);
			System.out.println("Results: ");
			showResults(results);
			continu = continu();
		}	
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