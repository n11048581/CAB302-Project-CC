package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLiteLink {
    public static Connection Connector() {
        // Method to set database connection
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:users.db");
            return conn;
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    public void changeScene(ActionEvent event, String destination, String title) {
        try {
            // Method that takes an fxml file and a page title, and changes the scene to match
            Parent root = FXMLLoader.load(getClass().getResource(destination));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }




}
