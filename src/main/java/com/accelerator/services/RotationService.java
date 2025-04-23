package com.accelerator.services;

import com.accelerator.dto.RotationParams;

public interface RotationService {

    RotationParams computeRotationParams(String[] data_N, String[] data_CA, String[] data_C);

    Double[] fullRotateAtom(Double[] initialAtom, Double[] cosines, Boolean[] clockwiseRotations);

    Double[] fullBackRotateAtom(Double[] initialAtom, Double[] cosines, Boolean[] clockwiseRotations);
}
