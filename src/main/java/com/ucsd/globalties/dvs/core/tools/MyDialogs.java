package com.ucsd.globalties.dvs.core.tools;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class MyDialogs {
    public static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.CLOSE);
        alert.showAndWait();
    }
}
