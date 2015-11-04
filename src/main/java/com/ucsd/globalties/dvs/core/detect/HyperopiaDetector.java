package com.ucsd.globalties.dvs.core.detect;

import com.ucsd.globalties.dvs.core.*;

public class HyperopiaDetector implements DiseaseDetector {

    public void detect(Patient p) {
        StringBuilder msg = new StringBuilder();

        Photo photo = p.getPhotos().get(0);        // Use horizontal picture for now.
        final double HYPEROPIA_THRESHOLD = 1.75;

        Crescent_info leftCrescent = photo.getLeftEye().getPupil().getCrescent();
        Crescent_info rightCrescent = photo.getRightEye().getPupil().getCrescent();

        if (leftCrescent.isCrescentIsAtBot() && rightCrescent.isCrescentIsAtBot()) {
            p.getMedicalRecord().put(EyeDisease.HYPEROPIA, "Pass");
        } else if (leftCrescent.isCrescentIsAtTop() || rightCrescent.isCrescentIsAtTop()) {
            msg.append("Refer\n");
            if (leftCrescent.isCrescentIsAtBot()) {
                double diopter = Pupil.findClosestDiopter(leftCrescent.getCrescentSize());
                if (diopter > HYPEROPIA_THRESHOLD)
                    msg.append(String.format("\tLeft eye crescent diopter is %.2f when allowed limit is %.2f\n", diopter, HYPEROPIA_THRESHOLD));
            }
            if (rightCrescent.isCrescentIsAtBot()) {
                double diopter = Pupil.findClosestDiopter(rightCrescent.getCrescentSize());
                if (diopter > HYPEROPIA_THRESHOLD)
                    msg.append(String.format("\tRight eye crescent diopter is %.2f when allowed limit is %.2f\n", diopter, HYPEROPIA_THRESHOLD));
            }
        } else {
            msg.append("Pass");
        }

        p.getMedicalRecord().put(EyeDisease.HYPEROPIA, msg.toString());
    }
}