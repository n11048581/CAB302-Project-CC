package test.fuelapp;

import javafx.application.Platform;
import javafx.concurrent.Task;
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

public class ComparePageController {
    Connection connection;
    DistanceMatrix distanceMatrix = new DistanceMatrix();
    DatabaseOperations databaseOperations = new DatabaseOperations();

    public ComparePageController() {
        connection = SQLiteLink.Connector();
        if (connection == null) {
            System.out.println("Database Connection Unsuccessful");
            System.exit(1);
        }
    }

    private double userFuelEfficiency;

    private String userLat;
    private String userLong;

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

    private FuelPriceAPI fuelPriceAPI = new FuelPriceAPI();


    @FXML
    private void initialize() throws SQLException {
        handlePriceCompare();
        IUser user = databaseOperations.getUserDetails(LoginController.current_user);

        if (user != null) {
            // Get lat and long from the user object
            userLat = String.valueOf(user.getLatitude());
            userLong = String.valueOf(user.getLongitude());
            userFuelEfficiency = Double.parseDouble(String.valueOf(user.getFuelEfficiency()));
            System.out.println("Received Coordinates: " + userLat + ", " + userLong); // Debugging statement
        } else {
            // Handle the case where user details are not available
            System.err.println("User details not found.");

            // Fallback values
            userLat = "-27.823611";
            userLong = "153.182556";
            userFuelEfficiency = 15.0;
        }

        /*
        Task<Void> task = new Task<Void>() {     // Background thread to fetch API data progressively
            @Override
            protected Void call() throws Exception {
                // userLat and userLong pulled from Users db table, assigned in Settings
                fuelPriceAPI.getStationsData(userLat, userLong, station -> {
                    Platform.runLater(() -> updateUIWithStation(station));
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
*/
    }

    // Update UI with each station's details, called in getStationsData()
    private void updateUIWithStation(StationDetails station) {

        double distance = StationCalculations.calculateDistance(station.getDistance());
        double travelCost = StationCalculations.calculateTravelCost(station, userFuelEfficiency, distance);

        Label label = new Label("Station: " + station.getName() +
                " - Price: " + station.getPrice() +
                " - Address: " + station.getAddress() +
                " - Distance: " + distance +
                " - Travel Cost: $" + travelCost +
                " - Fuel Type: " + station.getFuelType());
        comparePriceBox.getChildren().add(label);
        comparePriceBox.getChildren().add(new Separator());
    }


    @FXML
    public void handlePriceCompare() throws SQLException {
        // Get the list of StationDetails objects
        comparePriceBox.getChildren().clear();
        comparePriceBox.getChildren().add(new Separator());

        // Clear list that tracks crow-flies distances

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        // Set SQL command to match user input against database
        String query = "SELECT * FROM gas_stations ORDER BY crow_flies_to_user";
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
                        " - Fuel type: " + resultSet.getString("fuel_type") +
                        " - Distance: " + String.format("%.2f", Double.parseDouble(resultSet.getString("crow_flies_to_user")))  + "kms");
                //  +" - Travel cost: " + station.getTravelCost());
                comparePriceBox.getChildren().add(label);
                comparePriceBox.getChildren().add(new Separator());

                //DatabaseOperations.crowFliesList.add(databaseOperations.getCrowFlies(Double.parseDouble(userLat), Double.parseDouble(userLong), Double.parseDouble(resultSet.getString("station_latitude")), Double.parseDouble(resultSet.getString("station_longitude"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void handleSettings(ActionEvent event) {
        // For now, just display an alert
        sqLiteLink.changeScene(event, "Settings.fxml", "Settetesttings");

    }

    @FXML
    public void LogOut(ActionEvent event) {
        // Logout and go back to the login page
        sqLiteLink.changeScene(event, "LogInPage.fxml", "Log In");
    }

    @FXML
    public void onEnter(ActionEvent event) throws SQLException {
        handleDistanceSearch();
    }
}
