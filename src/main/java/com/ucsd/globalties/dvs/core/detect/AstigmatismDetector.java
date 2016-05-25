package com.ucsd.globalties.dvs.core.detect;

import com.ucsd.globalties.dvs.core.*;
import com.ucsd.globalties.dvs.core.model.DiseaseRecord;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Detect Astigmatism in a Patient.
 * TODO port this algorithm from the Python code when Crescent detection is added
 *
 * @author Rahul
 */
@Slf4j
public class AstigmatismDetector implements DiseaseDetector {

    public void detect(Patient p) throws IOException{
        StringBuilder msg = new StringBuilder();
        DiseaseRecord diseaseRecord = new DiseaseRecord();
        diseaseRecord.setMDiseaseName(EyeDisease.ASTIGMATISM);

        if(p.getPhotos().size() < 2) {
            log.info("One or more images have been removed by algorithm so we cannot diagnose astigmatism");
            p.getDiseaseRecord().add(new DiseaseRecord(EyeDisease.ASTIGMATISM, 400));
            return;
        }
        Photo horizontal = p.getPhotos().get(0);
        Photo vertical   = p.getPhotos().get(1);

        Crescent_info leftHorizontal  = horizontal.getLeftEye().getPupil().getCrescent();
        Crescent_info rightHorizontal = horizontal.getRightEye().getPupil().getCrescent();

        Crescent_info leftVertical =  vertical.getLeftEye().getPupil().getCrescent();
        Crescent_info rightVertical = vertical.getRightEye().getPupil().getCrescent();

        double horizontalSize = leftHorizontal.getCrescentSize();
        double verticalSize   = leftVertical.getCrescentSize();

        diseaseRecord.setMStatus(DiseaseRecord.PASS);

        if( horizontalSize < verticalSize - verticalSize * 0.1 || horizontalSize > verticalSize + verticalSize * 0.1) {
            diseaseRecord.setMStatus(DiseaseRecord.REFER);
            diseaseRecord.setMDescription(String.format(
                    "Left eye crescent is asymmetrical: horizontal crescent size %.2f vertical crescent size %.2f\n",
                    horizontalSize, verticalSize));
        }

        horizontalSize = rightHorizontal.getCrescentSize();
        verticalSize   = rightVertical.getCrescentSize();
        if(horizontalSize < verticalSize - verticalSize * 0.1 || horizontalSize > verticalSize + verticalSize * 0.1) {
            diseaseRecord.setMStatus(DiseaseRecord.REFER);
            diseaseRecord.setMDescription(String.format(
                    "Left eye crescent is asymmetrical: horizontal crescent size %.2f vertical crescent size %.2f\n",
                    horizontalSize, verticalSize));
        }

        p.getDiseaseRecord().add(diseaseRecord);
    }
}
