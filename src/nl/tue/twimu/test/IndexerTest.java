package nl.tue.twimu.test;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;

public class IndexerTest {

	@Before
	public void setUp() throws Exception {
		BasicConfigurator.configure();
	}

	@After
	public void tearDown() throws Exception {
	}

	/*@Test
	@Ignore
	public void test() {
		Indexer indexer = new Indexer();
		Querier q = new Querier(indexer, TFIDFMatrix.USE_PAGE_RANK);
		List<String> results = q.search("play");
		System.out.println("Query: play");
		for (String s : results)
			System.out.println(s);
		
	
		results = q.search("#smart, #pretty");
		System.out.println("Query: #smart, #pretty");
		for (String s : results)
			System.out.println(s);
	}*/

}
