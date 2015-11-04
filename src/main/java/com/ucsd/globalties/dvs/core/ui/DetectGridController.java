package com.ucsd.globalties.dvs.core.ui;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import lombok.extern.slf4j.Slf4j;

import com.ucsd.globalties.dvs.core.Main;
/**
 * Interaction logic for the detection grid
 * @author Sabit
 *
 */
@Slf4j
public class DetectGridController implements Initializable, ControlledUpdateScreen{
  private NavigationController navigationController;
  private RootViewController rootViewController;
  
  @FXML
  private GridPane root;
  @FXML
  private TilePane tilePaneLeft;
  @FXML
  private TilePane tilePaneRight;
  @FXML
  private Button btnPrev;
  @FXML
  private Button btnNext;

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'detect_grid.fxml'.";
    tilePaneLeft.setPrefRows(2);
    tilePaneRight.setPrefRows(2);
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
  private void goToPhotoGrid(ActionEvent event) {
    navigationController.setScreen(Main.photoGridID);
  }
  @FXML
  private void goToResultsGrid(ActionEvent event) {
    navigationController.setScreen(Main.resultGridID);
  }

  @Override
  public void update() {
    Map<String,String> detected = rootViewController.getController().detectAll();
    resetState();
    if (detected.get("left_eye_horizontal") != null) {
      ImageView iView = new ImageView();
      iView.setImage(new Image("file:"+detected.get("left_eye_horizontal")));
      iView.setFitHeight(100);
      iView.setPreserveRatio(true);
      iView.setSmooth(true);
      iView.setCache(true);
      tilePaneLeft.getChildren().add(iView);
    }    
    if (detected.get("left_eye_pupil_horizontal") != null) {
      ImageView iView = new ImageView();
      iView.setImage(new Image("file:"+detected.get("left_eye_pupil_horizontal")));
      iView.setFitHeight(100);
      iView.setPreserveRatio(true);
      iView.setSmooth(true);
      iView.setCache(true);
      tilePaneLeft.getChildren().add(iView);
    }    
    if (detected.get("right_eye_horizontal") != null) {
      ImageView iView = new ImageView();
      iView.setImage(new Image("file:"+detected.get("right_eye_horizontal")));
      iView.setFitHeight(100);
      iView.setPreserveRatio(true);
      iView.setSmooth(true);
      iView.setCache(true);
      tilePaneLeft.getChildren().add(iView);
    }    
    if (detected.get("right_eye_pupil_horizontal") != null) {
      ImageView iView = new ImageView();
      iView.setImage(new Image("file:"+detected.get("right_eye_pupil_horizontal")));
      iView.setFitHeight(100);
      iView.setPreserveRatio(true);
      iView.setSmooth(true);
      iView.setCache(true);
      tilePaneLeft.getChildren().add(iView);
    }
    if (detected.get("left_eye_vertical") != null) {
      ImageView iView = new ImageView();
      iView.setImage(new Image("file:"+detected.get("left_eye_vertical")));
      iView.setFitHeight(100);
      iView.setPreserveRatio(true);
      iView.setSmooth(true);
      iView.setCache(true);
      tilePaneRight.getChildren().add(iView);
    }    
    if (detected.get("left_eye_pupil_vertical") != null) {
      ImageView iView = new ImageView();
      iView.setImage(new Image("file:"+detected.get("left_eye_pupil_vertical")));
      iView.setFitHeight(100);
      iView.setPreserveRatio(true);
      iView.setSmooth(true);
      iView.setCache(true);
      tilePaneRight.getChildren().add(iView);
    }    
    if (detected.get("right_eye_vertical") != null) {
      ImageView iView = new ImageView();
      iView.setImage(new Image("file:"+detected.get("right_eye_vertical")));
      iView.setFitHeight(100);
      iView.setPreserveRatio(true);
      iView.setSmooth(true);
      iView.setCache(true);
      tilePaneRight.getChildren().add(iView);
    }    
    if (detected.get("right_eye_pupil_vertical") != null) {
      ImageView iView = new ImageView();
      iView.setImage(new Image("file:"+detected.get("right_eye_pupil_vertical")));
      iView.setFitHeight(100);
      iView.setPreserveRatio(true);
      iView.setSmooth(true);
      iView.setCache(true);
      tilePaneRight.getChildren().add(iView);
    }    
  }

  @Override
  public void resetState() {
    tilePaneLeft.getChildren().clear();
    tilePaneRight.getChildren().clear();    
  }
}