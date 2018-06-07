# HaploGrep

We provide a fast and free [haplogroup classification service](https://haplogrep.uibk.ac.at/). You can upload your mtDNA profiles (VCF or HSD) and receive the mitochondrial haplogroup in return. So far, HaploGrep has been cited over 400 times (June 2018). 

## Commandline Version
    mkdir haplogrep-cmd
    cd haplogrep-cmd
    wget https://github.com/seppinho/haplogrep-cmd/releases/download/v2.1.6/haplogrep-2.1.6.jar
    java -jar haplogrep-2.1.6.jar --in <input> --format vcf/hsd --out haplogroups.txt
   
## Additional Parameters      
* If your variants are from genotyping arrays, please addd the `--chip` parameter (Range will only cover included SNPs from the array). Default is off.
* For adding additional output columns (e.g. found or remaining polymorphisms) please add the `--extend-report` flag. Default is off.
* To change the metric to Hamming or Jaccard add the `--metric` parameter. Default ist Kulczynski.
* The used Phylotree version can be changed using the `--phylotree` parameter. Default is 17.

## Heteroplasmies
Heteroplasmies are often stored as heterozygous genotypes (0/1). If a HF field (= Heteroplasmy Frequency of variant allele; introduced by MToolBox) is specified in the VCF header, we add variants with a HF > 0.96 to the input profile.

Please have a look at [mtDNA-Server](http://mtdna-server.uibk.ac.at) to check for heteroplasmies and contamination in your NGS data.   

## Google User Group
We would love to hear your input. If you have any questions regarding Haplogrep, please join our [Google User Group](https://groups.google.com/forum/#!forum/haplogrep).

## Blog
Check out our [blog](http://haplogrep.uibk.ac.at/blog/) regarding mtDNA topics.

   
## Cite use
If you use Haplogrep, please cite or two papers:

https://onlinelibrary.wiley.com/doi/full/10.1002/humu.21382

http://nar.oxfordjournals.org/content/early/2016/04/15/nar.gkw233

## Contact
Division of Genetic Epidemiology

Medical University of Innsbruck 

[Sebastian Schoenherr](mailto:sebastian.schoenherr@i-med.ac.at) and [Hansi Weissensteiner](mailto:hansi.weissensteiner@i-med.ac.at) 
