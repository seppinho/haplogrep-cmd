package genepi.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

import core.SampleFile;
import core.TestSample;
import genepi.haplogrep.util.HgClassifier;
import importer.FastaImporter;
import importer.FastaImporter.References;
import importer.HsdImporter;
import importer.VcfImporter;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;
import util.ExportUtils;
import vcf.Sample;

public class HaplogrepCommand implements Callable<Integer> {

	public static String VERSION = "v2.3.0";

	@Option(names = { "--in", "--input" }, description = "Input VCF, fasta or hsd file", required = true)
	String in;

	@Option(names = { "--out", "--output" }, description = "Output file location", required = true)
	String out;

	@Option(names = { "--format" }, description = "vcf, fasta or hsd", required = true)
	String format;

	@Option(names = { "--phylotree" }, description = "Specifiy phylotree version", required = false)
	String tree;

	@Option(names = {
			"--rsrs" }, description = "Use RSRS Version", required = false, showDefaultValue = Visibility.ALWAYS)
	boolean rsrs = false;

	@Option(names = {
			"--fixNomenclature" }, description = "Fix mtDNA nomenclature conventions based on rules", required = false, showDefaultValue = Visibility.ALWAYS)
	boolean fixNomenclature = false;

	@Option(names = {
			"--extend-report" }, description = "Add flag for a extended final output", required = false, showDefaultValue = Visibility.ALWAYS)
	boolean extendedReport = false;

	@Option(names = {
			"--write-fasta" }, description = "Write results in fasta format", required = false, showDefaultValue = Visibility.ALWAYS)
	boolean writeFasta = false;
	
	@Option(names = {
	"--write-fasta-msa" }, description = "Write multiple sequence alignment (_MSA.fasta) ", required = false, showDefaultValue = Visibility.ALWAYS)
boolean writeFastaMSA = false;

	@Option(names = {
			"--chip" }, description = "VCF data from a genotype chip", required = false, showDefaultValue = Visibility.ALWAYS)
	boolean chip = false;

	@Option(names = {
			"--metric" }, description = "Specifiy other metric (hamming or jaccard) than default (kulczynski)", required = false)
	String metric;

	@Option(names = {
			"--lineage" }, description = "Export lineage information as dot file, \\n0=no tree, 1=with SNPs, 2=only structure, no SNPs", required = false)
	String lineage;

	@Option(names = { "--hits" }, description = "Calculate best n hits", required = false)
	String hits;

	@Option(names = {
			"--hetLevel" }, description = "Add heteroplasmies with a level > X to profile (default: 0.9)", required = false)
	String hetLevel;

	@Override
	public Integer call() {

		if (fixNomenclature && !format.toLowerCase().equals("fasta")) {
			System.out.println("The --fixNomenclature flag only works for FASTA.");
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
			tree = "17_revised";
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
		System.out.println("Extended Report: " + extendedReport);
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

					classifier.run(newSampleFile, phylotree, fluctrates, metric, Integer.valueOf(hits),
							fixNomenclature);

					ArrayList<TestSample> samples = newSampleFile.getTestSamples();

					ExportUtils.createReport(samples, out, extendedReport);

					ExportUtils.calcLineage(samples, Integer.valueOf(lineage), out);

					if (writeFasta) {
						ExportUtils.generateFasta(samples, out);
					}
					
					if (writeFastaMSA)
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

}
