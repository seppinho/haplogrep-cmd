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
import util.ExportUtils;
import vcf.Sample;
import core.SampleFile;
import core.TestSample;

public class Haplogrep extends Tool {

	public static String VERSION = "v2.1.20";

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
		addOptionalParameter("metric", "specifiy other metric (hamming or jaccard) than default (kulczynski)", Tool.STRING);
		addFlag("lineage", "export lineage information");
		addOptionalParameter("hits", "calculate best n hits", Tool.STRING);
	}

	@Override
	public void init() {

		System.out.println("Welcome to HaploGrep " + VERSION);
		System.out.println("Instiute of Genetic Epidemiology, Medical University of Innsbruck");
		System.out.println("Hansi Weissensteiner, Sebastian Schönherr, Lukas Forer, Dominic Pacher");
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

		boolean extended = isFlagSet("extend-report");

		boolean chip = isFlagSet("chip");

		boolean lineage = isFlagSet("lineage");

		boolean rsrs = isFlagSet("rsrs");
		
		boolean fixNomenclature = isFlagSet("fixNomenclature");

		if (chip && !format.equals("vcf")) {
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

		File input = new File(in);

		if (!input.exists()) {
			System.out.println("Error. Please check if input file exists");
			return -1;
		}

		String phylotree = "phylotree/phylotree$VERSION$RSRS.xml";

		String fluctrates = "weights/weights$VERSION$RSRS.txt";

		phylotree = phylotree.replace("$VERSION", tree);

		fluctrates = fluctrates.replace("$VERSION", tree);

		if (rsrs) {
			phylotree = phylotree.replace("$RSRS", "_rsrs");
			fluctrates = fluctrates.replace("$RSRS", "_rsrs");
		} else {
			phylotree = phylotree.replace("$RSRS", "");
			fluctrates = fluctrates.replace("$RSRS", "");
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
					lines = ExportUtils.vcfTohsd(samples);
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

					if (lineage) {
						ExportUtils.calcLineage(samples, out); 
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

		//haplogrep = new Haplogrep(new String[] { "--in", "test-data/h100/H100.fasta", "--out",
		//		"test-data/test.txt", "--format", "fasta","--hits", "1","--fixNomenclature"});

		haplogrep.start();

	}

}
