package com.accelerator.services.implementations;

import static java.lang.Double.parseDouble;
import static java.lang.Math.pow;
import static java.util.Objects.nonNull;

import com.accelerator.services.BendService;
import com.accelerator.services.DistanceService;
import java.util.List;
import java.util.SortedMap;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class BendServiceImpl implements BendService {

    private static final String CARBON_A = "CA";
    private static final Double COS_70 = 0.24202014332;

    @Resource
    private DistanceService distanceService;

    @Override
    public boolean isBend(SortedMap<Double, List<String[]>> pdbData,
                   Double preResidueKey,
                   Double residueKey,
                   Double postResidueKey) {

        List<String[]> preAminoAcidResidue = pdbData.get(preResidueKey);
        List<String[]> aminoAcidResidue = pdbData.get(residueKey);
        List<String[]> postSecondAminoAcidResidue = pdbData.get(postResidueKey);

        Double[] first_C = getAtomCoordinates(findData(preAminoAcidResidue, CARBON_A));
        Double[] midl_C = getAtomCoordinates(findData(aminoAcidResidue, CARBON_A));
        Double[] last_C = getAtomCoordinates(findData(postSecondAminoAcidResidue, CARBON_A));

        if (nonNull(first_C) && nonNull(midl_C) && nonNull(last_C)) {
            Double a_length = distanceService.countDistance(first_C, midl_C);
            Double b_length = distanceService.countDistance(midl_C, last_C);
            Double c_length = distanceService.countDistance(last_C, first_C);

            Double cos_b = findCosByLegs(a_length, b_length, c_length);
            return cos_b > COS_70;
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

    private Double[] getAtomCoordinates(String[] atom) {
        if (atom != null) {
            Double[] atom_coordinates = new Double[3];
            atom_coordinates[0] = parseDouble(atom[4]);
            atom_coordinates[1]= parseDouble(atom[5]);
            atom_coordinates[2]= parseDouble(atom[6]);
            return atom_coordinates;
        }
        return null;
    }

    private Double findCosByLegs(Double a_length, Double b_length, Double c_length) {
        return (pow(a_length, 2) + pow(b_length, 2) - pow(c_length, 2)) / (2 *  a_length * b_length);
    }
}
