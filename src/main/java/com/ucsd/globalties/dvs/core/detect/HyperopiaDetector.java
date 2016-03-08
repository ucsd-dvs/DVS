package com.ucsd.globalties.dvs.core.detect;

import com.sun.javafx.binding.DoubleConstant;
import com.ucsd.globalties.dvs.core.*;
import com.ucsd.globalties.dvs.core.model.DiseaseRecord;

public class HyperopiaDetector implements DiseaseDetector {

    /**
     * The patient has Hyperopia if the diopter is greater than 1.75
     * @param p Patient
     */
    public void detect(Patient p) {
        StringBuilder msg = new StringBuilder();

        // TODO implement vertical picture
        Photo photo = p.getPhotos().get(0);        // Use horizontal picture for now.

        Crescent_info leftCrescent = photo.getLeftEye().getPupil().getCrescent();
        Crescent_info rightCrescent = photo.getRightEye().getPupil().getCrescent();

        DiseaseRecord diseaseRecord = new DiseaseRecord();
        diseaseRecord.setMDiseaseName(EyeDisease.HYPEROPIA);

        // We save the details of the test regardless of whether it passes or not
        // b/c the client wants details of all tests

        double diopter;
        // Do left eye
        diopter = Pupil.findClosestDiopter(leftCrescent.getCrescentSize());
        diseaseRecord.getMHorizontalImage().getMLeftEye().getMValues().replace(
                DiseaseRecord.HYPEROPIA_VALUE, Double.toString(diopter));

        // Do Right eye
        diopter = Pupil.findClosestDiopter(rightCrescent.getCrescentSize());
        diseaseRecord.getMHorizontalImage().getMRightEye().getMValues().replace(
                DiseaseRecord.HYPEROPIA_VALUE, Double.toString(diopter));

        if (leftCrescent.isCrescentIsAtBot() && rightCrescent.isCrescentIsAtBot()) {
            diseaseRecord.setMStatus(DiseaseRecord.PASS);
        } else if (leftCrescent.isCrescentIsAtTop() || rightCrescent.isCrescentIsAtTop()) {
            diseaseRecord.setMStatus(DiseaseRecord.REFER);
//            if (leftCrescent.isCrescentIsAtBot()) {
//                double diopter = Pupil.findClosestDiopter(leftCrescent.getCrescentSize());
//                if (diopter > HYPEROPIA_THRESHOLD) {
//                    diseaseRecord.getMHorizontalImage().getMLeftEye().setMThreshold(Double.toString(HYPEROPIA_THRESHOLD));
//                    diseaseRecord.getMHorizontalImage().getMLeftEye().getMValues().add(Double.toString(diopter));
//                }
//            }
//            if (rightCrescent.isCrescentIsAtBot()) {
//                double diopter = Pupil.findClosestDiopter(rightCrescent.getCrescentSize());
//                if (diopter > HYPEROPIA_THRESHOLD) {
//                    diseaseRecord.getMHorizontalImage().getMRightEye().setMThreshold(Double.toString(HYPEROPIA_THRESHOLD));
//                    diseaseRecord.getMHorizontalImage().getMRightEye().getMValues().add(Double.toString(diopter));
//                }
//            }
        } else {
            diseaseRecord.setMStatus(DiseaseRecord.PASS);
        }

        p.getDiseaseRecord().add(diseaseRecord);
    }
}