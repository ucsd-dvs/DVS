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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
        HORIZONTAL, VERTICAL
    }

    public enum TemplateType {
        TM_SQDIFF, TM_SQDIFF_NORMED, TM_CCOEFF, TM_CCOEFF_NORMED, TM_CCORR, TM_CCORR_NORMED
    }

    @Getter
    private PhotoType type;
    private TemplateType templateType;
    private String path;
    @Getter
    private double pupillaryDistance = 0;

    @Getter
    private Patient patient;

    private Pair<Eye, Eye> eyes;

    private Mat mFace;

    private boolean DEBUG = false;

    public Photo(String path, Patient patient, PhotoType type) {
        this.path = path;
        if (!new File(path).exists()) {
            throw new RuntimeException("Invalid file specified: " + path);
        }
        this.patient = patient;
        this.type = type;
    }

    public Eye getLeftEye() {
        getEye();
        if(eyes == null) return null;
        return eyes.getLeft();
    }

    public Eye getRightEye() {
        getEye();
        if(eyes == null) return null;
        return eyes.getRight();
    }

    private void getEye() {
        if (eyes == null) {
            try {
                eyes = findEyes();
            }
            catch (IOException e) {}
        }
    }

    private Rect getTemplate(CascadeClassifier classifier, Rect faceRec) {
        MatOfRect mEyes = new MatOfRect();
        Mat region = new Mat(mFace, faceRec);
        classifier.detectMultiScale(region, mEyes, 1.15, 2, Objdetect.CASCADE_FIND_BIGGEST_OBJECT|Objdetect.CASCADE_SCALE_IMAGE, new Size(30,30), new Size());
        Rect isolatedEye = null;
        Rect[] eyes = mEyes.toArray();
        for(int i = 0; i < eyes.length; i++) {
            Rect eye = eyes[i];
            eye.x += faceRec.x;
            eye.y += faceRec.y;
            isolatedEye = new Rect((int)(eye.tl().x + eye.tl().x * 0.02),
                    (int)(eye.tl().y + eye.height * 0.3),
                    (int)(eye.width - eye.width * 0.15), (int)(eye.height * 0.5));
            region = mFace.submat(isolatedEye);
            if (DEBUG) {
                Imshow testImage = new Imshow("Eye");
                testImage.showImage(region);
            }
            break;
        }
        return isolatedEye;
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

        detectedFace = faceDetections.toArray()[0];
        Rect faceBox = new Rect(detectedFace.x, detectedFace.y, detectedFace.width, detectedFace.height);
        return faceBox;
    }

    public Pair<Eye, Eye> findEyes() throws IOException{
        Mat image = Highgui.imread(path);

        BufferedImage imagePassed = ImageIO.read(new File(path));
        int width = imagePassed.getWidth();
        int height = imagePassed.getHeight();

        if(DEBUG)
        System.out.println(width + " " + height);

        if(type == PhotoType.VERTICAL && (width > height)) {
            // TODO: Figure out why this varies across computers.
            System.out.println("picture is not vertical.");
            Core.transpose(image, image);
            Core.flip(image, image, 0);
            if(DEBUG) {
                Imshow im = new Imshow("sdfd");
                im.showImage(image);
            }
        }

        // find face
        Rect faceBox = findFaceRoi(image);
        // Detect eyes from cropped face image
        CascadeClassifier eyeDetector = new CascadeClassifier(Main.HAAR_EYE_PATH);
        /******************************************************/
        mFace = (faceBox != null) ? new Mat(image, faceBox) : image;
        Rect bothEyes =  new Rect(faceBox.x + faceBox.width/8,
                        (int) (faceBox.y + faceBox.height/4.5),
                        faceBox.width - 2 * faceBox.width/8,
                        (int) faceBox.height/30);

        Core.rectangle(image, bothEyes.tl(), bothEyes.br(), new Scalar(255,0,0,255), 2);
        Rect rightArea = new Rect(faceBox.x + faceBox.width/16,
                        (int)(faceBox.y + faceBox.height/4.5),
                        (faceBox.width - 2 * faceBox.width/16)/2,
                        (int)(faceBox.height/3.0));
        Rect leftArea = new Rect(faceBox.x + faceBox.width/16 + (faceBox.width - 2 * faceBox.width/16)/2,
                        (int)(faceBox.y + faceBox.height/4.5),
                        (faceBox.width - 2 * faceBox.width/16)/2,
                        (int)(faceBox.height/3.0));
        Core.rectangle(image, leftArea.tl(), leftArea.br(), new Scalar(255, 0, 0, 255), 2);
        Core.rectangle(image, rightArea.tl(), rightArea.br(), new Scalar(255, 0, 0, 255), 2);
        if(DEBUG) {
            Imshow testImage = new Imshow("Faces With Eyes");
            testImage.showImage(image);
        }
        mFace = image;
        List<Rect> detectedEyes = new ArrayList<Rect>();
        Rect templateL = getTemplate(eyeDetector, leftArea);
        Rect templateR = getTemplate(eyeDetector, rightArea);
        if(templateL != null) detectedEyes.add(templateL);
        if(templateR != null) detectedEyes.add(templateR);
        /******************************************************/
        List<Rect> eyes = new ArrayList<Rect>(2);
        Mat faceImage = mFace;
        if(detectedEyes.size() != 2) {
            MatOfRect eyeDetections = new MatOfRect();
            eyeDetector.detectMultiScale
                    (faceImage, eyeDetections, 1.15, 5, Objdetect.CASCADE_FIND_BIGGEST_OBJECT | Objdetect.CASCADE_SCALE_IMAGE, new Size(30, 30), new Size(faceImage.width(), faceImage.height()));

            detectedEyes = eyeDetections.toList();
            log.info("Detected {} eyes for img: {}", detectedEyes.size(), path);
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
        } else {
            eyes = detectedEyes;
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
