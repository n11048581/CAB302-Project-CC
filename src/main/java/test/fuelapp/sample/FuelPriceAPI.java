package test.fuelapp.sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import test.fuelapp.*;


public class FuelPriceAPI {
    private static final String BASE_API_URL = "https://fppdirectapi-prod.fuelpricesqld.com.au";
    private static final String TOKEN = "5552f2b5-d71d-454f-909d-c0aefa2057c4";
    private Map<String, StationDetails> stationsMap;


    public void getStationsData(String fixedLat, String fixedLong, Consumer<StationDetails> stationCallback) {
        stationsMap = new HashMap<>();
        DatabaseOperations databaseOperations = new DatabaseOperations();
        try {
            // Fuel type map
            Map<String, String> fuelTypesMap = getFuelTypes();

            // Full site details map
            stationsMap = getFullSiteDetails();

            // Update stationsMap with site prices
            getSitesPrices(stationsMap, fuelTypesMap);

            DistanceMatrix distanceMatrix = new DistanceMatrix();

            // Variable to track primary keys in database and match with api call
            int DatabaseIdCounter = 1;

            // Print combined data
            for (StationDetails station : stationsMap.values()) {
                // Only print stations that have a real fuel type and price
                if (!station.fuelType.equals("N/A") && !station.price.equals("N/A")) {
                    try {
                        // Get and set distance between user and station (Google Distance Matrix)
                        String distance = distanceMatrix.getDistance(fixedLat, fixedLong, station.getLatitude(), station.getLongitude());
                        station.setDistance(distance);

                        /* Update all data in the database with all data from API
                        IApiUpdate newApiUpdate = new ApiUpdateImplementation(DatabaseIdCounter, station.name, station.address, station.fuelType, Double.valueOf(station.price), station.latitude, station.longitude);
                        databaseOperations.updateStationData(newApiUpdate);
                        */

                        // Update only the price data in the database with data from API
                        //databaseOperations.updatePriceData(Double.valueOf(station.price), DatabaseIdCounter);

                        //DatabaseIdCounter = DatabaseIdCounter + 1;

                        // READ IF YOU THINK THE API ISN'T WORKING RIGHT
                        // Demo for purpose of testing, delete later
                        /*
                        if (DatabaseIdCounter >= 100) {
                            break;
                        }
                        */

                        stationCallback.accept(station); // Calls UI updating function

                    } catch (Exception e) {
                        System.err.println("Error calculating distance for station: " + station.name);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error during API call: " + e.getMessage());
        }
    }

    public Map<String, StationDetails> getStationsMap() {
        return stationsMap;
    }

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
                    System.err.println("Error: 'Fuels' key not found or is not an array.");
                }
            } else {
                System.err.println("Error: Expected JSON object in getFuelTypes.");
            }
        } else {
            System.err.println("Error: HTTP response code " + responseCode);
        }

        return fuelTypesMap;
    }

    // Overloaded getFuelTypes method for production use
    private static Map<String, String> getFuelTypes() throws Exception {
        URL url = new URL(BASE_API_URL + "/Subscriber/GetCountryFuelTypes?countryId=21");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return getFuelTypes(connection); // Call the refactored method
    }




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
                    System.err.println("Error: 'S' key not found or is not an array.");
                }
            } else {
                System.err.println("Error: Unexpected JSON structure in getFullSiteDetails.");
            }
        } else {
            System.err.println("Error: HTTP response code " + responseCode);
        }

        return stationsMap;
    }
    public static Map<String, StationDetails> getFullSiteDetails() throws Exception {
        URL url = new URL(BASE_API_URL + "/Subscriber/GetFullSiteDetails?countryId=21&geoRegionLevel=3&geoRegionId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "FPDAPI SubscriberToken=" + TOKEN);
        connection.setRequestProperty("Content-Type", "application/json");

        return getFullSiteDetails(connection); // Call the overloaded method
    }






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
                            station.fuelType = fuelTypesMap.getOrDefault(fuelId, "Unknown Fuel Type");
                            station.price = price;
                        }
                    }
                } else {
                    System.err.println("Error: 'SitePrices' key not found or is not an array.");
                }
            } else {
                System.err.println("Error: Unexpected JSON structure in getSitesPrices.");
            }
        } else {
            System.err.println("Error: HTTP response code " + responseCode);
        }


    }

    private static void getSitesPrices(Map<String, StationDetails> stationsMap, Map<String, String> fuelTypesMap) throws Exception {
        // Create URL for GetSitesPrices endpoint
        URL url = new URL(BASE_API_URL + "/Price/GetSitesPrices?countryId=21&geoRegionLevel=3&geoRegionId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Call the overloaded method with connection object
        getSitesPrices(connection, stationsMap, fuelTypesMap);
    }





    // Store station details
    public static class StationDetails {
        private String name;
        private String address;
        private String latitude;
        private String longitude;
        private String distance;
        private String fuelType = "N/A";
        private String price = "N/A";
        private double travelCost;


        public StationDetails(String name, String address, String latitude, String longitude) {
            this.name = name;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        // Getters
        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getFuelType() {
            return fuelType;
        }

        public String getPrice() {
            return price;
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }

        public double getTravelCost() { // New getter for travel cost
            return travelCost;
        }

        public void setTravelCost(double travelCost) { // New setter for travel cost
            this.travelCost = travelCost;
        }

        /*








        // Setters
        public void setFuelType(String fuelType) {
            this.fuelType = fuelType;
        }

        public void setPrice(String price) {
            this.price = price;
        }

 */

    }

}