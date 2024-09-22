package test.fuelapp.sample;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DistanceMatrix {
    private static final String API_KEY = "AIzaSyCAw7bmtK1OCKCExICGGI8M3E-X70kh49I";
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

    // Method to get the driving distance between two points (Google Distance Matrix API call)
    public String getDistance(String originLat, String originLon, String destLat, String destLon) {
        String urlString = BASE_URL +
                "?origins=" + originLat + "," + originLon +
                "&destinations=" + destLat + "," + destLon +
                "&mode=driving" +
                "&key=" + API_KEY;

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse JSON, get distance
                JsonElement jsonResponse = JsonParser.parseString(response.toString());
                if (jsonResponse.isJsonObject()) {
                    JsonObject rootObject = jsonResponse.getAsJsonObject();
                    JsonArray rowsArray = rootObject.getAsJsonArray("rows");
                    if (rowsArray != null && !rowsArray.isEmpty()) {
                        JsonObject rowObject = rowsArray.get(0).getAsJsonObject();
                        JsonArray elementsArray = rowObject.getAsJsonArray("elements");
                        if (elementsArray != null && !elementsArray.isEmpty()) {
                            JsonObject elementObject = elementsArray.get(0).getAsJsonObject();
                            if (elementObject.has("distance")) {
                                JsonObject distanceObject = elementObject.getAsJsonObject("distance");
                                return distanceObject.get("text").getAsString(); // Return the distance in a readable format
                            }
                        }
                    }
                }
            } else {
                System.err.println("Error: HTTP response code " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error fetching distance: " + e.getMessage());
        }

        return "An error has occurred"; //Only reaches this return if error occurs
    }
}