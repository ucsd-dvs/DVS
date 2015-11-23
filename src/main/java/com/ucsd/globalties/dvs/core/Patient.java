package com.ucsd.globalties.dvs.core;

import com.ucsd.globalties.dvs.core.model.DiseaseRecord;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * The Patient class contains all information about the patient.
 * All information entered in the first screen, along with the photos (and therefore
 * the eyes, pupils, and more) are references in this class.
 *
 * @author Rahul
 */
@Builder // Automatically generate a builder class for the patient that supports optional parameters.
@Slf4j
public class Patient {
    @Getter
    @Setter
    private String firstName, lastName, birth, gender, ethnicity,
            language, roomNumber, school, comment;

    @Setter
    @Getter
    private List<Photo> photos; //Has horizontal and vertical photo

    @Getter
    private Map<EyeDisease, String> medicalRecord;

    @Getter
    private List<DiseaseRecord> diseaseRecord;

    /**
     * Populate the patient's medical record with results from the diagnoses of all disease detectors.
     * BY THE WAY, this is lame. Back end code shouldn't really have null checks here, because that
     * should be prevented by the front end. The back end should never advance to this point with null
     * references to any necessary components.
     * TODO PLZ REFACTOR.
     */
    public void diagnose() {
        diseaseRecord = new ArrayList<>();

        for (Iterator<Photo> it = photos.iterator(); it.hasNext(); ) {
            Photo p = it.next();
            Eye left = p.getLeftEye();
            Eye right = p.getRightEye();
            if (left == null || right == null) {
                it.remove();
            } else if (left.getPupil() == null || right.getPupil() == null) {
                it.remove();
            } else if (left.getPupil().getWhiteDot() == null || right.getPupil().getWhiteDot() == null) {
                log.warn("No white dot detected for " + p.getType() + " "
                        + "photo. Patient either has severe strabismus, or the algorithm failed.");
                it.remove();
            }
        }
        if (photos.isEmpty()) {
            log.warn("Skipping diagnosis for patient {} {} because no adequate photos exist.", firstName, lastName);
            return;
        } else if (photos.size() < 2) {
            log.warn("A photo was removed from {} {} because not enough eyes/pupils/whitedots were found.", firstName, lastName);
        }
        for (EyeDisease disease : EyeDisease.values()) {
            disease.getDetector().detect(this);
        }
        log.info("Done detecting. Medical record: " + diseaseRecord.toString());
    }

    /**
     * TODO This is kind of lame, please refactor.
     *
     * @return
     */
    public Map<String, String> getPatientData() {
        Map<String, String> data = new HashMap<>();
        data.put("Name", firstName + " " + lastName);
        data.put("Date of Birth", birth);
        data.put("Gender", gender);
        data.put("Ethnicity", ethnicity);
        data.put("Language", language);
        data.put("School", school);
        data.put("Room Number", roomNumber);
        data.put("School", school);
        data.put("Comment", comment);
        return data;
    }

    /**
     * DEBUG method
     */
    public void print() {
        log.info("[PATIENT] ==========================");
        log.info("First Name: {}, Last Name: {}", firstName, lastName);
        log.info("Date of Birth: {}", birth);
        log.info("Gender: {}", gender);
        log.info("Ethnicity: {}", ethnicity);
        log.info("Language: {}", language);
        log.info("Room Number: {}", roomNumber);
        log.info("School: {}", school);
        log.info("Comment: {}", comment);
        log.info("========================");
    }
}
