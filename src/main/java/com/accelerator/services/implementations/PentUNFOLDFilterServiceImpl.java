package com.accelerator.services.implementations;

import com.accelerator.convertors.AminoAcidConvertor;
import com.accelerator.convertors.SubNumberConvertor;
import com.accelerator.services.PentUNFOLDFilterService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Double.parseDouble;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Service("pentUNFOLDFilterService")
public class PentUNFOLDFilterServiceImpl implements PentUNFOLDFilterService {

    @Resource
    AminoAcidConvertor aminoAcidConvertor;

    @Resource
    SubNumberConvertor subNumberConvertor;

    private static final String PDB_CHAIN_REGEX = "(ATOM(.*)|HETATM(.*)|TER(.*))[A-Z]{3}\\s%s\\s{0,3}-?\\d+(.*)\r?";
    private static final String DSSP_START_NUMBERS_MATCHING = "\\d{%s}\\s(.*)";
    private static final int MAX_DSSP_NEEDED_SPACES = 4;
    private static final String AMINO_ACIDS = "[^GLYSEQDNFAKRHCVPWIMT]";
    List<String> dsspContent;
    List<String> pdbContent;
    SortedMap<Double, List<String[]>> pdbToDsspContent;
    String aminoAcidSequence = "";
    boolean isTerminatedChain;

    @Override
    public List<String> filterDssp(List<String> dsspContext, String chainContext, boolean isFileNeeded) {
        dsspContent = new ArrayList<>();
        if (!isFileNeeded){
            dsspContext.stream()
                    .filter(dsspString -> isTargetChain(dsspString, chainContext))
                    .forEach(this::addSpacesToString);
        } else {
            dsspContext.stream()
                    .filter(dsspString -> isTargetChain(dsspString, chainContext))
                    .forEach(this::addSpacesToString);
        }
        return dsspContent;
    }

    @Override
    public List<String> filterPdb(List<String> pdbContext, String chainContext) {
        pdbContent = new ArrayList<>();
        isTerminatedChain = false;
        pdbContext.stream()
                .filter(pdbString -> filterPdbStream(pdbString, chainContext))
                .forEach(this::addNumberAndAminoAcid);
        return pdbContent;
    }

    @Override
    public SortedMap<Double, List<String[]>> filterPdbToDssp(List<String> pdbContext, String chainContext) {
        pdbToDsspContent = new TreeMap<>();
        isTerminatedChain = false;
        pdbContext.stream()
                .filter(pdbString -> filterPdbStream(pdbString, chainContext))
                .forEach(this::addNumberAndAminoAcidAndAtomAndCoordinates);
        return pdbToDsspContent;
    }

    @Override
    public String getSequence() {
        String aminoAcidSequenceToReturn = this.aminoAcidSequence;
        this.aminoAcidSequence = "";
        return aminoAcidSequenceToReturn;
    }

    @Override
    public String filterSequence(String sequence) {
        return sequence.replaceAll(AMINO_ACIDS, "");
    }

    private void addSpacesToString(String dsspString){
        for(int i = 1; i <= MAX_DSSP_NEEDED_SPACES; i++) {
            if (dsspString.matches(format(DSSP_START_NUMBERS_MATCHING, i))) {
                dsspString = addSpaces(dsspString, i);
                break;
            }
        }
        dsspContent.add(dsspString.substring(16, 17));
        dsspContent.add(dsspString.substring(13, 14));
        dsspContent.add(getDsspNumber(dsspString));
    }

    private boolean isTargetChain(String dsspString, String chainContext) {
        return dsspString.length() > 18
                && dsspString.substring(10, 13).equals(" " + chainContext + " ");
    }

    private boolean filterPdbStream(String pdbString, String chain) {
        boolean isTermResidue = pdbString.startsWith("TER");
        boolean isTargetChain = pdbString.matches(format(PDB_CHAIN_REGEX, chain.equals("") ? " " : chain));
        if (isTermResidue && isTargetChain) {
            isTerminatedChain = true;
        }
        return !isTerminatedChain && isTargetChain;
    }

    private String addSpaces(String dsspString, int numbers) {
        int neededSpaces = MAX_DSSP_NEEDED_SPACES - numbers;
        StringBuilder spaces = new StringBuilder();
        for (;neededSpaces >= 0; neededSpaces--) {
            spaces.append(" ");
        }
        return format("%s%s", spaces.toString(), dsspString);
    }

    private void addNumberAndAminoAcid(String pdbString) {
        Pattern generalPattern = Pattern.compile("\\d+\\s*[A-Z]+\\s*([A-Z]{3}\\s[\\w|\\s]\\s*\\d+[A-Z]?\\s)");
        Pattern numberPattern = Pattern.compile("(\\s*\\d+[A-Z]?\\s)");
        Matcher generalMatcher = generalPattern.matcher(pdbString);
        if(generalMatcher.find()) {
            String generalString = generalMatcher.group(1);
            Matcher numberMatcher = numberPattern.matcher(generalString);
            numberMatcher.find();
            String number = replaceLitersToPointDigit(numberMatcher.group(1).trim());
            if(!pdbContent.contains(number)) {
                String aminoAcid = getAminoAcid(generalString);
                if (aminoAcid != null) {
                    pdbContent.add(number);
                    pdbContent.add(aminoAcid);
                    aminoAcidSequence += aminoAcid;
                }
            }
        }
    }

    private void addNumberAndAminoAcidAndAtomAndCoordinates(String pdbString) {
        String[] atomData = new String[7];
        Pattern generalPattern = Pattern.compile("\\d+\\s*\\S+\\s*[A-Z]?([A-Z]{3}\\s[\\w|\\s]\\s*-?\\d+[A-Z]?\\s+\\S+\\s+\\S+\\s+\\S+)\r?");
        Pattern atomPattern = Pattern.compile("\\d+\\s*(\\S+)\\s*[A-Z]?[A-Z]{3}\\s[\\w|\\s]\\s*-?\\d+[A-Z]?\\s+\\S+\\s+\\S+\\s+\\S+");
        Pattern chainPattern = Pattern.compile("\\s*[A-Z]{3}\\s(\\w|\\s)\\s*-?\\d+[A-Z]?\\s+");
        Pattern numberPattern = Pattern.compile("(\\s*-?\\d+[A-Z]?\\s)");
        Pattern coordinatesPattern = Pattern.compile("[A-Z]{3}\\s[\\w|\\s]\\s*-?\\d+[A-Z]?\\s+(\\S+\\s+\\S+\\s+\\S+)");
        Matcher generalMatcher = generalPattern.matcher(pdbString);
        if(generalMatcher.find()) {
            String generalString = generalMatcher.group(1);
            Matcher atomMatcher = atomPattern.matcher(pdbString);
            Matcher numberMatcher = numberPattern.matcher(generalString);
            Matcher chainMatcher = chainPattern.matcher(generalString);
            Matcher coordinatesMatcher = coordinatesPattern.matcher(generalString);

            atomMatcher.find();
            numberMatcher.find();
            chainMatcher.find();
            coordinatesMatcher.find();

            List<String> coordinates = stream(coordinatesMatcher.group(1).split(" ")).filter(coordinate -> coordinate.length() > 0).collect(toList());
            String aminoAcidNumber = replaceLitersToPointDigit(numberMatcher.group(1).trim());
            List<String[]> aminoAcid = pdbToDsspContent.get(parseDouble(aminoAcidNumber));

            atomData[0] = atomMatcher.group(1);
            atomData[1] = getAminoAcid(generalString);
            atomData[2] = chainMatcher.group(1);
            atomData[3] = aminoAcidNumber;
            for(int i = 4; i < 7; i++){
                atomData[i] = coordinates.get(i-4);
            }

            if (aminoAcid != null){
                aminoAcid.add(atomData);
                pdbToDsspContent.put(parseDouble(aminoAcidNumber), aminoAcid);
            } else {
                List<String[]> newAminoAcid = new ArrayList<>();
                newAminoAcid.add(atomData);
                pdbToDsspContent.put(parseDouble(aminoAcidNumber), newAminoAcid);
            }
        }
    }

    private String getAminoAcid(String generalString) {
        String aminoAcid = generalString.substring(0, 3);
        return aminoAcidConvertor.convertToShort(aminoAcid);
    }

    private String getDsspNumber(String dsspString) {
        Pattern numberPattern = Pattern.compile("\\s+\\d+\\s+(-?\\d+[A-Z]?)\\s*");
        Matcher generalMatcher = numberPattern.matcher(dsspString);
        generalMatcher.find();
        return replaceLitersToPointDigit(generalMatcher.group(1));
    }

    private String replaceLitersToPointDigit(String fullNumber) {
        String finalNumber = null;
        Pattern subNumberPattern = Pattern.compile("-?\\d+([A-Z])");
        Matcher numberMatcher = subNumberPattern.matcher(fullNumber);
        if(numberMatcher.find()) {
            String subNumber = numberMatcher.group(1);
            String pointDigit = subNumberConvertor.convertToPointDigit(subNumber);
            finalNumber = fullNumber.replace(subNumber, pointDigit);
        }
        return finalNumber != null ? finalNumber : fullNumber;
    }
}
