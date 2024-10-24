package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ProfileController {
    SQLiteLink sqLiteLink = new SQLiteLink();
    private DatabaseOperations databaseOperations = new DatabaseOperations();
    private String currentUsername = LoginController.current_user;

    private String userBookmarks;

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
    private VBox vbox_fav_stations;

    @FXML
    private Label label_current_user;

    @FXML
    public void initialize() throws SQLException{
        label_current_user.setText(LoginController.current_user);
        loadUserDetailsProfile();
        displayBookmarks();
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
            userBookmarks = String.valueOf(user.getBookmark());
        }
    }

    public void displayBookmarks() throws SQLException {
        // Create array of users bookmarks
        String[] rawBookmarks = userBookmarks.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
        ArrayList<String> bookmarkArray = new ArrayList<String>();
        Collections.addAll(bookmarkArray, rawBookmarks);

        // Clear all previous bookmarked stations
        vbox_fav_stations.getChildren().clear();
        vbox_fav_stations.getChildren().add(new Separator());

        // For every bookmarked station fetch the stations name and current fuel price
        for (int displayBookmarkCounter = 0; displayBookmarkCounter < bookmarkArray.size(); displayBookmarkCounter++){
            String bookmarkLabelText = databaseOperations.fetchBookmarks(Integer.parseInt(bookmarkArray.get(displayBookmarkCounter)));
            // Ignore base data
            if (Objects.equals(bookmarkLabelText, "null - 0.0")){continue;}

            // Create and style new label to display data
            Label label = new Label(bookmarkLabelText);
            label.setStyle("-fx-font-size: 20;");
            label.setFont(Font.font("SansSerif"));
            label.setTextFill(Color.web("#344E41"));
            vbox_fav_stations.getChildren().add(label);
            vbox_fav_stations.getChildren().add(new Separator());
        }
    }

    public void toSettings(ActionEvent event) {
        // Redirect to log in page
        sqLiteLink.changeScene(event, "Settings.fxml", "Settings");
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
