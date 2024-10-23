package test.fuelapp;

import com.gluonhq.maps.MapView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import test.fuelapp.sample.FuelPriceAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class LandingPageController {
    Connection connection;
    DatabaseOperations databaseOperations = new DatabaseOperations();
    SQLiteLink sqLiteLink = new SQLiteLink();
    FuelPriceAPI fuelPriceAPI = new FuelPriceAPI();

    ArrayList<String> currentSearchResults = new ArrayList<String>();

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
    private String bookmark;


    private DatabaseOperations dbOperations = new DatabaseOperations();

    @FXML
    public void initialize() throws SQLException {

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
            //bookmark = String.valueOf(user.getBookmark());
        }
    }

    /**
     * Resets the map if it already exists, then retrieves a set of all gas stations less than userMaxDistance.
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
            //Select all stations with a distance less than the user's max travel distance
            String query = "SELECT * FROM gas_stations WHERE crow_flies_to_user < ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userMaxTravelDistance);
            resultSet = preparedStatement.executeQuery();


            //While there are still results in the set, update the map with the stations
            while (resultSet.next()) {
                Map.updateStationLayerDB(resultSet.getString("station_latitude"),resultSet.getString("station_longitude"),resultSet.getString("station_name"));
            }

            resultSet.close();
        } catch (Exception e) {
        e.printStackTrace();
        }
    }
}
