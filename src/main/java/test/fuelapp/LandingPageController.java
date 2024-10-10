package test.fuelapp;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import test.fuelapp.sample.StationDetails;
import java.util.ArrayList;
import java.util.List;


import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

public class LandingPageController {
    SQLiteLink sqLiteLink = new SQLiteLink();

    @FXML
    public void handleLogin(ActionEvent event) {
        showAlert("Login button clicked", "This would take you to the login page.");
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        showAlert("Register button clicked", "This would take you to the registration page.");
    }

    @FXML
    private VBox gluonMap;




    @FXML
    public void initialize() {
        MapView mapView = Map.createMapView();
        gluonMap.getChildren().add(mapView);
        VBox.setVgrow(mapView, Priority.ALWAYS);

    }

    //@FXML
    //public void handleSearch(ActionEvent event) {
    //    showAlert("Search button clicked", "This would take you to the search page.");
    //}

    @FXML
    public void handlePriceCompare(ActionEvent event) {
        sqLiteLink.changeScene(event, "PricePage.fxml", "Price Compare");
    }

    @FXML
    public void handleCalculator(ActionEvent event) {
        showAlert("Calculator button clicked", "This would take you to the fuel efficiency calculator.");
    }

    @FXML
    public void handleMap(ActionEvent event) {
        showAlert("Map button clicked", "This would take you to the map view.");
    }

    @FXML
    public void handleSettings(ActionEvent event) {
        showAlert("Settings button clicked", "This would take you to the settings page.");
    }

    public void LogOut (ActionEvent event) {
        // Redirect to log in page
        sqLiteLink.changeScene(event, "LogInPage.fxml", "Log In");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
