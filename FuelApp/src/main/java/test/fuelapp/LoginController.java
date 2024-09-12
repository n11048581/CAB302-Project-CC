package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public LoginModel loginModel = new LoginModel();
    @FXML
    private Button button_login;

    @FXML
    private Button button_signup;
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
            }
            else {
                isConnected.setText("Credentials are incorrect");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



}
