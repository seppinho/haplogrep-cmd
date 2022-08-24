/**
 * 
 */
package genepi.haplogrep;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import core.Polymorphism;
import core.SampleFile;
import core.TestSample;
import genepi.haplogrep.util.HgClassifier;
import importer.FastaImporter;
import importer.VcfImporter;
import importer.FastaImporter.References;
import util.ExportUtils;
import vcf.Sample;

/**
 * @author hansi
 *
 */
public class ExportTest {

	static String phylotree17 = "phylotree17.xml";
	static String weights17 = "weights17.txt";
	static String phylotree17FU1 = "phylotree17_FU1.xml";
	static String weights17FU1 = "weights17_FU1.txt";
	static String phylotree17FU1a = "phylotree17_FU1a.xml";
	static String weights17FU1a = "weights17_FU1a.txt";

	@Test
	public void testExportLineage() throws Exception {

		String file = "test-data/fasta/L1b1a3.fasta";
		String out = "test-data/fasta/L1b1a3_out.txt";
		FastaImporter impFasta = new FastaImporter();

		ArrayList<String> lines = impFasta.load(new File(file), References.RCRS);
		SampleFile samples1 = new SampleFile(lines);

		// classify
		HgClassifier classifier = new HgClassifier();
		classifier.run(samples1, phylotree17, weights17FU1, "fasta");

		//System.out.println(samples1.getTestSamples().get(0).getDetectedHaplogroup() + " " + samples1.getTestSamples().get(0).getResults().get(0).getSearchResult().getDetailedResult().getFoundNotFoundPolysArray().toString());

		ExportUtils.calcLineage(samples1.getTestSamples(), 2, out);
		
		Reader fr = new FileReader(new File("test-data/fasta/L1b1a3_out.dot"));
		BufferedReader br = new BufferedReader(fr);

		int countLines=0;
		while (br.ready()) {
			br.readLine();
			countLines++;
		}
		assertEquals(74, countLines);

		
	}

	@Test
	public void testFastaExportImportInterface() throws Exception {
		HashSet<String> set1 = new HashSet<>();
		HashSet<String> set2 = new HashSet<>();
		FastaImporter impFasta = new FastaImporter();

		String tempFile = "test-data/tmp.fasta";

		// read in file
		String file = "test-data/h100/H100.fasta";
		ArrayList<String> lines = impFasta.load(new File(file), References.RCRS);

		SampleFile samples = new SampleFile(lines);

		// classify
		HgClassifier classifier = new HgClassifier();
		classifier.run(samples, phylotree17FU1a, weights17FU1a, "fasta");

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
		classifier.run(samples, phylotree17FU1a, weights17FU1a, "fasta");

		ExportUtils.generateFasta(samples.getTestSamples(), "test-data/tmp2.fasta");

		assertEquals(set1, set2);

		FileUtils.delete(new File("test-data/tmp.fasta"));
		FileUtils.delete(new File("test-data/tmp2.fasta"));

	}

	@Test
	public void testFastaExportMSA() throws Exception {
		HashSet<String> set1 = new HashSet<>();
		FastaImporter impFasta = new FastaImporter();

		String tempFile = "test-data/tmp.fasta";
		File tmp = new File(tempFile);
		tmp.createNewFile();

		// read in file
		String file = "test-data/h100/H100s.fasta";
		ArrayList<String> lines = impFasta.load(new File(file), References.RCRS);

		SampleFile samples = new SampleFile(lines);

		// classify
		HgClassifier classifier = new HgClassifier();
		classifier.run(samples, phylotree17FU1a, weights17FU1a, "fasta");

		String[] splits = lines.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set1.add(splits[i]);
		}

		ExportUtils.generateFastaMSA(samples.getTestSamples(), tmp.getAbsolutePath());

		Reader fr = new FileReader(new File("test-data/tmp_MSA.fasta"));
		BufferedReader br = new BufferedReader(fr);
		FileWriter fw = new FileWriter(tempFile);

		int len = 0;
		int countentries = 0;
		String line = "";
		while (br.ready()) {
			line = br.readLine();
			if (!line.contains(">")) {
				len += line.length();
				fw.write(line.replace("-", "") + "\n");
			} else {
				countentries++;
				fw.write(line + "\n");
			}
		}
		fw.close();
		br.close();
		fr.close();

		assertEquals(len / line.length(), countentries);

		ArrayList<String> lines2 = impFasta.load(new File(tempFile), References.RCRS);
		SampleFile samples2 = new SampleFile(lines2);

		classifier = new HgClassifier();
		classifier.run(samples2, phylotree17FU1a, weights17FU1a, "fasta");

		ArrayList<Polymorphism> poly1 = samples.getTestSample("gi_1219401131_gb_KY923845.1_").getSample()
				.getPolymorphisms();
		ArrayList<Polymorphism> poly2 = samples2.getTestSample("gi_1219401131_gb_KY923845.1__H100").getSample()
				.getPolymorphisms();

		assertEquals(poly1.toString(), poly2.toString());

		FileUtils.delete(new File("test-data/tmp_MSA.fasta"));
		FileUtils.delete(new File("test-data/tmp.fasta"));
	}

}
