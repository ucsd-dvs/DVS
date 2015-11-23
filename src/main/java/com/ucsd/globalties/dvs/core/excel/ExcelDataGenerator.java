package com.ucsd.globalties.dvs.core.excel;

import com.ucsd.globalties.dvs.core.Eye;
import com.ucsd.globalties.dvs.core.EyeDisease;
import com.ucsd.globalties.dvs.core.Patient;
import com.ucsd.globalties.dvs.core.model.DiseaseRecord;
import com.ucsd.globalties.dvs.core.tools.MyDialogs;
import com.ucsd.globalties.dvs.core.ui.RootViewController;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Basic excel export class that writes patient information into an excel file
 * TODO make excel file prettier with formatting and colors
 * TODO add disease detection results to excel file
 *
 * @author Sabit
 */
@Slf4j
public class ExcelDataGenerator {
    static final boolean DEBUG = true;


    public static void exportPatientData(List<Patient> patientList) {
        if (patientList == null || patientList.size() == 0) {
            if (DEBUG) log.info("patients list is empty");
            MyDialogs.showError("You have no data to export");
            return;
        }

        String fileName = askForFileName();
        if (fileName == null) {
            if (DEBUG) log.info("no filename provided");
            return;
        }

        String pwd = askForPassword();
        if (pwd == null) {
            if (DEBUG) log.info("no password provided");
            return;
        }

        //This was used when filename was hardcoded
        //Could be used in future for setting default filename
        //SimpleDateFormat sdf = new SimpleDateFormat("MM_dd_YYYY_hm");

        Workbook workBook = new XSSFWorkbook();
        Sheet sheet = workBook.createSheet("Patient Data");
        int rowNum = 0, cellNum = 0;
        Row headerRow = sheet.createRow(rowNum++);

        // Create Row headers for Patient information
        Map<String, String> pData = patientList.get(0).getPatientData();
        for (String key : pData.keySet()) {
            Cell cell = headerRow.createCell(cellNum++);
            cell.setCellValue(key);
        }
        List<DiseaseRecord> diseaseRecord = patientList.get(0).getDiseaseRecord();
        for (DiseaseRecord disease : diseaseRecord) {
            Cell cell = headerRow.createCell(cellNum++);
            cell.setCellValue(disease.getDiseaseName().toString());
        }

        for (Patient p : patientList) {
            Row row = sheet.createRow(rowNum++);
            cellNum = 0;
            Map<String, String> patientData = p.getPatientData();
            for(String value : patientData.values()) {
                Cell cell = row.createCell(cellNum++);
                cell.setCellValue(value);
            }
            for(DiseaseRecord disease : diseaseRecord) {
                Cell cell = row.createCell(cellNum++);
                cell.setCellValue(disease.getStatus());
            }
        }

        //TODO: better error reporting for the user in all these exceptions

        try(FileOutputStream out = new FileOutputStream(fileName)) {
            workBook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * Encrypt excel file w/ password
         */
        POIFSFileSystem fs = new POIFSFileSystem();
        EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);

        Encryptor enc = info.getEncryptor();
        enc.confirmPassword(pwd);

        try(OPCPackage opc = OPCPackage.open(fileName, PackageAccess.READ_WRITE)) {
            OutputStream os = enc.getDataStream(fs);
            opc.save(os);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        //TODO: need to clear all patient data so the warning on exit doesn't show up
        try(FileOutputStream fos = new FileOutputStream(fileName)) {
            fs.writeFilesystem(fos);
            patientList.clear();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * creates and populates a dialog window to ask user for the password
     * currently does not enforce any password format
     * TODO: possibly ask Liliana or the Doctor about necessary security
     *
     * @return password to be used in encrypting the excel file or null if cancel was clicked
     */
    private static String askForPassword() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Password Entry");

        // Create UI components and add to pane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Platform.runLater(() -> { passwordField.requestFocus(); });
        PasswordField passwordField1 = new PasswordField();
        passwordField1.setPromptText("Reenter Password");

        Label errorLabel = new Label("Your passwords do not match!");
        errorLabel.setTextFill(Color.RED);

        grid.add(errorLabel, 0, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Reenter Password:"), 0, 2);
        grid.add(passwordField1, 1, 2);

        errorLabel.setVisible(false);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        /**
         * Handle all UI scripting
         */
        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            String pwd = passwordField1.getText();
            if (!pwd.equals(newValue)) {
                errorLabel.setVisible(true);
                okButton.setDisable(true);
            } else {
                errorLabel.setVisible(false);
                okButton.setDisable(false);
            }
        });

        passwordField1.textProperty().addListener((observable, oldValue, newValue) -> {
            String pwd = passwordField.getText();
            if (!pwd.equals(newValue)) {
                errorLabel.setVisible(true);
                okButton.setDisable(true);
            } else {
                errorLabel.setVisible(false);
                okButton.setDisable(false);
            }
        });

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new String(passwordField.getText());
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        return (result.isPresent()) ? result.get() : null;
    }

    /**
     * TODO: user clicks save w/o providing filename, the default filename should be used at the directory user was in
     *
     * @return the absolute path of the filename or null if cancel was clicked
     */
    private static String askForFileName() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Export File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel .xlsx File", "*.xlsx"));
        File savedFile = fileChooser.showSaveDialog(RootViewController.stage);

        //FIXME: if user replaces existing file then .xlsx gets appended again
        return (savedFile != null) ? savedFile.getAbsolutePath() + ".xlsx" : null;
    }
}
