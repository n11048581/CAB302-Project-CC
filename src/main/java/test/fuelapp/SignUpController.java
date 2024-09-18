package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    public SQLiteLink sqLiteLink = new SQLiteLink();
    public LoginModel loginModel = new LoginModel();

    @FXML
    private TextField tf_username;
    @FXML
    private PasswordField pf_password;
    @FXML
    private Label usernameTaken;
    @FXML
    private Label passwordNotSecure;
    @FXML
    private Label passwordNotMatch;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void BackToLogin(ActionEvent event) {
        // Redirect to log in page
        sqLiteLink.changeScene(event, "LogInPage.fxml", "Log In");
    }

    public void CreateAccount(ActionEvent event) {
        try {
            // Regex to match against entered password
            String passwordRegex = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
            if (loginModel.isExistingAccount(tf_username.getText())) {
                // If entered username matches one in database, alert the user the username is taken
                passwordNotMatch.setText("");
                passwordNotSecure.setText("");
                usernameTaken.setText("Username taken");
            } else if (!pf_password.getText().matches(passwordRegex)) {
                // If entered password doesn't match regex, alert the user of password requirements
                usernameTaken.setText("");
                passwordNotSecure.setText("Passwords must contain:\n" +
                        "\n" +
                        "- At least 8 characters\n" +
                        "\n" +
                        "- At least one digit\n" +
                        "\n" +
                        "- At least one uppercase character and one lowercase character\n" +
                        "\n" +
                        "- At least one special character (@#%$^ etc.)\n" +
                        "\n" +
                        "- And cannot contain spaces");
            } else if (!Objects.equals(tf_username.getText(), pf_password.getText())) {
                // If password and repeated password do not match, alert the user
                usernameTaken.setText("");
                passwordNotSecure.setText("");
                passwordNotMatch.setText("Passwords do not match");
            }
            else {
                // Connect to database and set SQL to insert into "users" table
                Connection conn = DriverManager.getConnection("jdbc:sqlite:users.db");
                String sql = "INSERT INTO users(username,password) VALUES(?,?)";

                // Initialise prepared statement and replace question marks in SQL string with data entered by user
                PreparedStatement preparedStatement = conn.prepareStatement(sql);

                preparedStatement.setString(1, tf_username.getText());
                preparedStatement.setString(2, pf_password.getText());
                preparedStatement.executeUpdate();

                // Change scene to home screen
                sqLiteLink.changeScene(event, "Home.fxml", "Home");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
