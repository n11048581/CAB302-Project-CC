package test.fuelapp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseOperations {
    // Initialise SQLite database connection
    Connection connection;
    public static List<Double> crowFliesList = new ArrayList<>();


    public DatabaseOperations() {
        connection = SQLiteLink.Connector();
        if (connection == null) {
            System.out.println("Database Connection Unsuccessful");
            System.exit(1);
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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // Close prepared statement
            assert prepStatement != null;
            prepStatement.close();
            assert resultSet != null;
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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // Close prepared statement
            if (prepStatement != null) {
                prepStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    public void canCreateAccount(ILogin login) throws SQLException {
        PreparedStatement prepStatement = null;
        try {
            String sql = "INSERT INTO users (username, password, fuel_type, fuel_efficiency, latitude, longitude, max_travel_distance, bookmarks) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

            // Initialise prepared statement and replace question marks in SQL string with data entered by user
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // Insert details given by user
            preparedStatement.setString(1, login.getUsername());
            preparedStatement.setString(2, login.getPassword());
            // Insert preset details to fill out database
            preparedStatement.setString(3, "All");
            preparedStatement.setDouble(4, 0.0);
            preparedStatement.setDouble(5, -27.4772309);
            preparedStatement.setDouble(6, 153.0270303);
            preparedStatement.setDouble(7, 1000);
            preparedStatement.setString(8, "0");
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close prepared statement
            if (prepStatement != null) {
                prepStatement.close();
            }
        }
    }

    public void updateStationData(IApiUpdate apiUpdate) throws SQLException {
        PreparedStatement prepStatement = null;
        try {
            String sql = "REPLACE INTO gas_stations (id, station_name, station_address, fuel_type, price, station_latitude, station_longitude) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, apiUpdate.getId());
            preparedStatement.setString(2, apiUpdate.getStationName());
            preparedStatement.setString(3, apiUpdate.getStationAddress());
            preparedStatement.setString(4, apiUpdate.getFuelType());
            preparedStatement.setDouble(5, apiUpdate.getPrice());
            preparedStatement.setString(6, apiUpdate.getStationLatitude());
            preparedStatement.setString(7, apiUpdate.getStationLongitude());

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (prepStatement != null) {
                prepStatement.close();
            }
        }
    }


    public Double getCrowFlies(double lat1, double lon1, double lat2, double lon2){
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;

        return dist;
    }

    public void updateCrowFlies() throws SQLException {
        PreparedStatement prepStatement = null;
        for (int q = 0; q < crowFliesList.size(); q++)
            try {
                String sql = "UPDATE gas_stations SET crow_flies_to_user = ? WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setDouble(1, crowFliesList.get(q));
                preparedStatement.setInt(2, q + 1);

                preparedStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (prepStatement != null) {
                    prepStatement.close();
                }
            }
    }


    // Converts decimal degrees to radians
    private double deg2rad(double deg) {
            return (deg * Math.PI / 180.0);
    }


    // Converts radians to decimal degrees
    private double rad2deg(double rad) {
            return (rad * 180.0 / Math.PI);
    }

    // Update database with new user details
    public void saveUserDetails(IUser user) throws SQLException {
        PreparedStatement prepStatement = null;
        try {
            String sql = "UPDATE users SET fuel_efficiency = ?, fuel_type = ?, latitude = ?, longitude = ?, max_travel_distance = ? WHERE username = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, user.getFuelEfficiency());
            preparedStatement.setString(2, user.getFuelType());
            preparedStatement.setDouble(3, user.getLatitude());
            preparedStatement.setDouble(4, user.getLongitude());
            preparedStatement.setDouble(5, user.getMaxTravelDistance());
            preparedStatement.setString(6, LoginController.current_user);

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (prepStatement != null) {
                prepStatement.close();
            }
        }
    }

    // Gets Settings values from DB (Users table)
    public IUser getUserDetails(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("username");
                double fuelEfficiency = rs.getDouble("fuel_efficiency");
                String fuelType = rs.getString("fuel_type");
                double latitude = rs.getDouble("latitude");
                double longitude = rs.getDouble("longitude");
                double maxTravelDistance = rs.getDouble("max_travel_distance");
                String bookmark = rs.getString("bookmarks");

                return new User(name, fuelEfficiency, fuelType, latitude, longitude, maxTravelDistance, bookmark);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    // Create a list of all station distances, to be ordered
    public void generateCrowFliesList(String userLatitude, String userLongitude) {
        String query = "SELECT * FROM gas_stations";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                DatabaseOperations.crowFliesList.add(getCrowFlies(Double.parseDouble(userLatitude), Double.parseDouble(userLongitude), Double.parseDouble(resultSet.getString("station_latitude")), Double.parseDouble(resultSet.getString("station_longitude"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //
    public void updateBookmarks(String username, String newBookmarks) throws SQLException{
        PreparedStatement prepStatement = null;
        try {
            String sql = "UPDATE users SET bookmarks = ? WHERE username = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newBookmarks);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            // Close prepared statement
            if (prepStatement != null) {
                prepStatement.close();
            }
        }
    }

    public String fetchBookmarks(int stationID) throws SQLException {
        PreparedStatement prepStatement = null;
        ResultSet resultSet = null;
        String query = "SELECT station_name, price FROM gas_stations WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, stationID);

            resultSet = preparedStatement.executeQuery();
            return resultSet.getString("station_name") + " - " + resultSet.getDouble("price") / 10;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Close prepared statement
            if (prepStatement != null) {
                prepStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }
}






