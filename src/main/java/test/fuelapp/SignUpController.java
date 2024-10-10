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

    // New fields

    @FXML
    private TextField tf_fuelEfficiency;
    @FXML
    private TextField tf_fuelType;
    @FXML
    private TextField tf_latitude;
    @FXML
    private TextField tf_longitude;
    @FXML
    private TextField tf_maxTravelDistance;
    @FXML
    private TextField tf_name;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void BackToLogin(ActionEvent event) {
        sqLiteLink.changeScene(event, "LogInPage.fxml", "Log In");
    }

    public void CreateAccount(ActionEvent event) {
        try {
            String passwordRegex = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";

            if (databaseOperations.isExistingAccount(tf_username.getText())) {
                usernameTaken.setText("Username taken");
                passwordNotMatch.setText("");
                passwordNotSecure.setText("");
            } else if (!pf_password.getText().matches(passwordRegex)) {
                usernameTaken.setText("");
                passwordNotMatch.setText("");
                passwordNotSecure.setText("""
                        Passwords must contain:\
                        
                        - At least 8 characters\
                        
                        - At least one digit\
                        
                        - At least one uppercase and one lowercase character\
                        
                        - At least one special character (@#%$^ etc.)\
                        
                        - And cannot contain spaces""");
            } else if (!Objects.equals(pf_password.getText(), pf_password_repeat.getText())) {
                usernameTaken.setText("");
                passwordNotSecure.setText("");
                passwordNotMatch.setText("Passwords do not match");
            } else {

                String name = tf_name.getText();
                double fuelEfficiency = Double.parseDouble(tf_fuelEfficiency.getText());
                String fuelType = tf_fuelType.getText(); // Splits on commas to handle list format
                double latitude = Double.parseDouble(tf_latitude.getText());
                double longitude = Double.parseDouble(tf_longitude.getText());
                double maxTravelDistance = Double.parseDouble(tf_maxTravelDistance.getText());

                IUser newUser = new User(name, fuelEfficiency, fuelType, latitude, longitude, maxTravelDistance);

                // Save user data
                databaseOperations.canCreateAccount(tf_username.getText(), pf_password.getText());


                databaseOperations.saveUserDetails(newUser); //saveUserToDatabase METHOD NEEDS IMPLEMENTING IN SQLiteLink CLASS//

                // Change scene to home screen
                sqLiteLink.changeScene(event, "LandingPage.fxml", "Home");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // Handle number format exceptions when parsing latitude, longitude, or fuel efficiency
            System.out.println("Invalid input format: " + e.getMessage());
        }
    }
}

