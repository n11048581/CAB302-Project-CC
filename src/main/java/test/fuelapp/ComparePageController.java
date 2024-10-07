package test.fuelapp;

import javafx.application.Platform;
import javafx.concurrent.Task;
import test.fuelapp.SQLiteLink;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import test.fuelapp.sample.FuelPriceAPI;
import test.fuelapp.sample.FuelPriceAPI.StationDetails;

import java.util.List;
import java.util.stream.Collectors;

public class ComparePageController {

    @FXML
    private TextField searchBar;

    @FXML
    private Button searchButton;

    @FXML
    private VBox comparePriceBox;

    private SQLiteLink sqLiteLink = new SQLiteLink();

    @FXML
    private RadioButton priceRadioButton;
    @FXML
    private RadioButton distanceRadioButton;

    private FuelPriceAPI fuelPriceAPI = new FuelPriceAPI(); // Create an instance of FuelPriceAPI

    @FXML
    private void initialize() {
        Task<Void> task = new Task<Void>() {     // Background thread to fetch API data progressively
            @Override
            protected Void call() throws Exception {
                fuelPriceAPI.getStationsData(station -> {
                    Platform.runLater(() -> updateUIWithStation(station)); // UI updates on JavaFX thread
                });
                return null;
            }
        };

        task.setOnFailed(e -> {
            System.err.println("Failed to fetch station data: " + task.getException().getMessage());
        });

        // Run task in separate thread
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // Update UI with each station's details, called in getStationsData()
    private void updateUIWithStation(StationDetails station) {
        Label label = new Label("Station: " + station.getName() +
                " - Price: " + station.getPrice() +
                " - Address: " + station.getAddress() +
                " - Distance: " + station.getDistance() +
                " - Travel Cost: $" + station.getTravelCost() +
                " - Fuel Type: " + station.getFuelType());
        comparePriceBox.getChildren().add(label);
        comparePriceBox.getChildren().add(new Separator());
    }

    @FXML
    public void handleSearch() {
        // Get the search query from the search bar
        String searchQuery = searchBar.getText();

        // Filter the stations list based on the search query
        List<StationDetails> filteredStations = fuelPriceAPI.getStationsMap().values().stream()
                .filter(station -> station.getName().contains(searchQuery) || station.getAddress().contains(searchQuery))
                .collect(Collectors.toList());

        // Clear the comparePriceBox
        comparePriceBox.getChildren().clear();

        // Iterate over the filtered stations and add a label for each one
        for (StationDetails station : filteredStations) {
            Label label = new Label("                       "
                    +"Station: " + station.getName() +
                    " - Price: " + station.getPrice() +
                    " - Address: " + station.getAddress());
            comparePriceBox.getChildren().add(label);
            comparePriceBox.getChildren().add(new Separator());
        }
    }

    @FXML
    public void handlePriceCompare() {
        // Get the list of StationDetails objects
        List<StationDetails> stations = fuelPriceAPI.getStationsMap().values().stream().collect(Collectors.toList());

        // Clear the comparePriceBox
        comparePriceBox.getChildren().clear();

        // Iterate over the StationDetails objects and add a label for each one
        for (StationDetails station : stations) {
            Label label = new Label("                       "
                    +"Station: " + station.getName() +
                    " - Price: " + station.getPrice() +
                    " - Address: " + station.getAddress() +
                    " - Fuel type: " + station.getFuelType()
                    +" - Distance: " + station.getDistance()
                    +" - Travel cost: " + station.getTravelCost());
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
