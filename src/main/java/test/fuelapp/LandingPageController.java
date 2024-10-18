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
import test.fuelapp.sample.StationDetails;

public class LandingPageController {
    SQLiteLink sqLiteLink = new SQLiteLink();

    @FXML
    public void handleLogin(ActionEvent event) {
        showAlert("Login button clicked", "This would take you to the login page.");
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        showAlert("Register button clicked", "This would take you to the registration page.");
    }

    @FXML
    private VBox gluonMap;

    private double userLat;
    private double userLong;

    private FuelPriceAPI fuelPriceAPI = new FuelPriceAPI();
    private DatabaseOperations dbOperations = new DatabaseOperations();

    @FXML
    public void initialize() {
        IUser user = dbOperations.getUserDetails(LoginController.current_user);

        if (user != null) {
            // Get lat and long from the user object
            userLat = user.getLatitude();
            userLong = user.getLongitude();
            System.out.println("Received Coordinates: " + userLat + ", " + userLong); // Debugging statement
            //??? why is it 39 153 now why did it change
        } else {
            // Handle the case where user details are not available
            System.err.println("User details not found.");

            // Fallback values
            userLat = -27.823611;
            userLong = 153.182556;
        }

        MapView mapView = Map.createMapView(userLat, userLong);
        gluonMap.getChildren().add(mapView);
        VBox.setVgrow(mapView, Priority.ALWAYS);

        Thread thread = getThread(mapView);
        thread.start();

    }

    private Thread getThread(MapView mapView) {
        Task<Void> task = new Task<Void>() {     // Background thread to fetch API data progressively
            @Override
            protected Void call() throws Exception {
                // userLat and userLong pulled from Users db table, assigned in Settings
                fuelPriceAPI.getStationsData(String.valueOf(userLat), String.valueOf(userLong), station -> {
                    Platform.runLater(() -> Map.updateStationLayer(station));
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
        return thread;
    }


    //@FXML
    //public void handleSearch(ActionEvent event) {
    //    showAlert("Search button clicked", "This would take you to the search page.");
    //}

    @FXML
    public void handlePriceCompare(ActionEvent event) {
        sqLiteLink.changeScene(event, "PricePage.fxml", "Price Compare");
    }

    @FXML
    public void handleCalculator(ActionEvent event) {
        sqLiteLink.changeScene(event,  "EfficiencyCalculator.fxml", "Fuel Efficiency Calculator");
    }

    @FXML
    public void handleMap(ActionEvent event) {
        showAlert("Map button clicked", "This would take you to the map view.");
    }

    @FXML
    public void handleSettings(ActionEvent event) {
        sqLiteLink.changeScene(event, "Settings.fxml", "Settetesttings");
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
}
