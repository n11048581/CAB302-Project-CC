package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Static class containing database connection method, and page redirect method. Used on all pages
 */
public class SQLiteLink {

    /**
     * Forms a jdbc connection with a local database
     * @return A valid connection if successful, or invalid or not
     */
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


    /**
     * Method to change JavaFX scenes
     * @param event The event the developer wishes to trigger a scene change
     * @param destination The fxml file the developer wishes to change to
     * @param title The title of the scene that will be generated
     */
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
