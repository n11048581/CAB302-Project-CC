package test.fuelapp;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    static String current_user;
    public DatabaseOperations databaseOperations = new DatabaseOperations();
    public SQLiteLink sqLiteLink = new SQLiteLink();

    // Declare JavaFX login page elements
    @FXML
    public Label isConnectedUsername;
    @FXML
    public Label label_welcome;
    @FXML
    public Label label_not_user;
    @FXML
    public Button button_signup;
    @FXML
    public Button button_login;
    @FXML
    public TextField tf_username;
    @FXML
    public PasswordField pf_password;


    // Declare JavaFX loading page elements
    @FXML
    public ImageView loading_gif;
    @FXML
    public Label label_loading;

    // Log user in when enter key is pressed
    @FXML
    public void onEnter(ActionEvent event) throws SQLException, InterruptedException{
        Login(event);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* Check to test if database connection is working, not needed for final submission
          if (loginModel.isDbConnected()) {
              // isConnectedUsername.setText("Connected");
          }
          else {
              isConnectedUsername.setText("Not Connected");
          }
          */
    }


    public void Login (ActionEvent event) throws InterruptedException, SQLException{
        try {
            // Ensure text fields aren't empty
            if (tf_username.getText().isEmpty() || pf_password.getText().isEmpty()) {
                isConnectedUsername.setText("Please enter username and password");
            }
            // If entered login details match an entry in the database, log user in
            else if (databaseOperations.isValidLogin(tf_username.getText(), pf_password.getText())){
                // Set current username to static variable, is used on every page
                isConnectedUsername.setText("");
                current_user = tf_username.getText();

                // Hide login elements
                isConnectedUsername.setVisible(false);
                label_welcome.setVisible(false);
                label_not_user.setVisible(false);
                button_signup.setVisible(false);
                button_login.setVisible(false);
                tf_username.setVisible(false);
                pf_password.setVisible(false);

                // Show temporary loading screen elements
                loading_gif.setVisible(true);
                label_loading.setVisible(true);

                // Start thread that will execute database updates and notify listener when completed.
                // Functionally, will read the current users last location and set app to display those details when first loading in
                executeTaskInSeparateThread(listener);
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


    // Small interface to assist listener thread
    public interface MyThreadListener{
        public void threadFinished();
    }


    // Method to run functionality in separate thread
    public void executeTaskInSeparateThread(final MyThreadListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Complete database updates that take a significant amount of time. Will calculate distances at the same time
                IUser user = databaseOperations.getUserDetails(current_user);
                DatabaseOperations.crowFliesList.clear();
                databaseOperations.generateCrowFliesList(Double.toString(user.getLatitude()),Double.toString(user.getLongitude()));
                try {
                    databaseOperations.updateCrowFlies();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                //Notify the listener when thread is finished
                listener.threadFinished();
            }
        }).start();
    }


    // Implement the listener
    MyThreadListener listener = new MyThreadListener() {
        @Override
        public void threadFinished() {
            // An additional fix to ensure the application thread is being run, as more UI updates need to happen (switching scenes). I am amazed this works
            Platform.runLater(
                    () -> {
                        Stage stage;
                        Parent root;
                        try {
                            // Switch scene to the landing page, without relying on an action event
                            stage = (Stage) button_signup.getScene().getWindow();
                            root = FXMLLoader.load(getClass().getResource("LandingPage.fxml"));
                            Scene scene = new Scene(root);
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }
    };

    public void SignUp (ActionEvent event) {
        // Redirect to sign in scene
        sqLiteLink.changeScene(event, "SignUp.fxml", "Sign Up");
    }
}
