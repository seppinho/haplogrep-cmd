# haplogrep-cmd
This repository includes a main class to call haplogrep from Java. It currently includes the haplogrep-core as a jar 

## Start from Executable
        
## Build from Source
    git clone https://github.com/seppinho/haplogrep-cmd
    cd haplogrep; mvn install 
    java -jar target/toolbox-1.0.1.jar haplogrep --in test-data/h100.hsd --format hsd --phylotree 17 --out final.txt --metric 1
