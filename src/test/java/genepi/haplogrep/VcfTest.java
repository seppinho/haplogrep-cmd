package genepi.haplogrep;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import importer.VcfImporter;

public class VcfTest {

	@Test
	public void IndelTest() throws Exception {
		String file = "test-data/vcf/testIndel.vcf";
		StringBuilder actual = new StringBuilder();
		VcfImporter impvcf = new VcfImporter();
		ArrayList<String> samples = impvcf.load(new File(file), false);

		String[] splits = samples.get(0).split("\t");
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}

		assertEquals("8281-8289d,8307d,8860G,15940C,15940d,", actual.toString());

	}
	
	@Test
	public void VcfPolyHaploidTest() throws Exception {
		String file = "test-data/vcf/HG00097.vcf";
		StringBuilder actual = new StringBuilder();
		VcfImporter impvcf = new VcfImporter();
		ArrayList<String> samples = impvcf.load(new File(file), false);

		String[] splits = samples.get(0).split("\t");
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}

		assertEquals("73G,195C,263G,309.1CC,315.1C,709A,750G,1438G,1888A,2706G,4216C,4769G,4917G,5277C,5426C,6489A,7028T,8697A,8860G,10463C,11251G,11719A,11812G,13368A,14233G,14766T,14905A,15028A,15043A,15326G,15452A,15607G,15928A,16126C,16182C,16183C,16189C,16294T,16296T,16298C,16519C,", actual.toString());

	}
	
	@Test
	public void VcfPolyDiploidTest() throws Exception {
		String file = "test-data/vcf/HG00097_Diploid.vcf";
		StringBuilder actual = new StringBuilder();
		VcfImporter impvcf = new VcfImporter();
		ArrayList<String> samples = impvcf.load(new File(file), false);

		String[] splits = samples.get(0).split("\t");
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}

		assertEquals("73G,195C,263G,309.1CC,315.1C,709A,750G,1438G,1888A,2706G,4216C,4769G,4917G,5277C,5426C,6489A,7028T,8697A,8860G,10463C,11251G,11719A,11812G,13368A,14233G,14766T,14905A,15028A,15043A,15326G,15452A,15607G,15928A,16126C,16182C,16183C,16189C,16294T,16296T,16298C,16519C,", actual.toString());

	}

	@Test
	public void test1000Del() throws Exception {
		String file = "test-data/vcf/1000Gdel.vcf";
		StringBuilder actual = new StringBuilder();
		VcfImporter impvcf = new VcfImporter();
		ArrayList<String> samples = impvcf.load(new File(file), false);
		
		String[] splits = samples.get(0).split("\t");
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}
		assertEquals("249d,290-291d,308d,310d,498d,521-524d,523-524d,8281-8289d,16188T,8307d,",
				actual.toString());

		splits = samples.get(1).split("\t");
		actual.setLength(0);
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}
		assertEquals("105T,249d,309.1CC,", actual.toString());

		splits = samples.get(2).split("\t");
		actual.setLength(0);
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}
		assertEquals("309T,", actual.toString());

		splits = samples.get(3).split("\t");
		actual.setLength(0);
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}
		assertEquals("309.1C,16189d,", actual.toString());

	}

}
