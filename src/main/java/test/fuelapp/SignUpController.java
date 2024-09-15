package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    public SQLiteLink sqLiteLink = new SQLiteLink();

    @FXML
    private TextField tf_username;
    @FXML
    private TextField tf_password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void BackToLogin (ActionEvent event) {
        // Redirect to log in page
        sqLiteLink.changeScene(event, "LogInPage.fxml", "Log In");
    }

    public void CreateAccount (ActionEvent event) {

    }

}
