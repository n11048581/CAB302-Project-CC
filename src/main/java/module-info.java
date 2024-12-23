module test.fuelapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;
    requires jdk.jdi;
    requires javafx.web;
    requires com.gluonhq.attach.util;
    requires com.gluonhq.maps;
    requires org.slf4j;
    requires com.google.gson;
    requires commons.lang;


    opens test.fuelapp to javafx.fxml;
    exports test.fuelapp;
    exports test.fuelapp.API;
    opens test.fuelapp.API to javafx.fxml;
}

