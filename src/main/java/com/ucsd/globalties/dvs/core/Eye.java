package com.ucsd.globalties.dvs.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Random;

/**
 * The Eye class represents a portion of the picture containing the eye.
 *
 * @author Rahul
 */
@Slf4j
public class Eye {

    @Getter
    private Mat mat;
    // the Photo from which this eye was derived
    private Photo photo;

    private Pupil pupil;

    /**
     * Create a new Eye object with the "parent" photo and the Mat that
     * describes its pixels.
     *
     * @param photo
     * @param mat
     */
    public Eye(Photo photo, Mat mat) {
        this.photo = photo;
        this.mat = mat;
    }

    /**
     * Lazy load/cache the pupil detection.
     *
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
        Mat circles = new Mat();
        //make copy of eye mat so we do not modify it
        mat.copyTo(src);
        //convert to grayscale since HoughCircles, and other algorithms, require this
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        //Imgproc.equalizeHist(gray,gray);
        //smooth out image to prevent false circles
        Imgproc.GaussianBlur(gray, gray, new Size(9, 9), 2.0, 2.0);
        //Finds circles
        //See http://docs.opencv.org/trunk/modules/imgproc/doc/feature_detection.html?highlight=cvhoughcircles#houghcircles
        //for parameter information
        Imgproc.HoughCircles(gray, circles, Imgproc.CV_HOUGH_GRADIENT, 2.0, (gray.height() / 4.0), 150.0, 20.0, (gray.height() / 8), (gray.height() / 2));
        //if none found return null
        if (circles.total() == 0) {
            log.info("Pupil not found.");
            return null;
        }

        //iterate over all circles and output circles/rects for debugging
    /*for(int i = 0; i < circles.total(); i++ )
    {
      //circle info is stored as [x,y,radius] with [x,y] forming the center of the circle
      //double[] circle = circles.get(0, i); 
      //log.info("x: " + circle[0] + " y: " + circle[1] + " r: " + circle[2]);
      //Core.rectangle(src, new Point(circle[0],circle[1]), new Point(circle[0]+2*circle[2],circle[1]+2*circle[2]), new Scalar(0,255,0),2);
      //Core.circle(src, new Point(circle[0], circle[1]), (int) circle[2], new Scalar(255,0,0),2);
    }*/
        //Highgui.imwrite(""+code+"_pupil_src.jpg", src);

        //pick the most center circle as Pupil. HoughCircles returns circles in decreasing order from center
        //if you suspect this is not the case, just sort the Mat using pythagorean theorem to get the distance from center of circle to center of eye image.
        double[] finalPupil = circles.get(0, 0);

        // Use the following code to debug where the detected pupil is in the image
    /*Mat fsrc = new Mat();
    mat.copyTo(fsrc);    
    Core.rectangle(fsrc, new Point(finalPupil[0],finalPupil[1]), new Point(finalPupil[0]+2*finalPupil[2],finalPupil[1]+2*finalPupil[2]), new Scalar(0,255,0),2);
    Core.circle(fsrc, new Point(finalPupil[0], finalPupil[1]), (int) finalPupil[2], new Scalar(255,0,0),2);
    Highgui.imwrite("detected-pupil.jpg", fsrc);*/

        log.info("Pupil found: x: {} y: {} r: {}", finalPupil[0], finalPupil[1], finalPupil[2]);
        //Crop eye mat and create pupil mat
        Point topLeft = new Point(finalPupil[0] - finalPupil[2] / 2, finalPupil[1] - finalPupil[2] / 2);
        Point bottomRight = new Point(finalPupil[0] + finalPupil[2] / 2, finalPupil[1] + finalPupil[2] / 2);
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
        if (bottomRight.x > src.size().width || bottomRight.y > src.size().height) {
            log.warn("Bottom right point is out of image bounds ({},{}).", bottomRight.x, bottomRight.y);
            if (bottomRight.x > src.size().width) {
                bottomRight.x = src.size().width;
            }
            if (bottomRight.y > src.size().height) {
                bottomRight.y = src.size().height;
            }
            log.warn("Continuing with ({},{})", bottomRight.x, bottomRight.y);
        }
        Rect pupilArea = new Rect(topLeft, bottomRight);
        Mat pupilMat = new Mat(src, pupilArea);
        photo.appendPupilX(finalPupil[0]);
        return new Pupil(this, pupilMat);
    }
}
;
