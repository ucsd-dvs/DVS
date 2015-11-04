package com.ucsd.globalties.dvs.core.detect;

import com.ucsd.globalties.dvs.core.EyeDisease;
import com.ucsd.globalties.dvs.core.Patient;
import com.ucsd.globalties.dvs.core.Photo;
import com.ucsd.globalties.dvs.core.WhiteDot;

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
    private static final double DISTANCE_THRESHOLD = 10;
    private static final double ANGLE_THRESHOLD = Math.PI / 2d;

    public void detect(Patient p) {
        StringBuilder msg = new StringBuilder();

        //for (Photo photo : p.getPhotos()) {
        //msg.append("\nStats for " + photo.getType().toString().toLowerCase() + " photo:\n");

        Photo photo = p.getPhotos().get(0);        // Use horizontal picture for now.

        boolean distWarning = false, angleWarning = false;
        WhiteDot leftDot = photo.getLeftEye().getPupil().getWhiteDot();
        WhiteDot rightDot = photo.getRightEye().getPupil().getWhiteDot();

        // Compare difference between the distances of the two white dots => No, not doing this
        //double distDiff = Math.abs(leftDot.getDistance() - rightDot.getDistance());

        double leftDistDiff = leftDot.getDistance();
        double rightDistDiff = rightDot.getDistance();
        String distMsg = "";

        if (leftDistDiff > DISTANCE_THRESHOLD || rightDistDiff > DISTANCE_THRESHOLD) {
            distMsg = String.format("\tDistance of %.2f and %.2f detected when allowed limit is %.2f\n", leftDistDiff, rightDistDiff, DISTANCE_THRESHOLD);
            distWarning = true;
        }

        //double angleDiff = Math.abs(leftDot.getAngle() - rightDot.getAngle());
        double leftAngleDiff = leftDot.getAngle();
        double rightAngleDiff = rightDot.getAngle();
        String angleMsg = "";
      /*
      if (leftAngleDiff > ANGLE_THRESHOLD || rightAngleDiff > ANGLE_THRESHOLD) {
        angleMsg = String.format("\tAngle of %.2f and %.2f detected when allowed limit is %.2f\n", leftAngleDiff, rightAngleDiff, ANGLE_THRESHOLD);
        angleWarning = true;
      }*/

        if (distWarning && angleWarning) {
            //msg.append("Patient has Strabismus");
            msg.append("Refer\n");
            msg.append(distMsg);
            msg.append(angleMsg);
        } else if (distWarning || angleWarning) {
            //msg.append("Patient may have Strabismus");
            msg.append("Refer\n");
            msg.append(distMsg);
            msg.append(angleMsg);
        } else {
            msg.append("Pass");
        }

        p.getMedicalRecord().put(EyeDisease.STRABISMUS, msg.toString());
    }
}
