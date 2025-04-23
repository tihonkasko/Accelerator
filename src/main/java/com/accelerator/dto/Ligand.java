package com.accelerator.dto;

public class Ligand {

    private String ligandName;
    private Double maxAcceptableDistance;
    private String pdbFile;

    public String getLigandName() {
        return ligandName;
    }

    public void setLigandName(String ligandName) {
        this.ligandName = ligandName;
    }

    public Double getMaxAcceptableDistance() {
        return maxAcceptableDistance;
    }

    public void setMaxAcceptableDistance(Double maxAcceptableDistance) {
        this.maxAcceptableDistance = maxAcceptableDistance;
    }

    public String getPDBFile() {
        return pdbFile;
    }

    public void setPdbFile(String PDBFile) {
        this.pdbFile = PDBFile;
    }
}
