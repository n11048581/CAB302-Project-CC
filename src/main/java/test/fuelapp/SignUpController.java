package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    public SQLiteLink sqLiteLink = new SQLiteLink();
    public LoginModel loginModel = new LoginModel();
    Connection connection = null;

    @FXML
    private TextField tf_username;
    @FXML
    private TextField tf_password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void BackToLogin(ActionEvent event) {
        // Redirect to log in page
        sqLiteLink.changeScene(event, "LogInPage.fxml", "Log In");
    }

    public void CreateAccount(ActionEvent event) {
        try {
            if (loginModel.isExistingAccount(tf_username.getText())) {
                System.out.println("Name taken");
            }
            else {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:users.db");
                String sql = "INSERT INTO users(username,password) VALUES(?,?)";

                PreparedStatement preparedStatement = conn.prepareStatement(sql);

                preparedStatement.setString(1, tf_username.getText());
                preparedStatement.setString(2, tf_password.getText());
                preparedStatement.executeUpdate();

                System.out.println("User added");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
