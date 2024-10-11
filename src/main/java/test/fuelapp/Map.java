package test.fuelapp;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import test.fuelapp.sample.StationDetails;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static test.fuelapp.sample.SampleData.getSampleData;

public class Map {


    private static final String API_KEY = "AIzaSyA4Eeiogc9Sqx4-w3tOPkQeEN16JAYl9Vk";

    // Class variables to store user's latitude and longitude
    private static double userLatitude;
    private static double userLongitude;

    //honestly it would probably be best if i movied all these map systems to its own class, like Map
    /**
     * Creates a map view
     * @return a map of the world
     */
    public static MapView createMapView() {
        MapView mapView = new MapView();
        //these values should be adjustable later - such as for screen size and user preferences
        mapView.setPrefSize(1600,1200);
        mapView.setZoom(15);
        double defaultLatitude = -27.470125;
        double defaultLongitude = 153.021072;
        mapView.setCenter(defaultLatitude, defaultLongitude);

        // Get user's location and set the center of the map
        getUserLocationAndSetMap(mapView);

        //Add markers (e.g., fuel stations) to the map
        //mapView.addLayer(createMarkerLayer(getSampleData()));


        return mapView;
    }

    /**
     * Retrieves the user's location using Google Geolocation API and updates the map
     * @param mapView the MapView object
     */
    private static void getUserLocationAndSetMap(MapView mapView) {
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

                // Save user's location in the class variables
                userLatitude = latitude;
                userLongitude = longitude;

                // Update the map's center to the user's location
                MapPoint userLocation = new MapPoint(latitude, longitude);
                mapView.setCenter(latitude, longitude); // Move map center to user's location
                mapView.flyTo(0., userLocation, 2.0);   // Smooth fly to user's location

                // Add a marker at the user's location
                addMarkerToMap(mapView, userLocation);
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
     *
     * Takes in a list of station details and returns a map layer containing markers placed at the locations of those stations
     * @param stationDetailsList a list containing station details
     * @return MapLayer
     */
    /*
    public static MapLayer createMarkerLayer(List<StationDetails> stationDetailsList) {
        MapMarker markers = new MapMarker();
        for (StationDetails stationDetails : stationDetailsList) {
            //issue is, the api returns a map. going to need a conversion process
            markers.addPoint(loadCoordinates(stationDetails), createMapIcon(stationDetails));
        }
        return markers;
    } */

    /**
     * Retrieves Station Details array string coordinates into MapPoint values.
     * @param stationDetails station details
     * @return MapPoint
     */
    /*
    public static MapPoint loadCoordinates(StationDetails stationDetails) {
        double latitude = Double.parseDouble(stationDetails.getLatitude());
        double longitude = Double.parseDouble(stationDetails.getLongitude());
        return new MapPoint(latitude, longitude);
    }

    public static Group createMapIcon(StationDetails stationDetails) {
        String stationName = stationDetails.getName();
        Group icon = new Group();
        Text name = new Text(10,10,stationName);
        name.setFill(Color.WHITE);
        name.setStroke(Color.BLACK);
        name.setStrokeWidth(0.5);
        icon.getChildren().add(name);
        icon.getChildren().add(new Circle(7,Color.RED));
        return icon;
    }
    */
}
