package test.fuelapp.API;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import test.fuelapp.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 *  FuelPriceAPI is a class providing methods for interacting with the Queensland Mandatory Fuel Reporting API.
 *  It fetches fuel type, petrol station location, and petrol price data for each petrol listing in Queensland
 *  and stores them in a map named StationDetails.
 *
 *  <p>Example usage:</p>
 *  <pre>
 *      FuelPriceAPI api = new FuelPriceAPI();
 *      fuelPriceAPI.getStationsData();
 *  </pre>
 */

public class FuelPriceAPI {
    private static final String BASE_API_URL = "https://fppdirectapi-prod.fuelpricesqld.com.au";
    private static final String TOKEN = "5552f2b5-d71d-454f-909d-c0aefa2057c4";
    private Map<String, StationDetails> stationsMap;

    /**
     * Joins data from the three API call methods and stores them in stationsMap.
     * <p>
     *     This method catches and handles exceptions which occur during API calls or database updates.
     *     Any Errors are logged to console to provide feedback on the nature of issues, such as network or database errors.
     * </p>
     */
    public void getStationsData() {
        stationsMap = new HashMap<>();
        int counter = 0;
        DatabaseOperations databaseOperations = new DatabaseOperations();
        try {
            // Fuel type map
            Map<String, String> fuelTypesMap = getFuelTypes();

            if (fuelTypesMap.isEmpty()) {
                throw new IOException("Failed to fetch fuel types. Empty API response returned.");
            }

            // Full site details map
            stationsMap = getFullSiteDetails();

            if (stationsMap.isEmpty()) {
                throw new IOException("Failed to fetch site details. Empty API response returned.");
            }

            // Update stationsMap with site prices
            getSitesPrices(stationsMap, fuelTypesMap);

            // Variable to track primary keys in database and match with api call
            int DatabaseIdCounter = 1;

            // Print combined data
            for (StationDetails station : stationsMap.values()) {
                // Only print stations that have a real fuel type and price
                if (station.isValid()) {
                    try {

                        IApiUpdate newApiUpdate = new ApiUpdateImplementation(DatabaseIdCounter, station.getName(), station.getAddress(), station.getFuelType(), Double.parseDouble(station.getPrice()), station.getLatitude(), station.getLongitude());
                        databaseOperations.updateStationData(newApiUpdate);
                        counter = counter + 1;
                        System.out.println("Update" + counter);

                    } catch (SQLException e) {
                        System.err.println("Database update failed for station: " + station.getName() + " -- " + e.getMessage());
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid data format in price for station: " + station.getName() + " -- " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error during API call: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetches fuel types from API and stores them in a fuelTypesMap
     * @param connection used in the non-overloaded method for testing purposes - relevant tests can be found in FuelPriceAPITest.java
     * @return a Map in which the keys are fuel type IDs and values are the fuel type names
     * @throws IOException if there is a network connection error or non-200 HTTP response code
     * @throws IllegalStateException if JSON response does not match expected format (e.g., missing Fuels array)
     */
    public static Map<String, String> getFuelTypes(HttpURLConnection connection) throws Exception {
        Map<String, String> fuelTypesMap = new HashMap<>();


        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "FPDAPI SubscriberToken=" + TOKEN);
        connection.setRequestProperty("Content-Type", "application/json");

        // Get response code
        int responseCode = connection.getResponseCode();
        // System.out.println("Fuel Types API Response Code: " + responseCode); // Debugging statement

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Print the raw JSON (debugging)
            //System.out.println("Fuel Types API Response: " + response.toString());

            // Parse JSON
            JsonElement jsonResponse = JsonParser.parseString(response.toString());

            // Check if response is JSON object
            if (jsonResponse.isJsonObject()) {
                JsonObject rootObject = jsonResponse.getAsJsonObject();

                // Isolate fuels array
                if (rootObject.has("Fuels") && rootObject.get("Fuels").isJsonArray()) {
                    JsonArray fuelTypesArray = rootObject.getAsJsonArray("Fuels");
                    System.out.println("Fuel Types Array Size: " + fuelTypesArray.size()); // Debugging statement

                    // Loop through each fuel type and store in map
                    for (JsonElement fuelTypeElement : fuelTypesArray) {
                        JsonObject fuelTypeObj = fuelTypeElement.getAsJsonObject();
                        String fuelId = fuelTypeObj.get("FuelId").getAsString();
                        String fuelName = fuelTypeObj.get("Name").getAsString();
                        fuelTypesMap.put(fuelId, fuelName);

                        // Debugging
                        //System.out.println("Fuel ID: " + fuelId + ", Fuel Name: " + fuelName);
                    }
                } else {
                    throw new IllegalStateException("Fuel array missing from JSON response.");
                }
            } else {
                throw new IllegalStateException("JSON object missing from API response.");
            }
        } else {
            throw new IOException("Unexpected HTTP response code: " + responseCode);
        }

        return fuelTypesMap;
    }

    /**
     * Overloaded method for production use
     */
    private static Map<String, String> getFuelTypes() throws Exception {
        URL url = new URL(BASE_API_URL + "/Subscriber/GetCountryFuelTypes?countryId=21");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return getFuelTypes(connection); // Call the refactored method
    }


    /**
     * Fetches site details from API and stores in stationsMap.
     * @param connection used in the non-overloaded method for testing purposes - relevant tests can be found in FuelPriceAPITest.java.
     * @return a Map where keys are site IDs and values are StationDetails objects for each petrol station site.
     * @throws IOException if there is a network connection error or non-200 HTTP response code
     * @throws IllegalStateException if JSON response does not match expected format (e.g., missing Site key array)
     */
    public static Map<String, StationDetails> getFullSiteDetails(HttpURLConnection connection) throws Exception {
        Map<String, StationDetails> stationsMap = new HashMap<>();

        // Get response code
        int responseCode = connection.getResponseCode();
        //System.out.println("Full Site Details API Response Code: " + responseCode); // Debugging

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // If 200 response, read response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Debugging raw JSON print statement
            //System.out.println("Full Site Details API Response: " + response.toString());

            JsonElement jsonResponse = JsonParser.parseString(response.toString());

            if (jsonResponse.isJsonObject()) {
                JsonObject rootObject = jsonResponse.getAsJsonObject();

                // Extract the 'S' (SiteId) array
                if (rootObject.has("S") && rootObject.get("S").isJsonArray()) {
                    JsonArray sitesArray = rootObject.getAsJsonArray("S");
                    System.out.println("Sites Array Size: " + sitesArray.size()); // Debugging statement

                    // Loop through each site and store details in the map
                    for (JsonElement siteElement : sitesArray) {
                        JsonObject siteObj = siteElement.getAsJsonObject();

                        // Isolate needed fields
                        String siteId = siteObj.has("S") ? siteObj.get("S").getAsString() : "N/A";
                        String name = siteObj.has("N") ? siteObj.get("N").getAsString() : "N/A";
                        String address = siteObj.has("A") ? siteObj.get("A").getAsString() : "N/A";
                        String latitude = siteObj.has("Lat") ? siteObj.get("Lat").getAsString() : "N/A";
                        String longitude = siteObj.has("Lng") ? siteObj.get("Lng").getAsString() : "N/A";

                        // Only add to map if siteId is present, preventing useless entries
                        if (!siteId.equals("N/A")) {
                            stationsMap.put(siteId, new StationDetails(name, address, latitude, longitude));
                        }
                    }
                } else {
                    throw new IllegalStateException("Site key not found or is not an array.");
                }
            } else {
                throw new IllegalStateException("JSON object missing from API response.");
            }
        } else {
            throw new IOException("Unexpected HTTP response code: " + responseCode);
        }

        return stationsMap;
    }

    /**
     * Overloaded method for production use.
     */
    public static Map<String, StationDetails> getFullSiteDetails() throws Exception {
        URL url = new URL(BASE_API_URL + "/Subscriber/GetFullSiteDetails?countryId=21&geoRegionLevel=3&geoRegionId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "FPDAPI SubscriberToken=" + TOKEN);
        connection.setRequestProperty("Content-Type", "application/json");

        return getFullSiteDetails(connection); // Call the overloaded method
    }


    /**
     * Fetches fuel prices from the API and updates the stationsMap with prices and the contents of fuelTypesMap
     * <p>
     *     This method makes a HTTP GET request to fetch site prices from the API, then updates each station's fuel type
     *     and cost in the stationsMap.
     * </p>
     * @param connection used in the non-overloaded method for testing purposes - relevant tests can be found in
     * FuelPriceAPITest.java.
     * @param stationsMap a map of site IDs and their corresponding StationDetails objects to be updated with prices
     * from this method
     * @param fuelTypesMap a map of fuel type IDs and the corresponding fuel type names used to set the fuel type for each station
     * @throws IOException if there is a network connection error or non-200 HTTP response code
     * @throws IllegalStateException if JSON response does not match expected format (e.g., missing SitePrices key)
     */
    public static void getSitesPrices(HttpURLConnection connection, Map<String, StationDetails> stationsMap, Map<String, String> fuelTypesMap) throws Exception {


        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "FPDAPI SubscriberToken=" + TOKEN);
        connection.setRequestProperty("Content-Type", "application/json");

        int responseCode = connection.getResponseCode();
        System.out.println("Sites Prices API Response Code: " + responseCode); // Debugging statement

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();





            JsonElement jsonResponse = JsonParser.parseString(response.toString());


            if (jsonResponse.isJsonObject()) {
                JsonObject rootObject = jsonResponse.getAsJsonObject();

                // Isolate SitePrices array
                if (rootObject.has("SitePrices") && rootObject.get("SitePrices").isJsonArray()) {
                    JsonArray pricesArray = rootObject.getAsJsonArray("SitePrices");
                    System.out.println("Sites Prices Array Size: " + pricesArray.size()); // Debugging

                    // Loop through each entry in price array and update map
                    for (JsonElement priceElement : pricesArray) {
                        JsonObject priceObj = priceElement.getAsJsonObject();
                        String siteId = priceObj.get("SiteId").getAsString();
                        String fuelId = priceObj.get("FuelId").getAsString();
                        String price = priceObj.get("Price").getAsString();

                        // Update the corresponding station with price and fuel type
                        if (stationsMap.containsKey(siteId)) {
                            StationDetails station = stationsMap.get(siteId);
                            station.setFuelType(fuelTypesMap.getOrDefault(fuelId, "Unknown Fuel Type"));
                            station.setPrice(price);
                        }
                    }
                } else {
                    throw new IllegalStateException("SitePrices key not found or is not an array.");
                }
            } else {
                throw new IllegalStateException("JSON object missing from API response.");
            }
        } else {
            throw new IOException("Unexpected HTTP response code: " + responseCode);
        }


    }

    /**
     *  Overloaded method for production use
     */
    private static void getSitesPrices(Map<String, StationDetails> stationsMap, Map<String, String> fuelTypesMap) throws Exception {
        // Create URL for GetSitesPrices endpoint
        URL url = new URL(BASE_API_URL + "/Price/GetSitesPrices?countryId=21&geoRegionLevel=3&geoRegionId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Call the overloaded method with connection object
        getSitesPrices(connection, stationsMap, fuelTypesMap);
    }

}