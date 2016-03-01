package com.ucsd.globalties.dvs.core.detect;

import com.sun.javafx.binding.StringFormatter;
import com.sun.javafx.tk.DummyToolkit;
import com.ucsd.globalties.dvs.core.*;
import com.ucsd.globalties.dvs.core.model.DiseaseRecord;

public class MyopiaDetector implements DiseaseDetector {

    public MyopiaDetector() {}

    /**
     * The patient has Myopia if the diopter is less than -3.25
     * @param p Patient
     */
    public void detect(Patient p) {
        // TODO implement for vertical picture
        Photo photo = p.getPhotos().get(0);        // Use horizontal picture for now.
        final double MYOPIA_THRESHOLD = -3.25;

        Crescent_info leftCrescent = photo.getLeftEye().getPupil().getCrescent();
        Crescent_info rightCrescent = photo.getRightEye().getPupil().getCrescent();

        DiseaseRecord disease = new DiseaseRecord();
        disease.setMDiseaseName(EyeDisease.MYOPIA);

        double diopter;
        // Do left eye
        diopter = Pupil.findClosestDiopter(leftCrescent.getCrescentSize());
        disease.getMHorizontalImage().getMLeftEye().getMThresholds().put(
                DiseaseRecord.MYOPIA_THRESHOLD, Double.toString(MYOPIA_THRESHOLD));
        disease.getMHorizontalImage().getMLeftEye().getMValues().put(
                DiseaseRecord.MYOPIA_VALUE, Double.toString(diopter));

        // Do right eye
        diopter = Pupil.findClosestDiopter(rightCrescent.getCrescentSize());
        disease.getMHorizontalImage().getMRightEye().getMThresholds().put(
                DiseaseRecord.MYOPIA_THRESHOLD, Double.toString(MYOPIA_THRESHOLD));
        disease.getMHorizontalImage().getMRightEye().getMValues().put(
                DiseaseRecord.MYOPIA_VALUE, Double.toString(diopter));

        if (leftCrescent.isCrescentIsAtTop() && rightCrescent.isCrescentIsAtTop()) {
            disease.setMStatus(DiseaseRecord.PASS);
        } else if (leftCrescent.isCrescentIsAtBot() || rightCrescent.isCrescentIsAtBot()) {
            disease.setMStatus(DiseaseRecord.REFER);
//            if (leftCrescent.isCrescentIsAtBot()) {
//                double diopter = Pupil.findClosestDiopter(leftCrescent.getCrescentSize());
//                if (diopter < MYOPIA_THRESHOLD) {
//                    disease.setDescription(String.format("Left eye crescent diopter is %.2f when allowed limit is %.2f", diopter, MYOPIA_THRESHOLD));
//                }
//            }
//            if (rightCrescent.isCrescentIsAtBot()) {
//                double diopter = Pupil.findClosestDiopter(rightCrescent.getCrescentSize());
//                if (diopter < MYOPIA_THRESHOLD) {
//                    disease.setDescription(String.format("Right eye crescent diopter is %.2f when allowed limit is %.2f", diopter, MYOPIA_THRESHOLD));
//                }
//            }
        } else {
            disease.setMStatus(DiseaseRecord.PASS);
        }

        p.getDiseaseRecord().add(disease);
    }
}
