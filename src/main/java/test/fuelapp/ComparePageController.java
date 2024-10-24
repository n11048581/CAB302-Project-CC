package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import test.fuelapp.sample.FuelPriceAPI;

import java.sql.*;
import java.util.*;

public class ComparePageController {
    Connection connection;
    DatabaseOperations databaseOperations = new DatabaseOperations();
    SQLiteLink sqLiteLink = new SQLiteLink();
    FuelPriceAPI fuelPriceAPI = new FuelPriceAPI();

    ArrayList<String> currentSearchResults = new ArrayList<String>();

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
    private String bookmark;

    public int currentPageNumber = 0;

    @FXML
    private Label label_current_user;
    @FXML
    private ImageView image_profile;

    @FXML
    private TextField searchBar;

    @FXML
    private Button searchButton;

    @FXML
    private RadioButton priceRadioButton;
    @FXML
    private RadioButton distanceRadioButton;

    @FXML
    private Label label_search0, label_search1, label_search2, label_search3, label_search4, label_search5, label_search6, label_search7, label_search8, label_search9;
    @FXML
    private HBox hbox_search0, hbox_search1, hbox_search2, hbox_search3, hbox_search4, hbox_search5, hbox_search6, hbox_search7, hbox_search8, hbox_search9;
    @FXML
    private Button button_search0, button_search1, button_search2, button_search3, button_search4, button_search5, button_search6, button_search7, button_search8, button_search9;

    @FXML
    private Button button_next_page;
    @FXML
    private Button button_previous_page;


    @FXML
    private void initialize() throws SQLException {
        // Set username
        label_current_user.setText(LoginController.current_user);

        // Initialise page by loading user details, sorting by distance and initialising page number button
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

    // Load current user's details from database
    public void loadUserDetailsComparePage() {
        IUser user = databaseOperations.getUserDetails(LoginController.current_user);
        if (user != null) {
            userFuelEfficiency = user.getFuelEfficiency();
            userFuelType = String.valueOf(user.getFuelType());
            userLat = String.valueOf(user.getLatitude());
            userLong= String.valueOf(user.getLongitude());
            userMaxTravelDistance = String.valueOf(user.getMaxTravelDistance());
            bookmark = String.valueOf(user.getBookmark());
        }
    }

    /* Update UI with each station's details, called in getStationsData()
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
    */

    // Read radio button and load data accordingly
    public void checkRadioButton() throws SQLException{
        searchBar.setText("");
        if (priceRadioButton.isSelected()) {
            handlePriceCompare("price");
        } else if (distanceRadioButton.isSelected()) {
            handlePriceCompare("crow_flies_to_user");
        }
    }

    // Event that triggers when a radio button is changed
    public void watchRadioButtons(ActionEvent event) throws SQLException{
        checkRadioButton();
    }


    @FXML
    public void handlePriceCompare(String orderByVal) throws SQLException {
        // Reset list and initialise
        currentSearchResults.clear();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        // Set SQL command to match user input against database
        try {
            // If the user wishes to see all fuel types perform an unfiltered search
            if (Objects.equals(userFuelType, "All")) {
                String query = "SELECT * FROM gas_stations WHERE crow_flies_to_user < ? ORDER BY " + orderByVal + " LIMIT 10 OFFSET ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, userMaxTravelDistance);
                preparedStatement.setDouble(2, currentPageNumber * 10);

            }
            // If the user specifies a specific fuel type, perform a filtered search
            else {
                String query = "SELECT * FROM gas_stations WHERE crow_flies_to_user < ? AND fuel_type = ? ORDER BY " + orderByVal + " LIMIT 10 OFFSET ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, userMaxTravelDistance);
                preparedStatement.setString(2, userFuelType);
                preparedStatement.setDouble(3, currentPageNumber * 10);
            }

            // Execute chosen query
            resultSet = preparedStatement.executeQuery();
            resultSet.next();


            // While there is an entry to show, create a label, HBox and button that displays data
            Label[] comparePageLabels =new Label[]{label_search0,label_search1, label_search2, label_search3, label_search4, label_search5, label_search6, label_search7, label_search8, label_search9};
            HBox[] comparePageHBoxes =new HBox[]{hbox_search0, hbox_search1, hbox_search2, hbox_search3, hbox_search4, hbox_search5, hbox_search6, hbox_search7, hbox_search8, hbox_search9};
            Button[] comparePageButtons =new Button[]{button_search0, button_search1, button_search2, button_search3, button_search4, button_search5, button_search6, button_search7, button_search8, button_search9};

            // Create a string array containing the users current bookmarks
            String[] rawBookmarks = bookmark.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
            String[] bookmarkArray = new String[rawBookmarks.length];
            for (int i = 0; i < rawBookmarks.length; i++) {
                bookmarkArray[i] = String.valueOf(rawBookmarks[i]);
            }

            // For each of the 10 results being shown per page
            for (int i = 0; i < 10; i++) {
                try {
                    // Create a label showing the data for the current station
                    comparePageLabels[i].setText(resultSet.getString("station_name") +
                            " : " + resultSet.getString("station_address") +
                            " - Price: " + resultSet.getDouble("price") / 10 +
                            " - Fuel type: " + resultSet.getString("fuel_type") +
                            " - Distance: " + String.format("%.2f", Double.parseDouble(resultSet.getString("crow_flies_to_user"))) + "kms");
                    // Adapt UI to what data is being shown, only displaying containers when there is data available

                    comparePageHBoxes[i].setStyle("-fx-background-color: DAD7CD; -fx-background-radius: 30;");
                    comparePageButtons[i].setTextFill(Color.web("#000000"));
                    comparePageButtons[i].setDisable(false);
                    comparePageButtons[i].setText("♥");

                    // If the current station has been bookmarked already, display a red heart
                    if (Arrays.asList(bookmarkArray).contains(resultSet.getString("id"))){
                        comparePageButtons[i].setTextFill(Color.web("#ff0000"));
                    }

                    // Add the current stations id to a list, used to manage bookmarks
                    currentSearchResults.add(resultSet.getString("id"));

                    // Move to next station
                    resultSet.next();
                // If no more stations to show, ie Null exception
                } catch (NullPointerException e){
                    // Set UI elements, disabling buttons and hiding unneeded elements
                    comparePageHBoxes[i].setStyle("-fx-background-color: A3B18A;");
                    comparePageLabels[i].setText("");
                    comparePageButtons[i].setText("");
                    comparePageButtons[i].setDisable(true);
                    button_next_page.setDisable(true);
                }
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
        // Initialise page numbers, read the search bar and reset query result list
        String searchQuery = searchBar.getText();
        int i = 0;
        currentPageNumber = 0;
        button_previous_page.setDisable(true);
        currentSearchResults.clear();

        // Initialise prepared statement and variable to store query results
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        // Set SQL command to match user input against database
        try {
             // If the user wishes to see all fuel types perform an unfiltered search
            if (Objects.equals(userFuelType, "All")) {
                String query = "SELECT * FROM gas_stations WHERE LOWER(station_name) LIKE ? AND crow_flies_to_user < ? ORDER BY " + orderByVal + " LIMIT 10 OFFSET ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, "%" + searchQuery.toLowerCase() + "%");
                preparedStatement.setString(2, userMaxTravelDistance);
                preparedStatement.setDouble(3, currentPageNumber * 10);

            }
            // If the user specifies a specific fuel type, perform a filtered search
            else {
                String query = "SELECT * FROM gas_stations WHERE LOWER(station_name) LIKE ? AND crow_flies_to_user < ? AND fuel_type = ? ORDER BY " + orderByVal + " LIMIT 10 OFFSET ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, "%" + searchQuery.toLowerCase() + "%");
                preparedStatement.setString(2, userMaxTravelDistance);
                preparedStatement.setString(3, userFuelType);
                preparedStatement.setDouble(4, currentPageNumber * 10);
            }

            // Execute query on entered username and password
            resultSet = preparedStatement.executeQuery();
            resultSet.next();


            // While there is an entry to show, create a label, HBox and button that displays data
            Label[] comparePageLabels =new Label[]{label_search0,label_search1, label_search2, label_search3, label_search4, label_search5, label_search6, label_search7, label_search8, label_search9};
            HBox[] comparePageHBoxes =new HBox[]{hbox_search0, hbox_search1, hbox_search2, hbox_search3, hbox_search4, hbox_search5, hbox_search6, hbox_search7, hbox_search8, hbox_search9};
            Button[] comparePageButtons =new Button[]{button_search0, button_search1, button_search2, button_search3, button_search4, button_search5, button_search6, button_search7, button_search8, button_search9};

            // Create a string array containing the users current bookmarks
            String[] rawBookmarks = bookmark.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
            String[] bookmarkArray = new String[rawBookmarks.length];
            for (int q = 0; q < rawBookmarks.length; q++) {
                bookmarkArray[q] = String.valueOf(rawBookmarks[q]);
            }

            // For each of the 10 results being shown per page
            for (int q = 0; q < 10; q++) {
                try {
                    // Create a label showing the data for the current station
                    comparePageLabels[q].setText(resultSet.getString("station_name") +
                            " : " + resultSet.getString("station_address") +
                            " - Price: " + resultSet.getDouble("price") / 10 +
                            " - Fuel type: " + resultSet.getString("fuel_type") +
                            " - Distance: " + String.format("%.2f", Double.parseDouble(resultSet.getString("crow_flies_to_user"))) + "kms");

                    // Adapt UI to what data is being shown, only displaying containers when there is data avaliable
                    comparePageHBoxes[q].setStyle("-fx-background-color: DAD7CD; -fx-background-radius: 30;");
                    comparePageButtons[q].setTextFill(Color.web("#000000"));
                    comparePageButtons[q].setDisable(false);
                    comparePageButtons[q].setText("♥");
                    // If the current station has been bookmarked already, display a red heart
                    if (Arrays.asList(bookmarkArray).contains(resultSet.getString("id"))){
                        comparePageButtons[q].setTextFill(Color.web("#ff0000"));
                    }

                    // Add the current stations id to a list, used to manage bookmarks
                    currentSearchResults.add(resultSet.getString("id"));

                    // Move to next station
                    resultSet.next();

                // If no more stations
                } catch (NullPointerException e){
                    // Set UI elements, disabling buttons and hiding unneeded elements
                    comparePageHBoxes[q].setStyle("-fx-background-color: A3B18A;");
                    comparePageLabels[q].setText("");
                    comparePageButtons[q].setText("");
                    comparePageButtons[q].setDisable(true);
                    button_next_page.setDisable(true);
                }
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
        // Check which radio button is selected and perform filtered search
        if (priceRadioButton.isSelected()) {
            handleDistanceSearch("price");
        } else if (distanceRadioButton.isSelected()) {
            handleDistanceSearch("crow_flies_to_user");
        }
    }


    public void map(ActionEvent event) {
        // Go back to the landing page
        sqLiteLink.changeScene(event, "LandingPage.fxml", "Map");
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


    public void goToProfile(ActionEvent event) {
        // go to profile page
        sqLiteLink.changeScene(event, "Profile.fxml", "Profile");
    }


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
        button_next_page.setDisable(false);
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


    @FXML
    public void handleBookmark(ActionEvent event) throws SQLException {
        // Initialise variables to index lists and track whether a result if found in list
        int matchFoundCounter = 0;
        boolean matchFound = false;

        // JavaFX buttons that correspond to the "bookmark" buttons
        Button[] comparePageButtons = new Button[]{button_search0, button_search1, button_search2, button_search3, button_search4, button_search5, button_search6, button_search7, button_search8, button_search9};

        // Read from IUser to get the latest bookmarks and convert text to a string array.
        String[] rawBookmarks = bookmark.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
        String[] bookmarkArray = new String[rawBookmarks.length];
        for (int i = 0; i < rawBookmarks.length; i++) {
            bookmarkArray[i] = String.valueOf(rawBookmarks[i]);
        }

        // Get the fx:id of the button that is pressed, performing a regex operation to extract only the number, used to track which button is pressed
        EventTarget targetString = event.getTarget();
        String numberOnly = targetString.toString().replaceAll("[^0-9]", "");
        int numberOnlyInt = Integer.parseInt(numberOnly);

        // For every element in the bookmark array, containing the users current bookmarks, check whether the pressed button is already bookmarked
        for (int bmarkCounter = 0; bmarkCounter < bookmarkArray.length; bmarkCounter++) {
            // If bookmark exists for this station
            if (bookmarkArray[bmarkCounter].contains(currentSearchResults.get(numberOnlyInt))) {
                matchFoundCounter = bmarkCounter;
                matchFound = true;
                break;
            }
        }

        // If a bookmark does exist for this record
        if (matchFound) {
            // Create an arraylist out of the string array
            ArrayList<String> updatedRemovedBookmarks = new ArrayList<String>();
            Collections.addAll(updatedRemovedBookmarks, rawBookmarks);

            // Use the ArrayList to remove the bookmark at the index found by the above for loop
            updatedRemovedBookmarks.remove(matchFoundCounter);

            // Convert ArrayList to a string so that it can be saved to the user table in database
            String bookmarkRemoveListString = String.join(", ", updatedRemovedBookmarks);
            databaseOperations.updateBookmarks(LoginController.current_user, bookmarkRemoveListString);

            // Set colour of heart icon to black
            comparePageButtons[numberOnlyInt].setTextFill(Color.web("#000000"));
        }
        // Else if bookmark does not exist
        else {
            // Create an arraylist out of the string array
            ArrayList<String> updatedNewBookmarks = new ArrayList<String>();
            updatedNewBookmarks.add(Arrays.toString(bookmarkArray).replaceAll("\\[", "").replaceAll("\\]", ""));

            // Add new element to array matching the id of the station the user selected
            updatedNewBookmarks.add(currentSearchResults.get(numberOnlyInt));

            // Convert to string and update user table with new bookmark data
            String bookmarkUpdateListString = String.join(", ", updatedNewBookmarks);
            databaseOperations.updateBookmarks(LoginController.current_user, bookmarkUpdateListString);

            // Set colour of heart icon to red
            comparePageButtons[numberOnlyInt].setTextFill(Color.web("#ff0000"));
        }
        // Ensure latest user data is fetched
        loadUserDetailsComparePage();
    }
}
