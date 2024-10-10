import org.junit.jupiter.api.*;
import test.fuelapp.DatabaseOperations;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest {
    DatabaseOperations databaseOperations = new DatabaseOperations();

    @Test
    void testIsExistingAccount() throws SQLException {
        boolean ans = true;
        boolean val;
        val = databaseOperations.isExistingAccount("test");
        assertEquals(ans, val);
    }

    @Test
    void testIsNotExistingAccount() throws SQLException {
        boolean ans = false;
        boolean val;
        val = databaseOperations.isExistingAccount("original");
        assertEquals(ans, val);
    }

    @Test
    void testIsValidLogin() throws SQLException {
        boolean ans = true;
        boolean val;
        val = databaseOperations.isValidLogin("test", "test");
        assertEquals(ans, val);
    }

    @Test
    void testIsNotValidLogin() throws SQLException {
        boolean ans = false;
        boolean val;
        val = databaseOperations.isValidLogin("badusername", "badpassword");
        assertEquals(ans, val);
    }
}
