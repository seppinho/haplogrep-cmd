1. Download fasta from GenBank - based on Accessions.txt - https://www.ncbi.nlm.nih.gov/sites/batchentrez
2. Run haplogrep on the sequences - generate extended report:
``` 
java -jar ~/tools/haplogrep/haplogrep-2.1.19.jar -in sequences.fasta --format fasta --out phylotreeAccess.ext.txt --extend-report
```
3. Compare *Not_Found_Polys* with *Remaining_Polys*
4. Add replacement rule
