module com.example.landingpage {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.testfx.junit5;
    requires org.testfx;
    requires org.testng;
    requires org.junit.jupiter.api;


    opens com.example.landingpage to javafx.fxml;
    exports com.example.landingpage;
}