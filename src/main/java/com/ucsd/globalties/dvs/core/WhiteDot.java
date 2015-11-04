package com.ucsd.globalties.dvs.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a detected white dot inside of a pupil.
 * Only the positional information is necessary, so this class 
 * probably shouldn't need changing (unless an additional variable 
 * is needed in the future for other disease detection algorithms).
 * @author Rahul
 *
 */
@AllArgsConstructor
public class WhiteDot {
  @Getter
  private double distance;
  @Getter
  private double area;
  @Getter
  private double angle;
  
  ///////////////// Methods ////////////////
  
  /* Method: getArea
   * 
   * Description: this method is used in the bounds checking
   * of getCrescent. The contour will be larger than the whitedot,
   * so the area of the whitedot will be used as a lower bound when 
   * searching for the crescent. 
   */
  public double getArea() {
	  	
	  return area;
  }
    
}

