package test.fuelapp;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import test.fuelapp.sample.FuelPriceAPI;
import test.fuelapp.sample.FuelPriceAPI.StationDetails;

import java.util.List;

public class EfficiencyCalculatorController {

    // Fetch user's location directly from the Map class
    private double userLat = Map.getUserLatitude();
    private double userLong = Map.getUserLongitude();
    private double userFuelEfficiency = 15.0; // Default value

    @FXML
    private VBox efficiencyCalculatorBox;

    private FuelPriceAPI fuelPriceAPI = new FuelPriceAPI();
    private SQLiteLink sqLiteLink = new SQLiteLink();

    @FXML
    private void initialize() {
        // Create a task to fetch and display station data asynchronously
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String latString = String.valueOf(userLat);  // Convert double to String
                String longString = String.valueOf(userLong);  // Convert double to String

                // Call the method with Strings instead of doubles
                fuelPriceAPI.getStationsData(latString, longString, station -> {
                    // Process each station here (e.g., sorting, displaying)
                    double distance = StationCalculations.calculateDistance(station.getDistance());
                    double totalCost = StationCalculations.calculateTotalCost(station, userFuelEfficiency, distance);

                    Platform.runLater(() -> addStationToUI(station));  // Update the UI with station data
                });

                return null;
            }
        };




        task.setOnFailed(e -> System.err.println("Failed to fetch station data: " + task.getException().getMessage()));

        // Run the task in a separate thread
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // Adds station details to the UI
    private void addStationToUI(StationDetails station) {
        double distance = StationCalculations.calculateDistance(station.getDistance());
        double travelCost = StationCalculations.calculateTravelCost(station, userFuelEfficiency, distance);

        Label label = new Label("Station: " + station.getName() +
                " - Price: " + station.getPrice() +
                " - Address: " + station.getAddress() +
                " - Distance: " + distance + " km" +
                " - Total Cost: $" + travelCost);
        efficiencyCalculatorBox.getChildren().add(label);
        efficiencyCalculatorBox.getChildren().add(new Separator());
    }

    // Scene navigation methods
    @FXML
    public void map(ActionEvent event) {
        sqLiteLink.changeScene(event, "LandingPage.fxml", "Landing Page");
    }

    @FXML
    public void handlePriceCompare(ActionEvent event) {
        sqLiteLink.changeScene(event, "PricePage.fxml", "Price Compare");
    }

    @FXML
    public void handleSettings(ActionEvent event) {
        sqLiteLink.changeScene(event, "Settings.fxml", "Settings");
    }

    @FXML
    public void LogOut(ActionEvent event) {
        sqLiteLink.changeScene(event, "LogInPage.fxml", "Log In");
    }
}
