package com.accelerator.services;

import java.util.List;
import java.util.SortedMap;

public interface BendService {

    boolean isBend(SortedMap<Double, List<String[]>> pdbData,
                   Double preResidueKey,
                   Double residueKey,
                   Double postResidueKey);
}
