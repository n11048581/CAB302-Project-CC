package test.fuelapp;

import java.sql.*;

public class DatabaseOperations {
    // Initialise SQLite database connection
    Connection connection;
    public DatabaseOperations() {
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

    public boolean isExistingAccount(String username) throws SQLException {
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

    public boolean isValidLogin(String username, String password) throws SQLException {
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

    public void canCreateAccount(String username, String password) throws SQLException {
        PreparedStatement prepStatement = null;
        try {
            String sql = "INSERT INTO users(username,password) VALUES(?, ? )";

            // Initialise prepared statement and replace question marks in SQL string with data entered by user
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            // Close prepared statement
            if (prepStatement != null) {
                prepStatement.close();
            }
        }
    }

    public void saveUserDetails(IUser user) {
        PreparedStatement prepStatement = null;
        try {
            String sql = "INSERT INTO users(name, fuel_efficiency, fuel_type, latitude, longitude) VALUES(?, ?, ?, ?, ?) WHERE username = ?";

            // Initialise prepared statement and replace question marks in SQL string with data entered by user
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, /* name */);
            preparedStatement.setString(2, /* fuel_efficiency */);
            preparedStatement.setString(3, /* fuel_type */);
            preparedStatement.setDouble(4, /* latitude */);
            preparedStatement.setDouble(5, /* longitude */);
            preparedStatement.setString(6, LoginController.current_user);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close prepared statement
            if (prepStatement != null) {
                prepStatement.close();
            }
        }

    /*
    public IUser getUserDetails (String username) {

    }
    */

    }
}

