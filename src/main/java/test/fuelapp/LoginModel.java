package test.fuelapp;

import java.sql.*;

public class LoginModel {
    // Initialise SQLite database connection
    Connection connection;
    public  LoginModel () {
        connection = SQLiteLink.Connector();
        if (connection == null) {
            System.out.println("Database Connection Unsuccessful");
            System.exit(1);
        }
    }

    public boolean isDbConnected() {
        // Method to help check database is connected
        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public  boolean isExistingAccount(String username) throws SQLException {
        // Take username and initialise prepared statements
        PreparedStatement prepStatement = null;
        ResultSet resultSet = null;
        // Set SQL query to read all existing usernames from the database
        String query = "SELECT * FROM users WHERE username = ?";
        try {
            // Execute query with given username
            prepStatement = connection.prepareStatement(query);
            prepStatement.setString(1, username);
            resultSet = prepStatement.executeQuery();
            return resultSet.next();
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            // Close prepared statement
            prepStatement.close();
            resultSet.close();
        }
    }


    public boolean isLogin(String username, String password) throws SQLException {
        // Take username and password and initialise prepared statements
        PreparedStatement prepStatement = null;
        ResultSet resultSet = null;
        // Set SQL command to match user input against database
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            // Execute query on entered username and password
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
            // Close prepared statement
            prepStatement.close();
            resultSet.close();
        }
    }



}
