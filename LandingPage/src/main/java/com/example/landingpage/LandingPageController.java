package com.example.landingpage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class LandingPageController {

    @FXML
    public void handleLogin(ActionEvent event) {
        showAlert("Login button clicked", "This would take you to the login page.");
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        showAlert("Register button clicked", "This would take you to the registration page.");
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        showAlert("Search button clicked", "This would take you to the search page.");
    }

    @FXML
    public void handlePriceCompare(ActionEvent event) {
        showAlert("Price Compare button clicked", "This would take you to the price comparison page.");
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
