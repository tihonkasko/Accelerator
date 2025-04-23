package com.accelerator.services;

import com.accelerator.dto.AminoAcid;
import com.accelerator.dto.Ligand;

import java.util.List;

public interface LigandPositionService {

    List<AminoAcid> getRelatedAminoAcids(Ligand ligand);
}
