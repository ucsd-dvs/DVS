package com.ucsd.globalties.dvs.core.ui;

import com.ucsd.globalties.dvs.core.EyeDisease;
import com.ucsd.globalties.dvs.core.Main;
import com.ucsd.globalties.dvs.core.model.DiseaseRecord;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.*;

@Slf4j
/**
 * Implementation logic for the result grid.
 *
 * @author Sabit
 */
public class ResultGridController implements Initializable, ControlledScreen {

    /***************************************************************************
     * Private Members
     ***************************************************************************/
    private NavigationController navigationController;
    private RootViewController rootViewController;

    /***************************************************************************
     * View Component ID bindings
     ***************************************************************************/
    @FXML private GridPane root;
    @FXML private Label nameLabel;
    @FXML private Label globalStatusLabel;

    // Myopia - Horizontal Picture
    @FXML private Label myopiaHLThreshold;
    @FXML private Label myopiaHLValue;
    @FXML private Label myopiaHRThreshold;
    @FXML private Label myopiaHRValue;
    // Myopia - Vertical Picture
    @FXML private Label myopiaVLThreshold;
    @FXML private Label myopiaVLValue;
    @FXML private Label myopiaVRThreshold;
    @FXML private Label myopiaVRValue;

    // Hyperopia - Horizontal Picture
    // Hyperopia - Vertical Picture

    // Strabismus - Horizontal Picture
    // Strabismus - Vertical Picture


    /***************************************************************************
     * Public Methods
     ***************************************************************************/
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'detect_grid.fxml'.";
        List<Node> controlList = new ArrayList<>();

//        log.info("ResultsScreen Initialized");
//        log.info("Child id: {}", root.getChildren().get(0).getId());
//        ((ScrollPane) root.getChildren().get(0)).prefViewportWidthProperty().bind(root.prefWidthProperty());
//        ((ScrollPane) root.getChildren().get(0)).prefViewportHeightProperty().bind(root.prefHeightProperty());
    }

    @Override
    public void setScreenParent(NavigationController navigationController) {
        this.navigationController = navigationController;
    }

    @Override
    public void setRootView(RootViewController rootViewController) {
        this.rootViewController = rootViewController;
    }

    @Override
    public void onLoad() {
        rootViewController.fireWindowSizeChangedEvent();
    }

    /**
     * Updates and creates UI elements to show Diagnosis results
     */
    @Override
    public void update() {
        resetState();
        bindButtons();

        nameLabel.setText(rootViewController.getController().getPatient().getFirstName() +
                " " + rootViewController.getController().getPatient().getLastName());

        rootViewController.getController().diagnose();
        List<DiseaseRecord> medicalRecord = rootViewController.getController().getRecords();
        String refer = "PASS";
        for(DiseaseRecord record : medicalRecord) {
            if (record.getMStatus() == DiseaseRecord.REFER) {
                refer = "REFER";
                break;
            }
            switch(record.getMDiseaseName())  {
                case MYOPIA: {
                    // left eye
                    myopiaHLThreshold.setText(record.getMHorizontalImage().getMLeftEye().getMThresholds().get(DiseaseRecord.MYOPIA_THRESHOLD));
                    myopiaHLValue.setText(record.getMHorizontalImage().getMLeftEye().getMValues().get(DiseaseRecord.MYOPIA_VALUE));
                    // right eye
                    myopiaHRThreshold.setText(record.getMHorizontalImage().getMRightEye().getMThresholds().get(DiseaseRecord.MYOPIA_THRESHOLD));
                    myopiaHRValue.setText(record.getMHorizontalImage().getMRightEye().getMValues().get(DiseaseRecord.MYOPIA_VALUE));
                    break;
                }
                case HYPEROPIA: {
                    break;
                }
                case STRABISMUS: {
                    break;
                }
                case ASTIGMATISM: {
                    break;
                }
                case ANISOMETROPIA: {
                    break;
                }
                case CATARACTS: {
                    break;
                }
                default: {
                    log.error("ResultGridController.java:update(): Shit's gone wrong man!");
                    break;
                }
            }
        }

        globalStatusLabel.setText(refer);

        setStyleForLabel(globalStatusLabel);

        rootViewController.getController().finalizePatient();
    } /* END update() */

    @Override
    public void resetState() {
//        contentBox.getChildren().clear();
//        controlList.clear();
    }

    @Override
    public void bindButtons() {
        rootViewController.getBackButton().setVisible(false);
        rootViewController.getNextButton().setText("Start Over");
        rootViewController.getNextButton().setMinWidth(100);
        rootViewController.getNextButton().setOnAction((event) -> goToInputGrid());
    }

    /***************************************************************************
     * Event Handlers
     ***************************************************************************/
    @FXML
    private void goToInputGrid() {
        navigationController.resetAll();
        navigationController.setScreen(Main.inputScreenID);
    }

    /***************************************************************************
     * Private Methods
     ***************************************************************************/

    /**
     * sets the css style for the labels depending on whether its "pass"/"refet"/"n/a"
     * @param label
     */
    private void setStyleForLabel(Label label) {
        if(label.getText().equalsIgnoreCase("pass")) {
            label.getStyleClass().addAll("alert-success", "container");
        } else if(label.getText().equalsIgnoreCase("refer")) {
            label.getStyleClass().addAll("alert-danger", "container");
        } else {
            label.getStyleClass().addAll("alert-warning", "container");
        }
    }

}
