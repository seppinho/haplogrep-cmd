# Haplogrep
HaploGrep provides a fully automated way to determine the haplogroup of mtDNA profiles.

## Run Haplogrep
Please use our [web service](https://haplogrep.uibk.ac.at/) to classify your profiles. 

## Install Haplogrep locally
     mkdir haplogrep-cmd
     cd haplogrep-cmd
     wget https://github.com/seppinho/haplogrep-cmd/releases/download/v2.1.3/haplogrep-2.1.3.jar      

## Run Haplogrep locally     
       java -jar haplogrep-2.1.3.jar --in test-data/h100.hsd --export 1 --format hsd --phylotree 17 --out final.txt --metric 1

For VCF, only SNPs and therefore no INDELS are added to the profile. 

## Google User Group
We would love to hear your input. If you have any questions regarding Haplogrep, please join our [Google User Group](https://groups.google.com/forum/#!forum/haplogrep).

## Blog
Check out our [blog](http://haplogrep.uibk.ac.at/blog/) regarding mtDNA topics.

## Contribute
    git clone https://github.com/seppinho/haplogrep-cmd
    cd haplogrep; mvn install 
    
## Cite use
If you use Haplogrep, please cite or two papers:

https://onlinelibrary.wiley.com/doi/full/10.1002/humu.21382

http://nar.oxfordjournals.org/content/early/2016/04/15/nar.gkw233

## Contact
Division of Genetic Epidemiology

Medical University of Innsbruck 

[Sebastian Schoenherr](mailto:sebastian.schoenherr@i-med.ac.at) and [Hansi Weissensteiner](mailto:hansi.weissensteiner@i-med.ac.at) 
