package genepi.haplogrep;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.junit.Test;

import core.Haplogroup;
import core.Polymorphism;
import core.Reference;
import core.Sample;
import core.SampleFile;
import core.TestSample;
import genepi.haplogrep.util.HgClassifier;
import importer.FastaImporter;
import importer.VcfImporter;
import importer.FastaImporter.References;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import qualityAssurance.rules.FixNomenclature;
import util.ExportUtils;

public class FastaTest {

	@Test
	public void rcrsTest() throws Exception {
		String file = "test-data/fasta/rCRS.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		Reference ref = impFasta.loadrCRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

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
		Reference ref = impFasta.loadRSRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

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
		Reference ref = impFasta.loadRSRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

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
		Reference ref = impFasta.loadrCRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		SampleFile newSampleFile = new SampleFile(samples, ref);

		HgClassifier classifier = new HgClassifier();

		classifier.run(newSampleFile, phylotree,ref, fluctrates, "kulczynski", 1, true);
		System.out.println(newSampleFile.getTestSamples().get(0).getResults().get(0).getHaplogroup());
		ArrayList<Polymorphism> polys = newSampleFile.getTestSamples().get(0).getSample().getPolymorphisms();
		assertEquals(true, polys.contains(new Polymorphism("315.1C", ref)));
		assertEquals(new Haplogroup("H100"),newSampleFile.getTestSamples().get(0).getTopResult().getHaplogroup());
	}
	
	@Test
	public void h100NomenclatureDeactivatedTest() throws Exception {

		String file = "test-data/h100/H100.fasta";
		String phylotree = "phylotree17.xml";
		String fluctrates = "weights17.txt";
		
		FastaImporter impFasta = new FastaImporter();
		Reference ref = impFasta.loadrCRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);
		SampleFile newSampleFile = new SampleFile(samples, ref);

		HgClassifier classifier = new HgClassifier();

		classifier.run(newSampleFile, phylotree, ref, fluctrates, "kulczynski", 1, false);
		ArrayList<Polymorphism> polys = newSampleFile.getTestSamples().get(0).getSample().getPolymorphisms();
		
		assertEquals(true, polys.contains(new Polymorphism("310.1C", ref)));
		assertEquals(new Haplogroup("H100"),newSampleFile.getTestSamples().get(0).getTopResult().getHaplogroup());
	}
	
	@Test
	public void parseSampleWithDeletions_No_NomenclatureFix() throws Exception {
		String file = "test-data/fasta/AY195749.fasta";
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		Reference ref = impFasta.loadrCRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

		String[] splits = samples.get(0).split("\t");

		boolean deletion = false;

		for (int i = 3; i < splits.length; i++) {
			if (splits[i].equals("514-515d")) {
				deletion = true;
			}
			actual.append(splits[i] + ",");
		}
		assertEquals(true, deletion);
	}
	
	@Test
	public void parseSampleWithDeletions_nomenclatureFix() throws Exception {
		boolean deletion = false;
		String file = "test-data/fasta/AY195749.fasta";
		String phylo = "phylotree17.xml";
		String weights = "weights17.txt";
		
		StringBuilder actual = new StringBuilder();
		FastaImporter impFasta = new FastaImporter();
		Reference ref = impFasta.loadrCRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);
		SampleFile s1 = new SampleFile(samples, ref);

	
		//fixNomenclature
		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree(phylo, weights, ref);
		s1.applyNomenclatureRules(phylotree, "rules.csv");

		int count=0;
		ArrayList<Polymorphism> polies = s1.getTestSamples().get(0).getSample().getPolymorphisms();
		
		for (Polymorphism poly : polies) { 		      
	           System.out.println(poly); 
	           if (poly.getPosition()==523 && poly.getMutation()==poly.getMutation().DEL) {
					deletion = true;
				}
	      }
			assertEquals(true, deletion);
	}
	

	@Test
	public void parseSampleWithInsertionsDeletions() throws Exception {
		String file = "test-data/fasta/InsertionTest.fasta";
		FastaImporter impFasta = new FastaImporter();
		Reference ref = impFasta.loadrCRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

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
		Reference ref = impFasta.loadrCRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

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
		Reference ref = impFasta.loadrCRS();
		ArrayList<String> samples = impFasta.load(new File(file), ref);

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
	
	
	/*@Test
	public void writeFasta() throws Exception {
		String file = "test-data/fasta/B6.fasta";
		FastaImporter impFasta = new FastaImporter();
		String out = "test-data/fasta/B6_";
		String phylo = "phylotree17.xml";
		String weights = "weights17.txt";
		
		ArrayList<String> samples = impFasta.load(new File(file), References.RCRS);

		SampleFile newSampleFile = new SampleFile(samples);
		
		HgClassifier classifier = new HgClassifier();

		classifier.run(newSampleFile, phylo, weights, "kulczynski", 1, false);
		
		ExportUtils.generateFasta(newSampleFile.getTestSamples(), out);
		
		String[] splits = samples.get(0).split("\t");
		HashSet<String> set = new HashSet<String>();

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		//assertEquals(true, set.contains("16182.1C"));
	}*/
	
	
	@Test
	public void writeFastaMSA() throws Exception {
		String file = "test-data/fasta/B5a1.fasta";
		FastaImporter impFasta = new FastaImporter();
		Reference ref= impFasta.loadrCRS();
		String out = "test-data/fasta/B5a1";
		String phylo = "phylotree17.xml";
		String weights = "weights17.txt";
		
		ArrayList<String> samples = impFasta.load(new File(file), ref);
		SampleFile newSampleFile = new SampleFile(samples, ref);
		
		HgClassifier classifier = new HgClassifier();

		classifier.run(newSampleFile, phylo, ref, weights, "kulczynski", 1, false);
		
		//WRITE MultipleAlignment File
		ExportUtils.generateFastaMSA(newSampleFile.getTestSamples(), out, ref);
		
		//Import written MSA file:
		String readIn = "test-data/fasta/B5a1_haplogrep2_MSA.fasta";

		try  
		{  
		FileReader fr=new FileReader(new File(readIn));   
		BufferedReader br=new BufferedReader(fr); 
		StringBuffer sb=new StringBuffer();   
		String line;  
		while((line=br.readLine())!=null)  
		{  
		sb.append(line + "\n");      
		}  
		fr.close();    
		System.out.println("Contents of File: ");  
		System.out.println(sb.toString());   //returns a string that textually represents the object  
		}  
		catch(IOException e)  
		{  
		e.printStackTrace();  
		} 		
	}
	
/*	@Test
	public void nextStrain44Test() throws Exception {

		String file = "test-data/sarscov2/sarscov2_example_sequences_nextstrain_44.fasta";
		String phylotree = "phylotree01_sarscov2.xml";
		String fluctrates = "weights01_sarscov2.txt";
		
		FastaImporter impFasta = new FastaImporter();
		Reference ref = impFasta.loadSARSCOV2();
		ArrayList<String> samples = impFasta.load(new File(file), ref);
		
		SampleFile newSampleFile = new SampleFile(samples, ref);

		HgClassifier classifier = new HgClassifier();

		classifier.run(newSampleFile, phylotree, ref, fluctrates, "kimura", 1, true);
		
		for (int i=0; i < newSampleFile.getTestSamples().size(); i++) {
			System.out.println(newSampleFile.getTestSamples().get(i).getSampleID() + "\t " + newSampleFile.getTestSamples().get(i).getDetectedHaplogroup());
		}
	//	System.out.println(newSampleFile.getTestSamples().get(0).getResults().get(0).getHaplogroup());
		ArrayList<Polymorphism> polys = newSampleFile.getTestSamples().get(0).getSample().getPolymorphisms();
		assertEquals(true, polys.contains(new Polymorphism("315.1C", ref)));
		assertEquals(new Haplogroup("H100"),newSampleFile.getTestSamples().get(0).getTopResult().getHaplogroup());
	}*/
	

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
