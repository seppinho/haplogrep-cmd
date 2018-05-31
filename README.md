# Haplogrep

HaploGrep determines the haplogroup of your mtDNA profiles fully automatically.  It supports standardized input (e.g. VCF)/ output formats (e.g. VCF, fasta, multiple alignment) and provides several tools for an intensive quality control. HaploGrep is based on Phylotree, a periodically updated classification tree. 

## Run Haplogrep
Please use our [web service](https://haplogrep.uibk.ac.at/) to classify your profiles. 

## Run Haplogrep locally
     mkdir haplogrep-cmd
     cd haplogrep-cmd
     wget https://github.com/seppinho/haplogrep-cmd/releases/download/v2.1.3/haplogrep-2.1.3.jar      
     java -jar haplogrep-2.1.3.jar  haplogrep --in test-data/h100.hsd --ext 0 --format hsd --phylotree 17 --out final.txt --metric 1
     
## Google User Group
We would love to hear your input. If you have any questions regarding Haplogrep, please join our [Google User Group](https://groups.google.com/forum/#!forum/haplogrep).

## Blog
Check out our [blog](http://haplogrep.uibk.ac.at/blog/) regarding mtDNA topics.

## Contribute
    git clone https://github.com/seppinho/haplogrep-cmd
    cd haplogrep; mvn install 
    java -jar target/haplogrep-2.1.3.jar haplogrep --in test-data/h100.hsd --format hsd --phylotree 17 --out final.txt --metric 1
    
## Cite use
If you use Haplogrep, please cite or two papers:

https://onlinelibrary.wiley.com/doi/full/10.1002/humu.21382

http://nar.oxfordjournals.org/content/early/2016/04/15/nar.gkw233

## Contact
Division of Genetic Epidemiology
Medical University of Innsbruck 

[Sebastian Schoenherr](mailto:sebastian.schoenherr@i-med.ac.at) and [Hansi Weissensteiner](mailto:hansi.weissensteiner@i-med.ac.at) 
