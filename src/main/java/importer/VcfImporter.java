package importer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
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

				Genotype genotype = vc.getGenotype(sample);

				String genotypeString = genotype.getGenotypeString(true);

				if (genotype.getType() == GenotypeType.HOM_VAR) {

					// diploid to haploid
					if (genotype.getPloidy() > 1) {

						Allele altAllele = Allele.create(genotype.getAlleles().get(0), false);

						final List<Allele> alleles = new ArrayList<Allele>();

						alleles.add(altAllele);

						genotype = new GenotypeBuilder(genotype).alleles(alleles).make();

					}

					genotypeString = genotype.getGenotypeString(true);

					// SNPs
					if (genotypeString.length() == reference.length()) {

						if (genotypeString.length() == 1) {

							profiles.get(index).append("\t");

							profiles.get(index).append(vc.getStart() + "" + genotypeString);

						} else {

							// check for SNPS with complex genotypes (REF: ACA, GT: ACT --> SNP is T)
							for (int i = 0; i < genotypeString.length(); i++) {

								if (reference.charAt(i) != genotypeString.charAt(i)) {

									profiles.get(index).append("\t");

									profiles.get(index).append((vc.getStart() + i) + "" + genotypeString.charAt(i));

									break;

								}

							}

						}
					}

					// DELETIONS
					else if (reference.length() > genotypeString.length()) {

						profiles.get(index).append("\t");

						// one position deletion
						if ((reference.length() - genotypeString.length()) == 1) {
							profiles.get(index).append(vc.getStart() + genotypeString.length() + "d");
						} else {
							profiles.get(index).append((vc.getStart() + genotypeString.length()) + "-"
									+ (vc.getStart() + reference.length() - 1) + "d");
						}
					}

					// INSERTIONS
					else if (reference.length() < genotypeString.length()) {

						profiles.get(index).append("\t");

						if (reference.length() == 1) {
							profiles.get(index).append(vc.getStart() + "." + 1
									+ genotypeString.substring(reference.length(), (genotypeString.length())));
						} else {
							profiles.get(index).append(vc.getStart() + "." + 1
									+ genotypeString.substring(0, (genotypeString.length() - reference.length())));
						}
					}

				}

				// Heteroplasmies
				if (genotype.getType() == GenotypeType.HET && genotype.hasAnyAttribute("HF")) {

					String hetFrequency = (String) vc.getGenotype(sample).getAnyAttribute("HF");

					if (Double.valueOf(hetFrequency) >= 0.96) {

						if (genotypeString.length() == reference.length()) {

							if (genotypeString.length() == 1) {

								profiles.get(index).append("\t");

								profiles.get(index).append(vc.getStart() + "" + genotypeString);

							} else {

								for (int i = 0; i < genotypeString.length(); i++) {

									if (reference.charAt(i) != genotypeString.charAt(i)) {

										profiles.get(index).append("\t");

										profiles.get(index).append((vc.getStart() + i) + "" + genotypeString.charAt(i));

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
						+ " excluded. No variants detected, assuming same haplogroup as the reference (rCRS: H2a2a1");
			}

		}

		vcfReader.close();

		return result;

	}

}
