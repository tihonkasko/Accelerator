package com.accelerator.dto;

public class RotationParams {
    Double[] cosines;
    Boolean[] clockwiseRotations;

    public Double[] getCosines() {
        return cosines;
    }

    public void setCosines(Double[] cosines) {
        this.cosines = cosines;
    }

    public Boolean[] getClockwiseRotations() {
        return clockwiseRotations;
    }

    public void setClockwiseRotations(Boolean[] clockwiseRotations) {
        this.clockwiseRotations = clockwiseRotations;
    }
}
