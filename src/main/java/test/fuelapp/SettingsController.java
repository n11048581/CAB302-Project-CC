package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class SettingsController {

    @FXML
    private TextField tf_fuelEfficiency;
    @FXML
    private TextField tf_fuelType;
    @FXML
    private TextField tf_latitude;
    @FXML
    private TextField tf_longitude;
    @FXML
    private TextField tf_maxTravelDistance;

    private DatabaseOperations dbOperations = new DatabaseOperations();

    private String currentUsername = LoginController.current_user;

    @FXML
    public void initialize() {
        loadUserDetails();
    }

    // Method for displaying existing Settings user account values
    public void loadUserDetails() {
        IUser user = dbOperations.getUserDetails(currentUsername);
        if (user != null) {
            tf_fuelEfficiency.setText(String.valueOf(user.getFuelEfficiency()));
            tf_fuelType.setText(String.valueOf(user.getFuelType()));
            tf_latitude.setText(String.valueOf(user.getLatitude()));
            tf_longitude.setText(String.valueOf(user.getLongitude()));
            tf_maxTravelDistance.setText(String.valueOf(user.getMaxTravelDistance()));
        }
    }

    // Method for setting the Settings page values, calls saveUserDetails to store in DB
    public void handleSave(ActionEvent event) {
        try {
            double fuelEfficiency = Double.parseDouble(tf_fuelEfficiency.getText());
            String fuelType = tf_fuelType.getText();
            double latitude = Double.parseDouble(tf_latitude.getText());
            double longitude = Double.parseDouble(tf_longitude.getText());
            double maxTravelDistance = Double.parseDouble(tf_maxTravelDistance.getText());

            IUser updatedUser = new User(currentUsername, fuelEfficiency, fuelType, latitude, longitude, maxTravelDistance);

            // Save detials to database
            dbOperations.saveUserDetails(updatedUser);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void BackToLanding(ActionEvent event) {
        // Redirect to log in page
        SQLiteLink sqLiteLink = new SQLiteLink();
        sqLiteLink.changeScene(event, "LandingPage.fxml", "Landing Page");
    }
}
