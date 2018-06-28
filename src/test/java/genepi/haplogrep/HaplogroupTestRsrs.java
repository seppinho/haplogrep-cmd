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

public class HaplogroupTestRsrs {


	private static Phylotree phylotree = null;

	@BeforeClass
	public static void init() throws NumberFormatException, IOException, JDOMException, InvalidPolymorphismException
	{	
		 phylotree = PhylotreeManager.getInstance().getPhylotree("data/phylotree/phylotree17_rsrs.xml","data/weights/weights17_rsrs.txt");
		
	}
	
	@Test
	public void testRSRS() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
	
		List<RankedResult> result =  phylotree.search(TestSample.parse("JQ704801	1-16569	T1a1c	146T	247G	308.1C	310.1C	709A	769G	825T	1018G	1888A	2758G	2885T	3594C	4104A	4216C	4312C	4917G	7146A	7256C	7521G	8468C	8655C	8697A	8701A	9120G	9540T	9899C	10398A	10463C	10664C	10688G	10810T	10873T	10915T	11251G	11914G	12633A	12705C	13105A	13276A	13368A	13506C	13650C	14905A	15452A	15607G	15928A	15965G	16126C	16129G	16163G	16186T	16187C	16223C	16230A	16278C	16294T	16311T"),new KulczynskiRanking());
		
		Assert.assertEquals("T1a1c", result.get(0).getHaplogroup().toString());
		
	}
	
	@Test
	public void testRSRS2() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
	
		List<RankedResult> result =  phylotree.search(TestSample.parse("JQ704908	1-16569	J1c1b2a 146T	152T	195T	228A	247G	295T	310.1C	462T	482C	489C	523-524d	769G	825T	1005C	1018G	2758G	2885T	3010A	3394C	3594C	4104A	4216C	4312C	7146A	7184G	7256C	7521G	8468C	8655C	8701A	9540T	10664C	10688G	10810T	10873T	10915T	11251G	11914G	12612G	12705C	13105A	13276A	13398G	13506C	13650C	13708A	13953C	14798C	15452A	15725T	15902G	16069T	16126C	16129G	16187C	16189T	16223C	16230A	16278C	16311T	16519T"),new KulczynskiRanking());
		
		Assert.assertEquals("J1c1b2a", result.get(0).getHaplogroup().toString());
		
	}
	
	@Test
	public void testRSRS3() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
	
		List<RankedResult> result =  phylotree.search(TestSample.parse("GU392105	1-16569	M13a1b	146T	195T	247G	314.1C	489C	769G	825T	1018G	2758G	2885T	3594C	3644C	4104A	4312C	5773A	6023A	6253C	6620C	7146A	7256C	7521G	8468C	8655C	10400T	10411G	10664C	10688G	10790C	10810T	10915T	11914G	13105A	13135A	13276A	13506C	13650C	14783C	15043A	15130T	15301A	15924G	16129G	16145A	16148T	16187C	16188T	16230A	16278C	16311T	16381C	16519T"),new KulczynskiRanking());
		
		Assert.assertEquals("M13a1b", result.get(0).getHaplogroup().toString());
		
	}
	
	@Test
	public void testRSRS4() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
	
		List<RankedResult> result =  phylotree.search(TestSample.parse("GU392104	1-16569	M13a1b	146T	195T	247G	314.1C	489C	769G	825T	1018G	2758G	2885T	3594C	3644C	4104A	4312C	5773A	6023A	6253C	6620C	7146A	7256C	7521G	8468C	8655C	10400T	10411G	10664C	10688G	10790C	10810T	10915T	11914G	13105A	13135A	13276A	13506C	13650C	14783C	15043A	15130T	15301A	15924G	16129G	16145A	16148T	16187C	16188T	16230A	16278C	16311T	16381C	16519T"),new KulczynskiRanking());
		
		Assert.assertEquals("M13a1b", result.get(0).getHaplogroup().toString());
		
	}
	
	@Test
	public void testRSRS5() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
	
		List<RankedResult> result =  phylotree.search(TestSample.parse("HQ287893	1-16569	H1a	146T	152T	195T	247G	308.1C	769G	825T	1018G	2706A	2758G	2885T	3010A	3594C	4104A	4312C	7028C	7146A	7256C	7521G	8468C	8655C	8701A	9540T	10398A	10664C	10688G	10810T	10873T	10915T	11719G	11914G	12705C	13105A	13276A	13506C	13650C	14766C	16129G	16162G	16187C	16189T	16223C	16230A	16278C	16311T"),new KulczynskiRanking());
		
		Assert.assertEquals("H1a", result.get(0).getHaplogroup().toString());
		
	}
	
	//TODO: we J2b1a+16311C! 
	/*@Test
	public void testRSRS6() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
	
		List<RankedResult> result =  phylotree.search(TestSample.parse("JQ703587	1-16569	J2b1a2	146T	150T	195T	247G	295T	302.1C	310.1C	489C	769G	825T	1018G	2758G	2885T	3594C	4104A	4216C	4312C	5633T	6216C	7146A	7256C	7476T	7521G	8468C	8655C	8701A	9540T	10172A	10664C	10688G	10810T	10873T	10915T	11251G	11914G	12612G	12705C	13105A	13276A	13506C	13650C	13708A	15257A	15452A	15812A	16069T	16126C	16129G	16187C	16189T	16193T	16223C	16230A	16255A	16311T"),new KulczynskiRanking());
		
		Assert.assertEquals("J2b1a2", result.get(0).getHaplogroup().toString());
		
	}*/
	
	@Test
	public void testRSRS7() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
	
		List<RankedResult> result =  phylotree.search(TestSample.parse("JQ703582	1-16569	H2a1c	73A	146T	152T	195T	247G	308.1C	310.1C	769G	825T	951A	1018G	1438A	2706A	2758G	2885T	3594C	3834A	4104A	4312C	4769A	7028C	7146A	7256C	7521G	8468C	8655C	8701A	9540T	10398A	10664C	10688G	10810T	10873T	10915T	11719G	11914G	12705C	13105A	13276A	13506C	13650C	14766C	16129G	16187C	16189T	16223C	16230A	16278C	16311T	16354T"),new KulczynskiRanking());
		
		Assert.assertEquals("H2a1c", result.get(0).getHaplogroup().toString());
		
	}
	
	@Test
	public void testRSRS8() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
	
		List<RankedResult> result =  phylotree.search(TestSample.parse("EU597572	1-16569	L1c2a1a	146T	151T	182T	186A	189C	198T	297G	302.1C	310.1C	316A	752G	1420C	2156.1A	2395d	3666A	4312C	5899.1C	5951G	6071C	6150A	6253C	7055G	7076G	7337A	7389C	7744C	8027A	8251A	8784G	8877C	9072G	10321C	10586A	10664C	10792G	10793T	10915T	11654G	11914G	12049T	12810G	13149G	13212T	13276A	13281C	13485G	13789C	14000A	14178C	14560A	14812T	14911T	15016T	15784C	16071T	16145A	16213A	16230A	16234T	16265C	16286G	16294T	16360T	16519T	16527T"),new KulczynskiRanking());
		
		Assert.assertEquals("L1c2a1a", result.get(0).getHaplogroup().toString());
		
	}
	
	@Test
	public void testRSRS9() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
	
		List<RankedResult> result =  phylotree.search(TestSample.parse("JQ703193	1-16569	H13a1a3	73A	146T	152T	195T	247G	308.1CC	310.1C	769G	825T	1018G	2259T	2706A	2758G	2885T	3594C	4104A	4312C	4745G	7028C	7146A	7256C	7521G	8468C	8655C	8701A	9540T	10398A	10664C	10688G	10810T	10873T	10915T	11368C	11719G	11914G	12705C	13105A	13276A	13506C	13650C	13680T	14239T	14281T	14683G	14766C	14872T	16129G	16187C	16189T	16223C	16230A	16278C	16311T	16519T"),new KulczynskiRanking());
		
		Assert.assertEquals("H13a1a3", result.get(0).getHaplogroup().toString());
		
	}
	
	@Test
	public void testRSRS10() throws NumberFormatException, JDOMException, IOException, InvalidPolymorphismException, HsdFileException, InvalidColumnCountException
	{
	
		List<RankedResult> result =  phylotree.search(TestSample.parse("JQ702877	1-16569	H10e	73A	146T	152T	195T	247G	314.1C	709A	769G	825T	1018G	2706A	2758G	2885T	3594C	4104A	4312C	7028C	7146A	7256C	7521G	8468C	8655C	8701A	9540T	10398A	10664C	10688G	10810T	10873T	10915T	11719G	11914G	12705C	13105A	13276A	13506C	13650C	14470A	14766C	16093C	16129G	16187C	16189T	16221T	16223C	16230A	16278C	16311T	16362C"),new KulczynskiRanking());
		
		Assert.assertEquals("H10e", result.get(0).getHaplogroup().toString());
		
	}
	

	
}
