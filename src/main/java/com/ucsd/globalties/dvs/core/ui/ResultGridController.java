package com.ucsd.globalties.dvs.core.ui;

import com.ucsd.globalties.dvs.core.EyeDisease;
import com.ucsd.globalties.dvs.core.Main;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;

@Slf4j
/**
 * Implementation logic for the result grid.
 *
 * @author Sabit
 */
public class ResultGridController implements Initializable, ControlledScreen {

    /***************************************************************************
     * Private Members
     ***************************************************************************/
    private NavigationController navigationController;
    private RootViewController rootViewController;

    /***************************************************************************
     * View Component ID bindings
     ***************************************************************************/
    @FXML private GridPane root;
    @FXML private VBox contentBox;

    /***************************************************************************
     * Public Methods
     ***************************************************************************/
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'detect_grid.fxml'.";
        List<Node> controlList = new ArrayList<>();

        log.info("ResultsScreen Initialized");
        log.info("Child id: {}", root.getChildren().get(0).getId());
        ((ScrollPane) root.getChildren().get(0)).prefViewportWidthProperty().bind(root.prefWidthProperty());
        ((ScrollPane) root.getChildren().get(0)).prefViewportHeightProperty().bind(root.prefHeightProperty());
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
     * Updates and creates UI elements to show Diagnosis results
     */
    @Override
    public void update() {
        resetState();
        bindButtons();

        for(int i = 0; i < 3; i++) {
            String msg = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam et sollicitudin ipsum, non efficitur diam. Nulla auctor lectus lorem, in lacinia nisi suscipit sed";
            Node node = makeElement("Myopia", "PASS", msg);
            Line line = makeHorizontalRule();
            contentBox.getChildren().add(node);
        }
        for(int i = 3; i < 5; i++) {
            String msg = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam et sollicitudin ipsum, non efficitur diam. Nulla auctor lectus lorem, in lacinia nisi suscipit sed";
            Node node = makeElement("Myopia", "REFER", msg);
            Line line = makeHorizontalRule();
            contentBox.getChildren().add(node);
        }
        for(int i = 5; i < 6; i++) {
            String msg = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam et sollicitudin ipsum, non efficitur diam. Nulla auctor lectus lorem, in lacinia nisi suscipit sed";
            Node node = makeElement("Myopia", "N/A", msg);
            Line line = makeHorizontalRule();
            contentBox.getChildren().add(node);
        }

        rootViewController.getController().finalizePatient();

        //get results
//        rootViewController.getController().diagnose();
//        Map<EyeDisease, String> medicalRecord = rootViewController.getController().getRecords();
//        int index = 0;
//        for (Map.Entry<EyeDisease, String> entry : medicalRecord.entrySet()) {
//            Label diseaseLabel = new Label(entry.getKey().toString());
//            Text commentText = new Text(entry.getValue());
//            root.add(diseaseLabel, 0, index);
//            root.add(commentText, 1, index);
//            controlList.add(diseaseLabel);
//            controlList.add(commentText);
//            index++;
//        }
    } /* END update() */

    @Override
    public void resetState() {
        contentBox.getChildren().clear();
//        controlList.clear();
    }

    @Override
    public void bindButtons() {
        rootViewController.getBackButton().setVisible(false);
        rootViewController.getNextButton().setText("Start Over");
        rootViewController.getNextButton().setMinWidth(100);
        rootViewController.getNextButton().setOnAction((event) -> goToInputGrid());
    }

    /***************************************************************************
     * Event Handlers
     ***************************************************************************/
    @FXML
    private void goToInputGrid() {
        navigationController.resetAll();
        navigationController.setScreen(Main.inputScreenID);
    }

    /***************************************************************************
     * Private Methods
     ***************************************************************************/
    /**
     * Creating UI components and returning the top most element.
     * This UI components are being created in code instead of in fxml is to
     * avoid having 20+ member variables referencing all the text that need
     * to be updated.
     *
     * Refer to result_grid.fxml for what this looks like in xml format
     * @param disease refer to EyeDisease for what these are
     * @param status accepted values are: PASS, REFER, or N/A
     * @param description further details on the status
     * @return A node representing a tree of elements to be drawn onto the UI
     */
    private VBox makeElement(String disease, String status, String description) {
        VBox vBox = new VBox();
        vBox.setSpacing(5);

        GridPane gp = new GridPane();
        ColumnConstraints leftCol = new ColumnConstraints();
        leftCol.setPercentWidth(50);
        ColumnConstraints rightCol = new ColumnConstraints();
        rightCol.setPercentWidth(50);
        gp.getColumnConstraints().addAll(leftCol, rightCol);

        HBox diseaseBox = new HBox();
        Label diseaseLabel = new Label(disease);

        diseaseLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");

        diseaseBox.getChildren().add(diseaseLabel);

        HBox statusBox = new HBox();
        statusBox.setAlignment(Pos.CENTER_RIGHT);
        Label statusLabel = new Label(status);

        if(status.equalsIgnoreCase("pass")) {
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else if(status.equalsIgnoreCase("refer")) {
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else if(status.equalsIgnoreCase("n/a")) {
            statusLabel.setStyle("-fx-text-fill: orange;");
        } else {
            log.error("Status not recognized");
        }

        statusBox.getChildren().add(statusLabel);

        gp.add(diseaseBox, 0, 0);
        gp.add(statusBox, 1, 0);

//        Text desc = new Text(description);
//        desc.wrappingWidthProperty().bind(root.prefWidthProperty());
//        desc.setWrappingWidth(root.prefWidth(0));

        Label desc = new Label(description);
        desc.setWrapText(true);

        vBox.getChildren().addAll(gp, desc);

        return vBox;
    } /* END makeElement() */

    /**
     * This creates a horizontal line that is used as the divider on
     * the results screen
     * @return a horizontal line
     */
    private Line makeHorizontalRule() {
        Line line = new Line();
        line.setStartX(0);
        line.setEndX(550);
        return line;
    } /* END makeHorizontalRule() */


}
