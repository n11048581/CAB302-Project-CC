package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.web.WebView;

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
    private WebView mapWebView;

    @FXML
    private void initialize() {
        Map map = new Map();
        mapWebView.getEngine().loadContent(map.getMapHTML());
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
