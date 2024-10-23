package test.fuelapp;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.util.Pair;


public class MapMarker extends MapLayer {

    private final ObservableList<Pair<MapPoint, Node>> points = FXCollections.observableArrayList();


    /**
     *
     * @param p
     * @param icon
     */
    public void addPoint(MapPoint p, Node icon) {
        points.add(new Pair<>(p, icon));
        this.getChildren().add(icon);
        this.markDirty();
    }

    /**
     *
     */
    public void clearPoints() {
        points.clear();
        this.getChildren().clear();
        this.markDirty();
    }

}
