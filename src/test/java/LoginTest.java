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
        val = databaseOperations.isValidLogin("goodusername", "GoodPassword@1");
        assertEquals(ans, val);
    }

    @Test
    void testIsNotValidLogin() throws SQLException {
        boolean ans = false;
        boolean val;
        val = databaseOperations.isValidLogin("badusername", "badpassword");
        assertEquals(ans, val);
    }

    @Test
    void testDoubleLogin() throws SQLException {
        boolean val;
        boolean val2;
        val = databaseOperations.isValidLogin("user1", "Password@1");
        val2 = databaseOperations.isValidLogin("user2", "Password@1");
        assertEquals(val,val2);
    }
}

