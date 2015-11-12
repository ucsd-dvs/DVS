package com.ucsd.globalties.dvs.core.ui;

import com.ucsd.globalties.dvs.core.Controller;
import com.ucsd.globalties.dvs.core.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
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
    @Getter
    @Setter
    private Controller controller;

    //Value injected by FXMLLoader
    @FXML
    private VBox root;
    @FXML
    private StackPane stackPane;

    @Getter
    private NavigationController uiController;

    //stage is the window in JavaFX. This is the parent window of root
    public static Stage stage;

    @FXML
    private MenuItem exportItem;

    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'main.fxml'.";
        assert exportItem != null : "fx:id=\"exportItem\" was not injected: check your FXML file 'main.fxml'.";

        //initialize the navigation controller with different screens
        uiController = new NavigationController(this);
        uiController.loadScreen(Main.inputScreenID, Main.inputScreenFile);
        uiController.loadScreen(Main.photoGridID, Main.photoGridFile);
        uiController.loadScreen(Main.detectGridID, Main.detectGridFile);
        uiController.loadScreen(Main.resultGridID, Main.resultGridFile);
        uiController.setScreen(Main.inputScreenID);
        root.getChildren().addAll(uiController);
    }

    @FXML
    private void exportToExcel(ActionEvent event) {
        controller.exportData();
    }

    @FXML
    private void createDummyData(ActionEvent event) {
        controller.createDummyData();
    }



}
