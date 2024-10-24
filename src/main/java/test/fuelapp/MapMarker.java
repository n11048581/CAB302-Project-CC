package test.fuelapp;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.util.Pair;


/**
 * A class that represents the main station marker layer for the map.
 */
public class MapMarker extends MapLayer {

    private final ObservableList<Pair<MapPoint, Node>> points = FXCollections.observableArrayList();


    /**
     * Adds a point and icon to the MapMarker Layer.
     * @param p A MapPoint consisting of Latitude and Longitude
     * @param icon An icon for the point
     */
    public void addPoint(MapPoint p, Node icon) {
        points.add(new Pair<>(p, icon));
        this.getChildren().add(icon);
        this.markDirty();
    }

    /**
     * Removes all points from the MapMarker Layer.
     */
    public void clearPoints() {
        points.clear();
        this.getChildren().clear();
        this.markDirty();
    }

    @Override
    protected void layoutLayer() {
        for (Pair<MapPoint, Node> candidate : points) {
            MapPoint point = candidate.getKey();
            Node icon = candidate.getValue();
            Point2D mapPoint = getMapPoint(point.getLatitude(), point.getLongitude());
            icon.setVisible(true);
            icon.setTranslateX(mapPoint.getX());
            icon.setTranslateY(mapPoint.getY());
        }
    }

}
