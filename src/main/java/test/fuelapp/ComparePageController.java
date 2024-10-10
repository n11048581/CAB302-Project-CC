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
    DistanceMatrix distanceMatrix = new DistanceMatrix();
    DatabaseOperations databaseOperations = new DatabaseOperations();

    String fixedLat = "-27.823611";
    String fixedLong = "153.182556";

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

        DatabaseOperations.crowFliesList.clear();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        // Set SQL command to match user input against database
        String query = "SELECT * FROM gas_stations";
        try {
            // Execute query on entered username and password
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

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
        } finally {
            // Close prepared statement
            //prepStatement.close();
            //resultSet.close();
        }
        databaseOperations.updateCrowFlies();
    }

    @FXML
    public void handleSearch() throws SQLException {
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
                if (i < 10) {
                    Label label = new Label("                       " +
                            "Station: " + resultSet.getString("station_name") +
                            " - Price: " + resultSet.getDouble("price") +
                            " - Address: " + resultSet.getString("station_address") +
                            " - Fuel type: " + resultSet.getString("fuel_type") +
                            " - Distance: " + distanceMatrix.getDistance(fixedLat, fixedLong, resultSet.getString("station_latitude"), resultSet.getString("station_longitude")));
                    //  +" - Travel cost: " + station.getTravelCost());
                    comparePriceBox.getChildren().add(label);
                    comparePriceBox.getChildren().add(new Separator());;
                    i = i + 1;
                }
            else {
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
        } finally {
            // Close prepared statement
            // prepStatement.close();
            // resultSet.close();
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
