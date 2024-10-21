package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProfileController {
    SQLiteLink sqLiteLink = new SQLiteLink();
    private DatabaseOperations databaseOperations = new DatabaseOperations();
    private String currentUsername = LoginController.current_user;

    @FXML
    private Label label_profile_username;
    @FXML
    private Label label_fuel_type;
    @FXML
    private Label label_fuel_efficiency;
    @FXML
    private Label label_latitude;
    @FXML
    private Label label_longitude;
    @FXML
    private Label label_max_distance;

    @FXML
    public void initialize() {
        loadUserDetailsProfile();
    }

    public void loadUserDetailsProfile() {
        IUser user = databaseOperations.getUserDetails(currentUsername);
        if (user != null) {
            label_profile_username.setText(String.valueOf(user.getName()));
            label_fuel_type.setText(String.valueOf(user.getFuelType()));
            label_fuel_efficiency.setText(String.valueOf(user.getFuelEfficiency()));
            label_latitude.setText(String.valueOf(user.getLatitude()));
            label_longitude.setText(String.valueOf(user.getLongitude()));
            label_max_distance.setText(String.valueOf(user.getMaxTravelDistance()));
        }
    }

    public void toSettings(ActionEvent event) {
        // Redirect to log in page
        sqLiteLink.changeScene(event, "Settings.fxml", "Settings");
    }

    public void toLanding(ActionEvent event) {
        // Redirect to log in page
        sqLiteLink.changeScene(event, "LandingPage.fxml", "Home");
    }
}
