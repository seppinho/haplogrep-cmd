package genepi.haplogrep;

import genepi.base.Tool;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
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
		addParameter("phylotree", "specifiy phylotree version");
		addParameter("format", "hsd");

	}

	@Override
	public void init() {
		System.out.println("Haplogrep 2.0 \n\n");
	}

	@Override
	public int run() {

		final Log log = LogFactory.getLog(Tools.class);

		String phylotree = "phylotree$VERSION.xml";

		String fluctrates = "weights$VERSION.txt";

		String in = (String) getValue("in");
		String out = (String) getValue("out");
		String tree = (String) getValue("phylotree");
		String format = (String) getValue("format");

		File file = new File(in);

		phylotree = phylotree.replace("$VERSION", tree);

		fluctrates = fluctrates.replace("$VERSION", tree);

		try {

			if (file.isFile()) {

				String uniqueID = UUID.randomUUID().toString();

				Session session = new Session(uniqueID);

				ArrayList<String> lines = null;

				if (format.equals("hsd")) {

					lines = importData(file);

				}

				if (lines != null) {

					SampleFile newSampleFile = new SampleFile(lines);

					session.setCurrentSampleFile(newSampleFile);

					determineHaplogroup(session, phylotree, fluctrates);

					exportResults(session, out);

				}

			} else {
				log.error("Please Check the input file");
				System.exit(-1);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}

		log.info("done.");

		System.exit(0);

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

	private static void determineHaplogroup(Session session, String phyloTree, String fluctrates)
			throws JDOMException, IOException, InvalidRangeException {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree(phyloTree, fluctrates);

		RankingMethod newRanker = new KulczynskiRanking(1);

		session.getCurrentSampleFile().updateClassificationResults(phylotree, newRanker);

	}

	private static void exportResults(Session session, String outFilename) throws IOException {

		StringBuffer result = new StringBuffer();

		Collection<TestSample> sampleCollection = null;

		sampleCollection = session.getCurrentSampleFile().getTestSamples();

		Collections.sort((List<TestSample>) sampleCollection);

		result.append("SampleID\tRange\tHaplogroup\tOverall_Rank\n");

		if (sampleCollection != null) {

			for (TestSample sample : sampleCollection) {

				result.append(sample.getSampleID());

				TestSample currentSample = session.getCurrentSampleFile().getTestSample(sample.getSampleID());

				for (RankedResult currentResult : currentSample.getResults()) {

					result.append("\t" + sample.getSample().getSampleRanges().toString().replaceAll("\\|", ""));

					result.append("\t" + currentResult.getHaplogroup());

					result.append("\t" + String.format(Locale.ROOT, "%.4f", currentResult.getDistance()));

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

		HaplogrepCMD test = new HaplogrepCMD(new String[] { "--in", "/home/seb/Desktop/wir2.hsd", "--out",
				"/home/seb/Desktop/franz3", "--format", "hsd", "--phylotree", "17" });
		test.start();

	}

}
