package genepi.distance;

import genepi.base.Tool;
import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.writer.CsvTableWriter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import java.io.IOException;
import core.Haplogroup;

public class DistanceCheck extends Tool {

	public static String VERSION = "v0.0.2";

	public DistanceCheck(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {
		addParameter("in", "input haplogroup");
		addParameter("out", "out haplogroup");
	}

	@Override
	public void init() {

		System.out.println("Welcome to Haplogrep Distance Check " + VERSION);
		System.out.println("Instiute of Genetic Epidemiology, Medical University of Innsbruck");
		System.out.println("");

	}

	@Override
	public int run() {

		String in = (String) getValue("in");
		String out = (String) getValue("out");

		CsvTableReader reader = new CsvTableReader(in, ';');

		CsvTableWriter writer = new CsvTableWriter(out, ';');

		int count = 0;

		String[] columns = new String[] { "hg1", "hg2", "distance" };
		writer.setColumns(columns);

		while (reader.next()) {

			count++;

			Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

			Haplogroup hgMajor = new Haplogroup(reader.getString("hg1"));

			Haplogroup hgMinor = new Haplogroup(reader.getString("hg2"));

			writer.setString("hg1", reader.getString("hg1"));
			writer.setString("hg2", reader.getString("hg2"));

			int distance = 0;
			try {
				distance = phylotree.getDistanceBetweenHaplogroups(hgMajor, hgMinor);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.err.println("Line " + count + " includes at least one unknown haplogroup: " + hgMajor + " / "
						+ hgMinor + ".");
				System.exit(-1);
			}
			writer.setInteger("distance", distance);
			writer.next();

		}

		System.out.println("Distance calculation finished. File written to " + out + " .");
		reader.close();
		writer.close();

		return 0;
	}

	public static void main(String[] args) throws IOException {

		DistanceCheck haplogrep = new DistanceCheck(args);

		haplogrep = new DistanceCheck(
				new String[] { "--in", "/home/seb/Desktop/test.txt", "--out", "/home/seb/Desktop/out.txt" });

		haplogrep.start();

	}

}
