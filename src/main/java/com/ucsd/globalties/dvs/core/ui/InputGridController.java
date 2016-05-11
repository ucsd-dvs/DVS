package com.ucsd.globalties.dvs.core.ui;

import com.ucsd.globalties.dvs.core.EyeDisease;
import com.ucsd.globalties.dvs.core.Main;
import com.ucsd.globalties.dvs.core.Patient;
import com.ucsd.globalties.dvs.core.tools.MyDialogs;
import com.ucsd.globalties.dvs.core.tools.WatchDir;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Interaction logic for input grid
 *
 * @author sabitn2
 */
@Slf4j
public class InputGridController implements Initializable, ControlledScreen {

    /***************************************************************************
     * Variables
     ***************************************************************************/
    public static String[] sceneLabels = {
            "First Name", "Last Name", "Date of Birth", "Gender",
            "Ethnicity", "Language", "School", "Room Number", "Comment"
    };
    private Map<String, TextField> inputValues = new HashMap<String, TextField>();


    private NavigationController navigationController;
    private RootViewController rootViewController;


    private TextField firstNameField;
    private TextField lastNameField;
    private ComboBox month;
    private ComboBox day;
    private ComboBox year;
    private ToggleGroup sex;
    private RadioButton maleRadio;
    private RadioButton femaleRadio;
    private ComboBox ethnicity;
    private ComboBox language;
    private TextField school;
    private TextField roomNumber;
    private TextArea comment;

    /***************************************************************************
     * View Components ID bindings
     ***************************************************************************/
    @FXML private GridPane root;

    /***************************************************************************
     * Public Methods
     ***************************************************************************/
    @Override
    public void initialize(URL url, ResourceBundle rsrc) {
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'input_grid.fxml'.";
        setupInputGrid();
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
        firstNameField.clear();
        lastNameField.clear();
        month.setValue(null);
        day.setValue(null);
        year.setValue(null);
        maleRadio.setSelected(false);
        femaleRadio.setSelected(false);
        ethnicity.setValue(null);
        language.setValue(null);
        school.clear();
        roomNumber.clear();
        comment.clear();
    }

    /***************************************************************************
     * Private Methods
     ***************************************************************************/
    /**
     * Creates and adds all the form components to the view
     */
    private void setupInputGrid() {

        createComponents();

        // First row
        root.add(new Label("First Name"), 0, 0);
        root.add(firstNameField, 1, 0);

        // Second Row
        root.add(new Label("Last Name"), 0, 1);
        root.add(lastNameField, 1, 1);

        // Third Row
        HBox hbComboBox = new HBox(10);
        hbComboBox.getChildren().add(month);
        hbComboBox.getChildren().add(day);
        hbComboBox.getChildren().add(year);
        root.add(new Label("Date of Birth"), 0, 2);
        root.add(hbComboBox, 1, 2);

        // Fourth Row
        root.add(new Label("Sex"), 0, 3);
        HBox sexBox = new HBox(10);
        sexBox.getChildren().add(maleRadio);
        sexBox.getChildren().add(femaleRadio);
        root.add(sexBox, 1, 3);

        // Fifth Row
        root.add(new Label("Ethnicity"), 0, 4);
        root.add(ethnicity, 1, 4);

        // Sixth Row
        root.add(new Label("Language"), 0, 5);
        root.add(language, 1, 5);

        // Seventh Row
        root.add(new Label("School"), 0, 6);
        root.add(school, 1, 6);

        // Eigth Row
        root.add(new Label("Room Number"), 0, 7);
        root.add(roomNumber, 1, 7);

        // Ninth Row
        root.add(new Label("Comments"), 0, 8);
        root.add(comment, 1, 8);
    } /* END setupInputGrid() */

    /**
     * TODO: group relevant components together and label them so user knows what can and can't be skipped
     * FIXME: fix the size and text wrap of the text area
     * Creates each component on the UI
     */
    private void createComponents() {
        // Name
        firstNameField = new TextField();
        Platform.runLater(() -> { firstNameField.requestFocus(); });
        lastNameField = new TextField();

        // Date of Birth
        month = new ComboBox();
        month.getItems().addAll(
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
        );
        year = new ComboBox();
        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        Date date = new Date();
        int currentYear = Integer.valueOf(dateFormat.format(date));
        year = new ComboBox();
        for (int i = 1990; i < currentYear; i++) {
            year.getItems().addAll(i);
        }
        day = new ComboBox();
        for (int i = 1; i < 32; i++) {
            day.getItems().addAll(i);
        }

        //Gender
        sex = new ToggleGroup();
        maleRadio = new RadioButton("Male");
        femaleRadio = new RadioButton("Female");
        maleRadio.setToggleGroup(sex);
        femaleRadio.setToggleGroup(sex);

        //Ethnicity
        ethnicity = new ComboBox();
        ethnicity.getItems().addAll(
                "Asian",
                "African",
                "African American",
                "Caucasian",
                "Hispanic",
                "Middle Eastern",
                "Multiracial",
                "Other"
        );

        //Language
        language = new ComboBox();
        language.getItems().addAll(
                "English",
                "Spanish",
                "Other"
        );

        //School
        school = new TextField();

        //Room Number
        roomNumber = new TextField();

        //Comment
        comment = new TextArea();
    } /* END createComponents() */

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy_MM_dd");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    /**
     * TODO Definitely want to improve passing user input to controller
     *
     * @param
     */
    private void goToPhotoGrid() {
        String folder = getCurrentTimeStamp();
        System.out.println("This is our folder: " + folder);
        File file = new File(System.getProperty("user.home")+"/Desktop/"+folder);
        if(!file.exists())
            file.mkdir();


        MyDialogs.showNotice("Please select a horizontal and vertical image, or begin taking pictures.");

        int i = 0;
        String dob =
                ((month.getValue() != null) ? month.getValue().toString() : "") + "/" +
                ((day.getValue() != null) ? day.getValue().toString() : "") + "/" +
                ((year.getValue() != null) ? year.getValue().toString() : "");
        RadioButton selected = (RadioButton) sex.getSelectedToggle();
        rootViewController.getController().setPatient(Patient.builder()
                .firstName(firstNameField.getText())
                .lastName(lastNameField.getText())
                .birth(dob)
                .gender((selected != null) ? selected.getText() : "")
                .ethnicity((ethnicity.getValue() != null) ? ethnicity.getValue().toString() : "")
                .language((ethnicity.getValue() != null) ? language.getValue().toString() : "")
                .roomNumber(roomNumber.getText())
                .school(school.getText())
                .comment(comment.getText())
                .diseaseRecord(new ArrayList<>())
                .build());
        navigationController.setScreen(Main.photoGridID);
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
        rootViewController.getExportToExcel().setVisible(false);
        rootViewController.getBackButton().setVisible(false);
        rootViewController.getNextButton().setText("Next >");
        rootViewController.getNextButton().setOnAction((event) -> goToPhotoGrid());
        rootViewController.getNextButton().setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }
}
