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

/**
 *  Class to handle backend of the login system, not including sign up
 */
public class LoginController implements Initializable {
    static String current_user;

    public DatabaseOperations databaseOperations = new DatabaseOperations();
    public SQLiteLink sqLiteLink = new SQLiteLink();


    // Declare JavaFX login page elements
    @FXML
    private Label isConnectedUsername;
    @FXML
    private Label label_welcome;
    @FXML
    private Label label_not_user;
    @FXML
    private Button button_signup;
    @FXML
    private Button button_login;
    @FXML
    private TextField tf_username;
    @FXML
    private PasswordField pf_password;


    // Declare JavaFX loading page elements
    @FXML
    private ImageView loading_gif;
    @FXML
    private Label label_loading;


    @FXML
    private Label label_redirect;

    /**
     * Attempt to log user in when enter key is pressed
     * @param event User presses the enter key
     * @throws SQLException Cannot form a database connection
     * @throws InterruptedException Thread error catch
     */
    @FXML
    public void onEnter(ActionEvent event) throws SQLException, InterruptedException{
        login(event);
    }

    /**
     * This method is run when the login fxml page is loaded, occurring on app launch
     * @param url Obtained from main when starting app
     * @param resourceBundle Obtained from main when starting app
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (SignUpController.isARedirect){
            label_redirect.setVisible(true);
            SignUpController.isARedirect = false;
        }
    }


    /**
     * Method to validate user's entered login, calling a thread to load their details if successful
     * @param event Used to assign onEnter() to this method
     */
    public void login(ActionEvent event){
        try {
            if (tf_username.getText().isEmpty() || pf_password.getText().isEmpty()) {
                isConnectedUsername.setText("Please enter username and password");
            }
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
                label_redirect.setVisible(false);

                // Show temporary loading screen elements
                loading_gif.setVisible(true);
                label_loading.setVisible(true);

                // Start thread that will execute database updates and notify listener when completed.
                // Functionally, will read the current users last location and set app to display those details when first loading in
                executeTaskInSeparateThread(loginListener);
            }
            else {
                isConnectedUsername.setText("Credentials are incorrect");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Interface to assist listener thread
     */
    public interface LoginListener{
        /**
         *  Detect when a thread finishes
         */
        void threadFinished();
    }

    /**
     * Run database updates in a separate thread
     * @param loginListener Take the declared login interface
     */
    // Method to run functionality in separate thread
    public void executeTaskInSeparateThread(final LoginListener loginListener){
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
                loginListener.threadFinished();
            }
        }).start();
    }


    /**
     * Listen for when database is done updating, and change to landing page scene
     */
    LoginListener loginListener = new LoginListener() {
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
                            stage.setTitle("Map");
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }
    };


    /**
     * Redirect to the sign-in page
     * @param event triggers when user clicks the 'create account' button
     */
    public void SignUp (ActionEvent event) {
        sqLiteLink.changeScene(event, "SignUp.fxml", "Sign Up");
    }
}
