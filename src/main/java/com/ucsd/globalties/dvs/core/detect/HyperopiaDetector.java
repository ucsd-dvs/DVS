package com.ucsd.globalties.dvs.core.detect;

import com.ucsd.globalties.dvs.core.*;
import com.ucsd.globalties.dvs.core.model.DiseaseRecord;

public class HyperopiaDetector implements DiseaseDetector {

    public void detect(Patient p) {
        StringBuilder msg = new StringBuilder();

        Photo photo = p.getPhotos().get(0);        // Use horizontal picture for now.
        final double HYPEROPIA_THRESHOLD = 1.75;

        Crescent_info leftCrescent = photo.getLeftEye().getPupil().getCrescent();
        Crescent_info rightCrescent = photo.getRightEye().getPupil().getCrescent();

        DiseaseRecord diseaseRecord = new DiseaseRecord();
        diseaseRecord.setDiseaseName(EyeDisease.HYPEROPIA);

        if (leftCrescent.isCrescentIsAtBot() && rightCrescent.isCrescentIsAtBot()) {
            diseaseRecord.setStatus("PASS");
        } else if (leftCrescent.isCrescentIsAtTop() || rightCrescent.isCrescentIsAtTop()) {
            diseaseRecord.setStatus("REFER");
            if (leftCrescent.isCrescentIsAtBot()) {
                double diopter = Pupil.findClosestDiopter(leftCrescent.getCrescentSize());
                if (diopter > HYPEROPIA_THRESHOLD)
                    diseaseRecord.setDescription(String.format("Left eye crescent diopter is %.2f when allowed limit is %.2f\n", diopter, HYPEROPIA_THRESHOLD));
            }
            if (rightCrescent.isCrescentIsAtBot()) {
                double diopter = Pupil.findClosestDiopter(rightCrescent.getCrescentSize());
                if (diopter > HYPEROPIA_THRESHOLD)
                    diseaseRecord.setDescription(String.format("Right eye crescent diopter is %.2f when allowed limit is %.2f\n", diopter, HYPEROPIA_THRESHOLD));
            }
        } else {
            diseaseRecord.setStatus("PASS");
        }

        p.getDiseaseRecord().add(diseaseRecord);
    }
}