package com.ucsd.globalties.dvs.core.detect;

import com.ucsd.globalties.dvs.core.EyeDisease;
import com.ucsd.globalties.dvs.core.Patient;

/**
 * Detect Cataracts in a Patient
 * TODO algorithm
 * @author Rahul
 *
 */
public class CataractsDetector implements DiseaseDetector {
  public void detect(Patient p) {
    p.getMedicalRecord().put(EyeDisease.CATARACTS, "N/A: WIP");
  }
}
