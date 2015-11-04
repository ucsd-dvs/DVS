package com.ucsd.globalties.dvs.core.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import com.ucsd.globalties.dvs.core.EyeDisease;
import com.ucsd.globalties.dvs.core.Main;

/**
 * Implementation logic for the result grid.
 * @author Sabit
 *
 */
public class ResultGridController implements Initializable, ControlledUpdateScreen {
  private NavigationController navigationController;
  private RootViewController rootViewController;
  private List<Node> controlList;
  
  @FXML
  private GridPane root;
  
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'detect_grid.fxml'."; 
    controlList = new ArrayList<>();
  }
  
  @Override
  public void setScreenParent(NavigationController navigationController) {
    this.navigationController = navigationController;    
  }
  
  @Override
  public void setRootView(RootViewController rootViewController) {
    this.rootViewController = rootViewController;    
  }
  
  @FXML
  private void goToInputGrid(ActionEvent event) {
    rootViewController.getController().finalizePatient();
    navigationController.resetAll();
    navigationController.setScreen(Main.inputScreenID);
  }

  @Override
  public void update() {
    //get results
    rootViewController.getController().diagnose();
    Map<EyeDisease, String> medicalRecord = rootViewController.getController().getRecords();
    int index = 0;
    for (Map.Entry<EyeDisease, String> entry : medicalRecord.entrySet()) {
      Label diseaseLabel = new Label(entry.getKey().toString());
      Text commentText =  new Text(entry.getValue());
      root.add(diseaseLabel, 0, index);
      root.add(commentText, 1, index);
      controlList.add(diseaseLabel);
      controlList.add(commentText);
      index++;
    }
  }

  @Override
  public void resetState() {
    root.getChildren().removeAll(controlList);
    controlList.clear();
  }

}
