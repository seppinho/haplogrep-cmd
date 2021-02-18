package genepi.haplogrep.main;

import genepi.base.Tool;
import genepi.haplogrep.util.HgClassifier;
import importer.FastaImporter;
import importer.HsdImporter;
import importer.VcfImporter;
import importer.FastaImporter.References;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.BasicConfigurator;

import util.ExportUtils;
import vcf.Sample;
import core.SampleFile;
import core.TestSample;

public class Haplogrep extends Tool {

	public static String VERSION = "v2.3.0";

	public Haplogrep(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {
		addParameter("in", "input VCF, fasta or hsd file");
		addParameter("out", "haplogroup output file");
		addParameter("format", "vcf, fasta, hsd");
		addOptionalParameter("phylotree", "specifiy phylotree version", Tool.STRING);
		addFlag("rsrs", "use RSRS Version");
		addFlag("fixNomenclature", "Fix mtDNA nomenclature conventions based on rules");
		addFlag("extend-report", "add flag for a extended final output");
		addFlag("chip", "VCF data from a genotype chip");
		addFlag("msa", "write multiple sequence alignment (_MSA.fasta) ");
		addFlag("fasta", "write FASTA  (.fasta) ");
		addOptionalParameter("metric", "specifiy other metric (hamming or jaccard) than default (kulczynski)", Tool.STRING);
		addOptionalParameter("lineage", "export lineage information as dot file, \n0=no tree, 1=with SNPs, 2=only structure, no SNPs" , Tool.STRING);
		addOptionalParameter("hits", "calculate best n hits", Tool.STRING);
		addOptionalParameter("hetLevel", "add heteroplasmies with a level > X to profile (default: 0.9)", Tool.STRING);
	}

	@Override
	public void init() {

		System.out.println("Welcome to Haplogrep Classify " + VERSION);
		System.out.println("");

	}

	@Override
	public int run() {

		String in = (String) getValue("in");
		String out = (String) getValue("out");
		String tree = (String) getValue("phylotree");
		String format = (String) getValue("format");
		String metric = (String) getValue("metric");
		String hits = (String) getValue("hits");
		String hetLevel = (String) getValue("hetLevel");
		String lineage = (String) getValue("lineage");
		
		boolean extended = isFlagSet("extend-report");

		boolean chip = isFlagSet("chip");

		boolean msa = isFlagSet("msa");
		
		boolean fasta = isFlagSet("fasta");

		boolean rsrs = isFlagSet("rsrs");
		
		boolean fixNomenclature = isFlagSet("fixNomenclature");

		if (fixNomenclature && !format.toLowerCase().equals("fasta")) {
			System.out.println(
					"The --fixNomenclature flag only works for FASTA.");
			return -1;
		}
		
		if (chip && !format.toLowerCase().equals("vcf")) {
			System.out.println(
					"The --chip flag only works for VCF. For hsd, please specify the included variants in the Haplogrep range.");
			return -1;
		}

		if (metric == null) {
			metric = "kulczynski";
		}

		if (tree == null) {
			tree = "17";
		}

		if (hits == null) {
			hits = "1";
		}
		
		if (hetLevel == null) {
			hetLevel = "0.9";
		}
		
		if (lineage == null) {
			lineage = "0";
		}

		File input = new File(in);

		if (!input.exists()) {
			System.out.println("Error. Please check if input file exists");
			return -1;
		}

		String phylotree = "phylotree$VERSION$RSRS.xml";

		String fluctrates = "weights$VERSION$RSRS.txt";

		phylotree = phylotree.replace("$VERSION", tree);

		fluctrates = fluctrates.replace("$VERSION", tree);
		
		References ref =null;
		
		if (rsrs) {
			phylotree = phylotree.replace("$RSRS", "_rsrs");
			fluctrates = fluctrates.replace("$RSRS", "_rsrs");
			ref=References.RSRS;
		} else {
			phylotree = phylotree.replace("$RSRS", "");
			fluctrates = fluctrates.replace("$RSRS", "");
			ref=References.RCRS;
		}

		System.out.println(phylotree);
		System.out.println("Parameters:");
		System.out.println("Input Format: " + format);
		System.out.println("Phylotree Version: " + tree);
		System.out.println("Reference: " + (rsrs ? "RSRS" : "rCRS"));
		System.out.println("Extended Report: " + extended);
		System.out.println("Fix Nomenclature: " + fixNomenclature);
		System.out.println("Used Metric: " + metric);
		System.out.println("Chip array data: " + chip);
		System.out.println("Lineage: " + lineage);
		System.out.println("");

		long start = System.currentTimeMillis();

		System.out.println("Start Classification...");

		try {

			if (input.isFile()) {

				ArrayList<String> lines = new ArrayList<String>();

				if (format.equals("hsd")) {

					HsdImporter importer = new HsdImporter();
					lines = importer.load(input);

				}

				else if (format.equals("vcf")) {
					VcfImporter importerVcf = new VcfImporter();
					HashMap<String, Sample> samples = importerVcf.load(input, chip);
					lines = ExportUtils.vcfTohsd(samples, Double.valueOf(hetLevel));
				}

				else if (format.equals("fasta")) {

					FastaImporter importer = new FastaImporter();
					
				
					if (rsrs) {
						lines = importer.load(input, References.RSRS);
					} else {
						lines = importer.load(input, References.RCRS);
					}

				}

				if (lines != null) {

					SampleFile newSampleFile = new SampleFile(lines);

					HgClassifier classifier = new HgClassifier();

					classifier.run(newSampleFile, phylotree, fluctrates, metric, Integer.valueOf(hits), fixNomenclature);
					
					ArrayList<TestSample> samples = newSampleFile.getTestSamples();
					
					ExportUtils.createReport(samples, out, extended);

					ExportUtils.calcLineage(samples,Integer.valueOf(lineage), out);
					
					if (fasta)
						{
							ExportUtils.generateFasta(samples, out);
						}
					
					if (msa)
					{
						ExportUtils.generateFastaMSA(samples, out);
					}

				}

			} else {
				return -1;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}

		System.out.println("HaploGrep file written to " + new File(out).getAbsolutePath() + " (Time: "
				+ ((System.currentTimeMillis() - start) / 1000) + " sec)");

		return 0; 
	}

	public static void main(String[] args) throws IOException {

		Haplogrep haplogrep = new Haplogrep(args);
		BasicConfigurator.configure();  

		//haplogrep = new Haplogrep(new String[] { "--in", "test-data/h100/H100.fasta", "--out",
			//	"test-data/test.txt", "--format", "fasta","--hits", "1","--fixNomenclature", "--lineage", "1"});
		//haplogrep = new Haplogrep(new String[] { "--in", "test-data/cambodia/B5a1_8281fix.hsd", "--out",
			//	"test-data/test.txt", "--format", "hsd","--hits", "1", "--lineage", "2"});
		haplogrep = new Haplogrep(new String[] { "--in", "test-data/cambodia/haplogroups_hsd.hsd", "--out",
				"test-data/test.txt", "--format", "hsd","--hits", "1", "--lineage", "3"});

		haplogrep.start();

	}

}
