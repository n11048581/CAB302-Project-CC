package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import javafx.concurrent.Task;

public class SettingsController {
    private DatabaseOperations databaseOperations = new DatabaseOperations();

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

    private String currentUsername = LoginController.current_user;
    private User currentUser;  // The current User object

    @FXML
    public void initialize() {
        loadUserDetails();
    }

    // Method for displaying existing Settings user account values
    public void loadUserDetails() {
        IUser user = databaseOperations.getUserDetails(currentUsername);
        if (user != null) {
            currentUser = (User) user;
            tf_fuelEfficiency.setText(String.valueOf(user.getFuelEfficiency()));
            tf_fuelType.setText(String.valueOf(user.getFuelType()));
            tf_latitude.setText(String.valueOf(user.getLatitude()));
            tf_longitude.setText(String.valueOf(user.getLongitude()));
            tf_maxTravelDistance.setText(String.valueOf(user.getMaxTravelDistance()));
        } else {
            // User not found, let's use Google API to get the current location
            currentUser = new User(currentUsername, 15.0, "Unleaded", 0.0, 0.0, 50.0); // Default values
            Map.updateUserLocation(currentUser);  // Get current location from Google API

            // Set the fields with the new location
            tf_latitude.setText(String.valueOf(currentUser.getLatitude()));
            tf_longitude.setText(String.valueOf(currentUser.getLongitude()));
        }
    }

    // Method for saving user details, calls saveUserDetails to store in DB
    public void handleSave(ActionEvent event) {
        try {
            double fuelEfficiency = Double.parseDouble(tf_fuelEfficiency.getText());
            String fuelType = tf_fuelType.getText();
            double latitude = Double.parseDouble(tf_latitude.getText());
            double longitude = Double.parseDouble(tf_longitude.getText());
            double maxTravelDistance = Double.parseDouble(tf_maxTravelDistance.getText());

            // Update the User object
            currentUser.setFuelEfficiency(fuelEfficiency);
            currentUser.setFuelType(fuelType);
            currentUser.setLatitude(latitude);
            currentUser.setLongitude(longitude);
            currentUser.setMaxTravelDistance(maxTravelDistance);

            // Save details to database
            databaseOperations.saveUserDetails(currentUser);

            // Update crow flies data (optional)
            Task<Void> updateCrowDatabase = new Task<Void>() {
                @Override
                public Void call() throws Exception {
                    databaseOperations.generateCrowFliesList(tf_latitude.getText(), tf_longitude.getText());
                    databaseOperations.updateCrowFlies();
                    return null;
                }
            };

            updateCrowDatabase.setOnFailed(e -> {
                System.err.println("Couldn't update database: " + updateCrowDatabase.getException().getMessage());
            });

            // Run task in separate thread
            Thread thread = new Thread(updateCrowDatabase);
            thread.setDaemon(true);
            thread.start();

            // return to landing page
            backToLanding(event);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void backToLanding(ActionEvent event) {
        // Redirect to landing page
        SQLiteLink sqLiteLink = new SQLiteLink();
        sqLiteLink.changeScene(event, "LandingPage.fxml", "Home");
    }
}
