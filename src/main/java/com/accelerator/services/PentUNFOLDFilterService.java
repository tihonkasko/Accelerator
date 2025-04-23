package com.accelerator.services;

import java.util.List;
import java.util.SortedMap;

public interface PentUNFOLDFilterService {

    List<String> filterDssp(List<String> dsspContext, String chainContext, boolean isFileNeeded);

    List<String> filterPdb(List<String> pdbContext, String chainContext);

    SortedMap<Double, List<String[]>> filterPdbToDssp(List<String> pdbContext, String chainContext);

    String getSequence();

    String filterSequence(String sequence);
}
