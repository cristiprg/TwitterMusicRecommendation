package nl.tue.twimu.test;

import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.tue.twimu.ir.Indexer;

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
	}

}
