package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    public SQLiteLink sqLiteLink = new SQLiteLink();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void BackToLogin (ActionEvent event) {
        sqLiteLink.changeScene(event, "LogInPage.fxml", "Log In");
    }

}
