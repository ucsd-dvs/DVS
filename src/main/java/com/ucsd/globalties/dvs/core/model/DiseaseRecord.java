package com.ucsd.globalties.dvs.core.model;

import com.ucsd.globalties.dvs.core.EyeDisease;
import lombok.Getter;
import lombok.Setter;

public class DiseaseRecord {
    @Getter
    @Setter
    private EyeDisease diseaseName;
    @Getter
    @Setter
    private String status;
    @Getter
    @Setter
    private String description;

    public DiseaseRecord() {}

    public DiseaseRecord(EyeDisease name, String status, String description) {
        this.diseaseName = name;
        this.status = status;
        this.description = description;
    }
}