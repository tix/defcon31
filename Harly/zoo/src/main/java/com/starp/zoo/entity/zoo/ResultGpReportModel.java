package com.starp.zoo.entity.zoo;

/**
 * @author covey
 */
public class ResultGpReportModel {
    private String identification;

    private String name;

    public ResultGpReportModel(String identification, String name) {
        this.identification = identification;
        this.name = name;
    }

    public ResultGpReportModel() {
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
