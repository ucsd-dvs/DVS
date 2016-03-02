package com.ucsd.globalties.dvs.core.ui;

import com.ucsd.globalties.dvs.core.Controller;
import com.ucsd.globalties.dvs.core.EyeDisease;
import com.ucsd.globalties.dvs.core.Main;
import com.ucsd.globalties.dvs.core.Patient;
import com.ucsd.globalties.dvs.core.detect.DiseaseDetector;
import com.ucsd.globalties.dvs.core.model.DiseaseRecord;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * The main front-end component. It loads layouts from the FXML layouts in the resources folder.
 * TODO continue improving the design
 * Some resources:
 * https://blogs.oracle.com/acaicedo/entry/managing_multiple_screens_in_javafx1
 * http://code.makery.ch/java/javafx-8-tutorial-intro/
 *
 * @author Sabit
 */
@Slf4j
public class RootViewController implements Initializable {

    /*************************************************************************
     * FXML components
     *************************************************************************/
    @FXML
    private AnchorPane root;
    @FXML
    @Getter
    private Button nextButton;
    @FXML
    @Getter
    private Button backButton;
    @FXML
    private MenuItem exportItem;

    /*************************************************************************
     * Member Variables
     *************************************************************************/
    @Getter
    @Setter
    private Controller controller;
    @Getter
    private NavigationController uiController;
    @Getter
    private ChangeListener<Number> windowWidthListener = null;
    @Getter
    private ChangeListener<Number> windowHeightListener = null;
    private ObservableValue<Number> observableValue = null;
    private Number oldWidth = null;
    private Number newWidth = null;
    private Number oldHeight = null;
    private Number newHeight = null;
    public static Stage stage; //stage is the window in JavaFX. This is the parent window of root

    /*************************************************************************
     * Public Methods
     *************************************************************************/
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'main.fxml'.";
        assert exportItem != null : "fx:id=\"exportItem\" was not injected: check your FXML file 'main.fxml'.";

        //initialize the navigation controller with different screens
        uiController = new NavigationController(this);
        uiController.setId("NavigationController"); // ID is for debugging
        uiController.loadScreen(Main.inputScreenID, Main.inputScreenFile);
        uiController.loadScreen(Main.photoGridID, Main.photoGridFile);
        uiController.loadScreen(Main.detectGridID, Main.detectGridFile);
        uiController.loadScreen(Main.resultGridID, Main.resultGridFile);
        uiController.setScreen(Main.inputScreenID);
        root.getChildren().addAll(uiController);
        root.setTopAnchor(uiController, 60.0);
        root.setLeftAnchor(uiController, 0.0);
        root.setRightAnchor(uiController, 0.0);

        observableValue = new ObservableValue<Number>() {
            @Override
            public void addListener(ChangeListener<? super Number> listener) {

            }
            @Override
            public void removeListener(ChangeListener<? super Number> listener) {

            }
            @Override
            public Number getValue() {
                return null;
            }
            @Override
            public void addListener(InvalidationListener listener) {

            }
            @Override
            public void removeListener(InvalidationListener listener) {

            }
        };
        oldWidth = new Number() {
            @Override
            public int intValue() {
                return 0;
            }
            @Override
            public long longValue() {
                return 0;
            }
            @Override
            public float floatValue() {
                return 0;
            }
            @Override
            public double doubleValue() {
                return root.getWidth();
            }
        };
        newWidth = new Number() {
            @Override
            public int intValue() {
                return 0;
            }
            @Override
            public long longValue() {
                return 0;
            }
            @Override
            public float floatValue() {
                return 0;
            }
            @Override
            public double doubleValue() {
                return root.getWidth();
            }
        };
        oldHeight = new Number() {
            @Override
            public int intValue() {
                return 0;
            }
            @Override
            public long longValue() {
                return 0;
            }
            @Override
            public float floatValue() {
                return 0;
            }
            @Override
            public double doubleValue() {
                return root.getHeight();
            }
        };
        newHeight = new Number() {
            @Override
            public int intValue() {
                return 0;
            }
            @Override
            public long longValue() {
                return 0;
            }
            @Override
            public float floatValue() {
                return 0;
            }
            @Override
            public double doubleValue() {
                return root.getHeight();
            }
        };


        attachListeners();
    }

    public void fireWindowSizeChangedEvent() {
        windowHeightListener.changed(observableValue, oldHeight, newHeight);
        windowWidthListener.changed(observableValue, oldWidth, newWidth);
    }

    private void attachListeners() {
        windowHeightListener = (observable, oldValue, newValue) -> {
                Double prefHeight = newValue.doubleValue() - 150.0;
                StackPane navController = (StackPane) root.getChildren().get(2);
                GridPane grid = (GridPane) navController.getChildren().get(0);
                grid.setPrefHeight(prefHeight);

                Double btnPrefHeight = newValue.doubleValue() * 0.05;
                if(btnPrefHeight > 40.0) btnPrefHeight = 40.0;
                backButton.setPrefHeight(btnPrefHeight);
        };
        root.heightProperty().addListener(windowHeightListener);

        windowWidthListener = (observable, oldValue, newValue) -> {
                Double prefWidth = newValue.doubleValue() - 100.0;
                StackPane navController = (StackPane) root.getChildren().get(2);
                GridPane grid = (GridPane) navController.getChildren().get(0);
                grid.setPrefWidth(prefWidth);

                Double btnPrefWidth = newValue.doubleValue() * 0.11;
                if(btnPrefWidth > 125.0) btnPrefWidth = 125.0;
                nextButton.setPrefWidth(btnPrefWidth);
        };
        root.widthProperty().addListener(windowWidthListener);
    }

    /*************************************************************************
     * Event Handlers
     *************************************************************************/
    @FXML
    private void exportToExcel(ActionEvent event) {
        controller.exportData();
    }

    @FXML
    private void createDummyData(ActionEvent event) {
        controller.createDummyData();
    }

    @FXML
    private void goToResults(ActionEvent event) {
        Patient patient = Patient.builder()
                .firstName("Jane")
                .lastName("Austin")
                .birth("")
                .gender("Female")
                .ethnicity("")
                .language("")
                .roomNumber("")
                .school("UCSD")
                .comment("Hello, Nurse!")
                .diseaseRecord(new ArrayList<>())
                .build();
        patient.getDiseaseRecord().add(new DiseaseRecord(EyeDisease.ANISOMETROPIA, DiseaseRecord.PASS, true));
        patient.getDiseaseRecord().add(new DiseaseRecord(EyeDisease.MYOPIA, DiseaseRecord.PASS, true));
        patient.getDiseaseRecord().add(new DiseaseRecord(EyeDisease.HYPEROPIA, DiseaseRecord.PASS, true));
        patient.getDiseaseRecord().add(new DiseaseRecord(EyeDisease.ASTIGMATISM, DiseaseRecord.PASS, true));
        patient.getDiseaseRecord().add(new DiseaseRecord(EyeDisease.CATARACTS, DiseaseRecord.PASS, true));
        patient.getDiseaseRecord().add(new DiseaseRecord(EyeDisease.STRABISMUS, DiseaseRecord.PASS, true));
        controller.setPatient(patient);
        uiController.setScreen(Main.resultGridID);
    }

    @FXML
    private void showMetrics(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Diagnosis Metrics");

        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        String message = "";
        message += "Myopia: " + DiseaseDetector.MYOPIA_THRESHOLD + "\n";
        message += "Hyperopia: " + DiseaseDetector.HYPEROPIA_THRESHOLD + "\n";
        message += "Strabismus Angle: " + DiseaseDetector.STRABISMUS_ANGLE_THRESHOLD + "\n";
        message += "Strabismus Distance: " + decimalFormat.format(DiseaseDetector.STRABISMUS_DISTANCE_THRESHOLD) + "\n";

        alert.setContentText(message);
        alert.showAndWait();
    }
}
