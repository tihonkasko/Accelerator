package com.accelerator.services.implementations;

import com.accelerator.dto.RotationParams;
import com.accelerator.services.RotationService;
import org.springframework.stereotype.Service;

import static java.lang.Double.parseDouble;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@Service
public class RotationServiceImpl implements RotationService {

    @Override
    public RotationParams computeRotationParams(String[] data_N, String[] data_CA, String[] data_C) {
        final Double[] cosines = { 1.0, 1.0, 1.0 };
        final Boolean[] clockwiseRotations = { true, true, true };

        if (data_C != null) {
            Double[] real_C = calculateNRelatedCoordinates(data_C, data_N);

            cosines[0] = calculateCosAlfa(real_C);
            clockwiseRotations[0] = real_C[1] < 0;
            Double[] rotated_C = rotateXAtom(real_C, cosines[0], clockwiseRotations[0]);

            cosines[1] = calculateCosBeta(rotated_C);
            clockwiseRotations[1] = rotated_C[2] < 0;
        }

        Double[] real_CA = calculateNRelatedCoordinates(data_CA, data_N);
        Double[] rotated_CA = rotateXAtom(real_CA, cosines[0], clockwiseRotations[0]);
        rotated_CA = rotateYAtom(rotated_CA, cosines[1], clockwiseRotations[1]);

        cosines[2] = calculateCosGamma(rotated_CA);
        clockwiseRotations[2] = rotated_CA[1] < 0;

        final RotationParams rotationParams = new RotationParams();
        rotationParams.setCosines(cosines);
        rotationParams.setClockwiseRotations(clockwiseRotations);
        return rotationParams;
    }

    @Override
    public Double[] fullRotateAtom(Double[] initialAtom, Double[] cosines, Boolean[] clockwiseRotations) {
        final Double[] X_rotedAtom = rotateXAtom(initialAtom, cosines[0], clockwiseRotations[0]);
        final Double[] Y_rotedAtom = rotateYAtom(X_rotedAtom, cosines[1], clockwiseRotations[1]);
        final Double[] Z_rotedAtom = rotateZAtom(Y_rotedAtom, cosines[2], clockwiseRotations[2]);
        return Z_rotedAtom;
    }

    @Override
    public Double[] fullBackRotateAtom(Double[] initialAtom, Double[] cosines, Boolean[] clockwiseRotations) {
        final Double[] Z_rotedAtom = rotateZAtom(initialAtom, cosines[2], !clockwiseRotations[2]);
        final Double[] Y_rotedAtom = rotateYAtom(Z_rotedAtom, cosines[1], !clockwiseRotations[1]);
        final Double[] X_rotedAtom = rotateXAtom(Y_rotedAtom, cosines[0], !clockwiseRotations[0]);
        return X_rotedAtom;
    }

    //                C
    //               /|
    //              / |
    //             /  |
    //          N /___n_____ c
    private double calculateCosAlfa(Double[] atom) {
        double X_NC = sqrt(pow(atom[0], 2) + pow(atom[1], 2));
        double X_Nn = atom[0] > 0 ? atom[0] : -atom[0];

        if (atom[0] < 0) {
            return X_Nn / X_NC;
        } else {
            return -(X_Nn / X_NC);
        }
    }

    private double calculateCosBeta(Double[] atom) {
        double Y_NC = sqrt(pow(atom[0], 2) + pow(atom[2], 2));
        double Y_Nn = atom[0] > 0 ? atom[0] : -atom[0];

        if (atom[0] < 0) {
            return Y_Nn / Y_NC;
        } else {
            return -(Y_Nn / Y_NC);
        }
    }

    private double calculateCosGamma(Double[] atom) {
        double Z_NCa = sqrt(pow(atom[1], 2) + pow(atom[2], 2));
        double Z_Nn = atom[2] > 0 ? atom[2] : -atom[2];

        if (atom[2] > 0) {
            return Z_Nn / Z_NCa;
        } else {
            return  -(Z_Nn / Z_NCa);
        }
    }

    private Double[] rotateXAtom(Double[] initialAtom, double cos, boolean clockwiseRotation) {
        if (clockwiseRotation) {
            return clockwiseXRotation(cos, initialAtom);
        } else {
            return counterclockwiseXRotation(cos, initialAtom);
        }
    }

    private Double[] rotateYAtom(Double[] initialAtom, double cos, boolean clockwiseRotation) {
        if (clockwiseRotation) {
            return clockwiseYRotation(cos, initialAtom);
        } else {
            return counterclockwiseYRotation(cos, initialAtom);
        }
    }

    private Double[] rotateZAtom(Double[] initialAtom, double cos, boolean clockwiseRotation) {
        if (clockwiseRotation) {
            return clockwiseZRotation(cos, initialAtom);
        } else {
            return counterclockwiseZRotation(cos, initialAtom);
        }
    }

    private Double[] clockwiseXRotation(double cos, Double[] element) {
        double sin = sqrt(1 - pow(cos, 2));
        Double [] newElement = {0.0, 0.0, 0.0};
        newElement[0] = (cos * element[0]) + (sin * element[1]);
        newElement[1] = (-sin * element[0]) + (cos * element[1]);
        newElement[2] = element[2];
        return newElement;
    }

    private Double[] counterclockwiseXRotation(double cos, Double[] element) {
        double sin = sqrt(1 - pow(cos, 2));
        Double [] newElement = {0.0, 0.0, 0.0};
        newElement[0] = (cos * element[0]) + (-sin * element[1]);
        newElement[1] = (sin * element[0]) + (cos * element[1]);
        newElement[2] = element[2];
        return newElement;
    }

    private Double[] clockwiseYRotation(double cos, Double[] element) {
        double sin = sqrt(1 - pow(cos, 2));
        Double [] newElement = {0.0, 0.0, 0.0};
        newElement[0] = (cos * element[0]) + (sin * element[2]);
        newElement[1] = element[1];
        newElement[2] = (-sin * element[0]) + (cos * element[2]);
        return newElement;
    }

    private Double[] counterclockwiseYRotation(double cos, Double[] element) {
        double sin = sqrt(1 - pow(cos, 2));
        Double [] newElement = {0.0, 0.0, 0.0};
        newElement[0] = (cos * element[0]) + (-sin * element[2]);
        newElement[1] = element[1];
        newElement[2] = (sin * element[0]) + (cos * element[2]);
        return newElement;
    }

    private Double[] clockwiseZRotation(double cos, Double[] element) {
        double sin = sqrt(1 - pow(cos, 2));
        Double [] newElement = {0.0, 0.0, 0.0};
        newElement[0] = element[0];
        newElement[1] = (cos * element[1]) + (sin * element[2]);
        newElement[2] = (-sin * element[1]) + (cos * element[2]);
        return newElement;
    }

    private Double[] counterclockwiseZRotation(double cos, Double[] element) {
        double sin = sqrt(1 - pow(cos, 2));
        Double [] newElement = {0.0, 0.0, 0.0};
        newElement[0] = element[0];
        newElement[1] = (cos * element[1]) + (-sin * element[2]);
        newElement[2] = (sin * element[1]) + (cos * element[2]);
        return newElement;
    }

    private Double[] calculateNRelatedCoordinates(String[] initialAtom, String[] N_atom) {
        Double[] atom = {0.0, 0.0, 0.0};
        if (initialAtom != null) {
            atom[0] = parseDouble(initialAtom[4]) - parseDouble(N_atom[4]);
            atom[1] = parseDouble(initialAtom[5]) - parseDouble(N_atom[5]);
            atom[2] = parseDouble(initialAtom[6]) - parseDouble(N_atom[6]);
        }
        return atom;
    }
}
