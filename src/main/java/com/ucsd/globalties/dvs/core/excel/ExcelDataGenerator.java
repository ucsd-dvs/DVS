package com.ucsd.globalties.dvs.core.excel;

import com.ucsd.globalties.dvs.core.Main;
import com.ucsd.globalties.dvs.core.Patient;
import com.ucsd.globalties.dvs.core.ui.RootViewController;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
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

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//
//

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
        if(patientList == null || patientList.size() == 0) {
            if(DEBUG) log.info("patients list is empty");
            showError("You have no data to export");
            return;
        }

        String fileName = askForFileName();
        if(fileName == null) {
            if(DEBUG) log.info("no filename provided");
            return;
        }

        String pwd = askForPassword();
        if(pwd == null) {
            if(DEBUG) log.info("no password provided");
            return;
        }

        try {
            //This was used when filename was hardcoded
            //Coudl be used in future for setting default filename
            //SimpleDateFormat sdf = new SimpleDateFormat("MM_dd_YYYY_hm");

            Workbook wb = new XSSFWorkbook();
            Sheet s = wb.createSheet("Patient Data");
            int rowNum = 0, cellNum = 0;
            Row fieldRow = s.createRow(rowNum++);

            for (String field : Main.sceneLabels) {
                Cell c = fieldRow.createCell(cellNum++);
                c.setCellValue(field);
            }

            for (Patient p : patientList) {
                Row r = s.createRow(rowNum++);
                cellNum = 0;
                Map<String, String> patientData = p.getPatientData();
                for (String field : Main.sceneLabels) {
                    Cell c = r.createCell(cellNum++);
                    c.setCellValue(patientData.get(field));
                }
            }
            FileOutputStream out = new FileOutputStream(fileName);
            wb.write(out);
            out.close();

            /**
             * Encrypt excel file w/ password
             */
            POIFSFileSystem fs = new POIFSFileSystem();
            EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);

            Encryptor enc = info.getEncryptor();
            enc.confirmPassword(pwd);

            OPCPackage opc = OPCPackage.open(fileName, PackageAccess.READ_WRITE);
            OutputStream os = enc.getDataStream(fs);
            opc.save(os);
            opc.close();

            FileOutputStream fos = new FileOutputStream(fileName);
            fs.writeFilesystem(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * creates and populates a dialog window to ask user for the password
     * currently does not enforce any password format
     * TODO: possibly ask Liliana or the Doctor about necessary security
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
            if(!pwd.equals(newValue)) {
                errorLabel.setVisible(true);
                okButton.setDisable(true);
            } else {
                errorLabel.setVisible(false);
                okButton.setDisable(false);
            }
        });

        passwordField1.textProperty().addListener((observable, oldValue, newValue) -> {
            String pwd = passwordField.getText();
            if(!pwd.equals(newValue)) {
                errorLabel.setVisible(true);
                okButton.setDisable(true);
            } else {
                errorLabel.setVisible(false);
                okButton.setDisable(false);
            }
        });

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if(dialogButton == ButtonType.OK) {
                return new String(passwordField.getText());
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        return (result.isPresent()) ? result.get() : null;
    }

    /**
     * TODO: user clicks save w/o providing filename, the default filename should be used at the directory user was in
     * @return the absolute path of the filename or null if cancel was clicked
     */
    private static String askForFileName() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Export File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel .xlsx File", "*.xlsx"));
        File savedFile = fileChooser.showSaveDialog(RootViewController.stage);
        return (savedFile != null) ? savedFile.getAbsolutePath()+".xlsx" : null;
    }

    private static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.CLOSE);
        alert.showAndWait();
    }
}
