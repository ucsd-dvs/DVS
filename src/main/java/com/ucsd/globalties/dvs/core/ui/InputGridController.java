package com.ucsd.globalties.dvs.core.ui;

import com.ucsd.globalties.dvs.core.EyeDisease;
import com.ucsd.globalties.dvs.core.Main;
import com.ucsd.globalties.dvs.core.Patient;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * Interaction logic for input grid
 *
 * @author sabitn2
 */
@Slf4j
//Patient information labels
public class InputGridController implements Initializable, ControlledScreen {
    public static String[] sceneLabels = {"First Name", "Last Name", "Date of Birth", "Gender", "Ethnicity", "Language", "Results", "Room Number"};
    private Map<String, TextField> inputValues = new HashMap<String, TextField>();


    private NavigationController navigationController;
    private RootViewController rootViewController;

    @FXML
    private GridPane root;

    @Override
    public void initialize(URL url, ResourceBundle rsrc) {
        assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'input_grid.fxml'.";
        setupInputGrid();
    }

    @Override
    public void setScreenParent(NavigationController navigationController) {
        this.navigationController = navigationController;
    }


    /*
     * Sets up the initial input grid.
     */
    private void setupInputGrid() {

        //Dropdown menu for days
        final ComboBox day = new ComboBox();
        for (int i = 1; i < 32; i++) {
            day.getItems().addAll(i);
        }

        //Dropdown menu for months
        final ComboBox month = new ComboBox();
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

        //Dropdown menu for years
        final ComboBox year = new ComboBox();
        for (int i = 1990; i < 2015; i++) {
            year.getItems().addAll(i);
        }

        //Dropdown menu for ethnicity
        final ComboBox ethnicity = new ComboBox();
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

        //Dropdown menu for languages
        final ComboBox languages = new ComboBox();
        languages.getItems().addAll(
                "English",
                "Spanish",
                "Other"
        );

        HBox hbComboBox = new HBox(10);
        //hbComboBox.setAlignment(Pos.BOTTOM_LEFT);
        hbComboBox.getChildren().add(day);
        hbComboBox.getChildren().add(month);
        hbComboBox.getChildren().add(year);

        HBox hbComboBoxEth = new HBox(10);
        hbComboBoxEth.getChildren().add(ethnicity);

        HBox hbComboBoxLan = new HBox(10);
        hbComboBoxLan.getChildren().add(languages);

        //Add sceneLabels for each field
        for (int i = 0; i < sceneLabels.length; i++) {
            String sceneLabel = sceneLabels[i];
            Label label = new Label(sceneLabel);

            TextField field = new TextField();
            inputValues.put(sceneLabel, field);
            root.add(label, 0, i);

            //add ComboBox for dropdown menu for DOB
            if (i == 2) {
                root.add(hbComboBox, 1, i);
            }

            //add Radio button for Gender
            else if (i == 3) {
                RadioButton rMale, rFemale;
                ToggleGroup group;
                rMale = new RadioButton("Male");
                rFemale = new RadioButton("Female");

                group = new ToggleGroup();
                rMale.setToggleGroup(group);
                rFemale.setToggleGroup(group);
                rMale.setSelected(true);

                HBox hbradio;
                hbradio = new HBox(50, rMale, rFemale);
                hbradio.setPadding(new Insets(10));

                root.add(hbradio, 1, i);

                //Stage st;
                //st.show();
            } else if (i == 4) {
                root.add(hbComboBoxEth, 1, i);
            } else if (i == 5) {
                root.add(hbComboBoxLan, 1, i);
            } else {
                root.add(field, 1, i);
            }
        }


        //root.add(comboBox, 1, sceneLabels.length + 3);
        Button btn = new Button("Next");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbBtn.getChildren().add(btn);
        root.add(hbBtn, 1, sceneLabels.length + 2);
        //1 = width, +3 = height
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                goToPhotoGrid(e);
            }
        });
    }

    /**
     * TODO Definitely want to improve passing user input to controller
     *
     * @param event
     */
    private void goToPhotoGrid(ActionEvent event) {
        int i = 0;
        Patient patient = new Patient(
                inputValues.get(sceneLabels[i++]).getText(),
                inputValues.get(sceneLabels[i++]).getText(),
                inputValues.get(sceneLabels[i++]).getText(),
                inputValues.get(sceneLabels[i++]).getText(),
                inputValues.get(sceneLabels[i++]).getText(),
                inputValues.get(sceneLabels[i++]).getText(),
                inputValues.get(sceneLabels[i++]).getText(),
                inputValues.get(sceneLabels[i++]).getText(),
                "",
                new EnumMap<EyeDisease, String>(EyeDisease.class));
        rootViewController.getController().setPatient(patient);

//        rootViewController.getController().setPatient(Patient.builder()
//                .name(inputValues.get(sceneLabels[i++]).getText())
//                .birth(inputValues.get(sceneLabels[i++]).getText())
//                .gender(inputValues.get(sceneLabels[i++]).getText())
//                .ethnicity(inputValues.get(sceneLabels[i++]).getText())
//                .language(inputValues.get(sceneLabels[i++]).getText())
//                .roomNumber(inputValues.get(sceneLabels[i++]).getText())
//                .school(inputValues.get(sceneLabels[i++]).getText())
//                .screeningComment(inputValues.get(sceneLabels[i++]).getText())
//                .medicalRecord(new EnumMap<EyeDisease, String>(EyeDisease.class))
//                .build());
        navigationController.setScreen(Main.photoGridID);
        //System.out.println(inputValues.get(sceneLabels[3]).getText());
    }

    @Override
    public void setRootView(RootViewController rootViewController) {
        this.rootViewController = rootViewController;
    }

    @Override
    public void resetState() {
        for (Map.Entry<String, TextField> entry : inputValues.entrySet()) {
            entry.getValue().clear();
        }
    }

}
