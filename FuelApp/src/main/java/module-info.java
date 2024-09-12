module test.fuelapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;


    opens test.fuelapp to javafx.fxml;
    exports test.fuelapp;
}