
We provide a fast and free [haplogroup classification service](https://haplogrep.uibk.ac.at/). You can upload your mtDNA profiles ([vcf](http://www.internationalgenome.org/wiki/Analysis/vcf4.0/) or [hsd](https://raw.githubusercontent.com/seppinho/haplogrep-cmd/master/haplogrep/test-data/h100.hsd) format) and receive mitochondrial haplogroups in return. So far, HaploGrep and the updated HaploGrep 2 have been cited over 400 times (Google Scholar - June 2018). Please join our [HaploGrep Google User Group](https://groups.google.com/forum/#!forum/haplogrep) for future updates and ongoing discussions. 

## Command-line Version for local usage

Download and execute the [latest release](https://github.com/seppinho/haplogrep-cmd/releases/download/v2.1.7/haplogrep-2.1.8.jar) (v2.1.8). 
 
      java -jar haplogrep-2.1.8.jar --in <input> --format vcf/hsd --out haplogroups.txt
   
HaploGrep requires Java 8 and works for Windows, Linux and Mac operating systems.
 
## Additional Parameters      
* For adding additional output columns (e.g. found or remaining polymorphisms) please add the `--extend-report` flag (Default: off).
* To change the metric to Hamming or Jaccard add the `--metric` parameter (Default: kulczynski).
* The used Phylotree version can be changed using the `--phylotree` parameter (Default: 17).
* If your variants are from genotyping arrays, please addd the `--chip` parameter (VCF only!). The range will then be limited to array SNPs only (Default: off).
* To output the complete path from rCRS root to your input sample use the `--lineage` parameter. (Default: off). We provide a textual format (`*.lineage.txt`) and a [Graphviz](http://www.graphviz.org/documentation/) format. You can upload the HaploGrep `*.graphviz.txt` file [here](https://graphs.grevian.org/graph).

## File Formats
HaploGrep's default input format is [VCF](http://www.internationalgenome.org/wiki/Analysis/vcf4.0/). You can also specify your samples in the hsd format, a simple tab-delimited file format consisting of 4 columns (ID, Range, Haplogroup and Polymorphisms). For readability, the polymorphisms are also tab-delimited. An example can be found [here](https://raw.githubusercontent.com/seppinho/haplogrep-cmd/master/haplogrep/test-data/h100.hsd). 

## Heteroplasmies (VCF only)
Heteroplasmies are often stored as heterozygous genotypes (0/1). If a HF field (= Heteroplasmy Frequency of variant allele; introduced by MToolBox) is specified in the VCF header, we add variants with a HF > 0.96 to the input profile.

Please have a look at [mtDNA-Server](http://mtdna-server.uibk.ac.at) to check for heteroplasmies and contamination in your NGS data.   

## Blog
Check out our [blog](http://haplogrep.uibk.ac.at/blog/) regarding mtDNA topics.

## Cite use
If you use HaploGrep, please cite our two HaploGrep papers ([HaploGrep](https://onlinelibrary.wiley.com/doi/abs/10.1002/humu.21382), [HaploGrep2](http://nar.oxfordjournals.org/content/early/2016/04/15/nar.gkw233)) in combination with [Phylotree 17](https://www.sciencedirect.com/science/article/pii/S1875176815302432).

## Contact
[Sebastian Schoenherr](mailto:sebastian.schoenherr@i-med.ac.at) ([@seppinho](https://twitter.com/seppinho)) and [Hansi Weissensteiner](mailto:hansi.weissensteiner@i-med.ac.at) ([@haansi](https://twitter.com/whansi)); Division of Genetic Epidemiology, Medical University of Innsbruck;
