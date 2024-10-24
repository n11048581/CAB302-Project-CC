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

    @FXML
    public void initialize() throws SQLException {
        label_current_user.setText(LoginController.current_user);

        loadUserDetails();
        loadStationLocation();

        MapView mapView = Map.createMapView(userLat, userLong);
        gluonMap.getChildren().add(mapView);

        //Thread thread = getThread(mapView);
        //thread.start();

    }

//    private Thread getThread(MapView mapView) {
//        Task<Void> task = new Task<Void>() {     // Background thread to fetch API data progressively
//            @Override
//            protected Void call() throws Exception {
//                // userLat and userLong pulled from Users db table, assigned in Settings
//                fuelPriceAPI.getStationsData(String.valueOf(userLat), String.valueOf(userLong), station -> {
//                    Platform.runLater(() -> Map.updateStationLayer(station));
//                });
//                return null;
//            }
//        };
//
//        task.setOnFailed(e -> {
//            System.err.println("Failed to fetch station data: " + task.getException().getMessage());
//        });
//
//        // Run task in separate thread
//        Thread thread = new Thread(task);
//        thread.setDaemon(true);
//        return thread;
//    }


    @FXML
    public void handlePriceCompare(ActionEvent event) {
        sqLiteLink.changeScene(event, "PricePage.fxml", "Compare Page");
    }

    @FXML
    public void handleCalculator(ActionEvent event) {
        showAlert("Calculator button clicked", "This would take you to the fuel efficiency calculator.");
    }

    public void handleProfile(ActionEvent event) {
        sqLiteLink.changeScene(event, "Profile.fxml", "Profile");
    }

    public void LogOut (ActionEvent event) {
        // Redirect to log in page
        sqLiteLink.changeScene(event, "LogInPage.fxml", "Log In");
    }

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
