package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import test.fuelapp.sample.FuelPriceAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ComparePageController {
    Connection connection;
    DistanceMatrix distanceMatrix = new DistanceMatrix();
    DatabaseOperations databaseOperations = new DatabaseOperations();

    String fixedLat = "-27.722947504112632";
    String fixedLong = "153.21388361118096";

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
        // fuelPriceAPI.getStationsData();  // Populate the data
        handlePriceCompare();

    }


    @FXML
    public void handlePriceCompare() throws SQLException {
        // Get the list of StationDetails objects
        comparePriceBox.getChildren().clear();
        comparePriceBox.getChildren().add(new Separator());

        // Clear list that tracks crow-flies distances
        DatabaseOperations.crowFliesList.clear();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        // Set SQL command to match user input against database
        String query = "SELECT * FROM gas_stations";
        try {
            // Execute query on entered username and password
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            // While there is an entry to show, create a label that displays station data
            while (resultSet.next()) {
                Label label = new Label("                       "
                        +"Station: " + resultSet.getString("station_name") +
                        " - Price: " + resultSet.getDouble("price") +
                        " - Address: " + resultSet.getString("station_address") +
                        " - Fuel type: " + resultSet.getString("fuel_type"));
                //  +" - Travel cost: " + station.getTravelCost());
                comparePriceBox.getChildren().add(label);
                comparePriceBox.getChildren().add(new Separator());

                DatabaseOperations.crowFliesList.add(databaseOperations.getCrowFlies(Double.parseDouble(fixedLat), Double.parseDouble(fixedLong), Double.parseDouble(resultSet.getString("station_latitude")), Double.parseDouble(resultSet.getString("station_longitude"))));
                System.out.println(DatabaseOperations.crowFliesList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        databaseOperations.updateCrowFlies();
    }

    @FXML
    public void handleDistanceSearch() throws SQLException {
        // Get the search query from the search bar
        String searchQuery = searchBar.getText();
        int i = 0;
        // Filter the stations list based on the search query
        comparePriceBox.getChildren().clear();
        comparePriceBox.getChildren().add(new Separator());

        // Initialise prepared statement and variable to store query results
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        // Set SQL command to match user input against database
        String query = "SELECT * FROM gas_stations WHERE LOWER(station_name) LIKE ? ORDER BY crow_flies_to_user";
        try {
            // Execute query on entered username and password
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, "%" + searchQuery.toLowerCase() + "%");
            resultSet = preparedStatement.executeQuery();


            while (resultSet.next()) {
                // If statement used to cap amount of results showing distance, useful when calling API
                if (i < 2000) {
                    // Create a label, pulling database data and display ordered by distance
                    Label label = new Label("                       " +
                            "Station: " + resultSet.getString("station_name") +
                            " - Price: " + resultSet.getDouble("price") +
                            " - Address: " + resultSet.getString("station_address") +
                            " - Fuel type: " + resultSet.getString("fuel_type") +
                            " - Distance: " + String.format("%.2f", Double.parseDouble(resultSet.getString("crow_flies_to_user")))  + "kms");
                            /* Functions that deal with API, will come back later
                                                        ^
                                                        |
                                                        |
                             " - Distance: " + distanceMatrix.getDistance(fixedLat, fixedLong, resultSet.getString("station_latitude"), resultSet.getString("station_longitude")));
                              +" - Travel cost: " + station.getTravelCost());
                            */
                    comparePriceBox.getChildren().add(label);
                    comparePriceBox.getChildren().add(new Separator());;
                    i = i + 1;
                }
            else {
                // Create label without distance field
                    Label label = new Label("                       " +
                            "Station: " + resultSet.getString("station_name") +
                            " - Price: " + resultSet.getDouble("price") +
                            " - Address: " + resultSet.getString("station_address") +
                            " - Fuel type: " + resultSet.getString("fuel_type"));
                    comparePriceBox.getChildren().add(label);
                    comparePriceBox.getChildren().add(new Separator());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
