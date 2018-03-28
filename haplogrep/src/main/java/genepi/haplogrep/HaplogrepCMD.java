package genepi.haplogrep;

import genepi.base.Tool;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.JDOMException;

import core.Polymorphism;
import core.SampleFile;
import core.TestSample;
import exceptions.parse.HsdFileException;
import exceptions.parse.sample.InvalidRangeException;

public class HaplogrepCMD extends Tool {

	public HaplogrepCMD(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {

		addParameter("in", "hsd file");
		addParameter("out", "write haplogrep final file");
		addParameter("ext", "write extended haplogrep out file (0:no, 1:yes)", INTEGER);
		addParameter("phylotree", "specifiy phylotree version");
		addParameter("metric", "specifiy metric (1:default; 2:Hamming, 3:Jaccard)");
		addParameter("format", "hsd");

	}

	@Override
	public void init() {
		System.out.println("Welcome to Haplogrep 2.0 \n\n");
	}

	@Override
	public int run() {

		String phylotree = "phylotree$VERSION.xml";

		String fluctrates = "weights$VERSION.txt";

		String in = (String) getValue("in");
		String out = (String) getValue("out");
		int ext = (int) getValue("ext");
		String tree = (String) getValue("phylotree");
		String format = (String) getValue("format");
		String metric = (String) getValue("metric");

		File f = new File(in);

		if (!f.exists()) {
			System.out.println("Error. Please check if input file exists");
			return -1;
		}

		phylotree = phylotree.replace("$VERSION", tree);

		fluctrates = fluctrates.replace("$VERSION", tree);

		try {

			if (f.isFile()) {

				String uniqueID = UUID.randomUUID().toString();

				Session session = new Session(uniqueID);

				ArrayList<String> lines = null;

				if (format.equals("hsd")) {

					lines = importData(f);

				}

				if (lines != null) {

					SampleFile newSampleFile = new SampleFile(lines);

					session.setCurrentSampleFile(newSampleFile);

					determineHaplogroup(session, phylotree, fluctrates, metric);

					exportResults(session, out, ext);

				}

			} else {
				return -1;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}

		System.out.println("");
		System.out.println("Haplogrep file written to " + out);
		return 0;
	}

	private static ArrayList<String> importData(File file) throws FileNotFoundException, IOException, HsdFileException {

		ArrayList<String> lines = new ArrayList<String>();

		BufferedReader in;

		in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

		String line = "";

		line = in.readLine(); // skip first line, because its the header

		if (!line.startsWith("SampleId\tRange") && !line.toUpperCase().contains("RANGE")) {

			lines.add(line);
		}

		while ((line = in.readLine()) != null) {
			lines.add(line);
		}

		in.close();

		return lines;

	}

	private static void determineHaplogroup(Session session, String phyloTree, String fluctrates, String metric)
			throws JDOMException, IOException, InvalidRangeException {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree(phyloTree, fluctrates);

		RankingMethod newRanker = null;

		switch (metric) {
		case "1":
			newRanker = new KulczynskiRanking(1);
			break;

		case "2":
			newRanker = new HammingRanking(1);
			break;

		case "3":
			newRanker = new JaccardRanking(1);
			break;

		default:
			newRanker = new KulczynskiRanking(1);
		}

		session.getCurrentSampleFile().updateClassificationResults(phylotree, newRanker);

	}

	private static void exportResults(Session session, String outFilename, int ext) throws IOException {

		StringBuffer result = new StringBuffer();

		Collection<TestSample> sampleCollection = null;

		sampleCollection = session.getCurrentSampleFile().getTestSamples();

		Collections.sort((List<TestSample>) sampleCollection);

		if(ext==0)
			result.append("SampleID\tRange\tHaplogroup\tOverall_Rank\n");
		else if (ext==1)
			result.append("SampleID\tRange\tHaplogroup\tOverall_Rank\tNot_Found_Polys\tFound_Polys\tRemaining_Polys\tAAC_In_Remainings\t Input_Sample\n");
		
		if (sampleCollection != null) {

			for (TestSample sample : sampleCollection) {

				result.append(sample.getSampleID());

				TestSample currentSample = session.getCurrentSampleFile().getTestSample(sample.getSampleID());

				for (RankedResult currentResult : currentSample.getResults()) {

					result.append("\t" + sample.getSample().getSampleRanges().toString().replaceAll("\\|", ""));

					result.append("\t" + currentResult.getHaplogroup());

					result.append("\t" + String.format(Locale.ROOT, "%.4f", currentResult.getDistance()));

					if (ext==1) {
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
						ArrayList<Polymorphism> hghelp = new ArrayList<>();
						for (Polymorphism currentPoly : found) {
									result.append(" "+currentPoly);
											
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

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		HaplogrepCMD test = new HaplogrepCMD(new String[] { "--in", "test-data/h100.hsd", "--out",
				"test-data/h100-haplogrep.txt", "--ext", "1", "--format", "hsd", "--phylotree", "17", "--metric", "1" });
		test.start();

	}

}
