module test.fuelapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;
    requires jdk.jdi;


    opens test.fuelapp to javafx.fxml;
    exports test.fuelapp;
}