package com.ucsd.globalties.dvs.core;


import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.atul.JavaOpenCV.Imshow;

/**
 * The Eye class represents a portion of the picture containing the eye.
 * 
 * @author Rahul
 *
 */
@Slf4j
public class Eye {
  
  @Getter
  private Mat mat;
  // the Photo from which this eye was derived
  private Photo photo;
  
  private Pupil pupil;
  
  private final double CROP_RATIO = 1.1;

  /**
   * Create a new Eye object with the "parent" photo and the Mat that
   * describes its pixels.
   * @param photo
   * @param mat
   */
  public Eye(Photo photo, Mat mat) {
    this.photo = photo;
    this.mat = mat;
  }

  /**
   * Lazy load/cache the pupil detection.
   * @return the pupil found in the eye Mat.
   */
  public Pupil getPupil() {
    if (pupil == null) {
      pupil = findPupil();
    }
    return pupil;
  }
  
  /*
   * Finds pupil in eye
   * 
   * Basically uses the same algorithm from 
   * http://docs.opencv.org/trunk/modules/imgproc/doc/feature_detection.html?highlight=cvhoughcircles#houghcircles
   * with small value tweaks.
   * @return Pupil
   */
  private Pupil findPupil() {
    //random code so that debug output will not override each other
    int code = Math.abs((new Random()).nextInt());
    Mat src = new Mat();
    Mat gray = new Mat();
    Mat invert = new Mat();
    //make copy of eye mat so we do not modify it
    mat.copyTo(src);
    /*
    Mat circles = new Mat();
    Imgproc.threshold(src, invert, 100, 255, Imgproc.THRESH_BINARY_INV);
//    Imshow inverted = new Imshow("Inverted");
//    inverted.showImage(invert);
    
    //convert to grayscale since HoughCircles, and other algorithms, require this
    Imgproc.cvtColor(invert, gray, Imgproc.COLOR_BGR2GRAY);
//    Imshow invertedAndGray= new Imshow("Inverted and Grayed");
//    invertedAndGray.showImage(gray);    
    
    
    Imgproc.dilate(gray, gray, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)));
    Imshow dilate = new Imshow("dilate");
//    dilate.showImage(gray);
    
    Imgproc.erode(gray, gray, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3)));
    Imshow erode= new Imshow("erode");
//    erode.showImage(gray);
    //Imgproc.equalizeHist(gray,gray);
    //smooth out image to prevent false circles
//    Imgproc.GaussianBlur(gray, gray, new Size(9,9), 2.0, 2.0);
//    Imshow blur= new Imshow("After Gaussian Blur");
//    blur.showImage(gray);    
    
    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    Imgproc.findContours(gray.clone(), contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
    Imgproc.drawContours(gray, contours, -1, new Scalar(255, 255, 255));
    Imshow con = new Imshow("contours");
//    con.showImage(gray);
    
    Imgproc.GaussianBlur(gray, gray, new Size(9,9), 2.0, 2.0);
    Imshow smooth = new Imshow("smooth");
//    smooth.showImage(gray);
    Imgproc.Canny(gray, gray, 150.0, 20.0);
    Imshow canny = new Imshow("canny");
//    canny.showImage(gray);
 
    //Finds circles
    //See http://docs.opencv.org/trunk/modules/imgproc/doc/feature_detection.html?highlight=cvhoughcircles#houghcircles
    //for parameter information
    Imgproc.HoughCircles(gray, circles, Imgproc.CV_HOUGH_GRADIENT, 2.0, (gray.height()/4.0), 150.0, 20.0, (gray.height()/16), (gray.height()/4));
    double[] finalPupil = circles.get(0, 0);
    if (circles.total() == 0) {
    	log.info("Pupil not found.");
    	return null;
  	}
    finalPupil[2] *= CROP_RATIO; // TODO: Change to final int constant 
    
    Mat fsrc = Mat.zeros(mat.rows(), mat.cols(), CvType.CV_8UC1);
    Core.circle(fsrc, new Point(finalPupil[0], finalPupil[1]), (int) finalPupil[2], new Scalar(255,0,0), -1);
    Imshow f = new Imshow("fsrc");
//    f.showImage(fsrc); 


    Mat dest = new Mat();
    mat.copyTo(dest, fsrc);
    
    Imshow dest1 = new Imshow("dest1");
    
    Mat destGray = new Mat();
    Mat innerCircle = new Mat();
    Imgproc.cvtColor(dest, destGray, Imgproc.COLOR_BGR2GRAY);
    Imgproc.HoughCircles(destGray, innerCircle, Imgproc.CV_HOUGH_GRADIENT, 2.0, (destGray.height()/4.0), 150.0, 20.0, (destGray.height()/16), (destGray.height()/4));
    double[] innerPupils = innerCircle.get(0, 0);

    if(innerPupils != null) {
      Core.circle(innerCircle, new Point(innerPupils[0], innerPupils[1]), (int) innerPupils[2], new Scalar(255, 0, 0), 2);
      dest.copyTo(innerCircle);
    }
    */


    /****************************************************************************/

    Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
    Imgproc.GaussianBlur(gray, gray, new Size(5,5), 9.0, 9.0);
    Imgproc.Canny(gray, gray, 5.0, 70.0);

    boolean foundPupil = false;
    double[] finalPupil = new double[3];
    //double[] accurayValues = { 2.0, 1.0, 1.5, 2.5 };
    double[] accurayValues = { 2.5, 2.0, 1.5, 1.0, 0.5, 0.4, 0.3, 0.2, 0.1, 0.001, 5.0, 10.0, 20.0 };
    Mat dest = new Mat();
    src.copyTo(dest);
    for(double accuracy : accurayValues) {
      MatOfPoint3f circles = new MatOfPoint3f();
      Imgproc.HoughCircles(gray, (Mat) circles, Imgproc.CV_HOUGH_GRADIENT, accuracy, (gray.height() / 4.0), 200.0, 100.0, (gray.height() / 16), gray.height() / 2);

      if(circles.toArray().length != 1)
        continue;

      for (Point3 circle : circles.toArray()) {
        int radius = (int) Math.round(circle.z);
        Point center = new Point(circle.x, circle.y);
        Core.circle(gray, center, 3, new Scalar(0, 255, 0), -1, 8, 0);
        Core.circle(gray, center, radius, new Scalar(255, 0, 0), 3, 8, 0);
        finalPupil[0] = circle.x;
        finalPupil[1] = circle.y;
        finalPupil[2] = circle.z;
      }
      break;

    }


    Imshow dest1 = new Imshow("foobar");
    Imshow dest2 = new Imshow("foobar");
    dest1.showImage(gray);
    dest2.showImage(src);
    /****************************************************************************/



    //======================> Logging pupils found <==============================
    
    log.info("Pupil found: x: {} y: {} r: finalPupil[0], finalPupil[1], finalPupil[2]");
    //Crop eye mat and create pupil mat
    Point topLeft = new Point(finalPupil[0]-finalPupil[2],finalPupil[1]-finalPupil[2]);
    Point bottomRight = new Point(finalPupil[0]+finalPupil[2],finalPupil[1]+finalPupil[2]);
    //check if top left point is negative and thus outside of image bounds and should be adjusted to be a valid point 
    //TODO do we even want these adjusted rects to be returned as pupils?
    if (topLeft.x < 0 || topLeft.y < 0) {
      log.warn("Top left point is out of image bounds ({},{}).", topLeft.x, topLeft.y);
      if (topLeft.x < 0) {
        topLeft.x = 0;
      }
      if (topLeft.y < 0) {
        topLeft.y = 0;
      }
      log.warn("Continuing with ({},{}).", topLeft.x, topLeft.y);
    }
    //check if bottom right point is larger than img size and thus should be adjusted
    if (bottomRight.x > dest.size().width || bottomRight.y > dest.size().height) {
      log.warn("Bottom right point is out of image bounds ({},{}).", bottomRight.x, bottomRight.y); 
      if (bottomRight.x > dest.size().width) {
        bottomRight.x = dest.size().width;
      }
      if (bottomRight.y > dest.size().height) {
        bottomRight.y = dest.size().height;
      }
      log.warn("Continuing with ({},{})", bottomRight.x, bottomRight.y);
    }
    Rect pupilArea = new Rect(topLeft, bottomRight);
    Mat pupilMat = new Mat(dest, pupilArea);
    dest2.showImage((pupilMat));

    if(pupilMat.empty()) {
      log.warn("[Eye.java] ERROR: Mat object should not be empty");
    }

    photo.appendPupilX(finalPupil[0]);
    return new Pupil(this, pupilMat);
  }
}
;
