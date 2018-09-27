
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

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
	public void parseSampleWithDeletions() throws Exception {
		String file = "test-data/fasta/AY195749.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		ArrayList<String> samples = impFasta.load(new File(file), References.RSRS);

		String[] splits = samples.get(0).split("\t");

		boolean deletion = false;

		for (int i = 3; i < splits.length; i++) {
			if (splits[i].equals("523d")) {
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
		assertEquals(true, set.contains("309.1C"));
		assertEquals(true, set.contains("309.2C"));
		assertEquals(true, set.contains("309.3T"));
		assertEquals(true, set.contains("3106d"));
		assertEquals(true, set.contains("8270d"));
		assertEquals(true, set.contains("8271d"));
		assertEquals(true, set.contains("8272d"));
		assertEquals(true, set.contains("8273d"));
		assertEquals(true, set.contains("8274d"));
		assertEquals(true, set.contains("8275d"));
		assertEquals(true, set.contains("8276d"));
		assertEquals(true, set.contains("8277d"));

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
		assertEquals(true, set.contains("309.1C"));
		assertEquals(true, set.contains("309.2C"));
		assertEquals(true, set.contains("309.3T"));
		assertEquals(true, set.contains("3106d"));
		assertEquals(true, set.contains("8270d"));
		assertEquals(true, set.contains("8271d"));
		assertEquals(true, set.contains("8272d"));
		assertEquals(true, set.contains("8273d"));
		assertEquals(true, set.contains("8274d"));
		assertEquals(true, set.contains("8275d"));
		assertEquals(true, set.contains("8276d"));
		assertEquals(true, set.contains("8277d"));

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
		assertEquals(true, set.contains("309.1C"));
		assertEquals(true, set.contains("309.2C"));
		assertEquals(true, set.contains("309.3T"));
		assertEquals(true, set.contains("3106d"));
		assertEquals(true, set.contains("8270d"));
		assertEquals(true, set.contains("8271d"));
		assertEquals(true, set.contains("8272d"));
		assertEquals(true, set.contains("8273d"));
		assertEquals(true, set.contains("8274d"));
		assertEquals(true, set.contains("8275d"));
		assertEquals(true, set.contains("8276d"));
		assertEquals(true, set.contains("8277d"));

	}
	
}
