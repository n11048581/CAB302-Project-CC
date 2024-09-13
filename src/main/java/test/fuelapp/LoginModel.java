package test.fuelapp;

import java.sql.*;

public class LoginModel {
    Connection connection;
    public  LoginModel () {
        connection = SQLiteLink.Connector();
        if (connection == null) {
            System.out.println("Connection Unsuccessful");
            System.exit(1);
        }
    }

    public boolean isDbConnected() {
        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isLogin(String username, String password) throws SQLException {
        PreparedStatement prepStatement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            prepStatement = connection.prepareStatement(query);
            prepStatement.setString(1, username);
            prepStatement.setString(2, password);

            resultSet = prepStatement.executeQuery();
            return resultSet.next();
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            prepStatement.close();
            resultSet.close();
        }
    }
}
