package com.ucsd.globalties.dvs.core.ui;

import com.ucsd.globalties.dvs.core.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.DoubleAccumulator;

/**
 * Implementation logic for photo grid
 *
 * @author Sabit
 */
public class PhotoGridController implements Initializable, ControlledScreen {

    /***************************************************************************
     * Private Members
     ***************************************************************************/
    private String hFilePath, vFilePath;
    private final FileChooser fileChooser = new FileChooser();
    private NavigationController navigationController;
    private RootViewController rootViewController;

    /***************************************************************************
     * View Component bindings
     ***************************************************************************/
    @FXML private GridPane root;
    @FXML private Button btnHoriz;
    @FXML private Button btnVert;
    @FXML private ImageView imgHoriz;
    @FXML private ImageView imgVert;

    /***************************************************************************
     * Public Methods
     ***************************************************************************/
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

    @Override
    public void resetState() {
        vFilePath = null;
        hFilePath = null;
        imgHoriz.setImage(null);
        imgVert.setImage(null);

    }

    @Override
    public void onLoad() {
        rootViewController.fireWindowSizeChangedEvent();
    }

    @Override
    public void update() throws Exception {
        ControlledScreen.super.update();
    }

    @Override
    public void bindButtons() {
        rootViewController.getBackButton().setVisible(true);
        rootViewController.getBackButton().setOnAction((event) -> goToInputGrid());
        rootViewController.getNextButton().setText("Next");
        rootViewController.getNextButton().setOnAction((event) -> goToDetectGrid());
    }

    /***************************************************************************
     * Event Handlers
     ***************************************************************************/
    /**
     * JavaFX ignores orientation of image so vertical images get loaded as horizontal
     * and has to be rotated
     */
    @FXML
    private void selectVerticalPicture(ActionEvent event) {
        File dir = new File(System.getProperty("user.dir")+"/src/main/resources/pics");
        fileChooser.setInitialDirectory(dir.getAbsoluteFile());
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file != null) {
            fileChooser.setInitialDirectory(file.getParentFile());
            vFilePath = file.getAbsolutePath();
            imgVert.setImage(new Image("file:///" + vFilePath));
            imgVert.setRotate(-90);
        }
    }

    @FXML
    private void selectHorizontalPicture(ActionEvent event) {
        File dir = new File(System.getProperty("user.dir")+"/src/main/resources/pics");
        fileChooser.setInitialDirectory(dir.getAbsoluteFile());
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file != null) {
            fileChooser.setInitialDirectory(file.getParentFile());
            hFilePath = file.getAbsolutePath();
            imgHoriz.setImage(new Image("file:///" + hFilePath));
        }
    }

    private void goToInputGrid() {
        navigationController.setScreen(Main.inputScreenID);
    }

    /**
     * TODO need to improve passing user input to controller
     */
    private void goToDetectGrid() {
        if (vFilePath == null || hFilePath == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select the appropriate images", ButtonType.CLOSE);
            alert.showAndWait();
        } else {
            rootViewController.getController().setPatientPhotos(hFilePath, vFilePath);
            navigationController.setScreen(Main.detectGridID);
        }
    }
}
