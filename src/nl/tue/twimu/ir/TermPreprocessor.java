package nl.tue.twimu.ir;

import nl.tue.twimu.ir.util.Stemmer;

public class TermPreprocessor {
	
	public static boolean doStemming = true;
	public static boolean trimHashTags = false;

	public static String termPreProcessing(String term) {

		// to lower case
		term = term.toLowerCase();

		String regEx = null;
		
		if (trimHashTags == true)
			regEx = "(^[^a-z0-9]*|[^a-z0-9]*$)";
		else
			regEx = "(^[^a-z0-9#:]*|[^a-z0-9]*$)";
		
		
		//Do stemming
		if (doStemming == true){
			//use regEx to remove e.g. exclamation marks, but keep hashtags at the end or beginning of words
			term = term.replaceAll(regEx, "");
		
			// perform stemming.
			Stemmer stemmer = new Stemmer();
			if (term.length() > 0) {
				// w = porterAnalyzer.stemQuery(w);
				stemmer.add(term.toCharArray(), term.toCharArray().length);
				stemmer.stem();
				term = stemmer.toString();
				}
		


		}

		return term;
	}
}
