package genepi.distance;

import genepi.base.Tool;
import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.writer.CsvTableWriter;
import importer.FastaImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import java.io.IOException;
import core.Haplogroup;
import core.Reference;

public class DistanceCheck extends Tool {

	public static String VERSION = "v0.0.3";
	Reference reference;
	
	public DistanceCheck(String[] args) {
		super(args);
	}

	@Override
	public void createParameters() {
		addParameter("in", "input haplogroups");
		addParameter("out", "output haplogroups including distance");
	}

	@Override
	public void init() {

		System.out.println("Welcome to Haplogrep Distance Check " + VERSION);
		System.out.println("");

	}

	@Override
	public int run() {

		String in = (String) getValue("in");
		String out = (String) getValue("out");

		CsvTableReader reader = new CsvTableReader(in, ';');

		CsvTableWriter writer = new CsvTableWriter(out, ';');

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt", reference);

		int count = 0;

		String[] columns = new String[] { "hg1", "hg2", "distance" };
		writer.setColumns(columns);

		while (reader.next()) {
			count++;

			if (reader.getRow().length < 2) {
				continue;
			}
			
			String hg1 = reader.getString("hg1");
			String hg2 = reader.getString("hg2");

			Haplogroup hgMajor = new Haplogroup(hg1);
			Haplogroup hgMinor = new Haplogroup(hg2);

			writer.setString("hg1", hg1);
			writer.setString("hg2", hg2);

			int distance = 0;
			try {
				distance = phylotree.getDistanceBetweenHaplogroups(hgMajor, hgMinor);
			} catch (Exception e) {
				System.err.println("Line " + count + " includes at least one unknown haplogroup: " + hgMajor + " / "
						+ hgMinor + ".");
				System.exit(-1);
			}
			writer.setInteger("distance", distance);
			writer.next();

		}

		System.out.println("Done.");
		System.out.println("File written to " + out + ".");
		reader.close();
		writer.close();

		return 0;
	}

	public static void main(String[] args) throws IOException {

		DistanceCheck haplogrep = new DistanceCheck(args);
		FastaImporter importFasta = new FastaImporter();
		haplogrep.reference = importFasta.loadrCRS(); //currently for RCRS - could be adapted to other references as well
		haplogrep = new DistanceCheck(
				new String[] { "--in", "/home/seb/Desktop/test.txt", "--out", "/home/seb/Desktop/out.txt" });

		haplogrep.start();

	}

}
