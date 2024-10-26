package test.fuelapp;

import com.gluonhq.maps.MapView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The controller class for the Landing Page view of the application.
 */
public class LandingPageController {
    Connection connection;
    DatabaseOperations databaseOperations = new DatabaseOperations();
    SQLiteLink sqLiteLink = new SQLiteLink();

    ArrayList<String> currentSearchResults = new ArrayList<String>();

    /**
     * Starts SQL connection.
     */
    public LandingPageController() {
        connection = SQLiteLink.Connector();
        if (connection == null) {
            System.out.println("Database Connection Unsuccessful");
            System.exit(1);
        }
    }

    @FXML
    private VBox gluonMap;

    private double userLat;
    private double userLong;
    private String userMaxTravelDistance;
    private String userFuelType;
    private String bookmark;

    @FXML
    private Label label_current_user;

    /**
     * Initialise landing page with correct UI elements, including the map, and load most recent user data
     * @throws SQLException If database cannot be accessed
     */
    @FXML
    public void initialize() throws SQLException {
        label_current_user.setText(LoginController.current_user);

        loadUserDetails();
        loadStationLocation();

        MapView mapView = Map.createMapView(userLat, userLong);
        gluonMap.getChildren().add(mapView);
    }


    /**
     * Redirect to price compare page
     * @param event Triggers when compare page menu button is pressed
     */
    @FXML
    public void handlePriceCompare(ActionEvent event) {
        sqLiteLink.changeScene(event, "PricePage.fxml", "Compare Page");
    }


    /**
     * Redirect to David's fuel efficiency calculator
     */
    @FXML
    public void handleCalculator(ActionEvent event) {
        showAlert("Calculator button clicked", "This would take you to the fuel efficiency calculator.");
    }


    /**
     * Redirect to profile page
     * @param event Triggers when profile menu button is pressed, or username is pressed
     */
    public void handleProfile(ActionEvent event) {
        sqLiteLink.changeScene(event, "Profile.fxml", "Profile");
    }


    /**
     * Log user out and redirect to login page
     * @param event Triggers when logout button is pressed
     */
    public void LogOut (ActionEvent event) {
        sqLiteLink.changeScene(event, "LogInPage.fxml", "Log In");
    }


    /**
     * Method to display an alert for the user
     * @param title A string designating the alert title
     * @param message A string designating the alert message
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Retrieves user details from the database.
     * Does nothing if a user is not found.
     */
    public void loadUserDetails() {
        IUser user = databaseOperations.getUserDetails(LoginController.current_user);
        if (user != null) {
            userLat = user.getLatitude();
            userLong= user.getLongitude();
            userMaxTravelDistance = String.valueOf(user.getMaxTravelDistance());
            userFuelType = String.valueOf(user.getFuelType());
            //bookmark = String.valueOf(user.getBookmark());
        }
    }

    /**
     * Resets the map if it already exists, then retrieves a set of all gas stations less than userMaxDistance.
     * If a specific fuel type is set, also retrieve fuel prices.
     * Automatically updates map with the retrieved information.
     * @throws SQLException
     */
    public void loadStationLocation() throws SQLException {
        // Reset map, list and initialise
        Map.resetStationLayer();
        currentSearchResults.clear();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            // If the user wishes to see all fuel types perform an unfiltered search
            if (Objects.equals(userFuelType, "All")) {
                System.out.println("All fuel types");
                String query = "SELECT * FROM gas_stations WHERE crow_flies_to_user < ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, userMaxTravelDistance);

                resultSet = preparedStatement.executeQuery();
                resultSet.next();

                while (resultSet.next()) {

                    String station_latitude = resultSet.getString("station_latitude");
                    String station_longitude = resultSet.getString("station_longitude");
                    String station_name = resultSet.getString("station_name");
                    double station_distance = resultSet.getDouble("crow_flies_to_user");
                    Map.updateStationLayerDB(station_latitude, station_longitude, station_name, station_distance);
                }

            }
            // If the user specifies a specific fuel type, perform a filtered search
            else {
                System.out.println("custom fuel type");
                String query = "SELECT * FROM gas_stations WHERE crow_flies_to_user < ? AND fuel_type = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, userMaxTravelDistance);
                preparedStatement.setString(2, userFuelType);

                resultSet = preparedStatement.executeQuery();
                resultSet.next();

                while (resultSet.next()) {
                    String station_latitude = resultSet.getString("station_latitude");
                    String station_longitude = resultSet.getString("station_longitude");
                    String station_name = resultSet.getString("station_name");
                    double station_distance = resultSet.getDouble("crow_flies_to_user");
                    double station_price = resultSet.getDouble("price") / 10;
                    Map.updateStationLayerDB(station_latitude, station_longitude, station_name, station_distance,station_price);
                }
            }
            //Select all stations with a distance less than the user's max travel distance

            resultSet.close();
        } catch (Exception e) {
        e.printStackTrace();
        }
    }
}
