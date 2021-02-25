package genepi.haplogrep;

import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import org.junit.Assert;
import java.io.IOException;
import java.util.List;

import org.jdom.JDOMException;
import org.junit.BeforeClass;
import org.junit.Test;
import search.ranking.HammingRanking;
import search.ranking.KulczynskiRanking;
import search.ranking.results.RankedResult;
import core.Reference;
import core.TestSample;
import exceptions.parse.HsdFileException;
import exceptions.parse.sample.InvalidPolymorphismException;
import exceptions.parse.samplefile.InvalidColumnCountException;
import importer.FastaImporter;
import junit.framework.TestCase;

public class HaplogroupSARSCOV2Test {


	private static Phylotree phylotree = null;
	static Reference reference; 
	
	@BeforeClass
	public static void init() throws NumberFormatException, IOException, JDOMException, InvalidPolymorphismException
	{	
		reference = new FastaImporter().loadSARSCOV2();		
		phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree01_SARSCOV2.xml","weights01_SARSCOV2.txt", reference);
	}
	
	@Test
	public void testSARSCOV2_brazil() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> result =  phylotree.search(TestSample.parse("Brazil/RJ-DCV3/2020	1-30000	20A	241T	3037T	14408T	23403G", reference),new KulczynskiRanking());
		System.out.println("___"+result.get(0).getSearchResult().getDetailedResult().getFoundPolys());
		Assert.assertEquals("20A", result.get(0).getHaplogroup().toString());
		
	}
	
	
}
