package com.ucsd.globalties.dvs.core.detect;

import com.ucsd.globalties.dvs.core.Patient;

import java.io.IOException;

/**
 * The basic interface that all disease detection classes implement.
 * TODO design this in a better way so that the EyeDisease is appropriately restricted
 * for each respective disease detector. Perhaps extend an abstract class with restrictive
 * functionality instead of implementing this interface directly.
 *
 * @author Rahul
 */
public interface DiseaseDetector {
    public void detect(Patient p) throws IOException;
}
