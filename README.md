# haplogrep-cmd
This repository includes a main class to call haplogrep from Java. It currently includes the haplogrep-core as a jar 

## Start from Executable
     mkdir haplogrep-cmd
     cd haplogrep-cmd
     wget https://github.com/seppinho/haplogrep-cmd/releases/download/v2.1.3/haplogrep-2.1.3.jar      
     java -jar haplogrep-2.1.3.jar  haplogrep --in test-data/h100.hsd --format hsd --phylotree 17 --out final.txt --metric 1
     
## Build from Source
    git clone https://github.com/seppinho/haplogrep-cmd
    cd haplogrep; mvn install 
    java -jar target/haplogrep-2.1.3.jar haplogrep --in test-data/h100.hsd --format hsd --phylotree 17 --out final.txt --metric 1
