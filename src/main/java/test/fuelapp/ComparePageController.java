package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import test.fuelapp.sample.StationDetails;
import test.fuelapp.sample.SampleData;

import java.util.List;


public class ComparePageController {

    @FXML
    private TextField searchBar;

    @FXML
    private Button searchButton;

    @FXML
    private Button mapButton;

    @FXML
    private Button priceCompareButton;

    @FXML
    private Button calculatorButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button logoutButton;


    @FXML
    private VBox comparePriceBox;

    @FXML
    private ScrollPane comparePriceScrollPane;



    private SQLiteLink sqLiteLink = new SQLiteLink();


    @FXML
    public void handleSearch() {
        // Get the search query from the search bar
        String searchQuery = searchBar.getText();

        // For now, just display an alert with the search query
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Search Query");
        alert.setHeaderText(null);
        alert.setContentText("You searched for: " + searchQuery);
        alert.showAndWait();
    }

    @FXML
    private RadioButton priceRadioButton;
    @FXML
    private RadioButton distanceRadioButton;

    @FXML
    private void initialize() {
        ToggleGroup toggleGroup = new ToggleGroup();
        priceRadioButton.setToggleGroup(toggleGroup);
        distanceRadioButton.setToggleGroup(toggleGroup);
        handlePriceCompare();
    }

    @FXML
    public void handlePriceCompare() {
        // Get the list of StationDetails objects
        List<StationDetails> stations = SampleData.getSampleData();

        // Iterate over the StationDetails objects and add a label for each one
        for (StationDetails station : stations) {
            Label label = new Label("                       "
                    +"Station: " + station.getName() +
                    " - Price: " + station.getPrice() +
                    " - Address: " + station.getAddress());
            comparePriceBox.getChildren().add(label);
            comparePriceBox.getChildren().add(new Separator());
        }
    }

    @FXML
    public void map(ActionEvent event) {
        // Go back to the landing page
        sqLiteLink.changeScene(event, "LandingPage.fxml", "Landing Page");
    }


    @FXML
    public void handleCalculator() {
        // For now, just display an alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Calculator");
        alert.setHeaderText(null);
        alert.setContentText("Calculator button clicked");
        alert.showAndWait();
    }

    @FXML
    public void handleSettings() {
        // For now, just display an alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Settings");
        alert.setHeaderText(null);
        alert.setContentText("Settings button clicked");
        alert.showAndWait();
    }

    @FXML
    public void LogOut(ActionEvent event) {
        // Logout and go back to the login page
        sqLiteLink.changeScene(event, "LogInPage.fxml", "Log In");
    }
}