package com.ucsd.globalties.dvs.core.detect;

import com.ucsd.globalties.dvs.core.EyeDisease;
import com.ucsd.globalties.dvs.core.Patient;
import com.ucsd.globalties.dvs.core.model.DiseaseRecord;

/**
 * Detect Cataracts in a Patient
 * TODO algorithm
 *
 * @author Rahul
 */
public class CataractsDetector implements DiseaseDetector {
    public void detect(Patient p) {
        p.getDiseaseRecord().add(new DiseaseRecord(EyeDisease.CATARACTS, DiseaseRecord.NOT_IMPLEMENTED));
    }
}
