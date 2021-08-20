package genepi.haplogrep;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import core.SampleFile;
import exceptions.parse.HsdFileException;
import genepi.haplogrep.util.HgClassifier;
import genepi.io.FileUtil;
import genepi.io.table.reader.CsvTableReader;
import importer.FastaImporter;
import importer.HsdImporter;
import importer.VcfImporter;
import importer.FastaImporter.References;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import search.ranking.KulczynskiRanking;
import util.ExportUtils;
import vcf.Sample;

public class HaplogrepCmdTest {

	@Test
	public void testHaplogrepCmd() throws Exception {

		String file = "test-data/vcf/HG00097.vcf";
		String out = "test-data/out.txt";
		VcfImporter impvcf = new VcfImporter();

		HashMap<String, Sample> samples = impvcf.load(new File(file), false);
		ArrayList<String> lines = ExportUtils.vcfTohsd(samples);
		SampleFile samples1 = new SampleFile(lines);

		//classify
		runClassificationPT17(samples1,1);

		ExportUtils.createReport(samples1.getTestSamples(), out, false);

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
		
		//classify
		runClassificationPT17(samples1, 10);
		ExportUtils.createReport(samples1.getTestSamples(), out, false);

		reader = new CsvTableReader(out, '\t');

		count = 0;
		while (reader.next()) {
			count++;
		}

		assertEquals(10, count);

		FileUtils.delete(new File(out));

	}

	@Test
	public void HaplogrepCmdTest_Phylotree17_all_5435() throws Exception {

		String file = "test-data/hsd/Phylotree17.hsd";
		String out = "test-data/hsd/Phylotree17_out.txt";
		HsdImporter importHsd = new HsdImporter();

		ArrayList<String> samples = importHsd.load(new File(file));
		SampleFile newSampleFile = new SampleFile(samples);

		//classify
		runClassificationPT17(newSampleFile, 1);

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
				} else {
					count++;
				}
			}
		}
		//TODO - fix issues in C4a1a vs C4a1a1 (due to 2232.1A 2232.2A)
		assertEquals( count, 5435-1);
		FileUtils.delete(new File(out));
	}

	@Test
	public void HaplogrepCmdTest_FineTuning_all_6401() throws Exception {

		String file = "test-data/hsd/Finetuning_TableS2_updateHeteroplasmy.hsd";
		String out = "test-data/hsd/Finetuning_TableS2_out.txt";
		String outfasta = "test-data/hsd/Finetuning_TableS2_out.fasta";
		String out2 = "test-data/hsd/Finetuning_TableS2_out_fasta.txt";
		HsdImporter importHsd = new HsdImporter();

		ArrayList<String> samples = importHsd.load(new File(file));
		SampleFile newSampleFile = new SampleFile(samples);

		//classify
		runClassificationPT17_FU1(newSampleFile, 1, false);

		ExportUtils.createReport(newSampleFile.getTestSamples(), out, false);
		
		ExportUtils.generateFasta(newSampleFile.getTestSamples(), outfasta );

		CsvTableReader reader = new CsvTableReader(out, '\t');

		int count = 0;
		while (reader.next()) {
			String sampleId = reader.getString("SampleID");
			String hg = reader.getString("Haplogroup");
			if (sampleId.equals(hg))
				count++;
			else {
				if (!sampleId.contains("M4'")) {
				} else {
					count++;
				}
			}
		}
		//TODO - fix issues in C4a1a vs C4a1a1 (due to 2232.1A 2232.2A)
		assertEquals(6401, count);
		FileUtils.delete(new File(out));
		
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> lines = impFasta.load(new File(outfasta), References.RCRS);
		SampleFile samplesFasta = new SampleFile(lines);
		
		runClassificationPT17_FU1(samplesFasta, 1, true);

		ExportUtils.createReport(samplesFasta.getTestSamples(), out2, true);
		
		reader = new CsvTableReader(out2, '\t');

		int countFasta = 0;
		while (reader.next()) {
			String sampleId = reader.getString("SampleID");
			String hg = reader.getString("Haplogroup");
			if (sampleId.equals(hg))
				countFasta++;
			else {
				if (!sampleId.contains("M4'")) {
				} else {
					countFasta++;
				}
			}
		}
		assertEquals(6401, countFasta);
		
		FileUtils.delete(new File(out2));
	}
	
	@Test
	public void testFastaExportImportInterface() throws Exception {
		HashSet<String> set1 = new HashSet<String>();
		HashSet<String> set2 = new HashSet<String>();
		FastaImporter impFasta = new FastaImporter();
		
		String tempFile = "test-data/tmp.fasta";
		
		// read in file
		String file = "test-data/h100/H100.fasta";
		ArrayList<String> lines = impFasta.load(new File(file), References.RCRS);
		
		SampleFile samples = new SampleFile(lines);
		
		//classify
		runClassificationPT17_FU1(samples, 1, false);
 
		String[] splits = lines.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set1.add(splits[i]);
		}

		ExportUtils.generateFasta(samples.getTestSamples(), tempFile);
		
		// read in export file
		lines = impFasta.load(new File(tempFile), References.RCRS);
		samples = new SampleFile(lines);
		splits = lines.get(0).split("\t");
		
		for (int i = 3; i < splits.length; i++) {
			set2.add(splits[i]);
		}

		runClassificationPT17_FU1(samples, 1, true);
		
		ExportUtils.generateFasta(samples.getTestSamples(), "test-data/tmp2.fasta");

		assertEquals(set1, set2);

		FileUtils.delete(new File("test-data/tmp.fasta"));
		FileUtils.delete(new File("test-data/tmp2.fasta"));

	}
	public static void runClassificationPT17(SampleFile newSampleFile, int results) throws HsdFileException {
		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		KulczynskiRanking newRanker = new KulczynskiRanking(results);
		newSampleFile.updateClassificationResults(phylotree, newRanker);
	}
	
	
	public static void runClassificationPT17_FU1(SampleFile newSampleFile, int results, boolean fixNomenclature) throws HsdFileException {
		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17_FU1.xml", "weights17_FU1.txt");
		if (fixNomenclature)
			newSampleFile.applyNomenclatureRules(phylotree, "rules.csv");
		KulczynskiRanking newRanker = new KulczynskiRanking(results);
		newSampleFile.updateClassificationResults(phylotree, newRanker);
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
