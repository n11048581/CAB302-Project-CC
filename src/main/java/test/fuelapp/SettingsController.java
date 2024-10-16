package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import javafx.concurrent.Task;

public class SettingsController  extends Thread {
    DatabaseOperations databaseOperations = new DatabaseOperations();

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

            // Save details to database
            dbOperations.saveUserDetails(updatedUser);

            Task<Void> updateCrowDatabase = new Task<Void>() {     // Background thread to fetch API data progressively
                @Override
                public Void call() throws Exception {
                    // userLat and userLong pulled from Users db table, assigned in Settings
                    DatabaseOperations.crowFliesList.clear();
                    databaseOperations.generateCrowFliesList(tf_latitude.getText(), tf_longitude.getText());
                    databaseOperations.updateCrowFlies();
                    return null;
                }
            };

            updateCrowDatabase.setOnFailed(e -> {
                System.err.println("Couldn't update database" + updateCrowDatabase.getException().getMessage());
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
        // Redirect to log in page
        SQLiteLink sqLiteLink = new SQLiteLink();
        sqLiteLink.changeScene(event, "LandingPage.fxml", "Landing Page");
    }
}
