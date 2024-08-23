package com.example.cab302_project;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.control.Separator;
import javafx.geometry.Orientation;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;


public class GasStationFinderController {
    private GasStationFinderView view;
    private List<GasStation> gasStations;
    private boolean isGasStationListDisplayed;

    public GasStationFinderController(GasStationFinderView view) {
        this.view = view;
        gasStations = new ArrayList<>();

        // Initialize UI components
        Button menuButton = (Button) view.getMenuButton();
        HBox sortBox = view.getSortBox();

        // Initialize UI state
        isGasStationListDisplayed = false;

        // just for check, sample data
        gasStations.add(new GasStation("Coles Express", 3.5, 1.69));
        gasStations.add(new GasStation("United Petroleum", 2.6, 1.67));
        gasStations.add(new GasStation("Shell", 7.4, 1.72));
        gasStations.add(new GasStation("Ampol Foodary", 2.1, 1.74));

        // Initialize event handlers
        menuButton.setOnAction(event -> {
            if (!isGasStationListDisplayed) {
                // Display gas station list
                view.getGasStationList().getChildren().clear();
                Separator topSeparator = new Separator(Orientation.HORIZONTAL);
                view.getGasStationList().getChildren().add(topSeparator); // Add separator between search bar and list

                for (GasStation gasStation : gasStations) {
                    Label label = new Label(gasStation.getName() + " - Distances: " + gasStation.getDistance() + "km, Prices: AU$" + gasStation.getPrice()+ "/ULP 91");
                    view.getGasStationList().getChildren().add(label);
                    Separator separator = new Separator(Orientation.HORIZONTAL);
                    view.getGasStationList().getChildren().add(separator);
                }
                isGasStationListDisplayed = true;

                // Change menu button to home button
                menuButton.setText("Home");

                // Add sort buttons to sort box
                sortBox.getChildren().clear();
                Button distanceButton = new Button("Distance");
                Button priceButton = new Button("Price");
                sortBox.getChildren().add(distanceButton);
                sortBox.getChildren().add(priceButton);

                // Sorting logic for Price or Distance buttons
                priceButton.setOnAction(priceEvent -> {
                    view.getGasStationList().getChildren().clear();
                    Separator priceTopSeparator = new Separator(Orientation.HORIZONTAL);
                    view.getGasStationList().getChildren().add(priceTopSeparator);

                    // Sort the list by price
                    Collections.sort(gasStations, Comparator.comparingDouble(GasStation::getPrice));

                    for (GasStation gasStation : gasStations) {
                        Label label = new Label(gasStation.getName() + " - Distances: " + gasStation.getDistance() + "km, Prices: AU$" + gasStation.getPrice()+ "/ULP 91");
                        view.getGasStationList().getChildren().add(label);
                        Separator separator = new Separator(Orientation.HORIZONTAL);
                        view.getGasStationList().getChildren().add(separator);
                    }
                });

                distanceButton.setOnAction(distanceEvent -> {
                    view.getGasStationList().getChildren().clear();
                    Separator distanceTopSeparator = new Separator(Orientation.HORIZONTAL);
                    view.getGasStationList().getChildren().add(distanceTopSeparator);

                    // Sort the list by distance
                    Collections.sort(gasStations, Comparator.comparingDouble(GasStation::getDistance));

                    for (GasStation gasStation : gasStations) {
                        Label label = new Label(gasStation.getName() + " - Distances: " + gasStation.getDistance() + "km, Prices: AU$" + gasStation.getPrice()+ "/ULP 91");
                        view.getGasStationList().getChildren().add(label);
                        Separator separator = new Separator(Orientation.HORIZONTAL);
                        view.getGasStationList().getChildren().add(separator);
                    }
                });
            } else {
                // Hide gas station list
                view.getGasStationList().getChildren().clear();
                isGasStationListDisplayed = false;

                // Change home button back to menu button
                menuButton.setText("Menu");

                // Clear sort box
                sortBox.getChildren().clear();
            }
        });
    }

    private List<Label> getGasStationLabels(List<GasStation> gasStations) {
        List<Label> labels = new ArrayList<>();
        for (GasStation gasStation : gasStations) {
            Label label = new Label(gasStation.getName() + " - Distances: " + gasStation.getDistance() + "km, Prices: AU$" + gasStation.getPrice() + "/ULP 91");
            labels.add(label);
        }
        return labels;
    }
}