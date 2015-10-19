package nl.tue.twimu.test;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.tue.twimu.ir.Indexer;
import nl.tue.twimu.ir.Querier;

public class IndexerTest {

	@Before
	public void setUp() throws Exception {
		BasicConfigurator.configure();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Indexer indexer = new Indexer();
		Querier q = new Querier(
				indexer.getMatrix().getArtists(),
				indexer.getMatrix().getTerms(),
				indexer.getMatrix().getMatrix());
		List<String> results = q.search("play");
		System.out.println("Query: play");
		for (String s : results)
			System.out.println(s);
		
	
		results = q.search("#smart, #pretty");
		System.out.println("Query: #smart, #pretty");
		for (String s : results)
			System.out.println(s);
	}

}
