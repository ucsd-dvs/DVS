package com.ucsd.globalties.dvs.core.ui;

import com.ucsd.globalties.dvs.core.Main;
import com.ucsd.globalties.dvs.core.tools.MyDialogs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Interaction logic for the detection grid
 *
 * @author Sabit
 */
@Slf4j
public class DetectGridController implements Initializable, ControlledScreen {

    /***************************************************************************
     * Private Members
     ***************************************************************************/
    private NavigationController navigationController;
    private RootViewController rootViewController;

    /***************************************************************************
     * View Component ID bindings
     ***************************************************************************/
    @FXML private GridPane root;
    @FXML private ImageView hLeftEye;
    @FXML private ImageView hLeftPupil;
    @FXML private ImageView hRightEye;
    @FXML private ImageView hRightPupil;
    @FXML private ImageView vLeftEye;
    @FXML private ImageView vLeftPupil;
    @FXML private ImageView vRightEye;
    @FXML private ImageView vRightPupil;
    private Map<String, String> detected;


    /***************************************************************************
     * Public Methods
     ***************************************************************************/
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'detect_grid.fxml'.";
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
     * TODO: add a progress bar to this screen here
     * TODO: hide the grid when there's an error
     * FIXME: this should be done in a separate thread so it doesn't lock up UI thread
     */
    @Override
    public void update() throws Exception {
        ControlledScreen.super.update();

        //FIXME: I want to update the view w/o detecting
        //So I can show a progress bar on the screen while the detection runs in the background

        detected = rootViewController.getController().detectAll();

        if(detected == null) {
            throw new Exception();
        }
        if (detected.get("left_eye_horizontal") != null) {
            hLeftEye.setImage(new Image("file:" + detected.get("left_eye_horizontal")));
        }
        if (detected.get("right_eye_horizontal") != null) {
            hRightEye.setImage(new Image("file:" + detected.get("right_eye_horizontal")));
        }
        if (detected.get("left_eye_vertical") != null) {
            vLeftEye.setImage(new Image("file:" + detected.get("left_eye_vertical")));
        }
        if (detected.get("right_eye_vertical") != null) {
            vRightEye.setImage(new Image("file:" + detected.get("right_eye_vertical")));
        }
    }

    @Override
    public void resetState() {}

    @Override
    public void bindButtons() {
            rootViewController.getExportToExcel().setVisible(false);
            rootViewController.getBackButton().setOnAction((event) -> goToPhotoGrid());
            rootViewController.getNextButton().setText("Next >");
            rootViewController.getNextButton().setOnAction((event) -> goToResultsGrid());
    }

    /***************************************************************************
     * Event Handlers
     ***************************************************************************/

    /**
     * FIXME: already detected eyes & pupils need to be cleared on back button.
     */
    @FXML
    private void goToPhotoGrid() {
        navigationController.setScreen(Main.photoGridID);
    }

    @FXML
    private void goToResultsGrid() {
        navigationController.setScreen(Main.resultGridID);
    }

}