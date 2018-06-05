# HaploGrep
HaploGrep provides a fully automated way to determine the haplogroup of mtDNA profiles.

## Run HaploGrep
Please use our [web service](https://haplogrep.uibk.ac.at/) to classify your profiles. 

## Getting started
    mkdir haplogrep-cmd
    cd haplogrep-cmd
    wget https://github.com/seppinho/haplogrep-cmd/releases/download/v2.1.4/haplogrep-2.1.4.jar
    java -jar haplogrep-2.1.4.jar --in <input.vcf> --format vcf/hsd --out haplogroups.txt

    
## Additonal Parameters      
For adding additional output columns (found/remaining polymorphisms) please add the `--extend-report` flag. To change the default metric (Kulczynski) to Hamming or Jaccard add the `--metric` parameter. The used Phylotree version can be changed using the `--phylotree` parameter.

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
