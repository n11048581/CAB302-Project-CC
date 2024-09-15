package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    public SQLiteLink sqLiteLink = new SQLiteLink();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void LogOut (ActionEvent event) {
        // Redirect to log in page
        sqLiteLink.changeScene(event, "LogInPage.fxml", "Log In");
    }
}
