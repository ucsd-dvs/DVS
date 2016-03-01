package com.ucsd.globalties.dvs.core.detect;

import com.sun.javafx.tk.DummyToolkit;
import com.ucsd.globalties.dvs.core.EyeDisease;
import com.ucsd.globalties.dvs.core.Patient;
import com.ucsd.globalties.dvs.core.Photo;
import com.ucsd.globalties.dvs.core.WhiteDot;
import com.ucsd.globalties.dvs.core.model.DiseaseRecord;

/**
 * Detect strabismus in a patient.
 * Currently, the algorithm gets the distance of the white dot from the center of the Pupil
 * and raises a warning if the difference of the distances between the white dots in the two eyes are
 * greater than a certain threshold (i.e. the white dots in each eye are too far apart, from a positional perspective
 * relative to their respective pupil).
 * <p>
 * Currently, the algorithm is inaccurate for cases when the detected pupil contains portions of the Eye,
 * because the threshold does not account for those cases. A fix in this regard would be for the Pupil detection
 * algorithm, so that the returned Mat is only a rect of the pupil/iris, instead of covering unnecessary eye area.
 * <p>
 * The fact that the pupil detection algorithm returns the iris is not an issue, because the white dots
 * are compared based on their relative distance to the "pupil" (which, in this case, is really the iris).
 * As long as the detected "pupil" is uniform in appearance, the threshold can be tweaked from an iris
 * perspective instead of a pupil perspective without losing accuracy (from a mathematical perspective).
 *
 * @author Rahul
 */
public class StrabismusDetector implements DiseaseDetector {
    /**
     * The patient has strabismus if the distance of the white dot is greater than
     * 10 from the center of the pupil or if the angle is greater than the threshold
     *
     * The angle is measured from the line of sight of the patients' eye to the line of
     * sight of an ideal eye (perpendicular to the eye)
     * @param p Patient
     */
    public void detect(Patient p) {
        final double DISTANCE_THRESHOLD = 10;
        final double ANGLE_THRESHOLD = Math.PI / 2d;

        // TODO implement for vertical picture
        Photo photo = p.getPhotos().get(0);        // Use horizontal picture for now.

        boolean distWarning = false, angleWarning = false;
        WhiteDot leftDot = photo.getLeftEye().getPupil().getWhiteDot();
        WhiteDot rightDot = photo.getRightEye().getPupil().getWhiteDot();

        double leftDistDiff = leftDot.getDistance();
        double rightDistDiff = rightDot.getDistance();

        double leftAngleDiff = leftDot.getAngle();
        double rightAngleDiff = rightDot.getAngle();

        DiseaseRecord diseaseRecord = new DiseaseRecord();
        diseaseRecord.setMDiseaseName(EyeDisease.STRABISMUS);

        // Do left eye
        diseaseRecord.getMHorizontalImage().getMLeftEye().getMThresholds().put(
                DiseaseRecord.STRABISMUS_DISTANCE_THRESHOLD, Double.toString(DISTANCE_THRESHOLD));
        diseaseRecord.getMHorizontalImage().getMLeftEye().getMValues().put(
                DiseaseRecord.STRABISMUS_DISTANCE_VALUE, Double.toString(leftDistDiff));
        diseaseRecord.getMHorizontalImage().getMLeftEye().getMThresholds().put(
                DiseaseRecord.STRABISMUS_ANGLE_THRESHOLD, Double.toString(ANGLE_THRESHOLD));
        diseaseRecord.getMHorizontalImage().getMLeftEye().getMValues().put(
                DiseaseRecord.STRABISMUS_ANGLE_VALUE, Double.toString(leftAngleDiff));


        // Do right eye
        diseaseRecord.getMHorizontalImage().getMRightEye().getMThresholds().put(
                DiseaseRecord.STRABISMUS_DISTANCE_THRESHOLD, Double.toString(DISTANCE_THRESHOLD));
        diseaseRecord.getMHorizontalImage().getMRightEye().getMValues().put(
                DiseaseRecord.STRABISMUS_DISTANCE_VALUE, Double.toString(rightDistDiff));
        diseaseRecord.getMHorizontalImage().getMRightEye().getMThresholds().put(
                DiseaseRecord.STRABISMUS_ANGLE_THRESHOLD, Double.toString(ANGLE_THRESHOLD));
        diseaseRecord.getMHorizontalImage().getMRightEye().getMValues().put(
                DiseaseRecord.STRABISMUS_ANGLE_VALUE, Double.toString(rightAngleDiff));

        if (distWarning && angleWarning) {
            diseaseRecord.setMStatus(DiseaseRecord.REFER);
        } else if (distWarning || angleWarning) {
            diseaseRecord.setMStatus(DiseaseRecord.REFER);
        } else {
            diseaseRecord.setMStatus(DiseaseRecord.PASS);
        }

        p.getDiseaseRecord().add(diseaseRecord);
    }
}
