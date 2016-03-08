package com.ucsd.globalties.dvs.core;

import com.ucsd.globalties.dvs.core.detect.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * An enum to represent all of the diseases that this program detects.
 * Register a DiseaseDetector that implements an algorithm to detect
 * the disease in the constructor of the EyeDisease enum object.
 *
 * @author Rahul
 */
@AllArgsConstructor
public enum EyeDisease {
    MYOPIA(new MyopiaDetector()),
    HYPEROPIA(new HyperopiaDetector()),
    ASTIGMATISM(new AstigmatismDetector()),
    STRABISMUS(new StrabismusDetector()),
    CATARACTS(new CataractsDetector()),
    ANISOMETROPIA(new AnisometropiaDetector());


    @Getter
    private DiseaseDetector detector;
}
