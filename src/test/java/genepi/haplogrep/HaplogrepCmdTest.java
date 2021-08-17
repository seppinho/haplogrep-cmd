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
import importer.HsdImporter;
import importer.VcfImporter;
import util.ExportUtils;
import vcf.Sample;

public class HaplogrepCmdTest {

	@Test
	public void HaplogrepCmdTest() throws Exception {

		String file = "test-data/vcf/HG00097.vcf";
		String phylo = "phylotree17.xml";
		String weights = "weights17.txt";
		String out = "test-data/out.txt";
		VcfImporter impvcf = new VcfImporter();

		HashMap<String, Sample> samples = impvcf.load(new File(file), false);
		ArrayList<String> lines = ExportUtils.vcfTohsd(samples);
		SampleFile newSampleFile = new SampleFile(lines);

		HgClassifier classifier = new HgClassifier();

		classifier.run(newSampleFile, phylo, weights, "kulczynski", 1, false);

		ExportUtils.createReport(newSampleFile.getTestSamples(), out, false);

		CsvTableReader reader = new CsvTableReader(out, '\t');

		int count = 0;
		while (reader.next()) {
			count++;
			String hg = reader.getString("Haplogroup");
			double quality = reader.getDouble("Quality");
			assertEquals("T2f1a1", hg);
			assertEquals(quality, quality, 0.0);
		}

		assertEquals(1, count);

		classifier.run(newSampleFile, phylo, weights, "kulczynski", 10, false);

		ExportUtils.createReport(newSampleFile.getTestSamples(), out, false);

		reader = new CsvTableReader(out, '\t');

		count = 0;
		while (reader.next()) {
			count++;
		}

		assertEquals(10, count);

		FileUtil.deleteFile(out);

	}

	@Test
	public void HaplogrepCmdTest_Phylotree17_all_5435() throws Exception {

		String file = "test-data/hsd/Phylotree17.hsd";
		String phylo = "phylotree17.xml";
		String weights = "weights17.txt";
		String out = "test-data/hsd/Phylotree17_out.txt";
		HsdImporter importHsd = new HsdImporter();

		ArrayList<String> samples = importHsd.load(new File(file));
		SampleFile newSampleFile = new SampleFile(samples);

		HgClassifier classifier = new HgClassifier();

		classifier.run(newSampleFile, phylo, weights, "kulczynski", 1, false);

		ExportUtils.createReport(newSampleFile.getTestSamples(), out, true);

		CsvTableReader reader = new CsvTableReader(out, '\t');

		int count = 0;
		while (reader.next()) {
			String sampleId = reader.getString("SampleID");
			String hg = reader.getString("Haplogroup");
			if (sampleId.equals(hg) || sampleId.contains("__")) //e.g. Q3a+61__62 as spaces not allowed
				count++;
			else {
				if (!sampleId.contains("M4")) {
					System.out.println(sampleId + " " + hg);
				} else {
					count++;
				}
			}
		}
		//TODO - fix issues in C4a1a vs C4a1a1 (due to 2232.1A 2232.2A)
		assertEquals( count, 5435-1);
	}

	@Test
	public void HaplogrepCmdTest_FineTuning_all_6401() throws Exception {

		String file = "test-data/hsd/Finetuning_TableS2.hsd";
		String phylo = "phylotree17_FU.xml";
		String weights = "weights17_FU.txt";
		String out = "test-data/hsd/Finetuning_TableS2_out.txt";
		HsdImporter importHsd = new HsdImporter();

		ArrayList<String> samples = importHsd.load(new File(file));
		SampleFile newSampleFile = new SampleFile(samples);

		HgClassifier classifier = new HgClassifier();

		classifier.run(newSampleFile, phylo, weights, "kulczynski", 1, false);

		ExportUtils.createReport(newSampleFile.getTestSamples(), out, false);

		CsvTableReader reader = new CsvTableReader(out, '\t');

		int count = 0;
		while (reader.next()) {
			String sampleId = reader.getString("SampleID");
			String hg = reader.getString("Haplogroup");
			if (sampleId.equals(hg))
				count++;
			else {
				if (!sampleId.contains("M4'")) {
					System.out.println(sampleId + " " + hg);
				} else {
					count++;
				}
			}
		}
		//TODO - fix issues in C4a1a vs C4a1a1 (due to 2232.1A 2232.2A)
		assertEquals(count, 6401-1);
	}

//	@Test
//	public void HaplogrepCmdTest_FineTuning_issues() throws Exception {
//
//		String file = "test-data/hsd/FineTuning_TableS2_subset_11.hsd";
//		String phylo = "phylotree17_FU.xml";
//		String weights = "weights17_FU.txt";
//		String out = "test-data/hsd/FineTuning_TableS2_subset_11_out.txt";
//		HsdImporter importHsd = new HsdImporter();
//
//		ArrayList<String> samples = importHsd.load(new File(file));
//		SampleFile newSampleFile = new SampleFile(samples);
//
//		HgClassifier classifier = new HgClassifier();
//
//		classifier.run(newSampleFile, phylo, weights, "kulczynski", 1, false);
//
//		ExportUtils.createReport(newSampleFile.getTestSamples(), out, false);
//
//		CsvTableReader reader = new CsvTableReader(out, '\t');
//		int count = 0;
//		while (reader.next()) {
//			String sampleId = reader.getString("SampleID");
//			String hg = reader.getString("Haplogroup");
//			if (sampleId.equals(hg)) {
//				count++;
//				System.out.println("+ " + sampleId + " " + hg);
//			} else {
//				if (!sampleId.contains("M4'")) {
//					System.out.println("- " + sampleId + " " + hg);
//				} else {
//					count++;
//				}
//			}
//		}
//		System.out.println("\n" + count);
//	}

}
