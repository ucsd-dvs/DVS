package com.ucsd.globalties.dvs.core;

import com.ucsd.globalties.dvs.core.Photo.PhotoType;
import com.ucsd.globalties.dvs.core.excel.ExcelDataGenerator;
import lombok.Setter;
import org.opencv.highgui.Highgui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An MVC-esque artifact that hides access of the logic (e.g. the Patient class)
 * from the front-end. Maybe you can improve this (specifically the front-end exclusive
 * pieces) but for now you may add to this if necessary, because this class should not have
 * much more functionality unless the front-end requires it.
 *
 * @author Rahul
 */
public class Controller {

    @Setter
    private Patient patient = null;
    private List<Patient> sessionPatients = null;

    public void setPatientPhotos(String hFilePath, String vFilePath) {
        List<Photo> photos = new ArrayList<Photo>();
        photos.add(new Photo(hFilePath, patient, PhotoType.HORIZONTAL));
        photos.add(new Photo(vFilePath, patient, PhotoType.VERTICAL));
        patient.setPhotos(photos);
    }

    public void finalizePatient() {
        if (sessionPatients == null) {
            sessionPatients = new ArrayList<Patient>();
        }
        sessionPatients.add(patient);
        patient = null;
    }

    public void diagnose() {
        patient.diagnose();
    }

    public Map<EyeDisease, String> getRecords() {
        return patient.getMedicalRecord();
    }

    public void exportData() {
        ExcelDataGenerator.exportPatientData(sessionPatients);
    }

    /**
     * TODO improve this to make it more elegant and more forward-compatible.
     *
     * @return a Map representing the successfully detected components and their output paths
     */
    public Map<String, String> detectAll() {
        Map<String, String> detected = new HashMap<String, String>();
        for (Photo photo : patient.getPhotos()) {

        }
        if (patient.getPhotos().get(0).getLeftEye() != null) {
            Highgui.imwrite(Main.OUTPUT_FILE + "left_eye_horizontal.jpg", patient.getPhotos().get(0).getLeftEye().getMat());
            detected.put("left_eye_horizontal", Main.OUTPUT_FILE + "left_eye_horizontal.jpg");
        }
        if (patient.getPhotos().get(0).getLeftEye().getPupil() != null) {
            patient.getPhotos().get(0).getLeftEye().getPupil().getWhiteDot();
            Highgui.imwrite(Main.OUTPUT_FILE + "left_eye_pupil_horizontal.jpg", patient.getPhotos().get(0).getLeftEye().getPupil().getMat());
            detected.put("left_eye_pupil_horizontal", Main.OUTPUT_FILE + "left_eye_pupil_horizontal.jpg");
        }
        if (patient.getPhotos().get(0).getRightEye() != null) {
            Highgui.imwrite(Main.OUTPUT_FILE + "right_eye_horizontal.jpg", patient.getPhotos().get(0).getRightEye().getMat());
            detected.put("right_eye_horizontal", Main.OUTPUT_FILE + "right_eye_horizontal.jpg");
        }
        if (patient.getPhotos().get(0).getRightEye().getPupil() != null) {
            patient.getPhotos().get(0).getRightEye().getPupil().getWhiteDot();
            Highgui.imwrite(Main.OUTPUT_FILE + "right_eye_pupil_horizontal.jpg", patient.getPhotos().get(0).getRightEye().getPupil().getMat());
            detected.put("right_eye_pupil_horizontal", Main.OUTPUT_FILE + "right_eye_pupil_horizontal.jpg");
        }

        if (patient.getPhotos().get(1).getLeftEye() != null) {
            Highgui.imwrite(Main.OUTPUT_FILE + "left_eye_vertical.jpg", patient.getPhotos().get(1).getLeftEye().getMat());
            detected.put("left_eye_vertical", Main.OUTPUT_FILE + "left_eye_vertical.jpg");
        }
        if (patient.getPhotos().get(1).getLeftEye().getPupil() != null) {
            patient.getPhotos().get(1).getLeftEye().getPupil().getWhiteDot();
            Highgui.imwrite(Main.OUTPUT_FILE + "left_eye_pupil_vertical.jpg", patient.getPhotos().get(1).getLeftEye().getPupil().getMat());
            detected.put("left_eye_pupil_vertical", Main.OUTPUT_FILE + "left_eye_pupil_vertical.jpg");
        }
        if (patient.getPhotos().get(1).getRightEye() != null) {
            Highgui.imwrite(Main.OUTPUT_FILE + "right_eye_vertical.jpg", patient.getPhotos().get(1).getRightEye().getMat());
            detected.put("right_eye_vertical", Main.OUTPUT_FILE + "right_eye_vertical.jpg");
        }
        if (patient.getPhotos().get(1).getRightEye().getPupil() != null) {
            patient.getPhotos().get(1).getRightEye().getPupil().getWhiteDot();
            Highgui.imwrite(Main.OUTPUT_FILE + "right_eye_pupil_vertical.jpg", patient.getPhotos().get(1).getRightEye().getPupil().getMat());
            detected.put("right_eye_pupil_vertical", Main.OUTPUT_FILE + "right_eye_pupil_vertical.jpg");
        }

        return detected;
    }

}
