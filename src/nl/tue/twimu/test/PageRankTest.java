package nl.tue.twimu.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.tue.twimu.ir.TermPreprocessor;
import nl.tue.twimu.model.Artist;
import nl.tue.twimu.model.TweetsDb;
import nl.tue.twimu.pagerank.PageRankBuilder;

public class PageRankTest {

	@Before
	public void setUp() throws Exception {
		BasicConfigurator.configure();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws FileNotFoundException, ClassNotFoundException, IOException {
		
		TweetsDb db = TweetsDb.loadFromCache();
		PageRankBuilder pageRankBuilder = new PageRankBuilder(db);
		
		ArrayList<Artist> rankedArtists =pageRankBuilder.getRankedArtists();
		for(Artist a : rankedArtists)
			System.out.println(a.getHandle());
		System.out.println(pageRankBuilder.getPageRankArray());
	}
}
