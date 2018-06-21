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
import core.TestSample;
import exceptions.parse.HsdFileException;
import exceptions.parse.sample.InvalidPolymorphismException;
import exceptions.parse.samplefile.InvalidColumnCountException;
import junit.framework.TestCase;

public class HaplogroupTest {


	private static Phylotree phylotree = null;

	@BeforeClass
	public static void init() throws NumberFormatException, IOException, JDOMException, InvalidPolymorphismException
	{	
		 phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml","weights17.txt");
		 if(phylotree == null) {
			 System.out.println("????");
		 }
	}
	
	@Test
	public void testH2a2() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
	
		List<RankedResult> result =  phylotree.search(TestSample.parse("K1a+150	1-16569		150T	497T	16093C	1189C	10398G	10550G	11299C	14798C	16224C	16311C	9055A	14167T	3480G	9698C	1811G	11467G	12308G	12372A	73G	11719A	14766T	2706G	7028T	1438G	4769G	750G	8860G	15326G	263G"),new HammingRanking());
		
		Assert.assertEquals("K1a+150", result.get(0).getHaplogroup().toString());
	}
	


	@Test
	public void testK1a24() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		
		List<RankedResult> result =  phylotree.search(TestSample.parse("K1a24	1-16569		15625A	150T	497T	16093C	1189C	10398G	10550G	11299C	14798C	16224C	16311C	9055A	14167T	3480G	9698C	1811G	11467G	12308G	12372A	73G	11719A	14766T	2706G	7028T	1438G	4769G	750G	8860G	15326G	263G	"),new HammingRanking());
		
		Assert.assertEquals("K1a24", result.get(0).getHaplogroup().toString());
	}
	
	

	@Test
	public void testK1a30() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		
		List<RankedResult> result =  phylotree.search(TestSample.parse("K1a30	1-16569		15650A	150T	497T	16093C	1189C	10398G	10550G	11299C	14798C	16224C	16311C	9055A	14167T	3480G	9698C	1811G	11467G	12308G	12372A	73G	11719A	14766T	2706G	7028T	1438G	4769G	750G	8860G	15326G	263G"),new HammingRanking());
		
		Assert.assertEquals("K1a30", result.get(0).getHaplogroup().toString());
	}
	
		
	@Test
	public void testH2a() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> result =  phylotree.search(TestSample.parse("1	1-16569	H2a2	263G 8860G 15326G 750G"),new HammingRanking());

		Assert.assertEquals("H2a", result.get(0).getHaplogroup().toString());
	}
	

	@Test
	public void testHG_H2_16291() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> result =  phylotree.search(TestSample.parse("HG_H2+16291	 1-16569		16291T	1438G	4769G	750G	8860G	15326G	263G"),new HammingRanking());
		
		Assert.assertEquals("H+16291", result.get(0).getHaplogroup().toString());
	}

	
	@Test
	public void testHG_H2_13708() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> result =  phylotree.search(TestSample.parse("HG_H2+13708	 1-16569		13708A	1438G	4769G	750G	8860G	15326G	263G"),new HammingRanking());
		
		Assert.assertEquals("H+13708", result.get(0).getHaplogroup().toString());
	}
	
	
	@Test
	public void testHG_H2_152() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> result =  phylotree.search(TestSample.parse("HG_H2+152	 1-16569		152C	1438G	4769G	750G	8860G	15326G	263G"),new HammingRanking());
		
		Assert.assertEquals("H+152", result.get(0).getHaplogroup().toString());
	}
	
	
	@Test
	public void testHG_H2_195() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> result =  phylotree.search(TestSample.parse("HG_H2+195	 1-16569		195C	1438G	4769G	750G	8860G	15326G	263G"),new HammingRanking());
		
		Assert.assertEquals("H+195", result.get(0).getHaplogroup().toString());
	}
	
	
	@Test
	public void testHG_H2_195_146() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> result =  phylotree.search(TestSample.parse("HG_H2+195+146	 1-16569		146C	195C	1438G	4769G	750G	8860G	15326G	263G"),new HammingRanking());
		
		Assert.assertEquals("H+195+146", result.get(0).getHaplogroup().toString());
	}
	
	@Test
	public void testHG_H2_16129() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> result =  phylotree.search(TestSample.parse("HG_H2+16129	 1-16569		16129A	1438G	4769G	750G	8860G	15326G	263G"),new HammingRanking());
		
		Assert.assertEquals("H+16129", result.get(0).getHaplogroup().toString());
	}
	
	@Test
	public void testHG_H2_16129_kulcz() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> 	result =  phylotree.search(TestSample.parse("HG_H2+16129	 1-16569		16129A	1438G	4769G	750G	8860G	15326G	263G"),new KulczynskiRanking());
		
		Assert.assertEquals("H+16129", result.get(0).getHaplogroup().toString());
	}
	

	
	@Test
	public void testH_152() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> result =  phylotree.search(TestSample.parse("1	1-16569	H_152	263G 8860G 15326G 750G"),new HammingRanking());
		
		Assert.assertEquals("H2a", result.get(0).getHaplogroup().toString());
	}
	
	@Test
	public void testH() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{

		List<RankedResult> result =  phylotree.search(TestSample.parse("1	1-16569	 	263G 8860G 15326G 750G 4769G 1438G"),new HammingRanking());
		
		Assert.assertEquals("H", result.get(0).getHaplogroup().toString());
	}
	
	@Test
	public void testHV() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> result =  phylotree.search(TestSample.parse("1	1-16569	 	263G 8860G 15326G 750G 4769G 1438G 2706G 7028T"),new HammingRanking());
		
		Assert.assertEquals("HV", result.get(0).getHaplogroup().toString());
	}
	
	@Test
	public void testR0() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> result =  phylotree.search(TestSample.parse("1	1-16569	 	263G 8860G 15326G 750G 4769G 1438G 2706G 7028T 14766T"),new HammingRanking());
		
		Assert.assertEquals("R0", result.get(0).getHaplogroup().toString());
	}
	@Test
	public void testL5a1a() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> result =  phylotree.search(TestSample.parse("1	1-16569	73G	152C	182T	189G	195C	247A	263G	315.1C	455.1T	455.2T	455.3C	522del	523del	709A	750G	769A	825A	851G	930A	1018A	1438G	1822C	2706G	3423C	3594T	4104G	4496T	4769G	5004C	5111T	5147A	5656G	6182A	6297C	7028T	256T	7424G	7521A	7873T	7972G	8155A	8188G	8582T	8655T	8701G	8754T	8860G	9305A	9329A	9540C	9899C	10398G	10688A	10810C	10873C	11015G	11025C	11719A	11881T	12236A	12432T	12705T	12950G	13105G	13506T	13650T	13722G	14212C	14239T	14581C	14766T	14905A	14971C	15217A	15326G	15884A	16129A	16148T	16166G	16187T	16189C	16223T	16278T	16311C	16355T	16362C"),new HammingRanking());
		
		Assert.assertEquals("L5a1a", result.get(0).getHaplogroup().toString());
	}
	@Test
	public void testH13a2a() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> result =  phylotree.search(TestSample.parse("1	1-16569	H13a2a	263G 309.1C 315.1C 709A 750G 1008G 1438G 1768A 2259T 4769G 8860G 14872T 15326G 16519C"),new HammingRanking());

		Assert.assertEquals("H13a2a", result.get(0).getHaplogroup().toString());
	}
	@Test
	public void testL3e1c() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> result =  phylotree.search(TestSample.parse("549	1-16569	L3e1c	73G	150T	189G	200G	263G	309.1C	315.1C	750G	1438G	2352C	2706G	3106N	3675G	4769G	5460A	6221C	6587T	7028T	8289.1C	8289.2C	8289.3C	8289.4C	8289.5C	8289.6T	8289.7C	8289.8T	8289.9A	8289.10C	8289.11C	8289.12C	8289.13C	8289.14C	8289.15T	8289.16C	8289.17T	8289.18A	8860G	9540C	10398G	10819G	10873C	11719A	12705T	14152G	14212C	14323A	14766T	15301A	15326G	15670C	15942C	16327T "),new HammingRanking());
		
		Assert.assertEquals("L3e1c", result.get(0).getHaplogroup().toString());
	}
	@Test
	public void testH13a2b1() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{

		List<RankedResult> result =  phylotree.search(TestSample.parse("549	1-16569	H13a2b1	263G  315.1C  709A  750G  1438G  2259T  4639C  4769G  5899.1C  7322G  8860G  13762G  14872T  15001C  15326G  16311C  16519C "),new HammingRanking());
			
		Assert.assertEquals("H13a2b1", result.get(0).getHaplogroup().toString());
	}
	
	


	@Test
	public void testH2a2b() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
		List<RankedResult> result =  phylotree.search(TestSample.parse("549	1-16569	H2a2b	263G	309.1C	309.2T	4080C	8860G	15326G	16291T"),new HammingRanking());
		
		Assert.assertEquals("H2a2b3", result.get(0).getHaplogroup().toString());
	}
	
}
