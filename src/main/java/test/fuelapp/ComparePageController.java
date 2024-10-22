package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import test.fuelapp.sample.FuelPriceAPI;
import test.fuelapp.sample.FuelPriceAPI.StationDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ComparePageController {
    Connection connection;
    DatabaseOperations databaseOperations = new DatabaseOperations();
    SQLiteLink sqLiteLink = new SQLiteLink();
    FuelPriceAPI fuelPriceAPI = new FuelPriceAPI();

    public ComparePageController() {
        connection = SQLiteLink.Connector();
        if (connection == null) {
            System.out.println("Database Connection Unsuccessful");
            System.exit(1);
        }
    }

    private double userFuelEfficiency;
    private String userFuelType;
    private String userLat;
    private String userLong;
    private String userMaxTravelDistance;

    public int currentPageNumber = 0;

    private boolean distanceSelected = true;

    @FXML
    private TextField searchBar;

    @FXML
    private Button searchButton;

    @FXML
    private VBox comparePriceBox;
    @FXML
    private ToggleGroup Compare;

    @FXML
    private RadioButton priceRadioButton;
    @FXML
    private RadioButton distanceRadioButton;

    @FXML
    private Label label_nothing_to_show;

    @FXML
    private Label label_search0, label_search1, label_search2, label_search3, label_search4, label_search5, label_search6, label_search7, label_search8, label_search9;
    @FXML
    private HBox hbox_search0, hbox_search1, hbox_search2, hbox_search3, hbox_search4, hbox_search5, hbox_search6, hbox_search7, hbox_search8, hbox_search9;
    @FXML
    private Button button_next_page;
    @FXML
    private Button button_previous_page;


    @FXML
    private void initialize() throws SQLException {
        button_previous_page.setDisable(true);
        loadUserDetailsComparePage();
        handlePriceCompare("crow_flies_to_user");

        /*
        String userLong;
        String userLat;
        if (user != null) {
            // Get lat and long from the user object
            userLat = String.valueOf(user.getLatitude());
            userLong = String.valueOf(user.getLongitude());
            System.out.println("Received Coordinates: " + userLat + ", " + userLong); // Debugging statement
        } else {
            // Handle the case where user details are not available
            System.err.println("User details not found.");

            // Fallback values
            userLat = "-27.823611";
            userLong = "153.182556";
            userFuelEfficiency = 15.0;
        }
*/



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

    public void loadUserDetailsComparePage() {
        IUser user = databaseOperations.getUserDetails(LoginController.current_user);
        if (user != null) {
            userFuelEfficiency = user.getFuelEfficiency();
            userFuelType = String.valueOf(user.getFuelType());
            userLat = String.valueOf(user.getLatitude());
            userLong= String.valueOf(user.getLongitude());
            userMaxTravelDistance = String.valueOf(user.getMaxTravelDistance());
        }
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

    public void checkRadioButton() throws SQLException{
        searchBar.setText("");
        if (priceRadioButton.isSelected()) {
            System.out.println("Price Selected");
            handlePriceCompare("price");
        } else if (distanceRadioButton.isSelected()) {
            System.out.println("Distance Selected");
            handlePriceCompare("crow_flies_to_user");
        }
    }
    public void watchRadioButtons(ActionEvent event) throws SQLException{
        checkRadioButton();
    }



    @FXML
    public void handlePriceCompare(String orderByVal) throws SQLException {
        // Get the list of StationDetails objects
        int i = 0;
        String currentLabel;
        String label_search;

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        // Set SQL command to match user input against database
        String query = "SELECT * FROM gas_stations WHERE crow_flies_to_user < ? ORDER BY " + orderByVal + " LIMIT 10 OFFSET ?";
        try {
            // Execute query on entered username and password
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userMaxTravelDistance);
            preparedStatement.setDouble(2, currentPageNumber * 10);

            resultSet = preparedStatement.executeQuery();

            label_nothing_to_show.setVisible(!resultSet.next());


            // While there is an entry to show, create a label that displays station data
            Label[] comparePageLabels =new Label[]{label_search0,label_search1, label_search2, label_search3, label_search4, label_search5, label_search6, label_search7, label_search8, label_search9};
            HBox[] comparePageHBoxes =new HBox[]{hbox_search0, hbox_search1, hbox_search2, hbox_search3, hbox_search4, hbox_search5, hbox_search6, hbox_search7, hbox_search8, hbox_search9};
            while (i < 10) {
                try {
                    comparePageHBoxes[i].setStyle("-fx-background-color: DAD7CD; -fx-background-radius: 30;");
                    comparePageLabels[i].setText(resultSet.getString("station_name") +
                            " : " + resultSet.getString("station_address") +
                            " - Price: " + resultSet.getDouble("price") / 10 +
                            " - Fuel type: " + resultSet.getString("fuel_type") +
                            " - Distance: " + String.format("%.2f", Double.parseDouble(resultSet.getString("crow_flies_to_user"))) + "kms");
                } catch (NullPointerException e){
                    comparePageHBoxes[i].setStyle("-fx-background-color: A3B18A;");
                    comparePageLabels[i].setText("");
                    button_next_page.setDisable(true);
                }
                i = i + 1;
                resultSet.next();
            }
            // Close statements to prevent database freezing up
            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDistanceSearch(String orderByVal) throws SQLException {
        // Get the search query from the search bar
        String searchQuery = searchBar.getText();
        int i = 0;
        // Initialise prepared statement and variable to store query results
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;


        // Set SQL command to match user input against database
        String query = "SELECT * FROM gas_stations WHERE LOWER(station_name) LIKE ? AND crow_flies_to_user < ? ORDER BY " + orderByVal + " LIMIT 10 OFFSET ?";
        try {
            // Execute query on entered username and password
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, "%" + searchQuery.toLowerCase() + "%");
            preparedStatement.setString(2, userMaxTravelDistance);
            preparedStatement.setDouble(3, currentPageNumber * 10);

            resultSet = preparedStatement.executeQuery();

            label_nothing_to_show.setVisible(!resultSet.next());

            // While there is an entry to show, create a label that displays station data
            Label[] comparePageLabels =new Label[]{label_search0,label_search1, label_search2, label_search3, label_search4, label_search5, label_search6, label_search7, label_search8, label_search9};
            HBox[] comparePageHBoxes =new HBox[]{hbox_search0, hbox_search1, hbox_search2, hbox_search3, hbox_search4, hbox_search5, hbox_search6, hbox_search7, hbox_search8, hbox_search9};

            while (i < 10) {
                try {
                    comparePageHBoxes[i].setStyle("-fx-background-color: DAD7CD; -fx-background-radius: 30;");
                    comparePageLabels[i].setText(resultSet.getString("station_name") +
                            " : " + resultSet.getString("station_address") +
                            " - Price: " + resultSet.getDouble("price") / 10 +
                            " - Fuel type: " + resultSet.getString("fuel_type") +
                            " - Distance: " + String.format("%.2f", Double.parseDouble(resultSet.getString("crow_flies_to_user"))) + "kms");
                } catch (NullPointerException e){
                    comparePageHBoxes[i].setStyle("-fx-background-color: A3B18A;");
                    comparePageLabels[i].setText("");
                    button_next_page.setDisable(true);
                }
                i = i + 1;
                resultSet.next();
            }
            // Close statements to prevent database freezing up
            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleFilter() throws SQLException{
        if (priceRadioButton.isSelected()) {
            System.out.println("Price Selected");
            handleDistanceSearch("price");
        } else if (distanceRadioButton.isSelected()) {
            System.out.println("Distance Selected");
            handleDistanceSearch("crow_flies_to_user");
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
    public void handleProfile(ActionEvent event) {
        // For now, just display an alert
        sqLiteLink.changeScene(event, "Profile.fxml", "Profile");

    }

    @FXML
    public void LogOut(ActionEvent event) {
        // Logout and go back to the login page
        sqLiteLink.changeScene(event, "LogInPage.fxml", "Log In");
    }

    @FXML
    public void PageForward(ActionEvent event) throws SQLException {
        // Logout and go back to the login page
        currentPageNumber = currentPageNumber + 1;
        button_previous_page.setDisable(false);
        checkRadioButton();

    }

    @FXML
    public void PageBack(ActionEvent event) throws SQLException {
        // Logout and go back to the login page
        currentPageNumber = currentPageNumber - 1;
        if (currentPageNumber == 0) {
            button_previous_page.setDisable(true);
        }
        checkRadioButton();
    }


   @FXML
    public void onEnter(ActionEvent event) throws SQLException {
       if (priceRadioButton.isSelected()) {
           System.out.println("Price Selected");
           handleDistanceSearch("price");
       } else if (distanceRadioButton.isSelected()) {
           System.out.println("Distance Selected");
           handleDistanceSearch("crow_flies_to_user");
       }
    }
}
