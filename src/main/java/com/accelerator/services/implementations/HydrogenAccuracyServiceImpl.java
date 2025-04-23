package com.accelerator.services.implementations;

import com.accelerator.convertors.AminoAcidConvertor;
import com.accelerator.dto.AminoAcid;
import com.accelerator.dto.HydrogenAccuracy;
import com.accelerator.dto.HydrogenAccuracyResponse;
import com.accelerator.dto.RotationParams;
import com.accelerator.services.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static java.lang.Double.parseDouble;
import static java.lang.Math.pow;

@Service("hydrogenAccuracyService")
public class HydrogenAccuracyServiceImpl implements HydrogenAccuracyService {

    @Resource
    HBoundService hBoundService;

    @Resource
    DistanceService distanceService;

    @Resource
    RotationService rotationService;

    @Resource
    HydrogenAIService hydrogenAIService;

    @Resource
    private AminoAcidConvertor aminoAcidConvertor;

    Double preSecondAminoAcidResidueKey;

    @Override
    public HydrogenAccuracyResponse calculateHydrogenAccuracyService(SortedMap<Double, List<String[]>> pdbData, Boolean ai) {

        HydrogenAccuracyResponse response  = new HydrogenAccuracyResponse();

        List<HydrogenAccuracy> relatedAminoAcids = findHBounds(pdbData, ai);
        response.setAllList(relatedAminoAcids);

        Map<String, Integer> composition = setComposition(relatedAminoAcids);
        response.setComposition(composition);

        Double averageNHDistance = getAverageNHDistance(relatedAminoAcids);
        response.setAverageNHDistance(averageNHDistance);

        Double averageHHDistance = getAverageHHDistance(relatedAminoAcids);
        response.setAverageHHDistance(averageHHDistance);

        return response;
    }

    private Map<String, Integer> setComposition(List<HydrogenAccuracy> relatedAminoAcids) {
        Map<String, Integer> composition = new HashMap<>();
        relatedAminoAcids.stream()
            .map(HydrogenAccuracy::getAminoAcid)
            .forEach(aminoAcids ->  {
                if (composition.containsKey(aminoAcids)){
                    composition.put(aminoAcids, composition.get(aminoAcids) + 1);
                } else {
                    composition.put(aminoAcids, 1);
                }
            });
        return composition;
    }

    @Override
    public List<HydrogenAccuracy> findHBounds(SortedMap<Double, List<String[]>> pdbData, Boolean ai) {
        List<HydrogenAccuracy> hydrogenAccuracies = new ArrayList<>();
        List<Map.Entry<Double, List<String[]>>> aminoAcidResidues = new ArrayList<>(pdbData.entrySet());
        for (int i = 0; i < aminoAcidResidues.size(); i++) {
            HydrogenAccuracy hydrogenAccuracy = new HydrogenAccuracy();
            RotationParams rotationParams = hBoundService.findHCoordinates(hydrogenAccuracy, aminoAcidResidues, pdbData, i, ai);

            List<AminoAcid> hBoundAminoAcids = new ArrayList<>();
            for(int j = 0; j < aminoAcidResidues.size(); j++) {
                if (Math.abs(j - i) > 2 && i > 0 && i < aminoAcidResidues.size() - 1) {
                    Double co_residueKey = aminoAcidResidues.get(j).getKey();
                    Double nh_residueKey = aminoAcidResidues.get(i).getKey();
                    Double pre_nh_ResidueKey = aminoAcidResidues.get(i-1).getKey();
                    hBoundService.findAccuracyHBound(hydrogenAccuracy, pdbData, co_residueKey, nh_residueKey, pre_nh_ResidueKey, hBoundAminoAcids);
                }
            }
            hydrogenAccuracy.sethBoundAminoAcids(hBoundAminoAcids);
            hydrogenAccuracies.add(hydrogenAccuracy);
            if(ai) {
                try {
                    printNNInfo(hydrogenAccuracy, rotationParams, aminoAcidResidues, pdbData, i);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return hydrogenAccuracies;
    }

    private Double getAverageNHDistance(List<HydrogenAccuracy> relatedAminoAcids) {
        Double [] countDistance = {0.0, 0.0};
        relatedAminoAcids.stream()
            .map(HydrogenAccuracy::getNhDistance)
            .forEach(distance -> {
                if (distance != null && distance > 0) {
                    countDistance[0] += distance;
                    countDistance[1] += 1.0;
                }
            });
        return countDistance[0] / countDistance[1];
    }

    private Double getAverageHHDistance(List<HydrogenAccuracy> relatedAminoAcids) {
        Double [] countDistance = {0.0, 0.0};
        relatedAminoAcids.stream()
            .map(HydrogenAccuracy::getDistanceToReal)
            .forEach(distance -> {
                if (distance != null && distance > 0) {
                    countDistance[0] += distance;
                    countDistance[1] += 1.0;
                }
            });
        return countDistance[0] / countDistance[1];
    }

    private void printNNInfo(HydrogenAccuracy hydrogenAccuracy, RotationParams rotationParams, List<Map.Entry<Double, List<String[]>>> aminoAcidResidues, SortedMap<Double, List<String[]>> pdbData, int index) throws IOException {
//        if (!(hydrogenAccuracy.getRealXCoordinate() == 0.0
//                && hydrogenAccuracy.getRealYCoordinate() == 0.0
//                && hydrogenAccuracy.getRealZCoordinate() == 0.0)
//                && hydrogenAccuracy.getCxCoordinate() != 0.0
//        ) {
        if (hydrogenAccuracy.getCxCoordinate() != 0.0 && !hydrogenAccuracy.getAminoAcid().equals("P")) {

            List<AminoAcid> aminoAcids = hydrogenAccuracy.gethBoundAminoAcids();
            aminoAcids.sort(Comparator.comparing(AminoAcid::gethBoundEnergy));
            String O_CoordinatesAndEnergy = "";
            Double firstAminoAcidResidueKey = aminoAcidResidues.get(index).getKey();
            List<String[]> firstAminoAcidResidue = pdbData.get(firstAminoAcidResidueKey);
            String[] data_N = findData(firstAminoAcidResidue, "N");
            for (int i = 0; i < 3; i++) {
                if (aminoAcids.size() > i) {
                    Double[] O_coordinates = {aminoAcids.get(i).getCoordinateOX(), aminoAcids.get(i).getCoordinateOY(), aminoAcids.get(i).getCoordinateOZ()};
                    O_coordinates[0] = O_coordinates[0] - parseDouble(data_N[4]);
                    O_coordinates[1] = O_coordinates[1] - parseDouble(data_N[5]);
                    O_coordinates[2] = O_coordinates[2] - parseDouble(data_N[6]);
                    Double[] newO_coordinates = rotationService.fullRotateAtom(O_coordinates, rotationParams.getCosines(), rotationParams.getClockwiseRotations());
                    O_CoordinatesAndEnergy += round(newO_coordinates[0], 4) + ",";
                    O_CoordinatesAndEnergy += round(newO_coordinates[1], 4) + ",";
                    O_CoordinatesAndEnergy += round(newO_coordinates[2], 4) + ",";
                    O_CoordinatesAndEnergy += aminoAcids.get(i).gethBoundEnergy() + ",";
                } else {
                    O_CoordinatesAndEnergy += "0.0,0.0,0.0,0.0,";
                }

            }


            String result = aminoAcidConvertor.convertShortToDigit(hydrogenAccuracy.getPreviousAminoAcid()) + ","
                    + aminoAcidConvertor.convertShortToDigit(hydrogenAccuracy.getAminoAcid()) + ","
                    + aminoAcidConvertor.convertShortToDigit(hydrogenAccuracy.getNextAminoAcid()) + ","
                    + hydrogenAccuracy.getCxCoordinate() + ","
                    + hydrogenAccuracy.getCaXCoordinate() + ","
                    + hydrogenAccuracy.getCaZCoordinate() + ","
                    + round(hydrogenAccuracy.getPowerXCoordinate()/10, 4) + ","
                    + round(hydrogenAccuracy.getPowerYCoordinate()/10, 4) + ","
                    + round(hydrogenAccuracy.getPowerZCoordinate()/10, 4) + ","
                    + O_CoordinatesAndEnergy;
//                    + hydrogenAccuracy.getRealXCoordinate() + ","
//                    + hydrogenAccuracy.getRealYCoordinate() + ","
//                    + hydrogenAccuracy.getRealZCoordinate();


            String[] finalCoordinates = hydrogenAIService.sendPOST(result);

            Double[] H_coordinates = new Double[3];
            H_coordinates[0] = parseDouble(finalCoordinates[0]);
            H_coordinates[1] = parseDouble(finalCoordinates[1]);
            H_coordinates[2] = parseDouble(finalCoordinates[2]);

            Double[] newH_coordinates = rotationService.fullBackRotateAtom(H_coordinates, rotationParams.getCosines(), rotationParams.getClockwiseRotations());

            H_coordinates[0] = newH_coordinates[0] + parseDouble(data_N[4]);
            H_coordinates[1] = newH_coordinates[1] + parseDouble(data_N[5]);
            H_coordinates[2] = newH_coordinates[2] + parseDouble(data_N[6]);


            hydrogenAccuracy.setPredictedXCoordinate(round(H_coordinates[0], 3));
            hydrogenAccuracy.setPredictedYCoordinate(round(H_coordinates[1], 3));
            hydrogenAccuracy.setPredictedZCoordinate(round(H_coordinates[2], 3));

            Double[] H_real = new Double[3];
            if (hydrogenAccuracy.getRealXCoordinate() != 0.0) {
                H_real[0] = hydrogenAccuracy.getRealXCoordinate();
                H_real[1] = hydrogenAccuracy.getRealYCoordinate();
                H_real[2] = hydrogenAccuracy.getRealZCoordinate();
                fillAccuracyDistance(hydrogenAccuracy, H_real, H_coordinates);
            } else {
                hydrogenAccuracy.setRealXCoordinate(null);
                hydrogenAccuracy.setRealYCoordinate(null);
                hydrogenAccuracy.setRealZCoordinate(null);
            }
            fillNHDistance(hydrogenAccuracy, H_coordinates, data_N);


        } else {
            hydrogenAccuracy.setRealXCoordinate(null);
            hydrogenAccuracy.setRealYCoordinate(null);
            hydrogenAccuracy.setRealZCoordinate(null);

        }
    }

    private void fillNHDistance(HydrogenAccuracy hydrogenAccuracy, Double[] real_h, String[] first_n) {
        if(real_h != null) {
            Double[] real_n = new Double[3];
            real_n[0] = parseDouble(first_n[4]);
            real_n[1] = parseDouble(first_n[5]);
            real_n[2] = parseDouble(first_n[6]);
            Double nhDistance = distanceService.countDistance(real_h, real_n);
            hydrogenAccuracy.setNhDistance(round(nhDistance, 3));
        }
    }

    private void fillAccuracyDistance(HydrogenAccuracy hydrogenAccuracy, Double[] real_h, Double[] predicted_h) {
        if (real_h != null && predicted_h != null) {
            Double distance = distanceService.countDistance(real_h, predicted_h);
            DecimalFormat df = new DecimalFormat("0.000");
            df.setRoundingMode(RoundingMode.UP);
            hydrogenAccuracy.setDistanceToReal(round(distance, 4));
        }
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private String[] findData(List<String[]> firstAminoAcidResidue, String atom) {
        for (String[] atomData : firstAminoAcidResidue) {
            if(atomData[0].equals(atom)){
                return atomData;
            }
        }
        return null;
    }
}
