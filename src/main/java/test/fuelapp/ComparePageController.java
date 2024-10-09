package test.fuelapp;

import test.fuelapp.SQLiteLink;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import test.fuelapp.sample.FuelPriceAPI;
import test.fuelapp.sample.FuelPriceAPI.StationDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ComparePageController {
    Connection connection;

    public ComparePageController() {
        connection = SQLiteLink.Connector();
        if (connection == null) {
            System.out.println("Database Connection Unsuccessful");
            System.exit(1);
        }
    }

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
    private void initialize() throws SQLException {
        // Initialize the station data from FuelPriceAPI
        //fuelPriceAPI.getStationsData();  // Populate the data
        handlePriceCompare();
    }

    @FXML
    public void handleSearch() {
        // Get the search query from the search bar
        String searchQuery = searchBar.getText();

        // Filter the stations list based on the search query
        List<StationDetails> filteredStations = fuelPriceAPI.getStationsMap().values().stream()
                .filter(station -> station.getName().contains(searchQuery) || station.getAddress().contains(searchQuery))
                .collect(Collectors.toList());

        System.out.println(filteredStations);


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
    public void handlePriceCompare() throws SQLException {
        // Get the list of StationDetails objects
        //List<StationDetails> stations = fuelPriceAPI.getStationsMap().values().stream().collect(Collectors.toList());
        comparePriceBox.getChildren().clear();

        PreparedStatement prepStatement = null;
        ResultSet resultSet = null;
        // Set SQL command to match user input against database
        String query = "SELECT * FROM gas_stations";
        try {
            // Execute query on entered username and password
            prepStatement = connection.prepareStatement(query);
            resultSet = prepStatement.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet.getString("station_name"));

                Label label = new Label("                       "
                +"Station: " + resultSet.getString("station_name") +
                " - Price: " + resultSet.getDouble("price") +
                " - Address: " + resultSet.getString("station_address") +
                " - Fuel type: " + resultSet.getString("fuel_type"));
                //+" - Distance: " + station.getDistance()
                //  +" - Travel cost: " + station.getTravelCost());
                comparePriceBox.getChildren().add(label);
                comparePriceBox.getChildren().add(new Separator());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close prepared statement
            //prepStatement.close();
            //resultSet.close();
        }
        

        // Clear the comparePriceBox

        // Iterate over the StationDetails objects and add a label for each one
      //  for (StationDetails station : stations) {
       //     Label label = new Label("                       "
       //             +"Station: " + station.getName() +
       //             " - Price: " + station.getPrice() +
      //              " - Address: " + station.getAddress() +
       //             " - Fuel type: " + station.getFuelType()
      //              +" - Distance: " + station.getDistance()
      //              +" - Travel cost: " + station.getTravelCost());
     //       comparePriceBox.getChildren().add(label);
       //     comparePriceBox.getChildren().add(new Separator());
        //}
    }


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
