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

    private FuelPriceAPI fuelPriceAPI = new FuelPriceAPI();

    private static MapMarker stationMarkers = new MapMarker();

    /**
     * Creates a map view
     * @return a map of the world
     */
    public static MapView createMapView(double latitude, double longitude) {
        MapView mapView = new MapView();
        //these values should be adjustable later - such as for screen size and user preferences
        mapView.setPrefSize(1600,1200);
        mapView.setZoom(15);
        mapView.setCenter(latitude, longitude);
        //probably put this in a conditional of some sorts? retrieve from database probably
        //issue - not sure if i can add a layer afterwards...
        mapView.addLayer(stationMarkers);
        mapView.addLayer(createCenterLayer(latitude,longitude));
        return mapView;
    }


    public static void updateStationLayer(MapView mapView, FuelPriceAPI.StationDetails stationDetails) {
        mapView.removeLayer(stationMarkers);
        stationMarkers.addPoint(loadCoordinates(stationDetails), createMapIcon(stationDetails));
        mapView.addLayer(stationMarkers);
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


    public static MapLayer createCenterLayer(double latitude, double longitude) {
        MapMarker marker = new MapMarker();
        marker.addPoint(new MapPoint(latitude, longitude), new Circle(5, Color.BLUE));
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
        icon.getChildren().add(new Circle(7,Color.RED));
        return icon;
    }

}
