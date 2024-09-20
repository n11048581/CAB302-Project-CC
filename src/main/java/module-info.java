module test.fuelapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;
    requires jdk.jdi;
    requires javafx.web;


    opens test.fuelapp to javafx.fxml;
    exports test.fuelapp;
    exports test.fuelapp.sample;
    opens test.fuelapp.sample to javafx.fxml;
}