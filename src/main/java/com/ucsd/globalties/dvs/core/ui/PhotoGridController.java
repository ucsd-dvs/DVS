package com.ucsd.globalties.dvs.core.ui;

import com.ucsd.globalties.dvs.core.Main;
import com.ucsd.globalties.dvs.core.tools.WatchDir;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    @FXML
    private GridPane root;
    @FXML
    private ImageView imgHoriz;
    @FXML
    private ImageView imgVert;
    @FXML
    private HBox imgHorizBox;
    @FXML
    private HBox imgVertBox;

    /***************************************************************************
     * Directory Watcher bindings
     ***************************************************************************/
    @Getter
    @Setter
    private StringProperty hStrProperty;
    @Getter
    @Setter
    private StringProperty vStrProperty;

    private WatchDir watcher = null;

    /***************************************************************************
     * Public Methods
     ***************************************************************************/
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'photo_grid.fxml'.";
//        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

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

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy_MM_dd");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    @Override
    public void update() throws Exception {
        ControlledScreen.super.update();

        /**
         * TODO: Questions to ask
         * 1) How do we respond if user takes picture outside of img upload screen
         * 2) How do we respond to events other than file creation
         * 3) How do we respond if user runs DVS before taking test picture (folder not yet created)
         * 4) Do we delete picture if it can't be processed?
         * 5) What do we do w/ existing pictures in folder?
         */

        String folder = getCurrentTimeStamp();
        System.out.println("This is our folder: " + folder);
        Path dir_path = Paths.get(System.getProperty("user.home") + "/Desktop/" + folder);
        WatchDir watcher = new WatchDir(dir_path, false);

        // Set horizontal picture
        hStrProperty = new SimpleStringProperty();
        hStrProperty.bind(watcher.messageProperty());

        //Set vertical picture
        hStrProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("hStrProperty change listener");
                System.out.println(newValue);
                if(hFilePath == null){
                    hFilePath = newValue;
                    imgHoriz.setImage(new Image("file:///" + newValue));
                } else if(vFilePath == null){
                    vFilePath = newValue;
                    imgVert.setImage(new Image("file:///" + newValue));
                    imgVert.setRotate(-90);
                }
            }

        });

        //Run watcher on individual thread
        Thread th = new Thread(watcher);
        th.setDaemon(true);
        th.start();

    }

    @Override
    public void bindButtons() {
        rootViewController.getBackButton().setVisible(true);
        rootViewController.getBackButton().setOnAction((event) -> goToInputGrid());
        rootViewController.getNextButton().setText("Next >");
        rootViewController.getNextButton().setOnAction((event) -> goToDetectGrid());
//        rootViewController.getNextButton().setDisable(true);
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

        vFilePath = null;
        imgVert.setImage(null);
    }

    @FXML
    private void selectHorizontalPicture(ActionEvent event) {
        hFilePath = null;
        imgHoriz.setImage(null);
    }

    private void goToInputGrid() {
        if (watcher != null) {
            if (!watcher.cancel()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Could not cancel watcher thread!", ButtonType.CLOSE);
                alert.showAndWait();
            } else {
                navigationController.setScreen(Main.inputScreenID);
            }
        }
    }

    /**
     * TODO need to improve passing user input to controller
     */
    private void goToDetectGrid() {
        if (watcher != null) {
            // FIXME thread isn't being cancelled for some reason
            if (!watcher.cancel()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Could not cancel watcher thread!", ButtonType.CLOSE);
                alert.showAndWait();
            } else {
                rootViewController.getController().setPatientPhotos(hFilePath, vFilePath);
                navigationController.setScreen(Main.detectGridID);
            }
        }
    }
}
