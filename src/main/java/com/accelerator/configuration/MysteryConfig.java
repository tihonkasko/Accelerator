package com.accelerator.configuration;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MysteryConfig
{
    public List<String> getAminoAcids(String charAminoAcids)
    {
        charAminoAcids = charAminoAcids.toUpperCase().replaceAll("[\\r\\n\\s]", "");
        boolean readingStarted = false;
        int startCodonNumber = 0;
        int codonNumber = 0;
        String startCodon = "";
        String beforeStarted = "";
        List<String> aminoAcids = new ArrayList<>();
        List<Character> characters = convertStringToCharList(charAminoAcids);

        return analiseSequence(readingStarted, startCodonNumber, codonNumber, startCodon,
            beforeStarted, aminoAcids, characters, charAminoAcids);
    }

    private List<String> analiseSequence(boolean readingStarted, int srartCodonNumber,
                                         int codonNumber, String startCodon, String beforeStarted,
                                         List<String> aminoAcids, List<Character> characters, String charAminoAcids)
    {
        while (!readingStarted && codonNumber < (characters.size() -2))
        {
            while(Objects.requireNonNull(startCodon).length() < 3 && codonNumber < characters.size())
            {
                startCodon += characters.get(codonNumber);
                codonNumber ++;
            }

            if ("AUG".equals(startCodon) || "ATG".equals(startCodon))
            {
                readingStarted = true;
                beforeStarted = getBeforestarted(srartCodonNumber, codonNumber, charAminoAcids);
                aminoAcids.add(beforeStarted);
                codonNumber += 2;
            }
            codonNumber -= 2;
            startCodon = "";
        }

        if (readingStarted)
        {
            String aminoAcidEncoding = "";
            while (codonNumber < (characters.size()-2)){
                while(Objects.requireNonNull(aminoAcidEncoding).length() < 3 )
                {
                    aminoAcidEncoding += characters.get(codonNumber);
                    codonNumber ++;
                }

                if (aminoAcidEncoding.equals("GGT") || aminoAcidEncoding.equals("GGC")
                    || aminoAcidEncoding.equals("GGA") || aminoAcidEncoding.equals("GGG")) {
                    aminoAcids.add("Glycine");
                }
                else if (aminoAcidEncoding.equals("GCT") || aminoAcidEncoding.equals("GCC")
                    || aminoAcidEncoding.equals("GCA") || aminoAcidEncoding.equals("GCG")) {
                    aminoAcids.add("Alanine");
                }
                else if (aminoAcidEncoding.equals("GTT") || aminoAcidEncoding.equals("GTC")
                    || aminoAcidEncoding.equals("GTA") || aminoAcidEncoding.equals("GTG")) {
                    aminoAcids.add("Valine");
                }
                else if (aminoAcidEncoding.equals("TTA") || aminoAcidEncoding.equals("TTG")
                    || aminoAcidEncoding.equals("CTT") || aminoAcidEncoding.equals("CTC")
                    || aminoAcidEncoding.equals("CTA")|| aminoAcidEncoding.equals("CTG")) {
                    aminoAcids.add("Leucine");
                }
                else if (aminoAcidEncoding.equals("ATT") || aminoAcidEncoding.equals("ATC")
                    || aminoAcidEncoding.equals("ATA")){
                    aminoAcids.add("Isoleucine");
                }
                else if (aminoAcidEncoding.equals("TCT") || aminoAcidEncoding.equals("TCC")
                    || aminoAcidEncoding.equals("TCA") || aminoAcidEncoding.equals("TCG")
                    || aminoAcidEncoding.equals("AGC") || aminoAcidEncoding.equals("AGT")){
                    aminoAcids.add("Serine");
                }
                else if (aminoAcidEncoding.equals("ACT") || aminoAcidEncoding.equals("ACC")
                    || aminoAcidEncoding.equals("ACA") || aminoAcidEncoding.equals("ACG")){
                    aminoAcids.add("Threonine");
                }
                else if (aminoAcidEncoding.equals("GAT") || aminoAcidEncoding.equals("GAC")){
                    aminoAcids.add("Aspartic Acid");
                }
                else if (aminoAcidEncoding.equals("GAA") || aminoAcidEncoding.equals("GAG")){
                    aminoAcids.add("Glutamic Acid");
                }
                else if (aminoAcidEncoding.equals("AAT") || aminoAcidEncoding.equals("AAC")){
                    aminoAcids.add("Asparagine");
                }
                else if (aminoAcidEncoding.equals("CAA") || aminoAcidEncoding.equals("CAG")){
                    aminoAcids.add("Glutamine");
                }
                else if (aminoAcidEncoding.equals("AAA") || aminoAcidEncoding.equals("AAG")){
                    aminoAcids.add("Lysine");
                }
                else if (aminoAcidEncoding.equals("CGT") || aminoAcidEncoding.equals("CGC")
                    || aminoAcidEncoding.equals("CGA") || aminoAcidEncoding.equals("CGG")
                    || aminoAcidEncoding.equals("AGA") || aminoAcidEncoding.equals("AGG")){
                    aminoAcids.add("Arginine");
                }
                else if (aminoAcidEncoding.equals("TGT") || aminoAcidEncoding.equals("TGC")){
                    aminoAcids.add("Cysteine");
                }
                else if (aminoAcidEncoding.equals("ATG")){
                    aminoAcids.add("Methionine");
                }
                else if (aminoAcidEncoding.equals("TTT") || aminoAcidEncoding.equals("TTC")){
                    aminoAcids.add("Phenylalanine");
                }
                else if (aminoAcidEncoding.equals("TAT") || aminoAcidEncoding.equals("TAC")){
                    aminoAcids.add("Tyrosine");
                }
                else if (aminoAcidEncoding.equals("TGG")){
                    aminoAcids.add("Tryptophan");
                }
                else if (aminoAcidEncoding.equals("CAT") || aminoAcidEncoding.equals("CAC")){
                    aminoAcids.add("Histidine");
                }
                else if (aminoAcidEncoding.equals("CCT") || aminoAcidEncoding.equals("CCC")
                    || aminoAcidEncoding.equals("CCA") || aminoAcidEncoding.equals("CCG")){
                    aminoAcids.add("Proline");
                }
                else if (aminoAcidEncoding.equals("TAA") || aminoAcidEncoding.equals("TAG") || aminoAcidEncoding.equals("TGA"))
                {
                    aminoAcids.add(aminoAcidEncoding);
                    return analiseSequence(false, codonNumber, codonNumber, startCodon, beforeStarted,
                        aminoAcids,
                        characters, charAminoAcids);
                }
                else {
                    aminoAcids.add(aminoAcidEncoding + getAfterString(codonNumber, charAminoAcids));
                    return  aminoAcids;
                }
                aminoAcidEncoding = "";
            }

        }
        else {
            aminoAcids.add("Nucleotide chain not recognized!");
        }
        String afterStarted = "";
        for (; codonNumber < characters.size(); codonNumber++) {
            afterStarted += characters.get(codonNumber);
        }
        aminoAcids.add(afterStarted);
        return  aminoAcids;
    }

    private String getAfterString(int codonNumber, String charAminoAcids)
    {
        String afterString = "";
        for (; codonNumber < charAminoAcids.toCharArray().length; codonNumber++ )
        {
            afterString += charAminoAcids.toCharArray()[codonNumber];
        }
        return afterString;
    }

    private String getBeforestarted(int srartCodonNumber, int codonNumber, String charAminoAcids)
    {
        String beforeStarted = "";
        for (; srartCodonNumber < codonNumber; srartCodonNumber++ )
        {
            beforeStarted += charAminoAcids.toCharArray()[srartCodonNumber];
        }
        return beforeStarted;
    }

    private List<Character> convertStringToCharList(String charNucleotids)
    {
        return new AbstractList<Character>() {

            @Override
            public Character get(int index)
            {
                return charNucleotids.charAt(index);
            }

            @Override
            public int size()
            {
                return charNucleotids.length();
            }
        };
    }
}
