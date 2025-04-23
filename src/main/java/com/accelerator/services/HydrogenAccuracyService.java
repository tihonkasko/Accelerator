package com.accelerator.services;

import com.accelerator.dto.HydrogenAccuracy;
import com.accelerator.dto.HydrogenAccuracyResponse;
import java.util.List;
import java.util.SortedMap;

public interface HydrogenAccuracyService {

    HydrogenAccuracyResponse calculateHydrogenAccuracyService(SortedMap<Double, List<String[]>> pdbData, Boolean ai);

    List<HydrogenAccuracy> findHBounds(SortedMap<Double, List<String[]>> pdbData, Boolean ai);
}
