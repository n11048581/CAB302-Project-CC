module com.example.cab302_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;


    opens com.example.cab302_project to javafx.fxml;
    exports com.example.cab302_project;
}