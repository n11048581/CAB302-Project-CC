package test.fuelapp;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import test.fuelapp.sample.FuelPriceAPI;
import test.fuelapp.sample.StationDetails;

import java.util.List;

import static test.fuelapp.sample.SampleData.getSampleData;

public class Map {


    private static final MapMarker stationMarkers = new MapMarker();

    /**
     * Creates a map view.
     * @return a map of the world
     * @param latitude
     * @param longitude
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
     * @param stationDetails
     */
    public static void updateStationLayer(FuelPriceAPI.StationDetails stationDetails) {
        //mapView.removeLayer(stationMarkers);

            stationMarkers.addPoint(loadCoordinates(stationDetails), createMapIcon(stationDetails));

        //mapView.addLayer(stationMarkers);
    }

    /**
     * Updates the station marker MapLayer with the provided station coordinates and name,
     * creating a point on the mapView at position with a label.
     * @param stationLatitude
     * @param stationLongitude
     * @param stationName
     */
    public static void updateStationLayerDB(String stationLatitude, String stationLongitude, String stationName) {
        stationMarkers.addPoint(loadCoordinatesDB(stationLatitude,stationLongitude),createMapIconDB(stationName));
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
     *
     * @param latitude
     * @param longitude
     * @return
     */
    public static MapLayer createCenterLayer(double latitude, double longitude) {
        MapMarker marker = new MapMarker();
        marker.addPoint(new MapPoint(latitude, longitude), new Circle(4, Color.BLUE));
        return marker;
    }

    public static void createBookmarkLayer() {

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

    /**
     *
     * @param lati
     * @param longi
     * @return
     */
    public static MapPoint loadCoordinatesDB(String lati, String longi) {
        double latitude = Double.parseDouble(lati);
        double longitude = Double.parseDouble(longi);
        return new MapPoint(latitude,longitude);
    }

    /**
     *
     * @param stationDetails
     * @return
     */
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

    /**
     *
     * @param stationName
     * @return
     */
    public static Group createMapIconDB(String stationName) {
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
     *
     */
    public static void resetStationLayer() {
        stationMarkers.clearPoints();
        System.out.println("points cleared");
    }

    /**
     *
     * @param map
     */
    public static void unloadMap(MapView map) {
        map.removeLayer(stationMarkers);
    }

    /**
     *
     * @param map
     */
    public static void loadMap(MapView map) {
        map.addLayer(stationMarkers);
    }

}

