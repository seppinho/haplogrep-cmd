package genepi.haplogrep;

import genepi.base.Tool;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeType;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import search.SearchResultTreeNode;
import search.ranking.HammingRanking;
import search.ranking.JaccardRanking;
import search.ranking.KulczynskiRanking;
import search.ranking.RankingMethod;
import search.ranking.results.RankedResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.jdom.JDOMException;

import core.Polymorphism;
import core.SampleFile;
import core.SampleRanges;
import core.TestSample;
import exceptions.parse.HsdFileException;
import exceptions.parse.sample.InvalidRangeException;

public class Haplogrep extends Tool {

	public static String VERSION = "2.1.8";

	public Haplogrep(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {

		addParameter("in", "input VCF or hsd file");
		addParameter("out", "haplogroup output file");
		addParameter("format", "hsd or vcf");
		addOptionalParameter("phylotree", "specifiy phylotree version", Tool.STRING);
		addFlag("extend-report", "add flag for a extended final output");
		addFlag("chip", "VCF data from a genotype chip");
		addOptionalParameter("metric", "specifiy other metric (hamming or jaccard)", Tool.STRING);
		addFlag("lineage", "export lineage information");
	}

	@Override
	public void init() {

		System.out.println("Welcome to HaploGrep " + VERSION);
		System.out.println("Division of Genetic Epidemiology, Medical University of Innsbruck");
		System.out.println("");

	}

	@Override
	public int run() {

		String phylotree = "phylotree$VERSION.xml";

		String fluctrates = "weights$VERSION.txt";

		String in = (String) getValue("in");
		String out = (String) getValue("out");
		String tree = (String) getValue("phylotree");
		String format = (String) getValue("format");
		String metric = (String) getValue("metric");

		boolean extended = isFlagSet("extend-report");

		boolean chip = isFlagSet("chip");

		boolean lineage = isFlagSet("lineage");

		if (chip && !format.equals("vcf")) {
			System.out.println("Please select VCF format when selecting chip parameter");
			return -1;
		}

		if (metric == null) {
			metric = "kulczynski";
		}

		if (tree == null) {
			tree = "17";
		}

		File input = new File(in);

		if (!input.exists()) {
			System.out.println("Error. Please check if input file exists");
			return -1;
		}

		phylotree = phylotree.replace("$VERSION", tree);

		fluctrates = fluctrates.replace("$VERSION", tree);

		System.out.println("Parameters:");
		System.out.println("Input Format: " + format);
		System.out.println("Phylotree Version: " + tree);
		System.out.println("Extended Report: " + extended);
		System.out.println("Used Metric: " + metric);
		System.out.println("Chip array data: " + chip);
		System.out.println("Lineage: " + lineage);
		System.out.println("");

		long start = System.currentTimeMillis();

		System.out.println("Start Classification...");

		try {

			if (input.isFile()) {

				String uniqueID = UUID.randomUUID().toString();

				Session session = new Session(uniqueID);

				ArrayList<String> lines = null;

				if (format.equals("hsd")) {

					lines = importDataHsd(input);

				}

				else if (format.equals("vcf")) {

					lines = importVcf(input, chip);

				}

				if (lines != null) {

					SampleFile newSampleFile = new SampleFile(lines);

					session.setCurrentSampleFile(newSampleFile);

					determineHaplogroup(session, phylotree, fluctrates, metric);

					exportResults(session, out, extended);

					if (lineage) {
						calcLineage(session, out);
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

		System.out.println("HaploGrep file written to " + out + " (Time: "
				+ ((System.currentTimeMillis() - start) / 1000) + " sec)");

		return 0;
	}

	private static ArrayList<String> importDataHsd(File file)
			throws FileNotFoundException, IOException, HsdFileException {

		ArrayList<String> lines = new ArrayList<String>();

		BufferedReader in;

		in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

		String line = in.readLine();

		// skip first line
		if (!line.toLowerCase().contains("range")) {

			lines.add(line);
		}

		while ((line = in.readLine()) != null) {
			lines.add(line);
		}

		in.close();

		return lines;

	}

	public ArrayList<String> importVcf(File file, boolean chip) throws Exception {

		final VCFFileReader vcfReader = new VCFFileReader(file, false);

		VCFHeader vcfHeader = vcfReader.getFileHeader();

		StringBuilder range = new StringBuilder();

		if (chip) {

			for (VariantContext vc : vcfReader) {

				range.append(vc.getStart() + ";");

			}

			vcfReader.close();

		} else {

			range.append("1-16569");

		}

		ArrayList<StringBuilder> profiles = new ArrayList<StringBuilder>();

		for (String sample : vcfHeader.getSampleNamesInOrder()) {

			StringBuilder profile = new StringBuilder();

			profiles.add(profile.append(sample + "\t" + range + "\t" + "?" + "\t"));

		}

		for (final VariantContext vc : vcfReader) {

			if (vc.getStart() > 16569) {

				System.out.println("Error! Position " + vc.getStart()
						+ " outside the range. Please double check if VCF only includes mtDNA data mapped to rCRS");
				System.exit(-1);

			}

			String reference = vc.getReference().getBaseString();

			int index = 0;

			for (String sample : vcfHeader.getSampleNamesInOrder()) {

				Genotype sampleGenotype = vc.getGenotype(sample);

				String genotype = sampleGenotype.getGenotypeString(false);

				if (sampleGenotype.getType() == GenotypeType.HOM_VAR) {

					// SNPs
					if (genotype.length() == reference.length()) {

						if (genotype.length() == 1) {

							profiles.get(index).append(vc.getStart() + "" + genotype);

							profiles.get(index).append("\t");

						} else {

							// not completely sure if this is needed, but let's check for longer genotype
							// strings on which pos SNP is located
							for (int i = 0; i < genotype.length(); i++) {

								if (reference.charAt(i) != genotype.charAt(i)) {

									profiles.get(index).append((vc.getStart() + i) + "" + genotype.charAt(i));

									profiles.get(index).append("\t");

									break;

								}

							}

						}
					}

					// DELETIONS
					else if (reference.length() > genotype.length()) {

						profiles.get(index).append((vc.getStart() + genotype.length()) + "-"
								+ (vc.getStart() + reference.length() - 1) + "d");

						profiles.get(index).append("\t");
					}

					// INSERTIONS
					else if (reference.length() < genotype.length()) {

						// only simple case
						// TODO
						if (reference.length() == 1) {

							profiles.get(index).append(vc.getStart() + "." + 1
									+ genotype.substring(reference.length(), (genotype.length())));

							profiles.get(index).append("\t");

						}
					}

				}

				// Heteroplasmies
				if (sampleGenotype.getType() == GenotypeType.HET && sampleGenotype.hasAnyAttribute("HF")) {

					String hetFrequency = (String) vc.getGenotype(sample).getAnyAttribute("HF");

					if (Double.valueOf(hetFrequency) >= 0.96) {

						if (genotype.length() == reference.length()) {

							if (genotype.length() == 1) {

								profiles.get(index).append(vc.getStart() + "" + genotype);

								profiles.get(index).append("\t");

							} else {

								for (int i = 0; i < genotype.length(); i++) {

									if (reference.charAt(i) != genotype.charAt(i)) {

										profiles.get(index).append((vc.getStart() + i) + "" + genotype.charAt(i));

										profiles.get(index);

										break;
									}

								}

							}

						}
					}
				}
				index++;
			} // end samples

		} // end variants

		ArrayList<String> result = new ArrayList<>();

		for (StringBuilder profile : profiles) {

			String profileString = profile.toString();

			if (profileString.split("\t").length > 3) {

				result.add(profileString + "\n");

			} else {
				System.out.println("Info: Sample " + profileString.substring(0, profile.indexOf("\t"))
						+ " excluded. No variants detected. Please double check if data has been aligned to rCRS reference");
			}

		}

		vcfReader.close();

		return result;

	}

	private static void determineHaplogroup(Session session, String phyloTree, String fluctrates, String metric)
			throws JDOMException, IOException, InvalidRangeException {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree(phyloTree, fluctrates);

		RankingMethod newRanker = null;

		switch (metric) {

		case "kulczynski":
			newRanker = new KulczynskiRanking(1);
			break;

		case "hamming":
			newRanker = new HammingRanking(1);
			break;

		case "jaccard":
			newRanker = new JaccardRanking(1);
			break;

		default:
			newRanker = new KulczynskiRanking(1);

		}

		session.getCurrentSampleFile().updateClassificationResults(phylotree, newRanker);

	}

	private static void exportResults(Session session, String outFilename, boolean extended) throws IOException {

		StringBuffer result = new StringBuffer();

		Collection<TestSample> sampleCollection = null;

		sampleCollection = session.getCurrentSampleFile().getTestSamples();

		Collections.sort((List<TestSample>) sampleCollection);

		if (!extended) {

			result.append("SampleID\tRange\tHaplogroup\tOverall_Rank\n");

		} else {

			result.append(
					"SampleID\tRange\tHaplogroup\tOverall_Rank\tNot_Found_Polys\tFound_Polys\tRemaining_Polys\tAAC_In_Remainings\t Input_Sample\n");
		}

		if (sampleCollection != null) {

			for (TestSample sample : sampleCollection) {

				result.append(sample.getSampleID() + "\t");

				TestSample currentSample = session.getCurrentSampleFile().getTestSample(sample.getSampleID());

				for (RankedResult currentResult : currentSample.getResults()) {

					SampleRanges range = sample.getSample().getSampleRanges();

					ArrayList<Integer> startRange = range.getStarts();

					ArrayList<Integer> endRange = range.getEnds();

					String resultRange = "";

					for (int i = 0; i < startRange.size(); i++) {
						if (startRange.get(i).equals(endRange.get(i))) {
							resultRange += startRange.get(i) + ";";
						} else {
							resultRange += startRange.get(i) + "-" + endRange.get(i) + ";";
						}
					}
					result.append(resultRange + "\t");

					result.append("\t" + currentResult.getHaplogroup());

					result.append("\t" + String.format(Locale.ROOT, "%.4f", currentResult.getDistance()));

					if (extended) {

						result.append("\t");

						ArrayList<Polymorphism> found = currentResult.getSearchResult().getDetailedResult()
								.getFoundPolys();

						ArrayList<Polymorphism> expected = currentResult.getSearchResult().getDetailedResult()
								.getExpectedPolys();

						Collections.sort(found);

						Collections.sort(expected);

						for (Polymorphism currentPoly : expected) {
							if (!found.contains(currentPoly))
								result.append(" " + currentPoly);
						}

						result.append("\t");

						for (Polymorphism currentPoly : found) {
							result.append(" " + currentPoly);

						}

						result.append("\t");
						ArrayList<Polymorphism> allChecked = currentResult.getSearchResult().getDetailedResult()
								.getRemainingPolysInSample();
						Collections.sort(allChecked);

						for (Polymorphism currentPoly : allChecked) {
							result.append(" " + currentPoly);
						}

						result.append("\t");

						ArrayList<Polymorphism> aac = currentResult.getSearchResult().getDetailedResult()
								.getRemainingPolysInSample();
						Collections.sort(aac);

						for (Polymorphism currentPoly : aac) {
							if (currentPoly.getAnnotation() != null)
								result.append(
										" " + currentPoly + " [" + currentPoly.getAnnotation().getAminoAcidChange()
												+ "| Codon " + currentPoly.getAnnotation().getCodon() + " | "
												+ currentPoly.getAnnotation().getGene() + " ]");
						}

						result.append("\t");

						ArrayList<Polymorphism> input = sample.getSample().getPolymorphisms();

						Collections.sort(input);

						for (Polymorphism currentPoly : input) {
							result.append(" " + currentPoly);
						}
					}
					result.append("\n");

				}
			}
		}

		FileWriter fileWriter = new FileWriter(outFilename);

		fileWriter.write(result.toString());

		fileWriter.close();

	}

	private void calcLineage(Session session, String out) throws IOException {

		StringBuilder build = new StringBuilder();

		StringBuilder graphViz = new StringBuilder();
		
		build.append("#Tab-delimited columns: FROM	POLYS_ON_PATH	TO\n");

		for (TestSample sample : session.getCurrentSampleFile().getTestSamples()) {

			build.append(sample.getSampleID() + "\n");

			graphViz.append("digraph {   \n");

			TestSample currentSample = session.getCurrentSampleFile().getTestSample(sample.getSampleID());

			for (RankedResult currentResult : currentSample.getResults()) {

				ArrayList<SearchResultTreeNode> currentPath = currentResult.getSearchResult().getDetailedResult()
						.getPhyloTreePath();

				for (int i = 0; i < currentPath.size(); i++) {

					if (i == 0) {
						build.append(currentPath.get(i).getHaplogroup() + "\t");
						graphViz.append("\"" + currentPath.get(i).getHaplogroup() + "\" -> ");
					}

					else {

						if (currentPath.get(i).getExpectedPolys().size() == 0) {
							build.append("-");
						} else {

							for (Polymorphism currentPoly : currentPath.get(i).getExpectedPolys()) {

								if (currentPath.get(i).getFoundPolys().contains(currentPoly)) {
									build.append(currentPoly + " ");
								}
							}
						}

						build.append("\t" + currentPath.get(i).getHaplogroup() + "\n");

						graphViz.append("\"" + currentPath.get(i).getHaplogroup() + "\";\n");

						if (i != (currentPath.size()-1)) {

							graphViz.append("\"" + currentPath.get(i).getHaplogroup() + "\" -> ");

							build.append(currentPath.get(i).getHaplogroup() + "\t");

						}
					}

				}

				build.append("\n");
				graphViz.append("}");
				graphViz.append("\n");
				graphViz.append("\n");
			}

		}

		FileWriter fileWriter = new FileWriter(out.substring(0, out.lastIndexOf(".")) + "_lineage.txt");

		fileWriter.write(build.toString());

		FileWriter fileWriter2 = new FileWriter(out.substring(0, out.lastIndexOf(".")) + "_graphviz.txt");

		fileWriter2.write(graphViz.toString());

		fileWriter.close();

		fileWriter2.close();

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		Haplogrep haplogrep = new Haplogrep(args);

		//haplogrep = new Haplogrep(new String[] { "--in", "test-data/ALL.chrMT.phase1.vcf", "--out",
		//		"test-data/h100-haplogrep.txt", "--format", "vcf", "--lineage" });

		haplogrep.start();

	}

}
