import org.junit.jupiter.api.*;
import test.fuelapp.LoginModel;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest {
    LoginModel loginModel = new LoginModel();

    @Test
    void testIsExistingAccount() throws SQLException {
        boolean ans = true;
        boolean val;
        val = loginModel.isExistingAccount("test");
        assertEquals(ans, val);
    }

    @Test
    void testIsNotExistingAccount() throws SQLException {
        boolean ans = false;
        boolean val;
        val = loginModel.isExistingAccount("original");
        assertEquals(ans, val);
    }

    @Test
    void testIsValidLogin() throws SQLException {
        boolean ans = true;
        boolean val;
        val = loginModel.isValidLogin("test", "test");
        assertEquals(ans, val);
    }

    @Test
    void testIsNotValidLogin() throws SQLException {
        boolean ans = false;
        boolean val;
        val = loginModel.isValidLogin("badusername", "badpassword");
        assertEquals(ans, val);
    }
}
