package test.fuelapp;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import test.fuelapp.API.FuelPriceAPI;
import test.fuelapp.API.StationDetails;

/**
 * A class that represents the main map on the Landing Page
 */
public class Map {


    private static final MapMarker stationMarkers = new MapMarker();

    /**
     * Creates a map view. Camera position is set to the coordinates.
     * @return a map of the world
     * @param latitude Latitude of the user
     * @param longitude Longitude of the user
     */
    public static MapView createMapView(double latitude, double longitude) {
        MapView mapView = new MapView();



        mapView.setPrefSize(1600,1200);
        mapView.setZoom(15);
        mapView.setCenter(latitude, longitude);

        mapView.addLayer(stationMarkers);
        mapView.addLayer(createCenterLayer(latitude,longitude));

        return mapView;
    }

    /**
     * Updates the station MapLayer with the provided station
     * @param stationDetails Station details
     */
    public static void updateStationLayer(StationDetails stationDetails) {
        //mapView.removeLayer(stationMarkers);

            stationMarkers.addPoint(loadCoordinates(stationDetails), createMapIcon(stationDetails));

        //mapView.addLayer(stationMarkers);
    }

    /**
     * Updates the station marker MapLayer with the provided station coordinates and name,
     * creating a point on the mapView at position with a label.
     * @param stationLatitude Latitude of the station
     * @param stationLongitude Longitude of the station
     * @param stationName Name of the station
     * @param distance distance of the station
     */
    public static void updateStationLayerDB(String stationLatitude, String stationLongitude, String stationName, double distance) {
        stationMarkers.addPoint(loadCoordinatesDB(stationLatitude,stationLongitude),createMapIconDB(stationName, distance));
    }


    /**
     * Overloaded method.
     * Updates the station marker MapLayer with the provided coordinates, name and price
     * creating a point on the mapView at position with a label.
     * @param stationLatitude latitude of the station
     * @param stationLongitude longitude of the station
     * @param stationName name of the station
     * @param fuelPrice fuel price of the station
     * @param distance distance of the station
     */
    public static void updateStationLayerDB(String stationLatitude, String stationLongitude, String stationName, double distance, double fuelPrice) {
        stationMarkers.addPoint(loadCoordinatesDB(stationLatitude,stationLongitude),createMapIconDB(stationName, distance, fuelPrice));
    }


//    /**
//     * Takes in a list of station details and returns a map layer containing markers placed at the locations of those stations
//     * @param stationDetailsList a list containing station details
//     * @return MapLayer
//     */
//    public static MapLayer createMarkerLayer(List<StationDetails> stationDetailsList) {
//        MapMarker markers = new MapMarker();
//        for (StationDetails stationDetails : stationDetailsList) {
//            //issue is, the api returns a map. going to need a conversion process
//            markers.addPoint(loadCoordinates(stationDetails), createMapIcon(stationDetails));
//        }
//        return markers;
//    }


    /**
     * Creates a MapLayer with a marker set to the provided coordinates
     * @param latitude Latitude of the marker
     * @param longitude Longitude of the marker
     * @return MapLayer
     */
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
    public static MapPoint loadCoordinates(StationDetails stationDetails) {
        double latitude = Double.parseDouble(stationDetails.getLatitude());
        double longitude = Double.parseDouble(stationDetails.getLongitude());
        return new MapPoint(latitude, longitude);
    }

    /**
     * Creates a MapPoint from two string coordinates.
     * @param latitudeString Latitude in String
     * @param longitudeString Longitude in String
     * @return a MapPoint
     */
    public static MapPoint loadCoordinatesDB(String latitudeString, String longitudeString) {
        double latitude = Double.parseDouble(latitudeString);
        double longitude = Double.parseDouble(longitudeString);
        return new MapPoint(latitude,longitude);
    }

    /**
     * Creates a MapIcon Group from station details
     * @param stationDetails station details
     * @return a Map Icon Group
     */
    public static Group createMapIcon(StationDetails stationDetails) {
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

    /**
     * Creates a MapIcon Group with label set to station name
     * @param stationName Name of the station
     * @param distance distance of the station
     * @return a MapIcon group
     */
    public static Group createMapIconDB(String stationName, double distance) {
        Group icon = new Group();
        Text name = new Text(10,10,stationName);
        Text travel = new Text(10, 25, String.format("%.2f", distance) + " km");
        name.setFill(Color.WHITE);
        name.setStroke(Color.BLACK);
        name.setStrokeWidth(0.5);
        travel.setFill(Color.WHITE);
        travel.setStroke(Color.BLACK);
        travel.setStrokeWidth(0.5);
        icon.getChildren().add(name);
        icon.getChildren().add(travel);
        icon.getChildren().add(new Circle(4,Color.RED));
        return icon;
    }

    /**
     * Overloaded method. Creates a MapIcon Group with labels set to station name and price
     * @param stationName name of the station
     * @param distance distance of the station
     * @param fuelPrice price of the fuel
     * @return a MapIcon group
     */
    public static Group createMapIconDB(String stationName, double distance, double fuelPrice) {
        Group icon = new Group();
        Text name = new Text(10,10,stationName);
        Text travel = new Text(10, 25, String.format("%.2f", distance) + " km");
        Text price = new Text(10, 40, String.format("%.1f", fuelPrice) + " c");
        name.setFill(Color.WHITE);
        name.setStroke(Color.BLACK);
        name.setStrokeWidth(0.5);
        price.setFill(Color.WHITE);
        price.setStroke(Color.BLACK);
        price.setStrokeWidth(0.5);
        travel.setFill(Color.WHITE);
        travel.setStroke(Color.BLACK);
        travel.setStrokeWidth(0.5);
        icon.getChildren().add(name);
        icon.getChildren().add(price);
        icon.getChildren().add(travel);
        icon.getChildren().add(new Circle(4,Color.RED));
        return icon;
    }

    /**
     * Resets the stationMarker layer.
     */
    public static void resetStationLayer() {
        stationMarkers.clearPoints();
    }

    /**
     * Removes the stationMarker layer from the map
     * @param map The mapView
     */
    public static void unloadMap(MapView map) {
        map.removeLayer(stationMarkers);
    }

    /**
     * Adds teh stationMarker layer to the map
     * @param map The mapview
     */
    public static void loadMap(MapView map) {
        map.addLayer(stationMarkers);
    }

}

