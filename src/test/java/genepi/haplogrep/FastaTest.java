package genepi.haplogrep;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

import core.Haplogroup;
import core.Polymorphism;
import core.SampleFile;
import genepi.haplogrep.util.HgClassifier;
import importer.FastaImporter;
import importer.FastaImporter.References;

public class FastaTest {

	@Test
	public void rcrsTest() throws Exception {
		String file = "test-data/fasta/rCRS.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RCRS);

		String[] splits = samples.get(0).split("\t");
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}

		assertEquals(0, actual.length());
	}

	@Test
	public void rsrsTest() throws Exception {
		String file = "test-data/fasta/rsrs.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RSRS);

		String[] splits = samples.get(0).split("\t");
		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}

		assertEquals(0, actual.length());

	}

	@Test
	public void rCrsWithRsrsReferenceTest() throws Exception {
		String file = "test-data/fasta/rCRS.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RSRS);

		String[] splits = samples.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			actual.append(splits[i] + ",");
		}

		// exactly 52 differences between rsrs and rCRS
		assertEquals(52, (splits.length) - 3);

	}

	@Test
	public void h100NomenclatureActivatedTest() throws Exception {

		String file = "test-data/h100/H100.fasta";
		String phylotree = "phylotree17.xml";
		String fluctrates = "weights17.txt";
		
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RCRS);
		SampleFile newSampleFile = new SampleFile(samples);

		HgClassifier classifier = new HgClassifier();

		classifier.run(newSampleFile, phylotree, fluctrates, "fasta");
		ArrayList<Polymorphism> polys = newSampleFile.getTestSamples().get(0).getSample().getPolymorphisms();
		assertEquals(true, polys.contains(new Polymorphism("315.1C")));
		assertEquals(new Haplogroup("H100"),newSampleFile.getTestSamples().get(0).getTopResult().getHaplogroup());

	}
	
	@Test
	public void h100NomenclatureDeactivatedTest() throws Exception {

		String file = "test-data/h100/H100.fasta";
		String phylotree = "phylotree17.xml";
		String fluctrates = "weights17.txt";
		
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RCRS);
		SampleFile newSampleFile = new SampleFile(samples);

		HgClassifier classifier = new HgClassifier();

		classifier.run(newSampleFile, phylotree, fluctrates, "fasta");
		ArrayList<Polymorphism> polys = newSampleFile.getTestSamples().get(0).getSample().getPolymorphisms();
		
		System.out.println(polys);
		assertEquals(true, polys.contains(new Polymorphism("315.1C")));
		assertEquals(new Haplogroup("H100"),newSampleFile.getTestSamples().get(0).getTopResult().getHaplogroup());
	}
	
	@Test
	public void parseSampleWithDeletions() throws Exception {
		String file = "test-data/fasta/AY195749.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RSRS);

		String[] splits = samples.get(0).split("\t");

		boolean deletion = false;

		for (int i = 3; i < splits.length; i++) {
			if (splits[i].equals("523-524d")) {
				deletion = true;
			}
			actual.append(splits[i] + ",");
		}

		assertEquals(true, deletion);

	}

	@Test
	public void parseSampleWithInsertionsDeletions() throws Exception {
		String file = "test-data/fasta/InsertionTest.fasta";
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RCRS);

		String[] splits = samples.get(0).split("\t");
		HashSet<String> set = new HashSet<String>();

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		assertEquals(true, set.contains("16182.1C"));
		assertEquals(true, set.contains("309.1CCT"));
		assertEquals(true, set.contains("3106-3106d"));
		assertEquals(true, set.contains("8270-8277d"));

	}

	// copied first two lines of fasta (including 309.1C etc to end of line)
	@Test
	public void parseSampleWithInsertionsDeletionsShuffle() throws Exception {
		String file = "test-data/fasta/InsertionTest2.fasta";
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RCRS);

		String[] splits = samples.get(0).split("\t");
		HashSet<String> set = new HashSet<String>();

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		assertEquals(true, set.contains("16182.1C"));
		assertEquals(true, set.contains("309.1CCT"));
		assertEquals(true, set.contains("3106-3106d"));
		assertEquals(true, set.contains("8270-8277d"));

	}

	// random shuffle
	@Test
	public void parseSampleWithInsertionsDeletionsShuffle2() throws Exception {
		String file = "test-data/fasta/InsertionTest3.fasta";
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RCRS);

		String[] splits = samples.get(0).split("\t");
		HashSet<String> set = new HashSet<String>();

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		assertEquals(true, set.contains("16182.1C"));
		assertEquals(true, set.contains("309.1CCT"));
		assertEquals(true, set.contains("3106-3106d"));
		assertEquals(true, set.contains("8270-8277d"));
	}

/*	@Test
	public void parsePhylotree17() throws Exception {
		String file = "test-data/fasta/Phylotree17hgs.zip";
		String fileTemp = "test-data/fasta/temp.fasta";

		// Phylotree phylotree =
		// PhylotreeManager.getInstance().getPhylotree("data/phylotree/phylotree17.xml","data/weights/weights17.txt");
		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("data/phylotree/phylotree17_rsrs.xml",
				"data/weights/weights17_rsrs.txt");

		FileInputStream fileInputStream = new FileInputStream(file);
		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
		ZipInputStream zin = new ZipInputStream(bufferedInputStream);
		ZipEntry ze = null;
		Map<String, String> differences = new HashMap<String, String>();
		while ((ze = zin.getNextEntry()) != null) {
			String expectedHG = ze.getName();
			OutputStream out = new FileOutputStream(fileTemp);
			byte[] buffer = new byte[20000];
			int len;
			while ((len = zin.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.close();
			FastaImporter impFasta = new FastaImporter();

			ArrayList<String> samples = impFasta.load(new File(fileTemp), References.RSRS);

			List<RankedResult> result = phylotree.search(TestSample.parse(samples.get(0)), new HammingRanking());
			String expected = samples.get(0).split("\t")[0];
			String resulting = result.get(0).getHaplogroup().toString();

			if (!expected.equals(resulting)) {
				differences.put(expected, resulting);
			}

		}
		/// System.out.println(differences.size());
		for (Map.Entry<String, String> entry : differences.entrySet()) {
			// System.out.println("Expected = " + entry.getKey() + ", Resulting = " +
			// entry.getValue());
		}
		zin.close();

	}*/
}
