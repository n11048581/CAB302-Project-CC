package test.fuelapp;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import test.fuelapp.sample.FuelPriceAPI;
import test.fuelapp.sample.StationDetails;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import static test.fuelapp.sample.SampleData.getSampleData;

public class Map {

    private FuelPriceAPI fuelPriceAPI = new FuelPriceAPI();

    private static MapMarker stationMarkers = new MapMarker();

    private static final String API_KEY = "AIzaSyA4Eeiogc9Sqx4-w3tOPkQeEN16JAYl9Vk";

    // Class variables to store user's latitude and longitude
    private static double userLatitude;
    private static double userLongitude;

    /**
     * Creates a map view
     * @return a map of the world
     */
    public static MapView createMapView(User user) {
        MapView mapView = new MapView();
        // These values should be adjustable later, such as for screen size and user preferences
        mapView.setPrefSize(1600, 1200);
        mapView.setZoom(10);

        // Use default latitude and longitude in case user location is not available
        double defaultLatitude = -27.470125;
        double defaultLongitude = 153.021072;

        // Update user location from Google API and store it in the User object
        updateUserLocation(user);

        // Check if user location was successfully updated, otherwise use default values
        double userLatitude = user.getLatitude() != 0.0 ? user.getLatitude() : defaultLatitude;
        double userLongitude = user.getLongitude() != 0.0 ? user.getLongitude() : defaultLongitude;

        // Set the map center to the user's location
        mapView.setCenter(userLatitude, userLongitude); // Move map center to user's location
        mapView.addLayer(stationMarkers);  // Add station markers layer
        mapView.addLayer(createCenterLayer(userLatitude, userLongitude));  // Add a marker for the user's location

        return mapView;
    }


    /**
     * Retrieves the user's location using Google Geolocation API and stores it in User object
     * @param user The User object to update with the new location
     */
    public static void updateUserLocation(User user) {
        try {
            // Google Geolocation API URL
            String apiUrl = "https://www.googleapis.com/geolocation/v1/geolocate?key=" + API_KEY;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // Send empty body as we're not providing any additional data (optional)
            String jsonInputString = "{}";
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // Parse JSON response
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder inline = new StringBuilder();
                while (scanner.hasNext()) {
                    inline.append(scanner.nextLine());
                }
                scanner.close();

                JsonObject data = JsonParser.parseString(inline.toString()).getAsJsonObject();

                // Extract latitude and longitude from the API response
                double latitude = data.getAsJsonObject("location").get("lat").getAsDouble();
                double longitude = data.getAsJsonObject("location").get("lng").getAsDouble();

                // Save user's location in the class variables and User object
                userLatitude = latitude;
                userLongitude = longitude;
                user.setLatitude(latitude);
                user.setLongitude(longitude);

                System.out.println("User location updated: Latitude: " + latitude + ", Longitude: " + longitude);

            } else {
                System.out.println("API request failed: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Adds a marker at the specified location
     * @param mapView the MapView object
     * @param point the location where the marker should be added
     */
    private static void addMarkerToMap(MapView mapView, MapPoint point) {
        // Create a marker for the user's location
        Group markerIcon = new Group();
        markerIcon.getChildren().add(new Circle(7, Color.RED));  // Simple red circle marker

        MapMarker marker = new MapMarker();
        marker.addPoint(point, markerIcon);

        // Add the marker layer to the map
        mapView.addLayer(marker);
    }

    public static double getUserLatitude() {
        return userLatitude;
    }

    public static double getUserLongitude() {
        return userLongitude;
    }

    /**
     * Updates the station MapLayer with the provided station
     * @param stationDetails
     */
    public static void updateStationLayer(FuelPriceAPI.StationDetails stationDetails) {
        //mapView.removeLayer(stationMarkers);
        stationMarkers.addPoint(loadCoordinates(stationDetails), createMapIcon(stationDetails));
        //mapView.addLayer(stationMarkers);
    }


    public static MapLayer createCenterLayer(double latitude, double longitude) {
        MapMarker marker = new MapMarker();
        marker.addPoint(new MapPoint(latitude, longitude), new Circle(4, Color.BLUE));
        return marker;
    }



    /**
     * Retrieves Station Details array string coordinates into MapPoint values.
     * @param stationDetails station details
     * @return MapPoint
     */
    public static MapPoint loadCoordinates(FuelPriceAPI.StationDetails stationDetails) {
        double latitude = Double.parseDouble(stationDetails.getLatitude());
        double longitude = Double.parseDouble(stationDetails.getLongitude());
        return new MapPoint(latitude, longitude);
    }

    public static Group createMapIcon(FuelPriceAPI.StationDetails stationDetails) {
        String stationName = stationDetails.getName();
        Group icon = new Group();
        Text name = new Text(10,10,stationName);
        name.setFill(Color.WHITE);
        name.setStroke(Color.BLACK);
        name.setStrokeWidth(0.5);
        icon.getChildren().add(name);
        icon.getChildren().add(new Circle(4,Color.RED));
        return icon;
    }

}