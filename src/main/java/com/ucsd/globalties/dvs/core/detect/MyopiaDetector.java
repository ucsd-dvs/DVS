package com.ucsd.globalties.dvs.core.detect;

import com.sun.javafx.binding.StringFormatter;
import com.ucsd.globalties.dvs.core.*;
import com.ucsd.globalties.dvs.core.model.DiseaseRecord;

public class MyopiaDetector implements DiseaseDetector {

    public void detect(Patient p) {
        Photo photo = p.getPhotos().get(0);        // Use horizontal picture for now.
        final double MYOPIA_THRESHOLD = -3.25;

        Crescent_info leftCrescent = photo.getLeftEye().getPupil().getCrescent();
        Crescent_info rightCrescent = photo.getRightEye().getPupil().getCrescent();

        DiseaseRecord disease = new DiseaseRecord();
        disease.setDiseaseName(EyeDisease.MYOPIA);

        if (leftCrescent.isCrescentIsAtTop() && rightCrescent.isCrescentIsAtTop()) {
            disease.setStatus("PASS");
        } else if (leftCrescent.isCrescentIsAtBot() || rightCrescent.isCrescentIsAtBot()) {
            disease.setStatus("REFER");
            if (leftCrescent.isCrescentIsAtBot()) {
                double diopter = Pupil.findClosestDiopter(leftCrescent.getCrescentSize());
                if (diopter < MYOPIA_THRESHOLD) {
                    disease.setDescription(String.format("Left eye crescent diopter is %.2f when allowed limit is %.2f", diopter, MYOPIA_THRESHOLD));
                }
            }
            if (rightCrescent.isCrescentIsAtBot()) {
                double diopter = Pupil.findClosestDiopter(rightCrescent.getCrescentSize());
                if (diopter < MYOPIA_THRESHOLD) {
                    disease.setDescription(String.format("Right eye crescent diopter is %.2f when allowed limit is %.2f", diopter, MYOPIA_THRESHOLD));
                }
            }
        } else {
            disease.setStatus("REFER");
        }

        p.getDiseaseRecord().add(disease);
    }
}
