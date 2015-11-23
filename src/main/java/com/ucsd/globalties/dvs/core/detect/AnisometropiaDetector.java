package com.ucsd.globalties.dvs.core.detect;

import com.ucsd.globalties.dvs.core.EyeDisease;
import com.ucsd.globalties.dvs.core.Patient;
import com.ucsd.globalties.dvs.core.model.DiseaseRecord;

/**
 * Detect Anisometropia in a Patient.
 * TODO port this algorithm from the Python code when Crescent detection is added
 *
 * @author Rahul
 */
public class AnisometropiaDetector implements DiseaseDetector {
    public void detect(Patient p) {
        p.getDiseaseRecord().add(new DiseaseRecord(EyeDisease.ANISOMETROPIA, "N/A: WIP", ""));
    }
}
