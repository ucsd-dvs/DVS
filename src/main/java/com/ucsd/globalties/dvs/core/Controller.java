package com.ucsd.globalties.dvs.core;

import com.ucsd.globalties.dvs.core.Photo.PhotoType;
import com.ucsd.globalties.dvs.core.excel.ExcelDataGenerator;
import com.ucsd.globalties.dvs.core.model.DiseaseRecord;
import com.ucsd.globalties.dvs.core.tools.EyesNotDetectedException;
import com.ucsd.globalties.dvs.core.tools.MyDialogs;
import com.ucsd.globalties.dvs.core.tools.WatchDir;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.opencv.highgui.Highgui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;

@Slf4j
/**
 * An MVC-esque artifact that hides access of the logic (e.g. the Patient class)
 * from the front-end. Maybe you can improve this (specifically the front-end exclusive
 * pieces) but for now you may add to this if necessary, because this class should not have
 * much more functionality unless the front-end requires it.
 *
 * @author Rahul
 */
public class Controller {

    private final java.util.Random rand = new java.util.Random();

    @Getter
    @Setter
    private Patient patient = null;
    private List<Patient> sessionPatients = null;

    @Getter @Setter private StringProperty hStrProperty;
    @Getter @Setter private StringProperty vStrProperty;

    public Controller() {
        hStrProperty = new SimpleStringProperty();
        hStrProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("hStrProperty change listener");
                System.out.println(newValue);
            }
        });

        vStrProperty = new SimpleStringProperty();
    }

    public void setPatientPhotos(String hFilePath, String vFilePath) {
        List<Photo> photos = new ArrayList<>();
        photos.add(new Photo(hFilePath, patient, PhotoType.HORIZONTAL));
        photos.add(new Photo(vFilePath, patient, PhotoType.VERTICAL));
        patient.setPhotos(photos);
    }

    public void finalizePatient() {
        if (sessionPatients == null) {
            sessionPatients = new ArrayList<Patient>();
        }
        if(patient == null) {
            log.info("[FINALIZING PATIENT]");
            patient = Patient.builder().build();
            patient.print();
        }
        sessionPatients.add(patient);
        patient = null;
    }

    public void diagnose() {
        patient.diagnose();
    }

//    public Map<EyeDisease, String> getRecords() {
//        return patient.getMedicalRecord();
//    }

    public List<DiseaseRecord> getRecords() {
        return patient.getDiseaseRecord();
    }

    public void exportData() {
        ExcelDataGenerator.exportPatientData(sessionPatients);
    }

    public void createDummyData() {
        if(sessionPatients == null) {
            sessionPatients = new ArrayList<Patient>();
        }
        for(int i = 0; i < 50; i++) {
            Patient patient = Patient.builder()
                    .firstName(generateName())
                    .lastName(generateName())
                    .birth(generateName())
                    .gender(generateName())
                    .ethnicity(generateName())
                    .language(generateName())
                    .roomNumber(generateName())
                    .school(generateName())
                    .comment(generateName())
                    .diseaseRecord(new ArrayList<>())
                    .build();
            patient.getDiseaseRecord().add(new DiseaseRecord(EyeDisease.ANISOMETROPIA, "PASS", "TEST"));
            patient.getDiseaseRecord().add(new DiseaseRecord(EyeDisease.MYOPIA, "PASS", "TEST"));
            patient.getDiseaseRecord().add(new DiseaseRecord(EyeDisease.HYPEROPIA, "PASS", "TEST"));
            patient.getDiseaseRecord().add(new DiseaseRecord(EyeDisease.ASTIGMATISM, "PASS", "TEST"));
            patient.getDiseaseRecord().add(new DiseaseRecord(EyeDisease.CATARACTS, "PASS", "TEST"));
            patient.getDiseaseRecord().add(new DiseaseRecord(EyeDisease.STRABISMUS, "PASS", "TEST"));
            sessionPatients.add(patient);
        }

        log.info("created {} dummy patients", sessionPatients.size());
    }

    private String generateName() {
        StringBuilder builder = new StringBuilder();
        while(builder.toString().length() == 0) {
            int length = rand.nextInt(5)+5;
            for(int i = 0; i < length; i++) {
                String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
                builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
            }
        }
        return builder.toString();
    }

    /**
     * TODO improve this to make it more elegant and more forward-compatible.
     *
     * @return a Map representing the successfully detected components and their output paths
     */
    public Map<String, String> detectAll() {
        Map<String, String> detected = new HashMap<>();
        Eye eye = null;
        Pupil pupil = null;

        /* Horizontal Picture */
        //Left eye
        eye = patient.getPhotos().get(0).getLeftEye();
        if(eye != null) {
            Highgui.imwrite(Main.OUTPUT_FILE + "left_eye_horizontal.jpg", eye.getMat());
            detected.put("left_eye_horizontal", Main.OUTPUT_FILE + "left_eye_horizontal.jpg");
        } else {
            return null;
        }
        pupil = eye.getPupil();
        if (pupil != null) {
            pupil.getWhiteDot();
            Highgui.imwrite(Main.OUTPUT_FILE + "left_eye_pupil_horizontal.jpg", pupil.getMat());
            detected.put("left_eye_pupil_horizontal", Main.OUTPUT_FILE + "left_eye_pupil_horizontal.jpg");
        }

        //Right eye
        eye = patient.getPhotos().get(0).getRightEye();
        if(eye != null) {
            Highgui.imwrite(Main.OUTPUT_FILE + "right_eye_horizontal.jpg", eye.getMat());
            detected.put("right_eye_horizontal", Main.OUTPUT_FILE + "right_eye_horizontal.jpg");
        } else {
            return null;
        }
        pupil = eye.getPupil();
        if (pupil != null) {
            pupil.getWhiteDot();
            Highgui.imwrite(Main.OUTPUT_FILE + "right_eye_pupil_horizontal.jpg", pupil.getMat());
            detected.put("right_eye_pupil_horizontal", Main.OUTPUT_FILE + "right_eye_pupil_horizontal.jpg");
        }

        /* Vertical Picture */
        //Left eye
        eye = patient.getPhotos().get(1).getLeftEye();
        if(eye != null) {
            Highgui.imwrite(Main.OUTPUT_FILE + "left_eye_vertical.jpg", eye.getMat());
            detected.put("left_eye_vertical", Main.OUTPUT_FILE + "left_eye_vertical.jpg");
        } else {
            return null;
        }
        pupil = eye.getPupil();
        if (pupil != null) {
            pupil.getWhiteDot();
            Highgui.imwrite(Main.OUTPUT_FILE + "left_eye_pupil_vertical.jpg", pupil.getMat());
            detected.put("left_eye_pupil_vertical", Main.OUTPUT_FILE + "left_eye_pupil_vertical.jpg");
        }
        //Right eye
        eye = patient.getPhotos().get(1).getRightEye();
        if(eye != null) {
            Highgui.imwrite(Main.OUTPUT_FILE + "right_eye_vertical.jpg", eye.getMat());
            detected.put("right_eye_vertical", Main.OUTPUT_FILE + "right_eye_vertical.jpg");
        } else {
            return null;
        }
        pupil = eye.getPupil();
        if (pupil != null) {
            pupil.getWhiteDot();
            Highgui.imwrite(Main.OUTPUT_FILE + "right_eye_pupil_vertical.jpg", pupil.getMat());
            detected.put("right_eye_pupil_vertical", Main.OUTPUT_FILE + "right_eye_pupil_vertical.jpg");
        }

        return detected;
    }

    public boolean checkPatientList() {
        return (sessionPatients == null || sessionPatients.size() < 1) ? false : true;
    }
}
