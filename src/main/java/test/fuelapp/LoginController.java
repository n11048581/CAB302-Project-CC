package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public LoginModel loginModel = new LoginModel();
    public SQLiteLink sqLiteLink = new SQLiteLink();

    @FXML
    private Label isConnected;

    @FXML
    private TextField tf_username;

    @FXML
    private TextField tf_password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (loginModel.isDbConnected()) {
            isConnected.setText("Connected");
        }
        else {
            isConnected.setText("Not Connected");
        }
    }

    public void Login (ActionEvent event) {
        try {
            if (loginModel.isLogin(tf_username.getText(), tf_password.getText())){
                isConnected.setText("Credentials are correct");
                sqLiteLink.changeScene(event, "Home.fxml", "Home");
            }
            else {
                isConnected.setText("Credentials are incorrect");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void SignUp (ActionEvent event) {
        sqLiteLink.changeScene(event, "SignUp.fxml", "Sign Up");
    }
}
