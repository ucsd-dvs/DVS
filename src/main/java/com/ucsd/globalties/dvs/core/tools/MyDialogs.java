package com.ucsd.globalties.dvs.core.tools;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class MyDialogs {
    public static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.CLOSE);
        alert.showAndWait();
    }

    public static void showNotice(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }

    public static void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
