package com.ucsd.globalties.dvs.core.detect;

import com.ucsd.globalties.dvs.core.Patient;

/**
 * The basic interface that all disease detection classes implement.
 * TODO design this in a better way so that the EyeDisease is appropriately restricted
 * for each respective disease detector. Perhaps extend an abstract class with restrictive
 * functionality instead of implementing this interface directly.
 *
 * @author Rahul
 */
public interface DiseaseDetector {
    static final double MYOPIA_THRESHOLD = -3.25;
    static final double HYPEROPIA_THRESHOLD = 1.75;
    static final double STRABISMUS_ANGLE_THRESHOLD = 10;
    static final double STRABISMUS_DISTANCE_THRESHOLD = Math.PI / 2d;
    static final double ASTIGMATISM_THRESHOLD = -1.5;

    public void detect(Patient p);
}
