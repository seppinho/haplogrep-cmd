package genepi.haplogrep;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import core.SampleFile;
import genepi.haplogrep.util.HgClassifier;
import genepi.io.FileUtil;
import genepi.io.table.reader.CsvTableReader;
import importer.VcfImporter;
import util.ExportUtils;
import vcf.Sample;

public class HaplogrepCmdTest {

	@Test
	public void HaplogrepCmdTest() throws Exception {

		String file = "test-data/vcf/HG00097.vcf";
		String phylo = "data/phylotree/phylotree17.xml";
		String weights = "data/weights/weights17.txt";
		String out = "test-data/out.txt";
		VcfImporter impvcf = new VcfImporter();

		HashMap<String, Sample> samples = impvcf.load(new File(file), false);
		ArrayList<String> lines = ExportUtils.samplesMapToHsd(samples);
		SampleFile newSampleFile = new SampleFile(lines);

		HgClassifier classifier = new HgClassifier();

		classifier.run(newSampleFile, phylo, weights, "kulczynski", 1);

		ExportUtils.createReport(newSampleFile.getTestSamples(), out, false);

		CsvTableReader reader = new CsvTableReader(out, '\t');

		int count = 0;
		while (reader.next()) {
			count++;
			String hg = reader.getString("Haplogroup");
			double quality = reader.getDouble("Quality");
			assertEquals("T2f1a1", hg);
			assertEquals(quality, quality,0.0);
		}

		assertEquals(1, count);
		
		
		classifier.run(newSampleFile, phylo, weights, "kulczynski", 10);
		
		ExportUtils.createReport(newSampleFile.getTestSamples(), out, false);

		reader = new CsvTableReader(out, '\t');

		count = 0;
		while (reader.next()) {
			count++;
		}
		
		assertEquals(10, count);
		
		FileUtil.deleteFile(out);

	}

}
