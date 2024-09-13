package test.fuelapp;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLiteLink {

    public static Connection Connector() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:users.db");
            return conn;
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
    }


}
