package genepi.haplogrep.vcf;

import java.io.File;
import java.util.ArrayList;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeType;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

public class VcfImporter {

	public ArrayList<String> vcfToHsd(File file, boolean chip) throws Exception {

		final VCFFileReader vcfReader = new VCFFileReader(file, false);

		VCFHeader vcfHeader = vcfReader.getFileHeader();

		StringBuilder range = new StringBuilder();

		if (chip) {

			for (VariantContext vc : vcfReader) {

				range.append(vc.getStart() + ";");

			}

			vcfReader.close();

		} else {

			range.append("1-16569");

		}

		ArrayList<StringBuilder> profiles = new ArrayList<StringBuilder>();

		for (String sample : vcfHeader.getSampleNamesInOrder()) {

			StringBuilder profile = new StringBuilder();

			profiles.add(profile.append(sample + "\t" + range + "\t" + "?"));

		}

		for (final VariantContext vc : vcfReader) {

			if (vc.getStart() > 16569) {

				System.out.println("Error! Position " + vc.getStart()
						+ " outside the range. Please double check if VCF includes variants mapped to rCRS only.");
				System.exit(-1);

			}

			String reference = vc.getReference().getBaseString();

			int index = 0;

			for (String sample : vcfHeader.getSampleNamesInOrder()) {

				Genotype sampleGenotype = vc.getGenotype(sample);

				String genotype = sampleGenotype.getGenotypeString(false);

				if (sampleGenotype.getType() == GenotypeType.HOM_VAR) {

					// SNPs
					if (genotype.length() == reference.length()) {

						if (genotype.length() == 1) {

							profiles.get(index).append("\t");

							profiles.get(index).append(vc.getStart() + "" + genotype);

						} else {

							// check for SNPS with complex genotypes (REF: ACA, GT: ACT --> SNP is T)
							for (int i = 0; i < genotype.length(); i++) {

								if (reference.charAt(i) != genotype.charAt(i)) {

									profiles.get(index).append("\t");

									profiles.get(index).append((vc.getStart() + i) + "" + genotype.charAt(i));

									break;

								}

							}

						}
					}

					// DELETIONS
					else if (reference.length() > genotype.length()) {

						profiles.get(index).append("\t");

						// one position deletion
						if ((reference.length() - genotype.length()) == 1) {
							profiles.get(index).append(vc.getStart() + genotype.length() + "d");
						} else {
							profiles.get(index).append((vc.getStart() + genotype.length()) + "-"
									+ (vc.getStart() + reference.length() - 1) + "d");
						}
					}

					// INSERTIONS
					else if (reference.length() < genotype.length()) {

						profiles.get(index).append("\t");

						if (reference.length() == 1) { 	
							profiles.get(index).append(vc.getStart() + "." + 1
									+ genotype.substring(reference.length(), (genotype.length())));
						} else {
							profiles.get(index).append(vc.getStart() + "." + 1
									+ genotype.substring(0, (genotype.length() - reference.length())));
						}
					}

				}

				// Heteroplasmies
				if (sampleGenotype.getType() == GenotypeType.HET && sampleGenotype.hasAnyAttribute("HF")) {

					String hetFrequency = (String) vc.getGenotype(sample).getAnyAttribute("HF");

					if (Double.valueOf(hetFrequency) >= 0.96) {

						if (genotype.length() == reference.length()) {

							if (genotype.length() == 1) {

								profiles.get(index).append("\t");

								profiles.get(index).append(vc.getStart() + "" + genotype);

							} else {

								for (int i = 0; i < genotype.length(); i++) {

									if (reference.charAt(i) != genotype.charAt(i)) {

										profiles.get(index).append("\t");

										profiles.get(index).append((vc.getStart() + i) + "" + genotype.charAt(i));

										break;
									}

								}

							}

						}
					}
				}
				index++;
			} // end samples

		} // end variants

		ArrayList<String> result = new ArrayList<>();

		for (StringBuilder profile : profiles) {

			String profileString = profile.toString();

			if (profileString.split("\t").length > 3) {

				result.add(profileString);

			} else {
				System.out.println("Info: Sample " + profileString.substring(0, profile.indexOf("\t"))
						+ " excluded. No variants detected. Please double check if data has been aligned to rCRS reference");
			}

		}

		vcfReader.close();

		return result;

	}

}
