package com.accelerator.services;

import com.accelerator.dto.AminoAcid;
import com.accelerator.dto.HydrogenAccuracy;
import com.accelerator.dto.RotationParams;

import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

public interface HBoundService {

    boolean isHBoundExist(SortedMap<Double, List<String[]>> pdbData,
                          Double co_residueKey,
                          Double nh_residueKey,
                          Double pre_nh_ResidueKey,
                          String[] h_coordinates);

    RotationParams findHCoordinates(HydrogenAccuracy hydrogenAccuracy, List<Entry<Double, List<String[]>>> aminoAcidResidues,
                                    SortedMap<Double, List<String[]>> pdbData, int index, Boolean ai);

    void findAccuracyHBound(HydrogenAccuracy hydrogenAccuracy, SortedMap<Double, List<String[]>> pdbData,
                            Double co_residueKey,
                            Double nh_residueKey,
                            Double pre_nh_residueKey, List<AminoAcid> hBoundAminoAcids);
}
