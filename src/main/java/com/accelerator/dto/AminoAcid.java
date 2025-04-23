package com.accelerator.dto;

public class AminoAcid {

    private int id;
    private String aminoAcidName;
    private Long aminoAcidResiduePDBNumber;
    private String aminoAcidAtom;
    private Double nearestAtomDistance;
    private Double hBoundEnergy;
    private Double hoDistance;

    private Double coordinateOX;
    private Double coordinateOY;
    private Double coordinateOZ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAminoAcidName() {
        return aminoAcidName;
    }

    public void setAminoAcidName(String aminoAcidName) {
        this.aminoAcidName = aminoAcidName;
    }

    public Long getAminoAcidResiduePDBNumber() {
        return aminoAcidResiduePDBNumber;
    }

    public void setAminoAcidResiduePDBNumber(Long aminoAcidResiduePDBNumber) {
        this.aminoAcidResiduePDBNumber = aminoAcidResiduePDBNumber;
    }

    public String getAminoAcidAtom() {
        return aminoAcidAtom;
    }

    public void setAminoAcidAtom(String aminoAcidAtom) {
        this.aminoAcidAtom = aminoAcidAtom;
    }

    public Double getNearestAtomDistance() {
        return nearestAtomDistance;
    }

    public void setNearestAtomDistance(Double nearestAtomDistance) {
        this.nearestAtomDistance = nearestAtomDistance;
    }

    public Double gethBoundEnergy() {
        return hBoundEnergy;
    }

    public void sethBoundEnergy(Double hBoundEnergy) {
        this.hBoundEnergy = hBoundEnergy;
    }

    public Double getHoDistance() {
        return hoDistance;
    }

    public void setHoDistance(Double hoDistance) {
        this.hoDistance = hoDistance;
    }

    public Double getCoordinateOX() {
        return coordinateOX;
    }

    public void setCoordinateOX(Double coordinateOX) {
        this.coordinateOX = coordinateOX;
    }

    public Double getCoordinateOY() {
        return coordinateOY;
    }

    public void setCoordinateOY(Double coordinateOY) {
        this.coordinateOY = coordinateOY;
    }

    public Double getCoordinateOZ() {
        return coordinateOZ;
    }

    public void setCoordinateOZ(Double coordinateOZ) {
        this.coordinateOZ = coordinateOZ;
    }
}
