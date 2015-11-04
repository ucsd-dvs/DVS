package com.ucsd.globalties.dvs.core.ui;

import com.ucsd.globalties.dvs.core.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialogs;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Implementation logic for photo grid
 *
 * @author Sabit
 */
public class PhotoGridController implements Initializable, ControlledScreen {
    private String hFilePath, vFilePath;
    private final FileChooser fileChooser = new FileChooser();

    @FXML
    private GridPane root;
    @FXML
    private Button btnHoriz;
    @FXML
    private Button btnVert;
    @FXML
    private Button btnPrev;
    @FXML
    private Button btnNext;
    @FXML
    private ImageView imgHoriz;
    @FXML
    private ImageView imgVert;

    private NavigationController navigationController;
    private RootViewController rootViewController;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'photo_grid.fxml'.";
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
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
    private void selectVerticalPicture(ActionEvent event) {
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file != null) {
            fileChooser.setInitialDirectory(file.getParentFile());
            vFilePath = file.getAbsolutePath();
            imgVert.setImage(new Image("file:///" + vFilePath));
        }
    }

    @FXML
    private void selectHorizontalPicture(ActionEvent event) {
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file != null) {
            fileChooser.setInitialDirectory(file.getParentFile());
            hFilePath = file.getAbsolutePath();
            imgHoriz.setImage(new Image("file:///" + hFilePath));
        }
    }

    @FXML
    private void goToInputGrid(ActionEvent event) {
        navigationController.setScreen(Main.inputScreenID);
    }

    /**
     * TODO need to improve passing user input to controller
     *
     * @param event
     */
    @FXML
    private void goToDetectGrid(ActionEvent event) {
        if (vFilePath == null || hFilePath == null) {
            //controlfx dialogs is deprecated since it will be merged into java
            //planned for 8u40, sometime March 2015
            Action alert = Dialogs.create()
                    .owner(root.getScene().getWindow())
                    .title("Alert!")
                    .message("Please select the appropriate images.")
                    .showError();
        } else {
            rootViewController.getController().setPatientPhotos(hFilePath, vFilePath);
            navigationController.setScreen(Main.detectGridID);
        }
    }

    @Override
    public void resetState() {
        vFilePath = null;
        hFilePath = null;
        imgHoriz.setImage(null);
        imgVert.setImage(null);

    }

}
