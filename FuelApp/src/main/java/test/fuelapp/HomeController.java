package test.fuelapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    private Label label_welcome_user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void initialize(String username) {
        label_welcome_user.setText(username);
    }
}
