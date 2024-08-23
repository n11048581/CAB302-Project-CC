package com.example.cab302_project;

import javafx.scene.layout.BorderPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GasStationFinderView {
    private BorderPane root;
    private TextField searchField;
    private Button menuButton;
    private VBox gasStationList;
    private HBox sortBox;
    private ImageView mapView;

    public GasStationFinderView() {
        // Creating UI Components
        root = new BorderPane();
        searchField = new TextField();
        menuButton = new Button("menu");
        gasStationList = new VBox(10);
        sortBox = new HBox(10);
        mapView = new ImageView();
        Image mapImage = new Image("file:///path/to/map.png");
        mapView.setImage(mapImage);
        mapView.setFitWidth(400);
        mapView.setFitHeight(300);

        // Setting Up UI Layout
        root.setTop(new HBox(searchField, menuButton));
        HBox.setHgrow(searchField, Priority.ALWAYS);
        VBox centerBox = new VBox();
        centerBox.getChildren().addAll(mapView, gasStationList);
        root.setCenter(gasStationList);
        root.setBottom(sortBox);
    }

    public BorderPane getRoot() {
        return root;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public Button getMenuButton() {
        return menuButton;
    }

    public VBox getGasStationList() {
        return gasStationList;
    }

    public HBox getSortBox() {
        return sortBox;
    }
}