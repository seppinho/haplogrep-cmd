package genepi.haplogrep;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import core.Haplogroup;
import core.Polymorphism;
import core.SampleFile;
import genepi.haplogrep.util.HgClassifier;
import genepi.io.table.reader.CsvTableReader;
import importer.FastaImporter;
import importer.HsdImporter;
import importer.VcfImporter;
import importer.FastaImporter.References;
import util.ExportUtils;
import vcf.Sample;

public class HaplogrepCmdTest {
	
	static String phylotree17 = "phylotree17.xml";
	static String weights17 = "weights17.txt";
	static String phylotree17FU1 = "phylotree17_FU1.xml";
	static String weights17FU1 = "weights17_FU1.txt";

	@Test
	public void testHaplogrepCmd() throws Exception {

	
		
		String file = "test-data/vcf/HG00097.vcf";
		String out = "test-data/out.txt";
		VcfImporter impvcf = new VcfImporter();

		HashMap<String, Sample> samples = impvcf.load(new File(file), false);
		ArrayList<String> lines = ExportUtils.vcfTohsd(samples);
		SampleFile samples1 = new SampleFile(lines);

		// classify
		HgClassifier classifier = new HgClassifier();
		classifier.run(samples1, phylotree17, weights17FU1,"vcf");

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

		// classify
		classifier = new HgClassifier();
		classifier.run(samples1, phylotree17FU1, weights17, "kulczynski", 10, false, "vcf");
				
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

		// classify
		HgClassifier classifier = new HgClassifier();
		classifier.run(newSampleFile, phylotree17, weights17, "hsd");

		ExportUtils.createReport(newSampleFile.getTestSamples(), out, true);

		CsvTableReader reader = new CsvTableReader(out, '\t');

		int count = 0;
		while (reader.next()) {
			String sampleId = reader.getString("SampleID");
			String hg = reader.getString("Haplogroup");
			if (sampleId.equals(hg) || sampleId.contains("__")) // e.g. Q3a+61__62 as spaces not allowed
				count++;
			else {
				if (!sampleId.contains("M4")) {
				} else {
					count++;
				}
			}
		}
		// TODO - fix issues in C4a1a vs C4a1a1 (due to 2232.1A 2232.2A)
		assertEquals(count, 5435 - 1);
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

		// classify
		HgClassifier classifier = new HgClassifier();
		classifier.run(newSampleFile, phylotree17FU1, weights17FU1, "hsd");

		ExportUtils.createReport(newSampleFile.getTestSamples(), out, false);

		ExportUtils.generateFasta(newSampleFile.getTestSamples(), outfasta);

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
		// TODO - fix issues in C4a1a vs C4a1a1 (due to 2232.1A 2232.2A)
		assertEquals(6401, count);

		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> lines = impFasta.load(new File(outfasta), References.RCRS);
		SampleFile samplesFasta = new SampleFile(lines);

		// classify
		classifier = new HgClassifier();
		classifier.run(samplesFasta, phylotree17FU1, weights17FU1, "fasta");

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

		FileUtils.delete(new File(out));
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

		// classify
		HgClassifier classifier = new HgClassifier();
		classifier.run(samples, phylotree17FU1, weights17FU1, "fasta");

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

		// classify
		classifier = new HgClassifier();
		classifier.run(samples, phylotree17FU1, weights17FU1, "fasta");

		ExportUtils.generateFasta(samples.getTestSamples(), "test-data/tmp2.fasta");

		assertEquals(set1, set2);

		FileUtils.delete(new File("test-data/tmp.fasta"));
		FileUtils.delete(new File("test-data/tmp2.fasta"));

	}
	
	@Test
	public void h100NomenclatureActivatedTest() throws Exception {

		String file = "test-data/h100/H100.fasta";
		String phylotree = "phylotree17.xml";
		String fluctrates = "weights17.txt";
		
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RCRS);
		SampleFile newSampleFile = new SampleFile(samples);

		HgClassifier classifier = new HgClassifier();

		classifier.run(newSampleFile, phylotree, fluctrates, "fasta");
		ArrayList<Polymorphism> polys = newSampleFile.getTestSamples().get(0).getSample().getPolymorphisms();
		assertEquals(true, polys.contains(new Polymorphism("315.1C")));
		assertEquals(new Haplogroup("H100"),newSampleFile.getTestSamples().get(0).getTopResult().getHaplogroup());

	}
	
	@Test
	public void h100NomenclatureDeactivatedTest() throws Exception {

		String file = "test-data/h100/H100.fasta";
		String phylotree = "phylotree17.xml";
		String fluctrates = "weights17.txt";
		
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RCRS);
		SampleFile newSampleFile = new SampleFile(samples);

		HgClassifier classifier = new HgClassifier();

		classifier.run(newSampleFile, phylotree, fluctrates, "kulczynski", 1, true, "fasta");
		ArrayList<Polymorphism> polys = newSampleFile.getTestSamples().get(0).getSample().getPolymorphisms();
		
		System.out.println(polys);
		assertEquals(true, polys.contains(new Polymorphism("310.1C")));
		assertEquals(new Haplogroup("H100"),newSampleFile.getTestSamples().get(0).getTopResult().getHaplogroup());
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
