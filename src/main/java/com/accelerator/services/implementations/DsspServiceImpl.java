package com.accelerator.services.implementations;

import static java.util.Objects.nonNull;

import com.accelerator.dto.HydrogenAccuracy;
import com.accelerator.services.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service("dsspService")
public class DsspServiceImpl implements DsspService {

    private static final List<Double> HELIX = Arrays.asList(3.0, 4.0, 5.0);
    private static final List<Double> BRIDGE = Arrays.asList(6.0, 7.0);

    @Resource
    PentUNFOLDFilterService pentUNFOLDFilterService;
    @Resource
    HBoundService hBoundService;
    @Resource
    BendService bendService;
    @Resource
    HydrogenAccuracyService hydrogenAccuracyService;

    SortedMap<Double, Set<Double[]>> hBoundsDescription;
    SortedMap<Double, String> secondaryStructure;
    List<String> finalDsspData;
    Double preSecondAminoAcidResidueKey;

    @Override
    public List<String> getDsspContext(List<String> pdbFile, String chain, Boolean ai) {
        SortedMap<Double, List<String[]>> pdbData = pentUNFOLDFilterService.filterPdbToDssp(pdbFile, chain);
        List<HydrogenAccuracy> findHBounds = hydrogenAccuracyService.findHBounds(pdbData, ai);
        helixDeterminate(pdbData, findHBounds);
        bridgeDeterminate(pdbData, findHBounds);
        secondaryStructureDeterminate();
        bendDeterminate(pdbData);
        prepareFinalDsspData(pdbData);
        return finalDsspData;
    }

    private void helixDeterminate(SortedMap<Double, List<String[]>> pdbData, List<HydrogenAccuracy> findHBounds) {
        hBoundsDescription = new TreeMap<>();
        secondaryStructure = new TreeMap<>();
        finalDsspData = new ArrayList<>();
        alphaHelixDeterminateByTurn(pdbData, 3.0, findHBounds);
        alphaHelixDeterminateByTurn(pdbData, 4.0, findHBounds);
        alphaHelixDeterminateByTurn(pdbData, 5.0, findHBounds);
    }

    private void bridgeDeterminate(SortedMap<Double, List<String[]>> pdbData, List<HydrogenAccuracy> findHBounds) {
        List<Map.Entry<Double, List<String[]>>> aminoAcidResidues = new ArrayList<>(pdbData.entrySet());
        for (int i = 1; i < aminoAcidResidues.size() - 4; i++) {
            Double firstAminoAcidResidueKey = aminoAcidResidues.get(i).getKey();
            Double preFirstAminoAcidResidueKey = aminoAcidResidues.get(i-1).getKey();
            Double postFirstAminoAcidResidueKey = aminoAcidResidues.get(i+1).getKey();
//            addNewHBoundRecord(firstAminoAcidResidueKey);
            for (int j = i + 3; j < aminoAcidResidues.size() - 1; j++){
                boolean isParallelBridge = false;
                boolean isAntiParallelBridge = false;
                Double secondAminoAcidResidueKey = aminoAcidResidues.get(j).getKey();
                Double preSecondAminoAcidResidueKey = aminoAcidResidues.get(j-1).getKey();
                Double postSecondAminoAcidResidueKey = aminoAcidResidues.get(j+1).getKey();
                if(isBridgePossible(firstAminoAcidResidueKey, preFirstAminoAcidResidueKey, postFirstAminoAcidResidueKey,
                        secondAminoAcidResidueKey, preSecondAminoAcidResidueKey, postSecondAminoAcidResidueKey)) {
                    String[] H_fst = hCoordByAI(firstAminoAcidResidueKey, findHBounds);
                    String[] H_sec = hCoordByAI(secondAminoAcidResidueKey, findHBounds);
                    String[] H_pst_fst = hCoordByAI(postFirstAminoAcidResidueKey, findHBounds);
                    String[] H_pst_sec = hCoordByAI(postSecondAminoAcidResidueKey, findHBounds);

                    isParallelBridge = isParallelBridge(firstAminoAcidResidueKey, preFirstAminoAcidResidueKey,
                            postFirstAminoAcidResidueKey, secondAminoAcidResidueKey, preSecondAminoAcidResidueKey,
                            postSecondAminoAcidResidueKey, pdbData, H_sec, H_pst_fst, H_pst_sec, H_fst);
                    if (!isParallelBridge) {
                        isAntiParallelBridge = isAntiParallelBridge(firstAminoAcidResidueKey, preFirstAminoAcidResidueKey,
                                postFirstAminoAcidResidueKey, secondAminoAcidResidueKey, preSecondAminoAcidResidueKey,
                                postSecondAminoAcidResidueKey, pdbData, H_pst_sec, H_pst_fst, H_sec, H_fst);
                    }
                    if(isAntiParallelBridge || isParallelBridge){
                        updateHBoundsDescription(firstAminoAcidResidueKey, secondAminoAcidResidueKey,
                                true, (isParallelBridge ? 6.0 : 7.0));
                    }
                }
            }
        }
    }

    private void bendDeterminate(SortedMap<Double, List<String[]>> pdbData) {
        List<Map.Entry<Double, List<String[]>>> aminoAcidResidues = new ArrayList<>(pdbData.entrySet());
        for (int i = 0; i < aminoAcidResidues.size(); i++) {
            Double aminoAcidResidueKey = aminoAcidResidues.get(i).getKey();
            Double preAminoAcidResidueKey = getAminoAcidResidueKey(i, -3, aminoAcidResidues);
            Double postAminoAcidResidueKey = getAminoAcidResidueKey(i, 3, aminoAcidResidues);
            if (nonNull(preAminoAcidResidueKey) && nonNull(postAminoAcidResidueKey)){
                boolean isBend = bendService.isBend(pdbData, preAminoAcidResidueKey, aminoAcidResidueKey, postAminoAcidResidueKey);
                if (isBend) {
                    fillBend(aminoAcidResidues, i, aminoAcidResidueKey);
                }
            }
        }
    }

    private void fillBend(List<Entry<Double, List<String[]>>> aminoAcidResidues, int index, Double aminoAcidResidueKey) {

        Double preOneAminoAcidResidueKey = getAminoAcidResidueKey(index, -1, aminoAcidResidues);
        Double preTwoAminoAcidResidueKey = getAminoAcidResidueKey(index, -2, aminoAcidResidues);
        Double postOneAminoAcidResidueKey = getAminoAcidResidueKey(index, 1, aminoAcidResidues);
        Double postTwoAminoAcidResidueKey = getAminoAcidResidueKey(index, 2, aminoAcidResidues);
        if (!secondaryStructure.get(preTwoAminoAcidResidueKey).equals("H")
            && secondaryStructure.get(preOneAminoAcidResidueKey).equals("H")
            && secondaryStructure.get(postOneAminoAcidResidueKey).equals("H")
            && secondaryStructure.get(postTwoAminoAcidResidueKey).equals("H")) {
                secondaryStructure.put(aminoAcidResidueKey, "H");
                secondaryStructure.put(preOneAminoAcidResidueKey, "");
        } else {
            secondaryStructure.put(aminoAcidResidueKey, "S");
        }
    }

    private Double getAminoAcidResidueKey(int currentNumber, int step, List<Entry<Double, List<String[]>>> aminoAcidResidues) {
        int targetNumber = currentNumber + step;
        if (targetNumber > aminoAcidResidues.size() - 1 || targetNumber < 0) {
            return null;
        }
        if (step < 0) {
            for (int i = 1; i <= -step; i++){
                Double currentKey = aminoAcidResidues.get(currentNumber).getKey();
                Double targetKey = aminoAcidResidues.get(currentNumber - i).getKey();
                if (currentKey - targetKey >  -step - 1 || i == -step) {
                    return targetKey;
                }
            }
        } else if (step > 0) {
            for (int i = 1; i <= step; i++){
                Double currentKey= aminoAcidResidues.get(currentNumber).getKey();
                Double targetKey = aminoAcidResidues.get(currentNumber + i).getKey();
                if (targetKey - currentKey >= step || i == step) {
                    return targetKey;
                }
            }
        }
        return null;
    }

    private void alphaHelixDeterminateByTurn(SortedMap<Double, List<String[]>> pdbData, Double turn, List<HydrogenAccuracy> findHBounds) {
        List<Map.Entry<Double, List<String[]>>> aminoAcidResidues = new ArrayList<>(pdbData.entrySet());
        for (int i = 0; i < aminoAcidResidues.size(); i++) {
            Double firstAminoAcidResidueKey = aminoAcidResidues.get(i).getKey();
            addNewHBoundRecord(firstAminoAcidResidueKey);
            Double secondAminoAcidResidueKey = findSecondAminoAcidResidueKey(aminoAcidResidues, firstAminoAcidResidueKey, turn, i);
            if (secondAminoAcidResidueKey != null) {
                boolean isHBoundExist = hBoundService.isHBoundExist(
                        pdbData, firstAminoAcidResidueKey, secondAminoAcidResidueKey,
                        preSecondAminoAcidResidueKey, hCoordByAI(secondAminoAcidResidueKey, findHBounds));
                updateHBoundsDescription(firstAminoAcidResidueKey, secondAminoAcidResidueKey, isHBoundExist, turn);
            }
        }
    }

    private String[] hCoordByAI(Double key, List<HydrogenAccuracy> description) {
        HydrogenAccuracy target = description.stream().filter(t -> t.getAminoAcidNumber().equals(key.toString())).findFirst().get();
        if (target.getPredictedXCoordinate() == null) {
            return null;
        } else {
            return new String[]{"H", "", "", "",
                    target.getPredictedXCoordinate().toString(),
                    target.getPredictedYCoordinate().toString(),
                    target.getPredictedZCoordinate().toString()};
        }
    }

    private boolean isBridgePossible(Double firstAminoAcidResidueKey, Double preFirstAminoAcidResidueKey,
                                     Double postFirstAminoAcidResidueKey, Double secondAminoAcidResidueKey,
                                     Double preSecondAminoAcidResidueKey, Double postSecondAminoAcidResidueKey) {
        return secondAminoAcidResidueKey - firstAminoAcidResidueKey > 3
                && firstAminoAcidResidueKey - preFirstAminoAcidResidueKey <= 1
                && postFirstAminoAcidResidueKey - firstAminoAcidResidueKey <= 1
                && secondAminoAcidResidueKey - preSecondAminoAcidResidueKey <= 1
                && secondAminoAcidResidueKey - preSecondAminoAcidResidueKey >= -1
                && postSecondAminoAcidResidueKey - secondAminoAcidResidueKey <= 1
                && postSecondAminoAcidResidueKey - secondAminoAcidResidueKey >= -1;
    }

    private boolean isParallelBridge(Double firstAminoAcidResidueKey, Double preFirstAminoAcidResidueKey,
                                     Double postFirstAminoAcidResidueKey, Double secondAminoAcidResidueKey,
                                     Double preSecondAminoAcidResidueKey, Double postSecondAminoAcidResidueKey,
                                     SortedMap<Double, List<String[]>> pdbData,
                                     String[] H1_coordinates, String[] H2_coordinates,
                                     String[] H3_coordinates, String[] H4_coordinates) {
        return (hBoundService.isHBoundExist(pdbData, preFirstAminoAcidResidueKey, secondAminoAcidResidueKey, preSecondAminoAcidResidueKey, H1_coordinates)
                && hBoundService.isHBoundExist(pdbData, secondAminoAcidResidueKey, postFirstAminoAcidResidueKey, firstAminoAcidResidueKey, H2_coordinates))
                || (hBoundService.isHBoundExist(pdbData, firstAminoAcidResidueKey, postSecondAminoAcidResidueKey, secondAminoAcidResidueKey, H3_coordinates)
                && hBoundService.isHBoundExist(pdbData, preSecondAminoAcidResidueKey, firstAminoAcidResidueKey, preFirstAminoAcidResidueKey, H4_coordinates));
    }

    private boolean isAntiParallelBridge(Double firstAminoAcidResidueKey, Double preFirstAminoAcidResidueKey,
                                         Double postFirstAminoAcidResidueKey, Double secondAminoAcidResidueKey,
                                         Double preSecondAminoAcidResidueKey, Double postSecondAminoAcidResidueKey,
                                         SortedMap<Double, List<String[]>> pdbData,
                                         String[] H1_coordinates, String[] H2_coordinates,
                                         String[] H3_coordinates, String[] H4_coordinates) {
        return (hBoundService.isHBoundExist(pdbData, preFirstAminoAcidResidueKey, postSecondAminoAcidResidueKey, secondAminoAcidResidueKey, H1_coordinates)
                && hBoundService.isHBoundExist(pdbData, preSecondAminoAcidResidueKey, postFirstAminoAcidResidueKey, firstAminoAcidResidueKey, H2_coordinates)
                || (hBoundService.isHBoundExist(pdbData, firstAminoAcidResidueKey, secondAminoAcidResidueKey, preSecondAminoAcidResidueKey, H3_coordinates)
                && hBoundService.isHBoundExist(pdbData, secondAminoAcidResidueKey, firstAminoAcidResidueKey, preFirstAminoAcidResidueKey, H4_coordinates)));
    }

    public Double findSecondAminoAcidResidueKey(List<Map.Entry<Double, List<String[]>>> aminoAcidResidues,
                                                 Double firstAminoAcidResidueKey, Double turn, int index) {
        preSecondAminoAcidResidueKey = null;
        int aminoAcidResiduesSize = aminoAcidResidues.size();
        for (int i = 1, loopTime = 1; i <= turn; i++, loopTime++){
            int secondIndex = index + loopTime;
            boolean isExistRecord = aminoAcidResiduesSize > secondIndex;
            Double secondAminoAcidResidueKey = isExistRecord ? aminoAcidResidues.get(secondIndex).getKey() : null;
            if (secondAminoAcidResidueKey != null) {
                if (secondAminoAcidResidueKey > firstAminoAcidResidueKey + turn) {
                    break;
                } else if(secondAminoAcidResidueKey - i > firstAminoAcidResidueKey) {
                    i = (int) (secondAminoAcidResidueKey - firstAminoAcidResidueKey);
                }
                if (secondAminoAcidResidueKey == firstAminoAcidResidueKey + turn || i == turn) {
                    preSecondAminoAcidResidueKey = aminoAcidResidues.get(secondIndex-1).getKey();
                    return secondAminoAcidResidueKey;
                }
            } else break;
        }
        return null;
    }

    private void updateHBoundsDescription(Double firstAminoAcidResidueKey, Double secondAminoAcidResidueKey,
                                          boolean isHBoundExist, Double turn) {
        Set<Double[]> firstAminoAcidRelations = hBoundsDescription.get(firstAminoAcidResidueKey);
        Set<Double[]> secondAminoAcidRelations = hBoundsDescription.get(secondAminoAcidResidueKey);
        if (firstAminoAcidRelations == null) {
            Set<Double[]> newAminoAcidRelations = new HashSet<>();
            addNewValue(isHBoundExist, newAminoAcidRelations, firstAminoAcidResidueKey, secondAminoAcidResidueKey, turn);
        } else {
            addNewValue(isHBoundExist, firstAminoAcidRelations, firstAminoAcidResidueKey, secondAminoAcidResidueKey, turn);
        }

        if (secondAminoAcidRelations == null) {
            Set<Double[]> newAminoAcidRelations = new HashSet<>();
            addNewValue(isHBoundExist, newAminoAcidRelations, secondAminoAcidResidueKey, firstAminoAcidResidueKey, turn);
        } else {
            addNewValue(isHBoundExist, secondAminoAcidRelations, secondAminoAcidResidueKey, firstAminoAcidResidueKey, turn);
        }
    }

    private void addNewValue(boolean isHBoundExist, Set<Double[]> aminoAcidRelations, Double firstAminoAcidResidueKey,
                             Double secondAminoAcidResidueKey, Double turn) {
        if(isHBoundExist) {
            Double[] aminoAcidData = new Double[2];
            aminoAcidData[0] = secondAminoAcidResidueKey;
            aminoAcidData[1] = turn;
            aminoAcidRelations.add(aminoAcidData);
        }
        hBoundsDescription.put(firstAminoAcidResidueKey, aminoAcidRelations);
        secondaryStructure.put(firstAminoAcidResidueKey, "");
    }

    private void addNewHBoundRecord(Double aminoAcidResidueKey) {
        boolean isRecordExist = hBoundsDescription.get(aminoAcidResidueKey) != null;
        if (!isRecordExist) {
            hBoundsDescription.put(aminoAcidResidueKey, new HashSet<>());
            secondaryStructure.put(aminoAcidResidueKey, null);
        }
    }

    private void prepareFinalDsspData(SortedMap<Double, List<String[]>> pdbData) {
        List<Map.Entry<Double, String>> secondaryStructureEntrySet = new ArrayList<>(secondaryStructure.entrySet());
        for(Map.Entry<Double, String> secondaryConfig : secondaryStructureEntrySet) {
            finalDsspData.add(secondaryConfig.getValue());
            finalDsspData.add(pdbData.get(secondaryConfig.getKey()).get(0)[1]);
            finalDsspData.add(pdbData.get(secondaryConfig.getKey()).get(0)[3]);
        }
    }

    private void secondaryStructureDeterminate() {
        List<Map.Entry<Double, Set<Double[]>>> hBoundsDescriptionEntrySet = new ArrayList<>(hBoundsDescription.entrySet());
        Double firstAminoAcid = 0.0;
        Double lastTurn = -1.0;
        for(int i = 0; i < hBoundsDescriptionEntrySet.size(); i++) {
            Map.Entry<Double, Set<Double[]>> hBoundConfig = hBoundsDescriptionEntrySet.get(i);
            firstAminoAcid = hBoundConfig.getKey();
            Set<Double[]> relations = hBoundConfig.getValue();
            FIRST_RELATIONS: for (Double[] relatedAminoAcid : relations) {
                Double secondAminoAcid = relatedAminoAcid[0];
                Double stepAminoAcid = relatedAminoAcid[1];
                if (BRIDGE.contains(stepAminoAcid)){
                    boolean isSingleBridge = isSingleBridge(hBoundsDescriptionEntrySet, i);
                    if (isSingleBridge) {
                        secondaryStructure.put(hBoundsDescriptionEntrySet.get(i).getKey(), "B");
                    } else {
                        secondaryStructure.put(hBoundsDescriptionEntrySet.get(i).getKey(), "E");
                    }
                    break;
                }
                if (HELIX.contains(stepAminoAcid)){
                    Set<Double> alfaHelix = new HashSet<>();
                    alfaHelix.add(firstAminoAcid);
                    alfaHelix.add(secondAminoAcid);
                    for(int sub_i = 1; sub_i <= stepAminoAcid; sub_i++){
                        if (hBoundsDescriptionEntrySet.size() > i + sub_i) {
                            Double sub_firstAminoAcid = hBoundsDescriptionEntrySet.get(i + sub_i).getKey();
                            alfaHelix.add(sub_firstAminoAcid);
                            for (Double[] sub_relatedAminoAcid : hBoundsDescriptionEntrySet.get(i + sub_i).getValue()) {
                                Double sub_secondAminoAcid = relatedAminoAcid[0];
                                if (secondAminoAcid + stepAminoAcid < sub_secondAminoAcid) {
                                    break;
                                } else if (sub_relatedAminoAcid[1].equals(stepAminoAcid) && sub_firstAminoAcid < secondAminoAcid) {
                                    int j = i;
                                    Double currentTurn = hBoundsDescriptionEntrySet.get(j).getKey();
                                    boolean isFirst = currentTurn - lastTurn > 1;
                                    do {
                                        if(isFirst){
                                            isFirst = false;
                                            j++;
                                            continue;
                                        }
                                        if(stepAminoAcid.equals(3.0)){
                                            secondaryStructure.put(hBoundsDescriptionEntrySet.get(j).getKey(), "G");
                                        } else if(stepAminoAcid.equals(4.0)) {
                                            secondaryStructure.put(hBoundsDescriptionEntrySet.get(j).getKey(), "H");
                                        } else if(stepAminoAcid.equals(5.0)) {
                                            secondaryStructure.put(hBoundsDescriptionEntrySet.get(j).getKey(), "I");
                                        }
                                        j++;
                                    } while (!hBoundsDescriptionEntrySet.get(j).getKey().equals(sub_secondAminoAcid));
                                    lastTurn = hBoundsDescriptionEntrySet.get(j).getKey();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isSingleBridge(List<Map.Entry<Double, Set<Double[]>>> hBoundsDescriptionEntrySet, int i) {
        Map.Entry<Double, Set<Double[]>> preHBoundConfig = hBoundsDescriptionEntrySet.get(i-1);
        boolean isPreHBoundMissing = isBridgeMissing(preHBoundConfig);
        Map.Entry<Double, Set<Double[]>> postHBoundConfig = hBoundsDescriptionEntrySet.get(i+1);
        boolean isPostHBoundMissing = isBridgeMissing(postHBoundConfig);
        return isPreHBoundMissing && isPostHBoundMissing;
    }

    private boolean isBridgeMissing(Map.Entry<Double, Set<Double[]>> hBoundConfig) {
        Set<Double[]> preRelations = hBoundConfig.getValue();
        for (Double[] relatedAminoAcid : preRelations) {
            Double stepAminoAcid = relatedAminoAcid[1];
            if (BRIDGE.contains(stepAminoAcid)){
                return false;
            }
        }
        return true;
    }
}
