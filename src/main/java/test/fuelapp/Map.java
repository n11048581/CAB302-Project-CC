package test.fuelapp;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import test.fuelapp.sample.StationDetails;

import java.util.List;

import test.fuelapp.sample.FuelPriceAPI;
import static test.fuelapp.sample.SampleData.getSampleData;

public class Map {
    //honestly it would probably be best if i movied all these map systems to its own class, like Map
    /**
     * Creates a map view
     * @return a map of the world
     */

    private FuelPriceAPI fuelPriceAPI = new FuelPriceAPI();

    public static MapView createMapView() {
        MapView mapView = new MapView();
        //these values should be adjustable later - such as for screen size and user preferences
        mapView.setPrefSize(1600,1200);
        mapView.setZoom(15);
        mapView.setCenter(-27.470125, 153.021072);
        mapView.addLayer(createMarkerLayer(getSampleData()));
        return mapView;
    }


    /**
     * Takes in a list of station details and returns a map layer containing markers placed at the locations of those stations
     * @param stationDetailsList
     * @return MapLayer
     */
    public static MapLayer createMarkerLayer(List<StationDetails> stationDetailsList) {
        MapMarker markers = new MapMarker();
        for (StationDetails stationDetails : stationDetailsList) {
            //would be nice if maybe it could have text too? might suck if you're zoomed out, though...
            markers.addPoint(loadCoordinates(stationDetails), createMapIcon(stationDetails));
        }
        return markers;
    }

    /**
     * Retrieves Station Details array string coordinates into MapPoint values.
     * @param stationDetails
     * @return MapPoint
     */
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

}
