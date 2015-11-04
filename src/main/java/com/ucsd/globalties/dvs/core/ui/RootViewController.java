package com.ucsd.globalties.dvs.core.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.ucsd.globalties.dvs.core.Controller;
import com.ucsd.globalties.dvs.core.Main;

/**
 * The main front-end component. It loads layouts from the FXML layouts in the resources folder.
 * TODO continue improving the design
 * Some resources:
 * https://blogs.oracle.com/acaicedo/entry/managing_multiple_screens_in_javafx1
 * http://code.makery.ch/java/javafx-8-tutorial-intro/
 * @author Sabit
 *
 */
@Slf4j
public class RootViewController implements Initializable {  
  @Getter @ Setter
  private Controller controller;  

  //Value injected by FXMLLoader
  @FXML
  private VBox root;
  @FXML
  private StackPane stackPane;
  private NavigationController uiController;
  
  @FXML
  private MenuItem exportItem;

  @Override // This method is called by the FXMLLoader when initialization is complete
  public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'main.fxml'.";    
    assert exportItem != null : "fx:id=\"exportItem\" was not injected: check your FXML file 'main.fxml'.";

    //initialize the navigation controller with different screens
    NavigationController mainContainer = new NavigationController(this);
    mainContainer.loadScreen(Main.inputScreenID, Main.inputScreenFile);
    mainContainer.loadScreen(Main.photoGridID, Main.photoGridFile);
    mainContainer.loadScreen(Main.detectGridID, Main.detectGridFile);    
    mainContainer.loadScreen(Main.resultGridID, Main.resultGridFile);
    mainContainer.setScreen(Main.inputScreenID);
    root.getChildren().addAll(mainContainer);
  }  
  
  @FXML
  private void exportToExcel(ActionEvent event) {
    controller.exportData();
    System.out.println("Export called");
  } 

}
