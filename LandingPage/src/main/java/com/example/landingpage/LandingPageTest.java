package com.example.landingpage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.testng.annotations.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class LandingPageTest extends ApplicationTest {

    private VBox root;
    private Button loginButton;
    private Button registerButton;
    private Button searchButton;

    @Override
    public void start(Stage stage) throws Exception {
        root = (VBox) FXMLLoader.load(getClass().getResource("com/example/landingpage/landing_page.fxml"));
        stage.setScene(new Scene(root));
        stage.show();

        loginButton = (Button) root.lookup(".button[text='Login']");
        registerButton = (Button) root.lookup(".button[text='Register']");
        searchButton = (Button) root.lookup(".button[text='Search']");
    }

    @Test
    public void testLoginButton(FxRobot robot) {
        robot.clickOn(loginButton);

        assertNotNull(loginButton);
        assertTrue(loginButton.isFocused());
    }

    @Test
    public void testRegisterButton(FxRobot robot) {
        robot.clickOn(registerButton);

        assertNotNull(registerButton);
        assertTrue(registerButton.isFocused());
    }

    @Test
    public void testSearchButton(FxRobot robot) {
        robot.clickOn(searchButton);

        assertNotNull(searchButton);
        assertTrue(searchButton.isFocused());
    }
}

