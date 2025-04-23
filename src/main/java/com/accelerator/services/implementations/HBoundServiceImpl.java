package com.accelerator.services.implementations;

import com.accelerator.dto.AminoAcid;
import com.accelerator.dto.Atom;
import com.accelerator.dto.HydrogenAccuracy;
import com.accelerator.dto.RotationParams;
import com.accelerator.services.*;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.SortedMap;

import static java.lang.Double.parseDouble;
import static java.lang.Math.pow;
import static java.lang.String.valueOf;
import static java.util.Objects.nonNull;

@Service("hBoundService")
public class HBoundServiceImpl implements HBoundService {

    private static final Double MIN_E_H_BOUND = -0.5;
    private static final Double N_H_DISTANCE = 1.01;
    private static final String CARBON = "C";
    private static final String CARBON_A = "CA";
    private static final String OXYGEN = "O";
    private static final String NITROGEN = "N";
    private static final String HYDROGEN = "H";

    @Resource
    private DistanceService distanceService;

    @Resource
    HydrogenAIService hydrogenAIService;

    @Resource
    private RotationService rotationService;

    @Resource
    private HydrogenAccuracyService hydrogenAccuracyService;

    private Double leadingDistance;

    private Double o_h_distance;
    private List<Atom> nearestAtoms;

    @Override
    public boolean isHBoundExist(SortedMap<Double, List<String[]>> pdbData, Double co_residueKey,
                                 Double nh_residueKey, Double pre_nh_ResidueKey, String[] h_coordinates) {

        return isHBoundExistDouble(pdbData, co_residueKey, nh_residueKey, pre_nh_ResidueKey, h_coordinates) < MIN_E_H_BOUND;
    }

    public Double isHBoundExistDouble(SortedMap<Double, List<String[]>> pdbData, Double co_residueKey,
                                      Double nh_residueKey, Double pre_nh_ResidueKey, String[] h_coordinates) {
        
        if (nh_residueKey - pre_nh_ResidueKey <= 1) {
            List<String[]> firstAminoAcidResidue = pdbData.get(co_residueKey);
            List<String[]> secondAminoAcidResidue = pdbData.get(nh_residueKey);
            List<String[]> preSecondAminoAcidResidue = pdbData.get(pre_nh_ResidueKey);
            
            if (isProline(firstAminoAcidResidue) || isProline(secondAminoAcidResidue)) {
                return 0.0;
            }

            String[] first_C = findData(firstAminoAcidResidue, CARBON);
            String[] first_O = findData(firstAminoAcidResidue, OXYGEN);
            String[] second_N = findData(secondAminoAcidResidue, NITROGEN);
            String[] second_H = findData(secondAminoAcidResidue, HYDROGEN);

            if (second_H == null) {
                if (h_coordinates != null) {
                    second_H = h_coordinates;
                } else {
                    String[] preSecond_C = findData(preSecondAminoAcidResidue, CARBON);
                    String[] second_C = findData(secondAminoAcidResidue, CARBON_A);
                    second_H = findHydrogenData(second_C, second_N, preSecond_C);
                }
            }

            if (nonNull(first_C) && nonNull(first_O) && nonNull(second_N)) {
                return countEnergy(first_C, first_O, second_N, second_H);
            }
        }
        return 0.0;
    }

    @Override
    public RotationParams findHCoordinates(HydrogenAccuracy hydrogenAccuracy, List<Entry<Double, List<String[]>>> aminoAcidResidues,
                                           SortedMap<Double, List<String[]>> pdbData, int index, Boolean ai) {
        Double firstAminoAcidResidueKey = aminoAcidResidues.get(index).getKey();
        List<String[]> firstAminoAcidResidue = pdbData.get(firstAminoAcidResidueKey);
        String[] data_H = findData(firstAminoAcidResidue, HYDROGEN);
        String[] data_N = findData(firstAminoAcidResidue, NITROGEN);
        String[] data_CA = findData(firstAminoAcidResidue, CARBON_A);
        String[] data_C = null;

        if (index > 0) {
            Double preFirstAminoAcidResidueKey = aminoAcidResidues.get(index-1).getKey();
            if (firstAminoAcidResidueKey == preFirstAminoAcidResidueKey + 1.0) {
                List<String[]> preFirstAminoAcidResidue = pdbData.get(preFirstAminoAcidResidueKey);
                data_C = findData(preFirstAminoAcidResidue, CARBON);
            }
        }

        hydrogenAccuracy.setAminoAcid(firstAminoAcidResidue.get(0)[1]);
        hydrogenAccuracy.setAminoAcidNumber(firstAminoAcidResidueKey.toString());

        Double[] absolute_H;
        if (data_H != null) {
            absolute_H = fillRealHCoordinates(hydrogenAccuracy, data_H);
        } else {
            absolute_H = fillPredictedCoordinates(hydrogenAccuracy, aminoAcidResidues, pdbData, new Double[]{0.0, 0.0, 0.0}, firstAminoAcidResidue, firstAminoAcidResidueKey, index);
        }
        Double[] predicted_H = fillPredictedCoordinates(hydrogenAccuracy, aminoAcidResidues, pdbData, absolute_H, firstAminoAcidResidue, firstAminoAcidResidueKey, index);

        fillAccuracyDistance(hydrogenAccuracy, absolute_H, predicted_H);
        fillAllNearestAtoms(hydrogenAccuracy, absolute_H, pdbData);
        fillNextAminoAcid(hydrogenAccuracy, aminoAcidResidues, index, pdbData, firstAminoAcidResidueKey);



        if (ai) {
            final RotationParams rotationParams = rotationService.computeRotationParams(data_N, data_CA, data_C);

            final Double[] cosines = rotationParams.getCosines();
            final Boolean[] clockwiseRotations = rotationParams.getClockwiseRotations();

            // Нормализованое положение углерода относительно азота, из предыдцщей АК.
            Double[] real_C = calculateNRelatedCoordinates(data_C, data_N);
            Double[] rotated_C = rotationService.fullRotateAtom(real_C, cosines, clockwiseRotations);

            // Нормализованое положение углерода относительно азота, с которым он связан в данной АК.
            Double[] real_CA = calculateNRelatedCoordinates(data_CA, data_N);
            Double[] rotated_CA = rotationService.fullRotateAtom(real_CA, cosines, clockwiseRotations);

            // Нормализованое положение водорода относительно азота.
            Double[] real_H = calculateNRelatedCoordinates(data_H, data_N);
            Double[] rotated_H = rotationService.fullRotateAtom(real_H, cosines, clockwiseRotations);

            // Power calculations
            Double[] power = calculatePower();
            Double[] rotatedPower = rotationService.fullRotateAtom(power, cosines, clockwiseRotations);

            fillHydrogenAccuracy(hydrogenAccuracy, rotated_H, rotated_C, rotated_CA, rotatedPower);
            absolute_H = fillRealHCoordinates(hydrogenAccuracy, data_H);
            fillAccuracyDistance(hydrogenAccuracy, absolute_H, predicted_H);
            return rotationParams;
        }
        return null;
    }

    private Double[] calculatePower() {
        Double[] power = new Double[3];
        power[0] = nearestAtoms.stream().map(Atom::getX_power).reduce(0.0, Double::sum);
        power[1] = nearestAtoms.stream().map(Atom::getY_power).reduce(0.0, Double::sum);
        power[2] = nearestAtoms.stream().map(Atom::getZ_power).reduce(0.0, Double::sum);
        return power;
    }

    private Double[] calculateNRelatedCoordinates(String[] initialAtom, String[] N_atom) {
        Double[] atom = {0.0, 0.0, 0.0};
        if (initialAtom != null) {
            atom[0] = round(parseDouble(initialAtom[4]) - parseDouble(N_atom[4]) , 4);
            atom[1] = round(parseDouble(initialAtom[5]) - parseDouble(N_atom[5]), 4);
            atom[2] = round(parseDouble(initialAtom[6]) - parseDouble(N_atom[6]), 4);
        }
        return atom;
    }

    private void fillAllNearestAtoms(HydrogenAccuracy hydrogenAccuracy, Double[] predicted_h, SortedMap<Double, List<String[]>> pdbData) {
        nearestAtoms = new ArrayList<>();
        pdbData.values().forEach(aminoAcid -> findNearestAtoms(predicted_h, aminoAcid, parseDouble(hydrogenAccuracy.getAminoAcidNumber())));
        hydrogenAccuracy.setNearestAtoms(nearestAtoms);
    }

    private void findNearestAtoms(Double[] h_coordinates, List<String[]> aminoAcid, double currentNumber) {
        aminoAcid.forEach(ac_data -> {
            if (h_coordinates != null) {
                Double[] atom_coordinates = { parseDouble(ac_data[4]), parseDouble(ac_data[5]), parseDouble(ac_data[6]) };
                Double distance = distanceService.countDistance(h_coordinates, atom_coordinates);
                addAtomToNearestAtoms(distance, ac_data, h_coordinates, currentNumber);
            }
        });
    }

    private void addAtomToNearestAtoms(Double distance, String[] ac_data, Double[] h_coordinates, Double currentNumber) {
        if (distance < 5.0
            && ac_data[0].charAt(0) != 'H'
            && !(ac_data[0].equals("N") && parseDouble(ac_data[3]) == currentNumber)
            && !(ac_data[0].equals("CA") && parseDouble(ac_data[3]) == currentNumber)
            && !(ac_data[0].equals("C") && parseDouble(ac_data[3]) == currentNumber - 1.0)
        ) {
            Atom nearestAtom = new Atom();
            Double coulombsLawPower = computeCoulombsLaw(round(distance, 4), ac_data[0]);
            nearestAtom.setPower(coulombsLawPower);
            nearestAtom.setDistance(round(distance, 4));
            nearestAtom.setAtom_name(ac_data[0]);
            nearestAtom.setAC_name(ac_data[1]);
            nearestAtom.setAC_number(parseDouble(ac_data[3]));

            Double xBias = round(h_coordinates[0] - parseDouble(ac_data[4]),4);
            Double yBias = round(h_coordinates[1] - parseDouble(ac_data[5]),4);
            Double zBias = round(h_coordinates[2] - parseDouble(ac_data[6]),4);
            nearestAtom.setX_coordinate(String.valueOf(xBias));
            nearestAtom.setY_coordinate(String.valueOf(yBias));
            nearestAtom.setZ_coordinate(String.valueOf(zBias));

            Double generalBias = computeGeneralBias(xBias, yBias, zBias);
            nearestAtom.setX_power(computeCoordinatePower(xBias, generalBias, coulombsLawPower));
            nearestAtom.setY_power(computeCoordinatePower(yBias, generalBias, coulombsLawPower));
            nearestAtom.setZ_power(computeCoordinatePower(zBias, generalBias, coulombsLawPower));
            nearestAtoms.add(nearestAtom);
        }
    }

    private Double computeCoordinatePower(Double coordinateBias, Double generalBias, Double сoulombsLawPower) {
        Double powerPart = coordinateBias / generalBias;
        return round(powerPart * сoulombsLawPower, 4);
    }

    private Double computeGeneralBias(Double xBias, Double yBias, Double zBias) {
        double biasValue = 0.0;
        biasValue += xBias > 0 ? xBias : (xBias * (-1));
        biasValue += yBias > 0 ? yBias : (yBias * (-1));
        biasValue += zBias > 0 ? zBias : (zBias * (-1));
        return biasValue;
    }

    private Double computeCoulombsLaw(double distance, String ac_atom){
        Integer charge = getCharge(ac_atom);
        return round((8.99 * charge) / (distance * distance), 4);
    }

    private Integer getCharge(String ac_atom) {
        if (ac_atom.charAt(0) == 'C') {
            return 6;
        } else if (ac_atom.charAt(0) == 'N') {
            return 7;
        } else if (ac_atom.charAt(0) == 'O') {
            return 8;
        }
        return 0;
    }

    @Override
    public void findAccuracyHBound(HydrogenAccuracy hydrogenAccuracy, SortedMap<Double, List<String[]>> pdbData, Double co_residueKey,
                                   Double nh_residueKey, Double pre_nh_residueKey, List<AminoAcid> hBoundAminoAcids) {

        Double energy = isHBoundExistDouble(pdbData, co_residueKey, nh_residueKey, pre_nh_residueKey, null);
        if (o_h_distance != null && o_h_distance < 5.0) {
            AminoAcid ac = new AminoAcid();
            List<String[]> aminoAcidResidue = pdbData.get(co_residueKey);
            ac.sethBoundEnergy(round(energy, 4));
            ac.setAminoAcidResiduePDBNumber(Long.valueOf(aminoAcidResidue.get(0)[3]));
            ac.setAminoAcidName(aminoAcidResidue.get(0)[1]);
            ac.setHoDistance(round(o_h_distance, 3));
            for (String[] element : aminoAcidResidue) {
                if ("O".equals(element[0])) {
                    ac.setCoordinateOX(parseDouble(element[4]));
                    ac.setCoordinateOY(parseDouble(element[5]));
                    ac.setCoordinateOZ(parseDouble(element[6]));
                }
            }
            hBoundAminoAcids.add(ac);
        }
        String hb = hydrogenAccuracy.getHydrogenBoundAminoAcids();
        if (energy < MIN_E_H_BOUND) {
            hydrogenAccuracy.setHydrogenBoundAminoAcids(hb != null  && !hb.equals(" ") ? hb + "+" : "+");
        } else if (hb == null){
            hydrogenAccuracy.setHydrogenBoundAminoAcids(" ");
        }
    }

    private boolean isProline(List<String[]> secondAminoAcidResidue) {
        if (secondAminoAcidResidue.get(0) != null) {
            return secondAminoAcidResidue.get(0)[1].equals("P");
        }
        return false;
    }

    private String[] findData(List<String[]> firstAminoAcidResidue, String atom) {
        for (String[] atomData : firstAminoAcidResidue) {
            if(atomData[0].equals(atom)){
                return atomData;
            }
        }
        return null;
    }

    private String[] findHydrogenData(String[] second_C, String[] second_N, String[] preSecond_C) {
        Double[] a_coordinates = new Double[3];
        a_coordinates[0]= getMean(second_C[4], preSecond_C[4]);
        a_coordinates[1]= getMean(second_C[5], preSecond_C[5]);
        a_coordinates[2]= getMean(second_C[6], preSecond_C[6]);

        Double[] n_coordinates = getAtomCoordinates(second_N);
        Double[] c_coordinates = getAtomCoordinates(second_C);
        Double[] pre_c_coordinates = getAtomCoordinates(preSecond_C);

        String[] second_H = new String[7];
        second_H[0] = HYDROGEN;
        second_H[1] = second_N[1];
        second_H[2] = second_N[2];
        second_H[3] = second_N[3];
        leadingDistance = distanceService.countDistance(a_coordinates, n_coordinates);
        for (int i = 4; i < 7; i++) {
            second_H[i] = calculateCoordinate(a_coordinates[i-4], second_N[i]);
        }
        leadingDistance = null;
        return second_H;
    }

    private Double countEnergy(String[] first_C, String[] first_O,
                               String[] second_N, String[] second_H) {
        Double[] c_coordinates = getAtomCoordinates(first_C);
        Double[] o_coordinates = getAtomCoordinates(first_O);
        Double[] n_coordinates = getAtomCoordinates(second_N);
        Double[] h_coordinates = getAtomCoordinates(second_H);

        Double o_n_distance = distanceService.countDistance(o_coordinates, n_coordinates);
        Double c_h_distance = distanceService.countDistance(c_coordinates, h_coordinates);
        o_h_distance = distanceService.countDistance(o_coordinates, h_coordinates);
        Double c_n_distance = distanceService.countDistance(c_coordinates, n_coordinates);

        Double energy = 0.42 * 0.2 * ((1/o_n_distance) + (1/c_h_distance) - (1/o_h_distance) - (1/c_n_distance)) * 332;

        return energy;
    }

    private Double getMean(String first_Coordinate, String second_Coordinate) {
        return (parseDouble(first_Coordinate) + parseDouble(second_Coordinate)) / 2;
    }
    private String calculateCoordinate(Double meanCoordinate, String nCoordinate) {
        Double difference = meanCoordinate - parseDouble(nCoordinate);
        Double finalDifference = (leadingDistance + N_H_DISTANCE) * difference / leadingDistance;
        return valueOf(meanCoordinate - finalDifference);
    }

    private Double[] getAtomCoordinates(String[] second_n) {
        Double[] atom_coordinates = new Double[3];
        atom_coordinates[0] = parseDouble(second_n[4]);
        atom_coordinates[1]= parseDouble(second_n[5]);
        atom_coordinates[2]= parseDouble(second_n[6]);
        return atom_coordinates;
    }

    private Double[] fillRealHCoordinates(HydrogenAccuracy hydrogenAccuracy, String[] data_H) {
        if(data_H != null) {
            Double[] real_H = new Double[3];
            real_H[0] = parseDouble(data_H[4]);
            real_H[1] = parseDouble(data_H[5]);
            real_H[2] = parseDouble(data_H[6]);
            hydrogenAccuracy.setRealXCoordinate(real_H[0]);
            hydrogenAccuracy.setRealYCoordinate(real_H[1]);
            hydrogenAccuracy.setRealZCoordinate(real_H[2]);
            return real_H;
        }
        return null;
    }

    private Double[] fillPredictedCoordinates(HydrogenAccuracy hydrogenAccuracy,
                                              List<Entry<Double, List<String[]>>> aminoAcidResidues,
                                              SortedMap<Double, List<String[]>> pdbData,
                                              Double[] real_H, List<String[]> firstAminoAcidResidue,
                                              Double firstAminoAcidResidueKey, int index) {
        List<String[]> preFirstAminoAcidResidue = getPreFirstAminoAcidResidue(aminoAcidResidues, pdbData, index, firstAminoAcidResidueKey);
        if (preFirstAminoAcidResidue != null) {
            hydrogenAccuracy.setPreviousAminoAcid(preFirstAminoAcidResidue.get(0)[1]);
            if (!isProline(firstAminoAcidResidue)){
                String[] preFirst_C = findData(preFirstAminoAcidResidue, CARBON);
                String[] first_C = findData(firstAminoAcidResidue, CARBON_A);
                String[] first_N = findData(firstAminoAcidResidue, NITROGEN);
                String[] data_H = findHydrogenData(first_C, first_N, preFirst_C);
                hydrogenAccuracy.setPredictedXCoordinate(round(parseDouble(data_H[4]), 4));
                hydrogenAccuracy.setPredictedYCoordinate(round(parseDouble(data_H[5]), 4));
                hydrogenAccuracy.setPredictedZCoordinate(round(parseDouble(data_H[6]), 4));
                Double[] predicted_H = new Double[3];
                predicted_H[0] = parseDouble(data_H[4]);
                predicted_H[1] = parseDouble(data_H[5]);
                predicted_H[2] = parseDouble(data_H[6]);

                fillNHDistance(hydrogenAccuracy, predicted_H, first_N);

                return predicted_H;
            }
        }
        return null;
    }

    private void fillNHDistance(HydrogenAccuracy hydrogenAccuracy, Double[] real_h, String[] first_n) {
        if(real_h != null) {
            Double[] real_n = new Double[3];
            real_n[0] = parseDouble(first_n[4]);
            real_n[1] = parseDouble(first_n[5]);
            real_n[2] = parseDouble(first_n[6]);
            Double nhDistance = distanceService.countDistance(real_h, real_n);
            hydrogenAccuracy.setNhDistance(round(nhDistance, 4));
        }
    }

    private List<String[]> getPreFirstAminoAcidResidue(List<Entry<Double, List<String[]>>> aminoAcidResidues,
                                                       SortedMap<Double, List<String[]>> pdbData,
                                                       int index, Double firstAminoAcidResidueKey) {
        if (index > 0) {
            Double preFirstAminoAcidResidueKey = aminoAcidResidues.get(index-1).getKey();
            if (firstAminoAcidResidueKey - preFirstAminoAcidResidueKey <= 1) {
                return pdbData.get(preFirstAminoAcidResidueKey);
            }
        }
        return null;
    }

    private void fillAccuracyDistance(HydrogenAccuracy hydrogenAccuracy, Double[] real_h, Double[] predicted_h) {
        if (real_h != null && predicted_h != null) {
            Double distance = distanceService.countDistance(real_h, predicted_h);
            DecimalFormat df = new DecimalFormat("0.000");
            df.setRoundingMode(RoundingMode.UP);
            hydrogenAccuracy.setDistanceToReal(round(distance, 4));
        }
    }

    private void fillNextAminoAcid(HydrogenAccuracy hydrogenAccuracy,
                                   List<Entry<Double, List<String[]>>> aminoAcidResidues, int index,
                                   SortedMap<Double, List<String[]>> pdbData, Double firstAminoAcidResidueKey) {
        if (index + 1 < aminoAcidResidues.size()) {
            Double nextAminoAcidResidueKey = aminoAcidResidues.get(index + 1).getKey();
            if (nextAminoAcidResidueKey - firstAminoAcidResidueKey <= 1) {
                List<String[]> nextAminoAcidResidue = pdbData.get(nextAminoAcidResidueKey);
                hydrogenAccuracy.setNextAminoAcid(nextAminoAcidResidue.get(0)[1]);
            }
        }
    }

    private void fillHydrogenAccuracy(HydrogenAccuracy hydrogenAccuracy,
                                      Double[] hydrogen, Double[] carbon,
                                      Double[] carbonA, Double[] power) {
        hydrogenAccuracy.setRealXCoordinate(round(hydrogen[0], 4));
        hydrogenAccuracy.setRealYCoordinate(round(hydrogen[1], 4));
        hydrogenAccuracy.setRealZCoordinate(round(hydrogen[2], 4));

        hydrogenAccuracy.setCxCoordinate(round(carbon[0], 4));
        hydrogenAccuracy.setCyCoordinate(round(carbon[1], 4));
        hydrogenAccuracy.setCzCoordinate(round(carbon[2], 4));

        hydrogenAccuracy.setCaXCoordinate(round(carbonA[0], 4));
        hydrogenAccuracy.setCaYCoordinate(round(carbonA[1], 4));
        hydrogenAccuracy.setCaZCoordinate(round(carbonA[2], 4));

        hydrogenAccuracy.setPowerXCoordinate(round(power[0], 4));
        hydrogenAccuracy.setPowerYCoordinate(round(power[1], 4));
        hydrogenAccuracy.setPowerZCoordinate(round(power[2], 4));

        hydrogenAccuracy.setGeneralPower(round(
                (power[0] > 0 ? power[0] : -power[0]) +
                        (power[1]> 0 ? power[1] : -power[1]) +
                        (power[2] > 0 ? power[2] : -power[2]), 4));
        hydrogenAccuracy.setGeneralPower(round(
                (power[0] > 0 ? power[0] : -power[0]) +
                        (power[1]> 0 ? power[1] : -power[1]) +
                        (power[2] > 0 ? power[2] : -power[2]), 4));
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
