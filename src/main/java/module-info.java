module test.fuelapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;
    requires jdk.jdi;
    requires javafx.web;
    requires com.google.gson;


    opens test.fuelapp to javafx.fxml;
    exports test.fuelapp;
    exports test.fuelapp.sample;
    opens test.fuelapp.sample to javafx.fxml;
}