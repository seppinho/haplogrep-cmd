package genepi.haplogrep;

import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import core.TestSample;
import exceptions.parse.HsdFileException;
import exceptions.parse.sample.InvalidPolymorphismException;
import exceptions.parse.samplefile.InvalidColumnCountException;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import search.SearchResult;
import search.ranking.KulczynskiRanking;
import search.ranking.results.RankedResult;

public class QualityTest {


	private static Phylotree phylotree = null;

	@BeforeClass
	public static void init() throws NumberFormatException, IOException, JDOMException, InvalidPolymorphismException
	{	
		 phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml","weights17.txt");
		
	}
	
	@Test
	public void calculateWeightsTest() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> result =  phylotree.search(TestSample.parse("test	16024-16569;1-576;	?	73G	263G	285T	315.1C	455.1T	523G	524T	16051G	16129A	16188.1C	16249C	16264G"),new KulczynskiRanking());
		
		SearchResult searchResult = result.get(0).getSearchResult();
		double found = searchResult.getWeightFoundPolys();
		double sample = searchResult.getSumWeightsAllPolysSample();
		double expected = searchResult.getExpectedWeightPolys();
		
		Assert.assertEquals(0.924, result.get(0).getDistance(),0.01);
		
		Assert.assertEquals(38.2, found, 0.01);
		Assert.assertEquals(42.7, sample, 0.01);
		Assert.assertEquals(40.2, expected, 0.01);
		
			}
	
	
}
