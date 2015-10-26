package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
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
		
		
		System.out.println("First, input a query as either a set of terms or a twitter handle (with @, like @justinbieber) or a genre (with *, like @rock)\n" 
		+ "To boolean-retrieveal-like connect queries, use AND, OR, AND NOT. Combined in command-order, not by classic boolean or-before-and. You cannot refine these with roccio.\n"
		+ "Unindexed words will lead to empty results.");
		
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
	}
	
	private boolean runQuery(){		
		String consoleInp = null;
		List<String> results = new LinkedList<>();
		boolean refine=true;
		
		double[] query = null;
		//boolean retrieval
		try {
			consoleInp = br.readLine();
			if(consoleInp.contains("and") || consoleInp.contains("or") || consoleInp.contains("not")) {
				refine = false;
			}
			String[] inputs = consoleInp.split(" ");
			String lastOP = "or";
			List<String> tmpRes;
			
			for(int i=0;i<inputs.length;i++) {
				String inp = inputs[i].toLowerCase();
				if(inp.equals("or") || inp.equals("and"))
					lastOP = inp;
				else if(inp.equals("not")) {
					if(lastOP.equals("and")) // || lastOP.equals("or")
						lastOP=lastOP+"not";
					else
						throw new RuntimeException("Invalid not. Only after and.");
				}
				else {
					if(inp.startsWith("*")) {
						tmpRes = indexer.db.artistsByGenre(inp.substring(1));
					} else {
						query = Querier.queryValues(inp, mx, pageRankType);
						if(query!=null) {
							tmpRes = querier.search(query, Querier.detectedArtistQuery);
						} else {
							tmpRes = new LinkedList<>();
						}
					}
					if(lastOP.equals("or")) {
						results.addAll(tmpRes);
					} else if(lastOP.equals("and")) {
						results.retainAll(tmpRes); //only keep common elements
					} else if(lastOP.equals("andnot")) {
						results.removeAll(tmpRes);
					}
				}
			}
			
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
		
		boolean continu = refine&&continu(); //does not even call continu, if refine is false
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
