package com.ucsd.globalties.dvs.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.ucsd.globalties.dvs.core.detect.AnisometropiaDetector;
import com.ucsd.globalties.dvs.core.detect.AstigmatismDetector;
import com.ucsd.globalties.dvs.core.detect.CataractsDetector;
import com.ucsd.globalties.dvs.core.detect.DiseaseDetector;
import com.ucsd.globalties.dvs.core.detect.HyperopiaDetector;
import com.ucsd.globalties.dvs.core.detect.MyopiaDetector;
import com.ucsd.globalties.dvs.core.detect.StrabismusDetector;

/**
 * An enum to represent all of the diseases that this program detects.
 * Register a DiseaseDetector that implements an algorithm to detect
 * the disease in the constructor of the EyeDisease enum object.
 * @author Rahul
 *
 */
@AllArgsConstructor
public enum EyeDisease {
	ASTIGMATISM(new AstigmatismDetector()),
	STRABISMUS(new StrabismusDetector()),
	CATARACTS(new CataractsDetector()),
	ANISOMETROPIA(new AnisometropiaDetector()),
	MYOPIA(new MyopiaDetector()),
	HYPEROPIA(new HyperopiaDetector());
	  
	@Getter
	private DiseaseDetector detector;
}
