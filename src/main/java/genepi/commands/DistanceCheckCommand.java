package genepi.commands;

import java.util.concurrent.Callable;

import core.Haplogroup;
import genepi.io.table.reader.CsvTableReader;
import genepi.io.table.writer.CsvTableWriter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import picocli.CommandLine.Option;

public class DistanceCheckCommand implements Callable<Integer> {

	@Option(names = { "--in", "--input" }, description = "input haplogroups", required = true)
	String in;

	@Option(names = { "--out", "--output" }, description = "output haplogroups including distance", required = true)
	String out;

	@Override
	public Integer call() {

		CsvTableReader reader = new CsvTableReader(in, ';');

		CsvTableWriter writer = new CsvTableWriter(out, ';');

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

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

}
