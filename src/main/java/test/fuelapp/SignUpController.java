package test.fuelapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    static boolean isARedirect = false;

    public SQLiteLink sqLiteLink = new SQLiteLink();
    public DatabaseOperations databaseOperations = new DatabaseOperations();

    @FXML
    private TextField tf_username;
    @FXML
    private PasswordField pf_password;
    @FXML
    private PasswordField pf_password_repeat;
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

    public void clearAllText() {
        usernameTaken.setText("");
        passwordNotMatch.setText("");
        passwordNotSecure.setText("");
    }

    public void CreateAccount(ActionEvent event) {
        try {
            // Regex to match against entered password
            String passwordRegex = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{7,}";
            // Clear all fields
            usernameTaken.setText("");
            passwordNotMatch.setText("");
            passwordNotSecure.setText("");
            if (databaseOperations.isExistingAccount(tf_username.getText())) {
                // If entered username matches one in database, alert the user the username is taken

                usernameTaken.setText("Username taken");
            } else if (!pf_password.getText().matches(passwordRegex)) {
                // If entered password doesn't match regex, alert the user of password requirements
                passwordNotSecure.setText("""
                        Passwords must contain:\
                        
                        - At least 7 characters\
                        
                        - At least one digit\
                        
                        - At least one uppercase character and one lowercase character\
                        
                        - And cannot contain spaces""");
            } else if (!Objects.equals(pf_password.getText(), pf_password_repeat.getText())) {
                // If password and repeated password do not match, alert the user
                passwordNotMatch.setText("Passwords do not match");
            }
            else {
                ILogin newUser = new LoginImplementation(tf_username.getText(), pf_password.getText());
                // Create new record in database for user account
                databaseOperations.canCreateAccount(newUser);

                isARedirect = true;

                // Change scene to home screen
                sqLiteLink.changeScene(event, "LoginPage.fxml", "Login");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onEnter(ActionEvent event) {
        CreateAccount(event);
    }
}
