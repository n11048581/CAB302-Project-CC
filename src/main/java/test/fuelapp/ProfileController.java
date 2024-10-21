package test.fuelapp;

import javafx.event.ActionEvent;

public class ProfileController {
    SQLiteLink sqLiteLink = new SQLiteLink();

    public void toSettings(ActionEvent event) {
        // Redirect to log in page
        sqLiteLink.changeScene(event, "Settings.fxml", "Settings");
    }

    public void toLanding(ActionEvent event) {
        // Redirect to log in page
        sqLiteLink.changeScene(event, "LandingPage.fxml", "Home");
    }
}
