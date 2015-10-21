package nl.tue.twimu.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RocchioTest {

	//private final static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		System.out.println("Hi! With this little app we'll test out the Rocchio feedback" + "component of our system.");
/*
		TFIDFMatrix mx = setUpMatrix();
		Querier querier = new Querier(mx.getArtists(), mx.getTerms(), mx);

		System.out.println("First, input a query as either a set of terms or a twitter handle (don't"
				+ "forget the @!)\n" + "The system is not robust! Please don't input non existent twitter handles!");
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
		double[] query;
		try {
			query = Rocchio.queryValues(consoleInp, mx);
		} catch (ArtistNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
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
		*/	
	}

	// app
	/*private static void showResults(List<String> results) {
		int counter = 1;
		for (String s : results)
			System.out.println((counter++) + ") " + s);

	}*/

	// not needed //then comment it away.
	/* private static TFIDFMatrix setUpMatrix() {
		TFIDFMatrix mx = null;
		try {
			mx = TFIDFMatrix.loadFromCache();
			System.out.println("Data loaded from memory succesfully!");
		} catch (Exception e) {
			System.out.println("Oops! The date couldn't be loaded from memory... Attempting to create it again...");
			// change this. Indexer should be implemented more transparently
			Indexer i = new Indexer();
			mx = i.getMatrix();
			System.out.println("Data created succesfully!");
		}
		return mx;
	}*/

	//not used
	// asks user what is relevant or not - 
	/*private static int[] getRelevance(TFIDFMatrix mx, List<String> results) {
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
	}*/

	//name: continu... seriously? 	//not used
	/*@Deprecated
	private static boolean continu() {
		try {
			System.out.println("Would you like to refine your query? Y/n");
			String consoleInp = br.readLine().toLowerCase();
			return (consoleInp.equals("y"));
		} catch (IOException e) {
			System.err.println("Error reading answer from console");
			e.printStackTrace();
		}
		return false;
	}*/
}
