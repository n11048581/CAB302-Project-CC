package test.fuelapp;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;

import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class SettingsController  extends Thread {
    private DatabaseOperations databaseOperations = new DatabaseOperations();
    private SQLiteLink sqLiteLink = new SQLiteLink();

    @FXML
    private TextField tf_fuelEfficiency;
    @FXML
    private ComboBox cb_fuelType;
    @FXML
    private TextField tf_latitude;
    @FXML
    private TextField tf_longitude;
    @FXML
    private TextField tf_maxTravelDistance;

    private String bookmark;

    @FXML
    private Label label_fe;
    @FXML
    private Label label_ft;
    @FXML
    private Label label_lat;
    @FXML
    private Label label_long;
    @FXML
    private Label label_max_distance;
    @FXML
    private Button button_save;
    @FXML
    private Button button_back;
    @FXML
    private Label label_settings;

    @FXML
    private HBox hbox_header;

    @FXML
    private ImageView loading_gif;
    @FXML
    private Label label_loading;

    private String currentUsername = LoginController.current_user;

    @FXML
    public void initialize() {
        loadUserDetailsSettings();
    }

    // Method for displaying existing Settings user account values
    public void loadUserDetailsSettings() {
        IUser user = databaseOperations.getUserDetails(currentUsername);
        if (user != null) {
            tf_fuelEfficiency.setText(String.valueOf(user.getFuelEfficiency()));
            cb_fuelType.setValue(String.valueOf(user.getFuelType()));
            tf_latitude.setText(String.valueOf(user.getLatitude()));
            tf_longitude.setText(String.valueOf(user.getLongitude()));
            tf_maxTravelDistance.setText(String.valueOf(user.getMaxTravelDistance()));
        }
    }

    // Method for setting the Settings page values, calls saveUserDetails to store in DB
    public void handleSave(ActionEvent event) {
        try {
            double fuelEfficiency = Double.parseDouble(tf_fuelEfficiency.getText());
            String fuelType = (String) cb_fuelType.getValue();
            double latitude = Double.parseDouble(tf_latitude.getText());
            double longitude = Double.parseDouble(tf_longitude.getText());
            double maxTravelDistance = Double.parseDouble(tf_maxTravelDistance.getText());

            // Create instance of user
            IUser updatedUser = new User(currentUsername, fuelEfficiency, fuelType, latitude, longitude, maxTravelDistance, bookmark);

            // Declare elements to be hidden in arrays
            Label[] hiddenLabels =new Label[]{label_fe, label_ft, label_lat, label_long, label_max_distance, label_settings};
            Button[] hiddenButtons =new Button[]{button_save, button_back};
            TextField[] hiddenTextFields = new TextField[]{tf_fuelEfficiency,tf_latitude,tf_longitude,tf_maxTravelDistance};

            // Loops to hide settings elements, can be expanded on if needed
            for (Label hiddenLabel : hiddenLabels) {
                hiddenLabel.setVisible(false);
            }
            for (Button hiddenButton : hiddenButtons) {
                hiddenButton.setVisible(false);
            }
            for (TextField hiddenTextField : hiddenTextFields) {
                hiddenTextField.setVisible(false);
            }
            cb_fuelType.setVisible(false);
            hbox_header.setVisible(false);
            // Show temporary loading screen elements
            loading_gif.setVisible(true);
            label_loading.setVisible(true);

            // Save updated details to user table in database
            databaseOperations.saveUserDetails(updatedUser);

            // Start thread that will execute database updates and notify listener when completed.
            // Functionally, will calculate distances for user's new location
            executeSettingsTaskInSeparateThread(settingsListener);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Small interface to assist listener thread
    public interface MyThreadListener{
        public void threadFinished();
    }

    public void executeSettingsTaskInSeparateThread(final SettingsController.MyThreadListener settingsListener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Complete database updates that take a significant amount of time. Will calculate distances at the same time
                IUser user = databaseOperations.getUserDetails(currentUsername);
                DatabaseOperations.crowFliesList.clear();
                databaseOperations.generateCrowFliesList(Double.toString(user.getLatitude()),Double.toString(user.getLongitude()));
                try {
                    databaseOperations.updateCrowFlies();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                //Notify the listener when thread is finished
                settingsListener.threadFinished();
            }
        }).start();
    }

    SettingsController.MyThreadListener settingsListener = new SettingsController.MyThreadListener() {
        @Override
        public void threadFinished() {
            // An additional fix to ensure the application thread is being run, as more UI updates need to happen (switching scenes). I am amazed this works
            Platform.runLater(
                    () -> {
                        Stage stage;
                        Parent root;
                        try {
                            // Switch scene to the landing page, without relying on an action event
                            stage = (Stage) button_back.getScene().getWindow();
                            root = FXMLLoader.load(getClass().getResource("Profile.fxml"));
                            Scene scene = new Scene(root);
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }
    };

    public void backToProfile(ActionEvent event) {
        // Redirect to log in page
        SQLiteLink sqLiteLink = new SQLiteLink();
        sqLiteLink.changeScene(event, "Profile.fxml", "Home");
    }

    @FXML
    public void LogOut(ActionEvent event) {
        // Logout and go back to the login page
        sqLiteLink.changeScene(event, "LogInPage.fxml", "Log In");
    }

    @FXML
    public void goToCalculator() {
        // For now, just display an alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Calculator");
        alert.setHeaderText(null);
        alert.setContentText("Calculator button clicked");
        alert.showAndWait();
    }

    @FXML
    public void goToMap(ActionEvent event) {
        // Go back to the landing page
        sqLiteLink.changeScene(event, "LandingPage.fxml", "Map");
    }

    @FXML
    public void goToComparePage(ActionEvent event) {
        // Go back to the landing page
        sqLiteLink.changeScene(event, "PricePage.fxml", "Compare Page");
    }
}







