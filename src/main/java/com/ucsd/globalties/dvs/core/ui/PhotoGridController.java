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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.DoubleAccumulator;

/**
 * Implementation logic for photo grid
 *
 * @author Sabit
 */
@Slf4j
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
    @FXML private ImageView imgHoriz;
    @FXML private ImageView imgVert;
    @FXML private HBox imgHorizBox;
    @FXML private HBox imgVertBox;

    /***************************************************************************
     * Public Methods
     ***************************************************************************/
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'photo_grid.fxml'.";
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        /**
         * Bindings so the images resize properly as the window resizes
         */
        imgHorizBox.prefWidthProperty().bind(root.prefWidthProperty());
        imgHorizBox.prefHeightProperty().bind(root.prefHeightProperty());
        imgVertBox.prefWidthProperty().bind(root.prefWidthProperty());
        imgVertBox.prefHeightProperty().bind(root.prefHeightProperty());
        // Necessary to divide vertical image by 1.2 because the resize happens
        // before the image gets rotated so the resulting image breaks out of
        // its parent container. The Horizontal image is divided so there isn't
        // a noticeable difference in size between the horizontal and vertical
        // images
        imgHoriz.fitWidthProperty().bind(imgHorizBox.widthProperty().divide(1.1));
        imgHoriz.fitHeightProperty().bind(imgHorizBox.heightProperty().divide(1.1));
        imgVert.fitWidthProperty().bind(imgVertBox.widthProperty().divide(1.2));
        imgVert.fitHeightProperty().bind(imgVertBox.heightProperty().divide(1.2));
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
