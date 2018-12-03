package genepi.haplogrep.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import contamination.objects.Sample;
import contamination.objects.Variant;
import core.Haplogroup;
import core.Polymorphism;
import core.SampleRanges;
import core.TestSample;
import genepi.haplogrep.Session;
import genepi.io.table.writer.CsvTableWriter;
import search.SearchResultTreeNode;
import search.ranking.results.RankedResult;

public class ExportTools {

	public static ArrayList<String> vcfToHsd(HashMap<String, Sample> samples) {
		ArrayList<String> lines = new ArrayList<String>();
		for (Sample sam : samples.values()) {

			StringBuilder build = new StringBuilder();
			build.append(sam.getId() + "\t" + sam.getRange() + "\t" + "?");

			for (Variant var : sam.getVariants()) {
				if (var.getType() == 1 || var.getType() == 4 || (var.getType() == 2 && var.getLevel() >= 0.8)) {
					build.append("\t" + var.getPos() + "" + var.getVariant());
				} else if (var.getType() == 5) {
					build.append("\t" + var.getInsertion());
				}
			}
			build.append("\n");

			lines.add(build.toString());
		}
		return lines;
	}

	public static void createReport(Session session, String outFilename, boolean extended) throws IOException {

		Collection<TestSample> sampleCollection = null;

		CsvTableWriter writer = new CsvTableWriter(outFilename,'\t');

		sampleCollection = session.getCurrentSampleFile().getTestSamples();

		Collections.sort((List<TestSample>) sampleCollection);

		if (!extended) {

			writer.setColumns(new String[] { "SampleID", "Range", "Haplogroup", "Overall_Rank" });

		} else {

			writer.setColumns(new String[] { "SampleID", "Range", "Haplogroup", "Overall_Rank", "Not_Found_Polys",
					"Found_Polys", "Remaining_Polys", "AAC_In_Remainings", "Input_Sample" });

		}

		if (sampleCollection != null) {

			for (TestSample sample : sampleCollection) {

				writer.setString("SampleID", sample.getSampleID());

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
					writer.setString("Range", resultRange);
					
					writer.setString("Haplogroup", currentResult.getHaplogroup().toString());
					
					writer.setString("Overall_Rank", String.format(Locale.ROOT, "%.4f", currentResult.getDistance()));

					if (extended) {

						ArrayList<Polymorphism> foundPolys = currentResult.getSearchResult().getDetailedResult()
								.getFoundPolys();

						ArrayList<Polymorphism> expectedPolys = currentResult.getSearchResult().getDetailedResult()
								.getExpectedPolys();

						Collections.sort(foundPolys);

						Collections.sort(expectedPolys);

						StringBuffer result = new StringBuffer();
						for (Polymorphism expected : expectedPolys) {
							if (!foundPolys.contains(expected)) {
								result.append(" " + expected.toString());
							}
						}

						writer.setString("Not_Found_Polys", result.toString().trim());

						result = new StringBuffer();
						for (Polymorphism currentPoly : foundPolys) {
							result.append(" " + currentPoly);
						}

						writer.setString("Found_Polys", result.toString().trim());

						ArrayList<Polymorphism> allChecked = currentResult.getSearchResult().getDetailedResult()
								.getRemainingPolysInSample();
						Collections.sort(allChecked);

						result = new StringBuffer();
						for (Polymorphism currentPoly : allChecked) {
							result.append(" " + currentPoly);
						}

						writer.setString("Remaining_Polys", result.toString().trim());

						ArrayList<Polymorphism> aac = currentResult.getSearchResult().getDetailedResult()
								.getRemainingPolysInSample();
						Collections.sort(aac);

						result = new StringBuffer();
						for (Polymorphism currentPoly : aac) {
							if (currentPoly.getAnnotation() != null)
								result.append(
										" " + currentPoly + " [" + currentPoly.getAnnotation().getAminoAcidChange()
												+ "| Codon " + currentPoly.getAnnotation().getCodon() + " | "
												+ currentPoly.getAnnotation().getGene() + " ]");
						}

						writer.setString("AAC_In_Remainings", result.toString().trim());

						ArrayList<Polymorphism> inputPolys = sample.getSample().getPolymorphisms();

						Collections.sort(inputPolys);

						result = new StringBuffer();
						for (Polymorphism input : inputPolys) {
							result.append(" " + input);
						}

						writer.setString("Input_Sample", result.toString().trim());

					}
					
					writer.next();

				}
			}
		}

		writer.close();

	}

	public static void calcLineage(Session session, String out) throws IOException {

		if (out.endsWith(".txt")) {
			out = out.substring(0, out.lastIndexOf("."));
		}

		HashSet<String> set = new HashSet<String>();
		String tmpNode = "";

		String graphViz = out + ".dot";

		FileWriter graphVizWriter = new FileWriter(graphViz);

		graphVizWriter.write("digraph {  label=\"Sample File: " + out + "\"\n");

		for (TestSample sample : session.getCurrentSampleFile().getTestSamples()) {

			TestSample currentSample = session.getCurrentSampleFile().getTestSample(sample.getSampleID());

			for (RankedResult currentResult : currentSample.getResults()) {

				ArrayList<SearchResultTreeNode> currentPath = currentResult.getSearchResult().getDetailedResult()
						.getPhyloTreePath();

				for (int i = 0; i < currentPath.size(); i++) {

					Haplogroup currentHg = currentPath.get(i).getHaplogroup();

					if (i == 0) {
						tmpNode = "\"" + currentHg + "\" -> ";
					}

					else {

						StringBuilder polys = new StringBuilder();

						if (currentPath.get(i).getExpectedPolys().size() == 0) {
							polys.append("-");
						} else {
							for (Polymorphism currentPoly : currentPath.get(i).getExpectedPolys()) {
								if (currentPath.get(i).getFoundPolys().contains(currentPoly)) {
									polys.append(currentPoly + " ");
								}
							}
						}

						String node = "\"" + currentHg + "\"[label=\"" + polys.toString().trim() + "\"];\n";

						if (!set.contains(tmpNode + node)) {
							graphVizWriter.write(tmpNode + node);
							set.add(tmpNode + node);
							tmpNode = "";
						}

						// Write currentHG also in new line for next iteration, but don't do this for
						// last element
						if (i != (currentPath.size() - 1)) {
							tmpNode = "\"" + currentHg + "\" -> ";
						}
					}

				}
			}

		}

		graphVizWriter.write("}");
		graphVizWriter.close();

	}

}
