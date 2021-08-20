package genepi.haplogrep.util;

import java.io.IOException;

import org.jdom.JDOMException;

import core.SampleFile;
import exceptions.parse.sample.InvalidRangeException;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import search.ranking.HammingRanking;
import search.ranking.JaccardRanking;
import search.ranking.KulczynskiRanking;
import search.ranking.RankingMethod;

public class HgClassifier {
	
	public void run(SampleFile newSampleFile, String phyloTree, String fluctrates, String format)
			throws InvalidRangeException, JDOMException, IOException {
		run(newSampleFile, phyloTree, fluctrates, "kulczynski", 1, false, format);
	}

	public void run(SampleFile newSampleFile, String phyloTree, String fluctrates, String metric, int amountResults, boolean skipRules, String format) throws JDOMException, IOException, InvalidRangeException {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree(phyloTree, fluctrates);

		RankingMethod newRanker = null;

		switch (metric) {

		case "kulczynski":
			newRanker = new KulczynskiRanking(amountResults);
			break;

		case "hamming":
			newRanker = new HammingRanking(amountResults);
			break;

		case "jaccard":
			newRanker = new JaccardRanking(amountResults);
			break;

		default:
			newRanker = new KulczynskiRanking(amountResults);

		}

		if (format.equals("fasta") && !skipRules) {
			newSampleFile.applyNomenclatureRules(phylotree, "rules.csv");

		}

		newSampleFile.updateClassificationResults(phylotree, newRanker);

	}

}
