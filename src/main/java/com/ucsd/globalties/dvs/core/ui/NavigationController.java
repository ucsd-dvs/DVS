package com.ucsd.globalties.dvs.core.ui;

import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;

import com.ucsd.globalties.dvs.core.tools.Pair;

/**
 * An extension of the stack pane to allow pushing different screens
 * to the top and keeping track of all screens
 * 
 * Modified from:
 * https://github.com/acaicedo/JFX-MultiScreen/blob/master/ScreensFramework/src/screensframework/ScreensController.java
 * @author Sabit
 *
 */
@Slf4j
public class NavigationController extends StackPane {
  //Holds the screens to be displayed
  private Map<String, Pair<Node,ControlledScreen>> screens = new HashMap<>();
  private RootViewController rootViewController;

  public NavigationController(RootViewController rootViewController) {
    super();
    this.rootViewController = rootViewController;
  }

  /**
   * Add screen with specified name and pair
   * @param name Name to reference screen by
   * @param screen The JavaFX node and Controller for screen
   */
  public void addScreen(String name, Pair<Node,ControlledScreen> screen) {
    screens.put(name, screen);
  }

  /**
   * Gets the node that represents the screen
   * @param name screen to get
   * @return Node corresponding to the screen name
   */
  public Node getScreen(String name) {
    return screens.get(name).getLeft();
  }
  
  /**
   * Loads the fxml file and adds to the screen to the screens collection,
   * and injects the screen pane into the controller
   * @param name Name of screen
   * @param resource Resource of screen
   * @return success
   */
  public boolean loadScreen(String name, String resource) {
    try {
      FXMLLoader myLoader = new FXMLLoader(getClass().getResource(resource));
      Parent loadScreen = (Parent) myLoader.load();
      ControlledScreen myScreenController = ((ControlledScreen) myLoader.getController());
      myScreenController.setScreenParent(this);
      myScreenController.setRootView(rootViewController);
      addScreen(name, new Pair<Node, ControlledScreen>(loadScreen,myScreenController));
      return true;
    } 
    catch (Exception e) {
      e.printStackTrace();
      log.error("Screen not loaded" + e);
      return false;
    }
  }

  /**
   * Adds the specified screen as the current top of the stack pane
   * @param name screen to add
   * @return success
   */
  public boolean setScreen(String name) {       
    if (screens.get(name) != null) {   //screen loaded
      Pair<Node, ControlledScreen> screenPair = screens.get(name);
      if (screenPair.getRight() instanceof ControlledUpdateScreen) {
        ((ControlledUpdateScreen) screenPair.getRight()).update();
      }
      if (!getChildren().isEmpty()) {    //if there is more than one screen
        getChildren().remove(0);                    //remove the displayed screen
        getChildren().add(0, screenPair.getLeft());     //add the screen
      } 
      else {
        
        getChildren().add(screenPair.getLeft());       //no other screen is displayed so show
      }
      return true;
    } 
    else {
      log.warn("Screen not found");
      return false;
    }
  }

  /**
   * Stop keeping track of the specified screen
   * @param name Screen to stop tracking
   * @return success
   */
  public boolean unloadScreen(String name) {
    if (screens.remove(name) == null) {
      log.warn("Screen does not exist");
      return false;
    } 
    else {
      return true;
    }
  }

  /**
   * Resets all screens to default configurations.
   */
  public void resetAll() {
    for (Map.Entry<String, Pair<Node,ControlledScreen>> entry : screens.entrySet()) {
      entry.getValue().getRight().resetState();
    }
    
  }
}
