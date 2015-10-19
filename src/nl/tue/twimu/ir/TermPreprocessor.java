package nl.tue.twimu.ir;

import nl.tue.twimu.ir.util.Stemmer;

public class TermPreprocessor {

	public static String termPreProcessing(String term) {

		// to lower case
		term = term.toLowerCase();

		// remove trailing and leading non-aplhanumeric
		// TODO: except the # character? to retain hashtags -
		// (^[^a-z0-9#:]*|[^a-z0-9]*$)
		term = term.replaceAll("(^[^a-z0-9]*|[^a-z0-9]*$)", "");

		// perform stemming on non-hashtags
		Stemmer stemmer = new Stemmer();
		if (term.length() > 0 && term.getBytes()[0] != '#') {
			// w = porterAnalyzer.stemQuery(w);
			stemmer.add(term.toCharArray(), term.toCharArray().length);
			stemmer.stem();
			term = stemmer.toString();
		}

		return term;
	}
}
