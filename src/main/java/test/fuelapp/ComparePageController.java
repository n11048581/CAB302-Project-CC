package test.fuelapp;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import test.fuelapp.API.FuelPriceAPI;

import java.sql.*;
import java.util.*;

/**
 * Class to handle the back end of the compare page
 */
public class ComparePageController {
    Connection connection;
    DatabaseOperations databaseOperations = new DatabaseOperations();
    SQLiteLink sqLiteLink = new SQLiteLink();
    FuelPriceAPI fuelPriceAPI = new FuelPriceAPI();

    ArrayList<String> currentSearchResults = new ArrayList<String>();

    /**
     * Exits app if database connection cannot be made
     */
    public ComparePageController() {
        connection = SQLiteLink.Connector();
        if (connection == null) {
            System.out.println("Database Connection Unsuccessful");
            System.exit(1);
        }
    }

    public int currentPageNumber = 0;

    private double userFuelEfficiency;
    private String userFuelType;
    private String userLat;
    private String userLong;
    private String userMaxTravelDistance;
    private String bookmark;


    @FXML
    private Label label_current_user;
    @FXML
    private Label label_loading_update;
    @FXML
    private TextField searchBar;


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

    /**
     * Initialise page by calling method to load user data and setting fxml elements
     * @throws SQLException If database cannot be accessed
     */
    @FXML
    private void initialize() throws SQLException {
        // Set username
        label_current_user.setText(LoginController.current_user);
        label_loading_update.setVisible(false);
        button_previous_page.setDisable(true);

        loadUserDetailsComparePage();
        handlePriceCompare("crow_flies_to_user");
    }

    /**
     * From user interface, load the current users data
     */
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


    /**
     * Read which radio button is selected and load the corresponding data
     * @throws SQLException If database cannot be accessed
     */
    public void checkRadioButton() throws SQLException{
        searchBar.setText("");
        if (priceRadioButton.isSelected()) {
            handlePriceCompare("price");
        } else if (distanceRadioButton.isSelected()) {
            handlePriceCompare("crow_flies_to_user");
        }
    }


    /**
     * Activate when either radio button is pressed
     * @param event A radiobutton's status is updated
     * @throws SQLException If database can't be accessed
     */
    public void watchRadioButtons(ActionEvent event) throws SQLException{
        checkRadioButton();
    }


    /**
     * The default system to order and display station data from the database
     * @param orderByVal Based on radiobuttons, the manner in which the user wants results ordered
     * @throws SQLException If the database cannot be accessed
     */
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
                    // Calculate travel cost
                    double fuelRequired = (Double.parseDouble(resultSet.getString("crow_flies_to_user")) / 100) * userFuelEfficiency;
                    double pricePerLiter = resultSet.getDouble("price") / 1000.0;
                    double travelCost = fuelRequired * pricePerLiter;
                    double roundedTravelCost = Double.parseDouble(String.format("%.2f", travelCost));

                    // Create a label showing the data for the current station
                    comparePageLabels[i].setText(resultSet.getString("station_name") +
                            " : " + resultSet.getString("station_address") +
                            " - Price: " + resultSet.getDouble("price") / 10 +
                            " - Fuel type: " + resultSet.getString("fuel_type") +
                            " - Travel Cost: $" + roundedTravelCost +
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


    /**
     * The method to order and display station data, taking into account the users search preference
     * @param orderByVal Based on radiobuttons, the manner in which the user wants results ordered
     * @throws SQLException If the database cannot be accessed
     */
    @FXML
    public void handleDistanceSearch(String orderByVal) throws SQLException {
        String searchQuery = searchBar.getText();
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

                    // Calculate travel cost
                    double fuelRequired = (Double.parseDouble(resultSet.getString("crow_flies_to_user")) / 100) * userFuelEfficiency;
                    double pricePerLiter = resultSet.getDouble("price") / 1000.0;
                    double travelCost = fuelRequired * pricePerLiter;
                    double roundedTravelCost = Double.parseDouble(String.format("%.2f", travelCost));

                    // Create a label showing the data for the current station
                    comparePageLabels[q].setText(resultSet.getString("station_name") +
                            " : " + resultSet.getString("station_address") +
                            " - Price: " + resultSet.getDouble("price") / 10 +
                            " - Fuel type: " + resultSet.getString("fuel_type") +
                            " - Travel Cost: $" + roundedTravelCost +
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


    /**
     * Tied to an action event that triggers when the user searches for a station, reads the radio button and sorts
     * @throws SQLException If the database cannot be accessed
     */
    @FXML
    public void handleFilter() throws SQLException{
        if (priceRadioButton.isSelected()) {
            handleDistanceSearch("price");
        } else if (distanceRadioButton.isSelected()) {
            handleDistanceSearch("crow_flies_to_user");
        }
    }


    /**
     * Redirect to map screen
     * @param event Triggers when map menu button is pressed
     */
    public void map(ActionEvent event) {
        sqLiteLink.changeScene(event, "LandingPage.fxml", "Map");
    }


    /**
     *  Redirect to David's fuel efficiency calculator
     */
    @FXML
    public void handleCalculator() {
        // For now, just display an alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Calculator");
        alert.setHeaderText(null);
        alert.setContentText("Calculator button clicked");
        alert.showAndWait();
    }

    /**
     * Redirect to profile page
     * @param event Triggers when profile menu button, or profile username/image is pressed
     */
    public void goToProfile(ActionEvent event) {
        sqLiteLink.changeScene(event, "Profile.fxml", "Profile");
    }


    /**
     * Log user out and redirect to login page
     * @param event Triggers when the logout menu button is pressed
     */
    public void LogOut(ActionEvent event) {
        sqLiteLink.changeScene(event, "LogInPage.fxml", "Log In");
    }


    /**
     * Advance the page counter and display the next 10 results from the user's query
     * @throws SQLException If the database cannot be accessed
     */
    public void PageForward() throws SQLException {
        // Move to next 10 results and allow user to go back a page
        currentPageNumber = currentPageNumber + 1;
        button_previous_page.setDisable(false);
        checkRadioButton();
    }


    /**
     * Reduce the page counter and display the previous 10 results from the user's query
     * @throws SQLException If the database cannot be accessed
     */
    public void PageBack() throws SQLException {
        // Logout and go back to the login page
        currentPageNumber = currentPageNumber - 1;
        if (currentPageNumber == 0) {
            button_previous_page.setDisable(true);
        }
        button_next_page.setDisable(false);
        checkRadioButton();

    }


    /**
     * Wait for user to press enter button and perform a search when they do
     * @throws SQLException If the database cannot be accessed
     */
    public void onEnter() throws SQLException {
       if (priceRadioButton.isSelected()) {
           handleDistanceSearch("price");
       } else if (distanceRadioButton.isSelected()) {
           handleDistanceSearch("crow_flies_to_user");
       }
    }


    /**
     * Save or delete bookmark from the user table in the database
     * @param event The fx-id of the object that triggered the event
     * @throws SQLException If the database cannot be accessed
     */
    @FXML
    public void handleBookmark(ActionEvent event) throws SQLException {
        int matchFoundCounter = 0;
        boolean matchFound = false;

        // JavaFX buttons that correspond to the "bookmark" buttons
        Button[] comparePageButtons = new Button[]{button_search0, button_search1, button_search2, button_search3, button_search4, button_search5, button_search6, button_search7, button_search8, button_search9};

        // Read from IUser to get the latest bookmarks
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
            if (bookmarkArray[bmarkCounter].contains(currentSearchResults.get(numberOnlyInt))) {
                matchFoundCounter = bmarkCounter;
                matchFound = true;
                break;
            }
        }

        // If a bookmark does exist for this record, remove the bookmark at the index found by the above for loop
        if (matchFound) {
            ArrayList<String> updatedRemovedBookmarks = new ArrayList<String>();
            Collections.addAll(updatedRemovedBookmarks, rawBookmarks);

            updatedRemovedBookmarks.remove(matchFoundCounter);

            String bookmarkRemoveListString = String.join(", ", updatedRemovedBookmarks);
            databaseOperations.updateBookmarks(LoginController.current_user, bookmarkRemoveListString);

            // Set colour of heart icon to black
            comparePageButtons[numberOnlyInt].setTextFill(Color.web("#000000"));
        }
        // Else if bookmark does not exist, add new element to array matching the id of the station the user selected
        else {
            ArrayList<String> updatedNewBookmarks = new ArrayList<String>();
            updatedNewBookmarks.add(Arrays.toString(bookmarkArray).replaceAll("\\[", "").replaceAll("\\]", ""));

            updatedNewBookmarks.add(currentSearchResults.get(numberOnlyInt));

            String bookmarkUpdateListString = String.join(", ", updatedNewBookmarks);
            databaseOperations.updateBookmarks(LoginController.current_user, bookmarkUpdateListString);

            // Set colour of heart icon to red
            comparePageButtons[numberOnlyInt].setTextFill(Color.web("#ff0000"));
        }
        // Ensure latest user data is fetched
        loadUserDetailsComparePage();
    }


    /**
     * Call a thread to update database with new data from API
     * @throws SQLException
     */
    public void handleAPIUpdate() throws SQLException{
        label_loading_update.setVisible(true);
        executeTaskInSeparateThread(updateAPIListener);
    }


    /**
     * Interface to support thread finish tracking
     */
    public interface UpdateAPIListener{
        public void threadAPIFinished();
    }


    /**
     * Run API update in thread
     * @param updateAPIListener Take the declared api update interface
     */
    public void executeTaskInSeparateThread(final UpdateAPIListener updateAPIListener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Update database with the newest API data
                fuelPriceAPI.updateNewStationsData();
                //Notify the listener when thread is finished
                updateAPIListener.threadAPIFinished();
            }
        }).start();
    }


    /**
     * Reload page when thread is finished
     */
    UpdateAPIListener updateAPIListener = new UpdateAPIListener() {
        @Override
        public void threadAPIFinished() {
            // Allow UI to update and will return updated data when finished
            Platform.runLater(
                    () -> {
                        try {
                            initialize();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }
    };
}
