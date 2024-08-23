package com.example.cab302_project;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class GasStationFinder extends Application {
    @Override
    public void start(Stage primaryStage) {
        GasStationFinderView view = new GasStationFinderView();
        GasStationFinderController controller = new GasStationFinderController(view);

        Scene scene = new Scene(view.getRoot(), 400, 300);
        primaryStage.setTitle("Find gas station");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
