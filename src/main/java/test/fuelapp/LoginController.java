package test.fuelapp;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    static String current_user;
    public DatabaseOperations databaseOperations = new DatabaseOperations();
    public SQLiteLink sqLiteLink = new SQLiteLink();

    // Declare JavaFX elements
    @FXML
    private Label isConnectedUsername;
    @FXML
    private TextField tf_username;
    @FXML
    private PasswordField pf_password;

    @FXML
    public void onEnter(ActionEvent event) throws SQLException{
        Login(event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { // Check to test if database connection is working, not needed for final submission
    //  if (loginModel.isDbConnected()) {
    //      // isConnectedUsername.setText("Connected");
    //  }
    //  else {
    //      isConnectedUsername.setText("Not Connected");
    //  }
    }

    public void Login (ActionEvent event) throws SQLException{
        try {
            // Ensure text fields aren't empty
            if (tf_username.getText().isEmpty() || pf_password.getText().isEmpty()) {
                isConnectedUsername.setText("Please enter username and password");
            }
            // If entered login details match an entry in the database, log user in
            else if (databaseOperations.isValidLogin(tf_username.getText(), pf_password.getText())){
                isConnectedUsername.setText("");
                current_user = tf_username.getText();
                IUser user = databaseOperations.getUserDetails(current_user);

                Task<Void> getUsersDetails = new Task<Void>() {     // Background thread to fetch API data progressively
                    @Override
                    public Void call() throws Exception {
                        // userLat and userLong pulled from Users db table, assigned in Settings
                        System.out.println("Thread Running");
                        DatabaseOperations.crowFliesList.clear();
                        databaseOperations.generateCrowFliesList(Double.toString(user.getLatitude()),Double.toString(user.getLongitude()));
                        databaseOperations.updateCrowFlies();
                        return null;
                    }
                };

                getUsersDetails.setOnFailed(e -> {
                    System.err.println("Couldn't update database" + getUsersDetails.getException().getMessage());
                });

                // Run task in separate thread
                Thread thread = new Thread(getUsersDetails);
                thread.setDaemon(true);
                thread.start();

                sqLiteLink.changeScene(event, "LandingPage.fxml", "Home");
            }
            // Else display error message
            else {
                isConnectedUsername.setText("Credentials are incorrect");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void SignUp (ActionEvent event) {
        // Redirect to sign in scene
        sqLiteLink.changeScene(event, "SignUp.fxml", "Sign Up");
    }
}
