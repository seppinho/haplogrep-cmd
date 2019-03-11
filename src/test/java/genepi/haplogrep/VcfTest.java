package genepi.haplogrep;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import importer.VcfImporter;
import util.ExportUtils;
import vcf.Sample;
import vcf.Variant;

public class VcfTest {

	@Test
	public void IndelTest() throws Exception {
		String file = "test-data/vcf/testIndel.vcf";
		VcfImporter impvcf = new VcfImporter();
		HashMap<String, Sample> samples = impvcf.load(new File(file), false);

		ArrayList<String> lines = ExportUtils.vcfTohsd(samples);

		assertEquals(
				"TL064	1-16569	?	8281d	8282d	8283d	8284d	8285d	8286d	8287d	8288d	8289d	8307d	8860G	15940C	15941d",
				lines.get(0).toString().trim());

	}

	@Test
	public void VcfPolyHaploidTest() throws Exception {
		String file = "test-data/vcf/HG00097.vcf";
		VcfImporter impvcf = new VcfImporter();
		HashMap<String, Sample> samples = impvcf.load(new File(file), false);

		ArrayList<String> lines = ExportUtils.vcfTohsd(samples);

		// currently excluded: 309.1CC complex INSERTION
		assertEquals(
				"HG00097	1-16569	?	73G	195C	263G	309.1CC	315.1C	709A	750G	1438G	1888A	2706G	4216C	4769G	4917G	5277C	5426C	6489A	7028T	8697A	8860G	10463C	11251G	11719A	11812G	13368A	14233G	14766T	14905A	15028A	15043A	15326G	15452A	15607G	15928A	16126C	16182C	16183C	16189C	16294T	16296T	16298C	16519C",
				lines.get(0).toString().trim());

	}

	@Test
	public void VcfPolyDiploidTest() throws Exception {
		String file = "test-data/vcf/HG00097_Diploid.vcf";
		StringBuilder actual = new StringBuilder();

		VcfImporter impvcf = new VcfImporter();
		HashMap<String, Sample> samples = impvcf.load(new File(file), false);

		Sample sample = samples.get("HG00097");

		for (Variant var : sample.getVariants()) {
			if (var.getType() != 5) {
				actual.append(var.getPos() + "" + var.getVariant() + ",");
			} else {
				actual.append(var.getInsertion() + ",");
			}
		}

		assertEquals(
				"73G,195C,263G,309.1CC,315.1C,709A,750G,1438G,1888A,2706G,4216C,4769G,4917G,5277C,5426C,6489A,7028T,8697A,8860G,10463C,11251G,11719A,11812G,13368A,14233G,14766T,14905A,15028A,15043A,15326G,15452A,15607G,15928A,16126C,16182C,16183C,16189C,16294T,16296T,16298C,16519C,",
				actual.toString());

	}

	@Test
	public void test1000Del() throws Exception {
		String file = "test-data/vcf/1000Gdel.vcf";
		StringBuilder actual = new StringBuilder();

		VcfImporter impvcf = new VcfImporter();
		HashMap<String, Sample> samples = impvcf.load(new File(file), false);

		Sample sample = samples.get("HG00096");
		for (Variant var : sample.getVariants()) {
			actual.append(var.getPos() + "" + var.getVariant() + ",");
		}

		assertEquals(
				"249d,290d,291d,308d,310d,498d,521d,522d,523d,524d,8281d,8282d,8283d,8284d,8285d,8286d,8287d,8288d,8289d,8307d,16188T,",
				actual.toString());

		sample = samples.get("HG00097");
		actual.setLength(0);
		for (Variant var : sample.getVariants()) {
			if (var.getType() != 5) {
				actual.append(var.getPos() + "" + var.getVariant() + ",");
			} else {
				actual.append(var.getInsertion() + ",");
			}
		}

		assertEquals("105T,249d,309.1CC,", actual.toString());

		sample = samples.get("HG00099");
		actual.setLength(0);
		for (Variant var : sample.getVariants()) {
			if (var.getType() != 5) {
				actual.append(var.getPos() + "" + var.getVariant() + ",");
			} else {
				actual.append(var.getInsertion() + ",");
			}
		}
		assertEquals("309T,", actual.toString());

		sample = samples.get("HG00100");
		actual.setLength(0);
		for (Variant var : sample.getVariants()) {
			if (var.getType() != 5) {
				actual.append(var.getPos() + "" + var.getVariant() + ",");
			} else {
				actual.append(var.getInsertion() + ",");
			}
		}
		assertEquals("309.1C,16189d,", actual.toString());

	}

}
