package com.ucsd.globalties.dvs.core;

import com.atul.JavaOpenCV.Imshow;
import com.ucsd.globalties.dvs.core.tools.EyesNotDetectedException;
import com.ucsd.globalties.dvs.core.tools.Pair;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.*;
import org.opencv.gpu.Gpu;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Photo class represents a picture chosen by the user.
 * Has methods to detect the face and eyes, and is used as an
 * entry point to the computer vision analysis through OpenCV
 *
 * @author Rahul
 */
@Slf4j
public class Photo {
    public enum PhotoType {
        HORIZONTAL, VERTICAL;
    }

    @Getter
    private PhotoType type;
    private String path;
    @Getter
    private double pupillaryDistance = 0;

    @Getter
    private Patient patient;

    private Pair<Eye, Eye> eyes;

    public Photo(String path, Patient patient, PhotoType type) {
        this.path = path;
        if (!new File(path).exists()) {
            throw new RuntimeException("Invalid file specified: " + path);
        }
        this.patient = patient;
        this.type = type;
    }

    public Eye getLeftEye() {
        if (eyes == null) {
            eyes = findEyes();
            if(eyes == null) return null;
        }
        return eyes.getLeft();
    }

    public Eye getRightEye() {
        if (eyes == null) {
            eyes = findEyes();
            if(eyes == null) return null;
        }
        return eyes.getRight();
    }

    private Rect findFaceRoi(Mat image) {
        CascadeClassifier faceDetector = new CascadeClassifier(Main.HAAR_FACE_PATH);
        MatOfRect faceDetections = new MatOfRect();
        int flag = Objdetect.CASCADE_FIND_BIGGEST_OBJECT | Objdetect.CASCADE_SCALE_IMAGE;
        // find faces and put the results inside of the faceDetections object.
        faceDetector.detectMultiScale(image, faceDetections, 1.05, 2, flag, new Size(30, 30), new Size(image.width(), image.height()));

        log.info("Detected {} faces for img: {}", faceDetections.toArray().length, path);
        Rect detectedFace;
        if (faceDetections == null || faceDetections.toArray().length == 0) {
            // go straight into eye detection on current image if no face is found
            // this will unfortunately take a longer time because the entire image has to be scanned for eyes,
            // but some photos will have faces cut off or non-detectable faces and this is a fail-safe method if no face
            // is found.
            return null;
        }

        Rect[] rects = faceDetections.toArray();
        Mat test = new Mat();
        image.copyTo(test);
        for(int i = 0; i < rects.length; i++) {
            Rect current = rects[i];
            Core.rectangle(test, new Point(current.x, current.y),
                    new Point(current.x + current.width, current.y + current.height), new Scalar(255,0,0,255), 3);
        }
        Imshow testImage = new Imshow("Faces Detected");
        if(test != null)
            testImage.showImage(test);

        detectedFace = faceDetections.toArray()[0];
        Rect faceBox = new Rect(detectedFace.x, detectedFace.y, detectedFace.width, (detectedFace.height * 2) / 3);
        return faceBox;
    }

    public Pair<Eye, Eye> findEyes() {
        Mat image = Highgui.imread(path);

        if(type == PhotoType.VERTICAL) {
            Core.transpose(image, image);
            Core.flip(image, image, 0);
//            Imshow im = new Imshow("sdfd");
//            im.showImage(image);
        }

        // find face
        Rect faceBox = findFaceRoi(image);
        // Detect eyes from cropped face image
        CascadeClassifier eyeDetector = new CascadeClassifier(Main.HAAR_EYE_PATH);
        MatOfRect eyeDetections = new MatOfRect();
        Mat faceImage = faceBox != null ? new Mat(image, faceBox) : image;
        eyeDetector.detectMultiScale(faceImage, eyeDetections);

        List<Rect> detectedEyes = eyeDetections.toList();
        log.info("Detected {} eyes for img: {}", detectedEyes.size(), path);
        List<Rect> eyes = new ArrayList<>(2);
        if (detectedEyes.size() < 2) {
            log.error("Minimum two eyes required");
            return null;
        } else if (detectedEyes.size() > 2) { // found an extra eye or two
            detectedEyes.sort(new RectAreaCompare());
            // we can safely get the last 2 biggest ones, because after the crop the eyes take up the most space
            eyes.add(detectedEyes.get(detectedEyes.size() - 1));
            eyes.add(detectedEyes.get(detectedEyes.size() - 2));
            /* TODO maybe add some more criteria here and have criterion weights for more accurate behavior, but
                    only necessary if future pictures have unsatisfactory eye detection rates. */
        } else {
            eyes.addAll(eyeDetections.toList());
        }
        eyes.sort(new EyeXCompare()); // simple sort to know which eye is left and which is right
        Mat leftEyeMat = new Mat(faceImage, eyes.get(0));
        Mat rightEyeMat = new Mat(faceImage, eyes.get(1));
        log.info("created left eye mat: " + leftEyeMat);
        log.info("created right eye mat: " + rightEyeMat);
        return new Pair<Eye, Eye>(new Eye(this, leftEyeMat), new Eye(this, rightEyeMat));
    }

    /**
     * Compare the area of the rectangles, putting the smaller rectangle before the larger.
     *
     * @author Rahul
     */
    private static class RectAreaCompare implements Comparator<Rect> {
        public int compare(Rect r1, Rect r2) {
            int r1Area = r1.height * r1.width;
            int r2Area = r2.height * r2.width;
            return r1Area < r2Area ? -1 : r1Area == r2Area ? 0 : 1;
        }
    }

    /**
     * Compare the 'x' coordinates of the eye, putting the smaller 'x' values before the larger.
     *
     * @author Rahul
     */
    private static class EyeXCompare implements Comparator<Rect> {
        public int compare(Rect r1, Rect r2) {
            return r1.x < r2.x ? -1 : r1.x == r2.x ? 0 : 1;
        }
    }

    /**
     * Helper function that is called by the Eye once it has detected the Pupil.
     * After two pupils have made this call, the pupillaryDistance will hold the
     * correctly computed value.
     * TODO maybe improve how this is done so that subsequent calls, though unnecessary and
     * impossible in a normal use case, do not break the result.
     * Also maybe test this.
     *
     * @param pupilX the x coordinate of the pupil
     */
    public void appendPupilX(double pupilX) {
        if (pupillaryDistance == 0) {
            pupillaryDistance = pupilX;
        } else {
            pupillaryDistance = Math.abs(pupillaryDistance - pupilX);
            log.info("Pupillary Distance: " + pupillaryDistance);
        }
    }

}
