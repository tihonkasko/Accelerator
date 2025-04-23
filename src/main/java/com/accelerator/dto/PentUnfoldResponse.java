package com.accelerator.dto;

public class PentUnfoldResponse {

    private String fileName;
    private Integer secondaryStructureResource;

    public PentUnfoldResponse(String fileName, Integer secondaryStructureResource) {
        this.fileName = fileName;
        this.secondaryStructureResource = secondaryStructureResource;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getSecondaryStructureResource() {
        return secondaryStructureResource;
    }

    public void setSecondaryStructureResource(Integer secondaryStructureResource) {
        this.secondaryStructureResource = secondaryStructureResource;
    }
}
