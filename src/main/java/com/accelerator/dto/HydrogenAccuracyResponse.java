package com.accelerator.dto;

import java.util.List;
import java.util.Map;

public class HydrogenAccuracyResponse {

    private List<HydrogenAccuracy> allList;
    private Map<String, Integer> composition;
    private Double averageNHDistance;
    private Double averageHHDistance;

    public List<HydrogenAccuracy> getAllList() {
        return allList;
    }

    public void setAllList(List<HydrogenAccuracy> allList) {
        this.allList = allList;
    }

    public Map<String, Integer> getComposition() {
        return composition;
    }

    public void setComposition(Map<String, Integer> composition) {
        this.composition = composition;
    }

    public Double getAverageNHDistance() {
        return averageNHDistance;
    }

    public void setAverageNHDistance(Double averageNHDistance) {
        this.averageNHDistance = averageNHDistance;
    }

    public Double getAverageHHDistance() {
        return averageHHDistance;
    }

    public void setAverageHHDistance(Double averageHHDistance) {
        this.averageHHDistance = averageHHDistance;
    }
}
