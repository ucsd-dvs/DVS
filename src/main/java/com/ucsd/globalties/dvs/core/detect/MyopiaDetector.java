package com.ucsd.globalties.dvs.core.detect;

import com.ucsd.globalties.dvs.core.Crescent_info;
import com.ucsd.globalties.dvs.core.EyeDisease;
import com.ucsd.globalties.dvs.core.Patient;
import com.ucsd.globalties.dvs.core.Photo;
import com.ucsd.globalties.dvs.core.Pupil;
import com.ucsd.globalties.dvs.core.WhiteDot;

public class MyopiaDetector implements DiseaseDetector{

  public void detect(Patient p) {
	  StringBuilder msg = new StringBuilder();
	      
	  Photo photo = p.getPhotos().get(0);		// Use horizontal picture for now.
	  final double MYOPIA_THRESHOLD = -3.25;
	  
      Crescent_info leftCrescent = photo.getLeftEye().getPupil().getCrescent();
      Crescent_info rightCrescent = photo.getRightEye().getPupil().getCrescent(); 
      
      if(leftCrescent.isCrescentIsAtTop() && rightCrescent.isCrescentIsAtTop()) {
    	  p.getMedicalRecord().put(EyeDisease.MYOPIA, "Pass");
      }
      else if(leftCrescent.isCrescentIsAtBot() || rightCrescent.isCrescentIsAtBot()) {
    	  msg.append("Refer\n");
    	  if(leftCrescent.isCrescentIsAtBot()) {
    		  double diopter = Pupil.findClosestDiopter(leftCrescent.getCrescentSize());
    		  if( diopter < MYOPIA_THRESHOLD )
    			  msg.append(String.format("\tLeft eye crescent diopter is %.2f when allowed limit is %.2f\n", diopter, MYOPIA_THRESHOLD));
    	  }
    	  if(rightCrescent.isCrescentIsAtBot()) {
    		  double diopter = Pupil.findClosestDiopter(rightCrescent.getCrescentSize());
    		  if( diopter < MYOPIA_THRESHOLD )
    			  msg.append(String.format("\tRight eye crescent diopter is %.2f when allowed limit is %.2f\n", diopter, MYOPIA_THRESHOLD));
    	  }
      }
      else {
    	  msg.append("Pass");
      }
      
      p.getMedicalRecord().put(EyeDisease.MYOPIA, msg.toString());
  }
}
