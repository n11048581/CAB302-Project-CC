import org.junit.jupiter.api.*;
import test.fuelapp.SQLiteLink;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {

    @Test
    public void testConnection() {
        Connection conn = SQLiteLink.Connector();
        assertNotNull(conn);
    }

}
