package com.ucsd.globalties.dvs.core;

import com.ucsd.globalties.dvs.core.tools.MyDialogs;
import com.ucsd.globalties.dvs.core.ui.RootViewController;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.*;
import java.util.Optional;
import java.util.concurrent.atomic.DoubleAccumulator;

@Slf4j
/**
 * The entry point of the program.
 * Loads OpenCV library, resources, and launches the JavaFX UI.
 * @author Rahul
 *
 */
public class Main extends Application {
    // OpenCV constants for face/eye detection
    private static final String HAAR_FACE = "/haarcascade_frontalface_alt.xml";
    private static final String HAAR_EYE = "/haarcascade_eye.xml";

    // Mostly front end constants.
    // TODO move to constants class or something
    public static final String[] sceneLabels = {"Name", "Date of Birth", "Gender", "Ethnicity", "Language",
            "Room Number", "School", "Screening Comment"};
    public static final String inputScreenID = "inputGrid";
    public static final String inputScreenFile = "/views/input_grid.fxml";
    public static final String photoGridID = "photoGrid";
    public static final String photoGridFile = "/views/photo_grid.fxml";
    public static final String detectGridID = "detectGrid";
    public static final String detectGridFile = "/views/detect_grid.fxml";
    public static final String resultGridID = "resultGrid";
    public static final String resultGridFile = "/views/result_grid.fxml";

    // The paths of the face/eye detection resource fields
    public static String HAAR_FACE_PATH;
    public static String HAAR_EYE_PATH;

    private static Controller controller;
    public static String OUTPUT_FILE;
    public static File TEMP_DIR;

    public static void main(String[] args) {
        initOpenCV();
        launch(args);
    }

    public static void initOpenCV() {
        // create program directories
        TEMP_DIR = new File(System.getProperty("java.io.tmpdir") + File.separator + "ucsd-dvs");
        if (!TEMP_DIR.exists()) {
            TEMP_DIR.mkdirs();
            log.info("temp dir did not exist; created folder: " + TEMP_DIR.getAbsolutePath());
        } else {
            log.info("temp dir already exists at " + TEMP_DIR.getAbsolutePath());
        }
        File outputDir = new File(TEMP_DIR.getAbsolutePath() + File.separator + "output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
            log.info("output dir did not exist; created folder: " + outputDir.getAbsolutePath());
        } else {
            log.info("output dir already exists at: " + outputDir.getAbsolutePath());
        }
        OUTPUT_FILE = outputDir.getAbsolutePath() + File.separator;
        controller = new Controller();
        loadLibraryComponents();
    }

    /**
     * Load the OpenCV resources used for face and eye detection.
     */
    public static void loadLibraryComponents() {
        // load OpenCV constants
        // System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        loadOpencvLibrary();
        // write necessary haar files to temp dir if they don't already exist
        File haarFaceFile = new File(TEMP_DIR.getAbsolutePath() + File.separator + HAAR_FACE);
        File haarEyeFile = new File(TEMP_DIR.getAbsolutePath() + File.separator + HAAR_EYE);
        log.info("haarfacefile: " + haarFaceFile.getAbsolutePath());
        try {
            if (!haarFaceFile.exists()) {
                haarFaceFile.createNewFile();
                InputStream faceIn = Main.class.getResourceAsStream(HAAR_FACE);
                copyResourceToFile(faceIn, haarFaceFile);
                log.info("haarface did not exist, created file at: " + HAAR_FACE_PATH);
            } else {
                log.info("haarface already exists at: " + haarFaceFile.getAbsolutePath());
            }
            if (!haarEyeFile.exists()) {
                haarEyeFile.createNewFile();
                InputStream eyeIn = Main.class.getResourceAsStream(HAAR_EYE);
                copyResourceToFile(eyeIn, haarEyeFile);
                log.info("haareye did not exist, created file at: " + HAAR_EYE_PATH);
            } else {
                log.info("haareye already exists at: " + haarEyeFile.getAbsolutePath());
            }
            HAAR_FACE_PATH = haarFaceFile.getAbsolutePath();
            HAAR_EYE_PATH = haarEyeFile.getAbsolutePath();
        } catch (IOException e) {
            log.error("Error creating haarcascade data files", e);
            System.exit(1); // welp, can't detect anything so might as well kill program
        }
    }

    /**
     * Load the OpenCV native library appropriate for this operating system. The
     * library file is stored in the running directory.
     */
    private static void loadOpencvLibrary() {
        try {
            InputStream in = null;
            String osName = System.getProperty("os.name");
            String fileName = "opencv_java249";
            String ext = ".dll";
            log.info(osName);
            if (osName.startsWith("Windows")) {
                int bitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));
                if (bitness == 32) {
                    log.info("32 bit detected");
                    in = Main.class.getResourceAsStream("/opencv/opencv_java249_x86.dll");
                } else if (bitness == 64) {
                    log.info("64 bit detected");
                    in = Main.class.getResourceAsStream("/opencv/opencv_java249_x64.dll");
                } else {
                    log.info("Unknown bit detected - trying with 32 bit");
                    in = Main.class.getResourceAsStream("/opencv/opencv_java249_x86.dll");
                }

            } else if (osName.equals("Mac OS X")) {
                log.info("Mac os detected");
                in = Main.class.getResourceAsStream("/opencv/libopencv_java249.dylib");
                ext = ".dylib";
            } else if (osName.equals("Linux")) {
                log.info("Linux os detected.");
                in = Main.class.getResourceAsStream("/opencv/libopencv_java2412.so");
                ext = ".so";
            }

            File nativeLib = new File(TEMP_DIR.getAbsolutePath() + File.separator + fileName + ext);
            if (nativeLib.exists()) {
                log.info("native library already exists at: " + nativeLib.getAbsolutePath());
                System.load(nativeLib.toString());
            } else {
                nativeLib.createNewFile();
                copyResourceToFile(in, nativeLib);
                log.info("native library created at: " + nativeLib.getAbsolutePath());
                System.load(nativeLib.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load opencv native library", e);
        }
    }

    private static void copyResourceToFile(InputStream in, File dst) throws IOException {
        int BUFFER_SIZE = Short.MAX_VALUE;
        OutputStream out = new BufferedOutputStream(new FileOutputStream(dst), BUFFER_SIZE);
        int b = 0;
        while ((b = in.read()) >= 0) {
            out.write(b);
        }
        out.flush();
        in.close();
        out.close();
    }

    /**
     * Launch the JavaFX UI
     */
    @Override
    public void start(Stage stage) throws Exception {
        try {
            stage.setTitle("Digital Vision Screening");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            AnchorPane anchorPane = loader.load();
            anchorPane.getStylesheets().add("/stylesheets/fextile.css");
            RootViewController rootViewController = loader.getController();
            rootViewController.setController(controller);
            rootViewController.stage = stage;
            stage.setScene(new Scene(anchorPane));
            stage.setMinHeight(600); // set minimum window size
            stage.show();

            /**
             * Override window close event and check if there's unexported data.
             * If there are then ask for confirmation to exit.
             * TODO: need an indicator to let user know if there's any unexported data
             */
            stage.setOnCloseRequest((WindowEvent event) -> {
                if(controller.checkPatientList()) {
                    event.consume();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText("You have unsaved data!");
                    alert.setContentText("Are you sure you want to exit?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if(result.get() == ButtonType.OK) {
                        stage.close();
                    }
                }
            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
