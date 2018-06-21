package genepi.haplogrep.util;

import java.io.IOException;

import org.jdom.JDOMException;

import exceptions.parse.sample.InvalidRangeException;
import genepi.haplogrep.Session;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import search.ranking.HammingRanking;
import search.ranking.JaccardRanking;
import search.ranking.KulczynskiRanking;
import search.ranking.RankingMethod;

public class HgClassifier {
	
	public static void run(Session session, String phyloTree, String fluctrates, String metric)
			throws JDOMException, IOException, InvalidRangeException {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree(phyloTree, fluctrates);

		RankingMethod newRanker = null;

		switch (metric) {

		case "kulczynski":
			newRanker = new KulczynskiRanking(1);
			break;

		case "hamming":
			newRanker = new HammingRanking(1);
			break;

		case "jaccard":
			newRanker = new JaccardRanking(1);
			break;

		default:
			newRanker = new KulczynskiRanking(1);

		}

		session.getCurrentSampleFile().updateClassificationResults(phylotree, newRanker);

	}

}
