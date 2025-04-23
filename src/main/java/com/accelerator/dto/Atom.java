package com.accelerator.dto;

public class Atom {

    private String X_coordinate;
    private String Y_coordinate;
    private String Z_coordinate;
    private Double X_power;
    private Double Y_power;
    private Double Z_power;
    private String atom_name;
    private Double AC_number;
    private String AC_name;
    private Double distance;
    private Double power;

    public String getX_coordinate() {
        return X_coordinate;
    }

    public void setX_coordinate(String x_coordinate) {
        X_coordinate = x_coordinate;
    }

    public String getY_coordinate() {
        return Y_coordinate;
    }

    public void setY_coordinate(String y_coordinate) {
        Y_coordinate = y_coordinate;
    }

    public String getZ_coordinate() {
        return Z_coordinate;
    }

    public void setZ_coordinate(String z_coordinate) {
        Z_coordinate = z_coordinate;
    }

    public String getAC_name() {
        return AC_name;
    }

    public void setAC_name(String AC_name) {
        this.AC_name = AC_name;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getAtom_name() {
        return atom_name;
    }

    public void setAtom_name(String atom_name) {
        this.atom_name = atom_name;
    }

    public Double getAC_number() {
        return AC_number;
    }

    public void setAC_number(Double AC_number) {
        this.AC_number = AC_number;
    }

    public Double getPower() {
        return power;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public Double getX_power() {
        return X_power;
    }

    public void setX_power(Double x_power) {
        this.X_power = x_power;
    }

    public Double getY_power() {
        return Y_power;
    }

    public void setY_power(Double y_power) {
        this.Y_power = y_power;
    }

    public Double getZ_power() {
        return Z_power;
    }

    public void setZ_power(Double z_power) {
        this.Z_power = z_power;
    }
}
