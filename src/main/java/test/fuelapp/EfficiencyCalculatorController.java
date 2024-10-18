package test.fuelapp;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EfficiencyCalculatorController {

    @FXML
    private VBox efficiencyCalculatorBox;

    @FXML
    private TextField fuelAmountInput;  // TextField for user to input fuel amount

    @FXML
    private Button calculateButton;  // Button to trigger calculation

    private SQLiteLink sqLiteLink = new SQLiteLink();
    private Connection connection;

    private double fuelAmount = 20.0;  // Default fuel amount (20L)

    public EfficiencyCalculatorController() {
        connection = SQLiteLink.Connector();  // Database connection
        if (connection == null) {
            System.out.println("Database Connection Unsuccessful");
            System.exit(1);
        }
    }

    @FXML
    private void initialize() {
        // Set default fuel amount
        fuelAmountInput.setText(String.valueOf(fuelAmount));

        // Calculate button action
        calculateButton.setOnAction(event -> {
            fuelAmount = Double.parseDouble(fuelAmountInput.getText());
            fetchAndDisplayStationsFromDatabase();  // Fetch data and sort by updated fuel amount
        });

        // Load and display the default data (20L)
        fetchAndDisplayStationsFromDatabase();
    }

    // Fetch and display station data from the SQLite database
    private void fetchAndDisplayStationsFromDatabase() {
        efficiencyCalculatorBox.getChildren().clear();  // Clear the existing UI

        String query = "SELECT * FROM gas_stations ORDER BY crow_flies_to_user";
        List<StationDetails> stationDetailsList = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Iterate over the results and create StationDetails objects
            while (resultSet.next()) {
                String stationName = resultSet.getString("station_name");
                double price = resultSet.getDouble("price");
                String address = resultSet.getString("station_address");
                String fuelType = resultSet.getString("fuel_type");
                double distance = resultSet.getDouble("crow_flies_to_user");

                // Calculate total refueling cost for each station
                double totalCost = fuelAmount * (price / 1000.0);  // Convert price from cents to dollars

                // Add the station data to the list
                stationDetailsList.add(new StationDetails(stationName, price, address, fuelType, distance, totalCost));
            }

            // Sort the list by totalCost (ascending order)
            stationDetailsList.sort(Comparator.comparingDouble(StationDetails::getTotalCost));

            // After sorting, update the UI
            for (StationDetails station : stationDetailsList) {
                addStationToUI(station);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Adds station details to the UI
    private void addStationToUI(StationDetails station) {
        Label label = new Label("Station: " + station.getStationName() +
                " - Price: $" + String.format("%.2f", station.getPrice() / 1000.0) +
                " - Address: " + station.getAddress() +
                " - Fuel Type: " + station.getFuelType() +
                " - Distance: " + String.format("%.2f", station.getDistance()) + " km" +
                " - Total Refueling Cost: $" + String.format("%.2f", station.getTotalCost()));
        efficiencyCalculatorBox.getChildren().add(label);
        efficiencyCalculatorBox.getChildren().add(new Separator());
    }

    // StationDetails class to store station information
    private class StationDetails {
        private String stationName;
        private double price;
        private String address;
        private String fuelType;
        private double distance;
        private double totalCost;

        public StationDetails(String stationName, double price, String address, String fuelType, double distance, double totalCost) {
            this.stationName = stationName;
            this.price = price;
            this.address = address;
            this.fuelType = fuelType;
            this.distance = distance;
            this.totalCost = totalCost;
        }

        public String getStationName() {
            return stationName;
        }

        public double getPrice() {
            return price;
        }

        public String getAddress() {
            return address;
        }

        public String getFuelType() {
            return fuelType;
        }

        public double getDistance() {
            return distance;
        }

        public double getTotalCost() {
            return totalCost;
        }
    }

    // Scene navigation methods
    @FXML
    public void map(javafx.event.ActionEvent event) {
        sqLiteLink.changeScene(event, "LandingPage.fxml", "Landing Page");
    }

    @FXML
    public void handlePriceCompare(javafx.event.ActionEvent event) {
        sqLiteLink.changeScene(event, "PricePage.fxml", "Price Compare");
    }

    @FXML
    public void handleSettings(javafx.event.ActionEvent event) {
        sqLiteLink.changeScene(event, "Settings.fxml", "Settings");
    }

    @FXML
    public void LogOut(javafx.event.ActionEvent event) {
        sqLiteLink.changeScene(event, "LogInPage.fxml", "Log In");
    }
}
