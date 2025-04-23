package com.accelerator.dto;

import java.util.List;

public class HydrogenAccuracy {

    private String aminoAcidNumber;
    private String aminoAcid;
    private String previousAminoAcid;
    private String nextAminoAcid;
    private String hydrogenBoundAminoAcids;
    private Double realXCoordinate;
    private Double realYCoordinate;
    private Double realZCoordinate;

    private Double caXCoordinate;
    private Double caYCoordinate;
    private Double caZCoordinate;

    private Double cxCoordinate;
    private Double cyCoordinate;
    private Double czCoordinate;

    private Double predictedXCoordinate;
    private Double predictedYCoordinate;
    private Double predictedZCoordinate;

    private Double powerXCoordinate;
    private Double powerYCoordinate;
    private Double powerZCoordinate;
    private Double distanceToReal;
    private Double nhDistance;
    private Double generalPower;
    private List<AminoAcid> hBoundAminoAcids;
    private List<Atom> nearestAtoms;

    public String getAminoAcidNumber() {
        return aminoAcidNumber;
    }

    public void setAminoAcidNumber(String aminoAcidNumber) {
        this.aminoAcidNumber = aminoAcidNumber;
    }

    public String getAminoAcid() {
        return aminoAcid;
    }

    public void setAminoAcid(String aminoAcid) {
        this.aminoAcid = aminoAcid;
    }

    public String getPreviousAminoAcid() {
        return previousAminoAcid;
    }

    public void setPreviousAminoAcid(String previousAminoAcid) {
        this.previousAminoAcid = previousAminoAcid;
    }

    public String getNextAminoAcid() {
        return nextAminoAcid;
    }

    public void setNextAminoAcid(String nextAminoAcid) {
        this.nextAminoAcid = nextAminoAcid;
    }

    public String getHydrogenBoundAminoAcids() {
        return hydrogenBoundAminoAcids;
    }

    public void setHydrogenBoundAminoAcids(String hydrogenBoundAminoAcids) {
        this.hydrogenBoundAminoAcids = hydrogenBoundAminoAcids;
    }

    public Double getDistanceToReal() {
        return distanceToReal;
    }

    public void setDistanceToReal(Double distanceToReal) {
        this.distanceToReal = distanceToReal;
    }

    public Double getNhDistance() {
        return nhDistance;
    }

    public void setNhDistance(Double nhDistance) {
        this.nhDistance = nhDistance;
    }

    public List<AminoAcid> gethBoundAminoAcids() {
        return hBoundAminoAcids;
    }

    public void sethBoundAminoAcids(List<AminoAcid> hBoundAminoAcids) {
        this.hBoundAminoAcids = hBoundAminoAcids;
    }

    public List<Atom> getNearestAtoms() {
        return nearestAtoms;
    }

    public void setNearestAtoms(List<Atom> nearestAtoms) {
        this.nearestAtoms = nearestAtoms;
    }

    public Double getGeneralPower() {
        return generalPower;
    }

    public void setGeneralPower(Double generalPower) {
        this.generalPower = generalPower;
    }

    public Double getRealXCoordinate() {
        return realXCoordinate;
    }

    public void setRealXCoordinate(Double realXCoordinate) {
        this.realXCoordinate = realXCoordinate;
    }

    public Double getRealYCoordinate() {
        return realYCoordinate;
    }

    public void setRealYCoordinate(Double realYCoordinate) {
        this.realYCoordinate = realYCoordinate;
    }

    public Double getRealZCoordinate() {
        return realZCoordinate;
    }

    public void setRealZCoordinate(Double realZCoordinate) {
        this.realZCoordinate = realZCoordinate;
    }

    public Double getCaXCoordinate() {
        return caXCoordinate;
    }

    public void setCaXCoordinate(Double caXCoordinate) {
        this.caXCoordinate = caXCoordinate;
    }

    public Double getCaYCoordinate() {
        return caYCoordinate;
    }

    public void setCaYCoordinate(Double caYCoordinate) {
        this.caYCoordinate = caYCoordinate;
    }

    public Double getCaZCoordinate() {
        return caZCoordinate;
    }

    public void setCaZCoordinate(Double caZCoordinate) {
        this.caZCoordinate = caZCoordinate;
    }

    public Double getCxCoordinate() {
        return cxCoordinate;
    }

    public void setCxCoordinate(Double cxCoordinate) {
        this.cxCoordinate = cxCoordinate;
    }

    public Double getCyCoordinate() {
        return cyCoordinate;
    }

    public void setCyCoordinate(Double cyCoordinate) {
        this.cyCoordinate = cyCoordinate;
    }

    public Double getCzCoordinate() {
        return czCoordinate;
    }

    public void setCzCoordinate(Double czCoordinate) {
        this.czCoordinate = czCoordinate;
    }

    public Double getPowerXCoordinate() {
        return powerXCoordinate;
    }

    public void setPowerXCoordinate(Double powerXCoordinate) {
        this.powerXCoordinate = powerXCoordinate;
    }

    public Double getPowerYCoordinate() {
        return powerYCoordinate;
    }

    public void setPowerYCoordinate(Double powerYCoordinate) {
        this.powerYCoordinate = powerYCoordinate;
    }

    public Double getPowerZCoordinate() {
        return powerZCoordinate;
    }

    public void setPowerZCoordinate(Double powerZCoordinate) {
        this.powerZCoordinate = powerZCoordinate;
    }

    public Double getPredictedXCoordinate() {
        return predictedXCoordinate;
    }

    public void setPredictedXCoordinate(Double predictedXCoordinate) {
        this.predictedXCoordinate = predictedXCoordinate;
    }

    public Double getPredictedYCoordinate() {
        return predictedYCoordinate;
    }

    public void setPredictedYCoordinate(Double predictedYCoordinate) {
        this.predictedYCoordinate = predictedYCoordinate;
    }

    public Double getPredictedZCoordinate() {
        return predictedZCoordinate;
    }

    public void setPredictedZCoordinate(Double predictedZCoordinate) {
        this.predictedZCoordinate = predictedZCoordinate;
    }
}
