package com.ucsd.globalties.dvs.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import com.atul.JavaOpenCV.Imshow;
import com.ucsd.globalties.dvs.core.tools.Pair;

/**
 * Pupil class represents a detected pupil.
 * It has a white dot and a crescent, which are used for disease detection algorithms.
 * @author Rahul
 *
 */
@Slf4j
public class Pupil {

    private final boolean _DEBUG = false;

    @Getter
    private Eye eye; // the Eye from which this Pupil was derived
    @Getter
  /*Mat is a n-dimensional dense array that can store images, 2d complex arrays,
   * or a matrix of 16-bit signed integers for algebraic operations*/
    private Mat mat;
    private static TreeMap<Double, Double> thresholdMap = new TreeMap<Double, Double>();

    private WhiteDot whiteDot;
    private Crescent_info crescent_info;

    private Imshow im = new Imshow("A");
    private Imshow im2 = new Imshow("B");
    private Imshow im3 = new Imshow("C");
    private Imshow imF = new Imshow("X");

  /*Important Values used in the code*/

    /*RBG values*/
    public final int WHITE = 255;
    /*This value might need to be alternated to allow the threshold effect to make the cresent
     * more prominent*/
    public final int GRAY = 250; //treshold value for white-dot
    public final int CRESCENT_THRESHOLD = 200; //threshold value for crescent
    public final int BLACK = 0;

    /*Values for contouring*/
    public int fillCONTOURS = -1;
    public int contourTHICKNESS = -1;

    /*Values for the circle*/
    public int circleTHICKNESS = 1;


    public Pupil(Eye eye, Mat mat) {
        this.eye = eye;
        this.mat = mat;
        fillMap(thresholdMap);
    }

    /**
     * Detect the white dot here. The idea is to return a double value (or maybe a
     * simple object that describes the white dot's size/position/necessary
     * information to detect diseases. I don't think we really need to crop it out
     * of the image; the positional information will probably suffice.
     *
     * @return a WhiteDot object identifying the white dot's positional information
     * relative to the pupil
     */
    public WhiteDot getWhiteDot() {
        if (whiteDot != null) {
            return whiteDot;
        }

	    /*Creating the image container to hold the imported image*/
        Mat src = new Mat();
	    /*Copies the data from the private mat variable into src*/
        mat.copyTo(src);
	    /*This will hold the gray-scaled image*/
        Mat gray = new Mat();

	    /*Converts the image from src into gray-scale and stores it into gray*/
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

	    /*This is the test image to test if the image will be converted to grayscale*/
        Highgui.imwrite("gray-test.jpg", gray);
        Highgui.imread("gray-test.jpg");

	    /*Applies the threshold effect on the image for white values. Takes the image in
	     * gray and detects pixels of GRAY and turns it WHITE, and it stores it back to gray.*/
        Double thresh = Imgproc.threshold(gray, gray, GRAY, WHITE, Imgproc.THRESH_BINARY);

	    /*Creating a list to hold the values of the detected contours. Each contour
	     * found will be stored as a vector of points*/
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

	    /*This will find the contours in a cloned gray and store the points of those contours
	     * in contours list.
	     * Imgproc.RETR_LIST = 1; Retrieves all of the contours without establishing any hierarchical
	     * relationships
	     * Imgproc.CHAIN_APPROX_SIMPLE = 2; Stores absolutely all the contour points.
	     * These are static final int constants defined in Imgproc object.
	     * */
        Imgproc.findContours(gray.clone(), contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

	    /*This draws the draws contour outlines in the image if thickness >= 0 or fills the area
	     * bounded by the contours if thickness<0. thickness is last parameter. fillCONTOURS will allow
	     * all the contours to be drawn.*/
        Imgproc.drawContours(gray, contours, fillCONTOURS, new Scalar(WHITE, WHITE, WHITE), contourTHICKNESS);

	    /*If the contours list has nothing in it, then it means that the patient does not have contours in
	     * their image. Stop function.*/
        if (contours.isEmpty()) {
            log.error("No contours found for this pupil.");
            return null;
        }

	    /*Creating a point representing a location in (x,y) coordinate space, specified in integer precision.
	     * This sets up a pointer to point to the very center of the image. As you can see, we have x point
	     * to half of mat(the image) and for the y axis to half of mat's height. TODO*/
        java.awt.Point pupilCenter = new java.awt.Point(mat.width() / 2, mat.height() / 2);

	    /*List holding the distances in the contours. This will hold pairs(left, right), where left is the contour
	     * and right is the contour's distance from the center of the pupil*/
        List<Pair<MatOfPoint, Double>> contourDistances = new ArrayList<>(contours.size());

	    /*For-loop will go through the contours list and evaluate each of the contour points found in the list.
	     * It will store the distance (in contourDistances) it finds between the center of the pupil to the
	     * contours it locates. */
        for (int i = 0; i < contours.size(); i++) {

	      /*Creates rectangle object. boundingRect calculates the up-right bounding rectangle of a point set using
	       * the value count in contours.*/
            Rect rect = Imgproc.boundingRect(contours.get(i));

	      /*To obtain the radius for the circle*/
            int radius = rect.width / 2;

	      /*Creates a circle using the information from the rectangle object.
	       * circle(Mat img, Point center, int radius, Scalar color, int thickness).
	       * Thickness is the outline of the circle.*/
            Core.circle(src, new Point(rect.x + rect.width / 2, rect.y + rect.width / 2), radius,
                    new Scalar(WHITE, BLACK, BLACK), circleTHICKNESS);

	      /*Points to the center of the circle*/
            java.awt.Point center = new java.awt.Point(rect.x + radius, rect.y + radius);

	      /*Gets the distance between the pupil to the contour and stores it as pairs in the
	       * contourDistance list. First element is the value of the contour, then the second is
	       * the distance between the pupil center to the contour.*/
            contourDistances.add(new Pair<>(contours.get(i), pupilCenter.distanceSq(center)));
        }

	    /*sort the contours based on the distance from the center of the pupil (ascending)*/
        contourDistances.sort(contourCompare);

	    /*Empty pair object*/
        Pair<MatOfPoint, Double> whiteDotPair = null;

	    /*For-each loop: For each pair found in the contourDistances list, find the closest contour that matches
	     * certain criteria (currently checks for size)*/
        for (Pair<MatOfPoint, Double> pair: contourDistances) {

	      /* pair.getLeft() is the contour and pair.getRight() is the contour's distance from the
	       * center of the pupil*/
	      /*This calculates the contour area and stores it into area*/
            double area = Imgproc.contourArea(pair.getLeft());

	      /*This will print out that the white dot is currently at the contour's distance from the center of
	       * the pupil. It will also tell us the current area of the contour*/
            log.info("whiteDot distance: {}, area: {}", pair.getRight(), area);

	      /*NEEDS TUNING: This is suppose to check the bounds*/
            if (area < 10 || area > 200.0) { //possibly change lower area to ~75, upper to

	        /*If the area falls between these ranges, then reiterate up the loop and don't evaluate whats
	         * below this if statement.*/
                continue;
            }
	      /*If the area doesn't call between those ranges, then continue onto the loop and break.*/

	      /*Stores the pair with the information about the contour's area and the distance between the
	       * contour and the center of the pupil into the whiteDotPair.*/
            whiteDotPair = pair;

	      /*Prints out information about the area of the found contour.*/
            log.info("selected pair with area: " + area);

	      /*Escape the for-loop*/
            break;
        }

	    /*If whiteDotPair is null, meaning that the area was never within the correct ranges, then we can't
	     *  detect the white dot in the eye.*/
        if (whiteDotPair == null) {
            log.error("[WhiteDot Detection] Unable to find suitable white dot");
            return null;
        }

	    /* whiteDotPair.getLeft() is the contour(of type MatOfPoint) and whiteDotPair.getRight() is the contour's
	     * distance from the center of the pupil*/
	    /*assume white dot is the contour closest to the center of the image*/
        MatOfPoint whiteDotContour = whiteDotPair.getLeft();

	    /*This creates a rectangle by calculating the up-right bounding rectangle of a point set.
	     * The function calculates and returns the minimal up-right bounding rectangle for the
	     * specified point set.
	     * Basically, creating a rectangle out of the value of the contour*/
        Rect rect = Imgproc.boundingRect(whiteDotContour);

	    /*Radius to center*/
        int radius = rect.width / 2;

	    /*Calculates area by pi * ((rectangle's width / 2)^2) */
        double wdarea = Math.PI * Math.pow(radius, 2);

	    /*Make new pointer point to the center of the rectangle*/
        java.awt.Point whiteDotCenter = new java.awt.Point(rect.x + radius, rect.y + radius);

	    /*Calculates distance between the pupil center and the white dot*/
        double distance = pupilCenter.distance(whiteDotCenter);

	    /*Calculate the difference in the distance between the white dot and the pupil center along the x-axis*/
        double xDist = whiteDotCenter.x - pupilCenter.x;

	    /*If the xdistance is greater than the distance between the pupil center and the white dot is greater,
	     * then it means that the distance calculation was incorrect*/
        if (xDist > distance) {
            log.error("[WhiteDot Detection] unfulfilled invariant: adjacent edge of triangle is bigger than hypotenuse");
            return null;
        }

	    /*Print out information about the x distance and the distance between pupil center and white dot.*/
        log.info("[WhiteDot Detection] Computing angle for xDist: {}, dist: {}", xDist, distance);
	    /*Calculates the arc-cosine of the x-distance and the distance to find the y-distance or height.*/
        double angle = Math.acos(xDist / distance);

	    /*Print information about the white dot detection*/
        log.info("[WhiteDot Detection] computed white dot with distance: {}, angle: {}, area: {}", distance,
                Math.toDegrees(angle), wdarea);

        try
        {
            getCrescent();
        }

        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }

	    /*Sets current info about white dot to a WhiteDot object (defined in WhiteDot.java)*/
        this.whiteDot = new WhiteDot(distance, wdarea, angle);
        return whiteDot;
    }

    private static Comparator<Pair<MatOfPoint, Double>> contourCompare = new Comparator<Pair<MatOfPoint, Double>>() {
        public int compare(Pair<MatOfPoint, Double> p1, Pair<MatOfPoint, Double> p2) {
            return p1.getRight() < p2.getRight() ? -1 : 1;
        }
    };

    /**
     * Return crescent information.
     * TODO when better pictures are taken
     * @return
     */
    @SuppressWarnings("null")
    public Crescent_info getCrescent() {

        if (crescent_info != null) {
            return crescent_info;
        }

        Mat source = new Mat();
        mat.copyTo(source);		// copies the instance variable into source to preserve the original file

        Mat hsv_converted = new Mat();
        Mat grayscale = new Mat();
        Mat thresholded_mat = new Mat();
        Mat crescent_mat = new Mat();
        Mat ring_removed_mat = new Mat();
        Mat skin_threshold = new Mat();
        Mat whiteDot_Thresholded = new Mat();
        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();
        MatOfPoint crescent_contour = null;
        Moments crescent_moments = new Moments();

        double maxContourSize = 0.0;
        double mat_y_axis_divider = mat.height() / 2.0;
        double crescent_y_axis;

        Imgproc.cvtColor(source, grayscale, Imgproc.COLOR_BGR2GRAY);		// Convert to grayscale

        Imgproc.threshold(grayscale, whiteDot_Thresholded, GRAY, WHITE, Imgproc.THRESH_BINARY);		// get whiteDot Threshold image

	  /* those values above 190, will turned to white, the remaining values to black*/
        Imgproc.threshold(grayscale, thresholded_mat, CRESCENT_THRESHOLD, WHITE, Imgproc.THRESH_BINARY);

        if(_DEBUG) {
            im2.showImage(whiteDot_Thresholded);
            im.showImage(thresholded_mat);
        }

        Core.subtract(thresholded_mat, whiteDot_Thresholded, crescent_mat);		// At this point, the mat should have crescent, eye-lid and sclera only.

        if(_DEBUG)
            im3.showImage(crescent_mat);


        //Kernel size corresponds to the grid around the center pixel which detects blobs
        int kernel_size = 3; //Corresponds to a 3x3 grid (The higher the number, the less that gets removed)
        //int kernel_offset = 1; //Center of the kernel??
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(kernel_size, kernel_size));
        //val kernel = cvCreateStructuringElementEx(kernel_size,kernel_size,kernel_offset,kernel_offset,Imgproc.MORPH_RECT, new Mat());
        //Imshow test = new Imshow("Before erode");

	  /*
	   * AT THIS POINT (BEFORE ERODE) YOU CAN SEE THE OUTLINE OF THE WHITE DOT AND CRESCENT
	   *
	   */

        //test.showImage(crescent_mat);
        this.erodeAndDilate(crescent_mat, ring_removed_mat, kernel);		// This erosion & dilation is used for the "ring" removal
        this.getSkinThreshold(source, skin_threshold);						// Stores skin threshold of the original image into skin_threshold

        //Core.subtract(ring_removed_mat, skin_threshold, crescent_mat);		// At this point, crescent_mat should only have crescent and sclera depending on
        // pupil team's progression

        Core.subtract(crescent_mat, ring_removed_mat, crescent_mat);
	  /*

	  			******SUBTRACT TAKES AWAY MYOPIA ******

	  */


        Imshow rrm = new Imshow("Ring Removed Mat");
        Imshow cm = new Imshow("CM");
        //rrm.showImage(ring_removed_mat);
        //cm.showImage(crescent_mat);


        //test.showImage(crescent_mat);
        Imgproc.findContours(crescent_mat.clone(), contourList, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(crescent_mat, contourList, fillCONTOURS, new Scalar(WHITE, WHITE, WHITE), contourTHICKNESS);
        //test.showImage(crescent_mat);
        //test.showImage(crescent_mat);

        if(_DEBUG)
            imF.showImage(crescent_mat);

        if(contourList.isEmpty()) {
            log.error("No contour found");
            return new Crescent_info(0.0, false, false);
        }

        // find the largest contour (should be crescent if sclera isn't in the image and assuming the previous algorithm worked)
        for (MatOfPoint currContour : contourList) {

            if(Imgproc.contourArea(currContour) > maxContourSize) {
                maxContourSize = Imgproc.contourArea(currContour);
                crescent_contour = currContour;
            }
        }

        // find the y-axis (from center of mass) of crescent contour
        crescent_moments = Imgproc.moments(crescent_contour, false);
        crescent_y_axis = (double)(crescent_moments.get_m01() / crescent_moments.get_m00());

        //TODO: make info instance
        boolean crescentIsAtTop = (crescent_y_axis < mat_y_axis_divider) ? true : false;

        return new Crescent_info(maxContourSize, crescentIsAtTop, !crescentIsAtTop);
    }

    public Mat erodeAndDilate(Mat src, Mat dst, Mat kernel) {

	  /*
	   * I'm going to try splitting the image just to get top of crescent
	   *
	   *
	   * I'm thinking we can separate the top and bottom half of the mat and erode and dialate them seperately (this is for horizontal)
	   * and then fuse them together.
	   *
	   */

        //Mat newSrc = new Mat((src.cols()/2 -1 ), )
        Imshow interImg = new Imshow("after");
        Imshow resultImg = new Imshow("trying it out");
        Mat inter = new Mat();
        Mat inter2 = new Mat();

        //SHOTGUN
        //resultImg.showImage(src);

        Imgproc.erode(src, inter2, kernel);
        //interImg.showImage(src);
        //resultImg.showImage(inter);
        //Imgproc.dilate(inter, inter2, kernel);

//        interImg.showImage(src);
        src.copyTo(inter2);
        //src.copyTo(inter2);
        double[] black = {0,0,0};
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@\n\n\n\n" + src.rows() + src.cols());
        double[] white = {255, 255,255};
        for(int i = 0; i < src.rows(); i++)
        {
            for(int j = 0; j < src.cols(); j++)
            {

                // && src.get(i-1, j-1)[0] > 100 && src.get(i+1, j-1)[0] > 100 && src.get(i-1, j+1)[0] > 100 && src.get(i-1, j-1)[0] > 100 )
                if(getScore(src,i,j) > 5)
                    inter2.put(i, j, white);
                else
                    inter2.put(i, j, black);
            }
        }



        //Imgproc.erode(src, dst, kernel);
        Imgproc.dilate(inter2, dst, kernel);
//        resultImg.showImage(dst);
        //interImg.showImage(dst);

        //Imgproc.dilate(inter2, inter, kernel);
        //Imgproc.erode(inter, dst, kernel);

//        if(_DEBUG)
//            resultImg.showImage(dst);

        return dst;
    }

    public int getScore(Mat mat, int row, int col) {
        int score = 0;
        if(mat.get(row, col)[0] < 100)
            return 0;
        if(row > 0) {
            if(mat.get(row-1, col)[0] > 100)
                score++;

            if(col > 0 && mat.get(row-1, col-1)[0] > 100)
                score++;
            if(col < mat.cols() - 1 && mat.get(row-1, col+1)[0] > 100)
                score++;
        }
        if(row < mat.rows() - 1) {
            if(mat.get(row+1, col)[0] > 100)
                score++;
            if(col > 0 && mat.get(row+1, col-1)[0] > 100)
                score++;
            if(col < mat.cols() - 1 && mat.get(row+1, col+1)[0] > 100)
                score++;
        }

        if(col > 0) {
            if(mat.get(row, col-1)[0] > 100)
                score++;
            if(row > 0 && mat.get(row - 1, col - 1)[0] > 100)
                score++;
            if(row < mat.rows() - 1 && mat.get(row - 1, col - 1)[0] > 100)
                score++;
        }
        if(col < mat.cols() - 1) {
            if(mat.get(row, col+1)[0] > 100)
                score++;
            if(row > 0 && mat.get(row - 1, col + 1)[0] > 100)
                score++;
            if(row < mat.rows() - 1 && mat.get(row - 1, col + 1)[0] > 100)
                score++;
        }

        return score;
    }

    // Will need original image?
    public Mat getSkinThreshold(Mat src, Mat dst) {
        Imshow test = new Imshow("After Skin Threshold");
        final int max = 255;
        double THL = 0.04 * max;     // Low threshold for hue
        double THH = 0.75 * max;    // High threshold for hue
        double TSL = 0.1 * max;      // Low threshold for saturation
        double TSH = 0.25 * max;     // High threshold for saturation
        double TVL = 0.9 * max;      // Low threshold for value
        double TVH = 1.0 * max;      // High threshold for value

        Mat hsv_cvted = new Mat();
        Imshow i1 = new Imshow("T");

        Scalar lowerBound = new Scalar(THL, TSL, TVL);
        Scalar upperBound = new Scalar(THH, TSH, TVH);

        Imgproc.cvtColor(src, hsv_cvted, Imgproc.COLOR_BGR2HSV);	// Convert BGR to HSV for thresholding
        Core.inRange(hsv_cvted, lowerBound, upperBound, dst);	// Do the thresholded image??
//        test.showImage(dst);

//        if(_DEBUG)
//            i1.showImage(dst);


        //test.showImage(src);
        //test.showImage(dst);
        return dst;
    }

    /**
     * Return the area of the DETECTED pupil.
     * This is NOT the area of the pupil itself, but usually of the iris.
     * This method does not return the exact value because we divide an int by 2,
     * but we need the area as a relative measure for the white dot, so the exact
     * value is not necessary as long as the white dot area is computed in the same inexact manner.
     * @return an approximation of the area of the Mat identifying this pupil.
     * Margin of error: [0, 0.5) (between 0 (inclusive) and 0.5 (exclusive))
     */
    public double getArea() {
        double radius = mat.width() / 2;
        return Math.PI * Math.pow(radius, 2);
    }

    public void fillMap(TreeMap<Double, Double> threshold) {

        // Using threshold 200. First argument is diopter.
        threshold.put(0.5136, -6.0);
        threshold.put(0.5173, -5.5);
        threshold.put(0.4832, -5.0);
        threshold.put(0.4695, -4.5);
        threshold.put(0.4326, -4.0);
        threshold.put(0.3779, -3.5);
        threshold.put(0.2927, -3.0);
        threshold.put(0.1790, -2.5);
        threshold.put(0.0634, -2.0);
        threshold.put(0.0000, -1.5);
        threshold.put(0.0000, -1.0);
        threshold.put(0.0003, -0.5);
        threshold.put(0.0, 0.0);
        threshold.put(0.0, 0.5);
        threshold.put(0.0001, 1.0);
        threshold.put(0.0052, 1.5);
        threshold.put(0.1273, 2.0);
        threshold.put(0.2521, 2.5);
        threshold.put(0.3473, 3.0);
        threshold.put(0.4094, 3.5);
        threshold.put(0.4560, 4.0);
    }

    public static Double findClosestDiopter(double crescent_size) {

        double minDiff = Double.MAX_VALUE;

        Double nearest = null;

        for (Double key : thresholdMap.keySet()) {

            double diff = Math.abs((double) crescent_size - (double) key);
            if (diff < minDiff) {
                nearest = key;
                minDiff = diff;
            }
        }

        return thresholdMap.get(nearest);
    }
}