import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.util.Map;
import test.fuelapp.API.FuelPriceAPI;



public class FuelPriceAPITest {

    @Test
    public void testGetFuelTypes_NetworkError() throws Exception {
        // Mock HttpURLConnection to simulate network error
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);

        // Simulate 500 error
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);

        // Call getFuelTypes with the mocked connection
        Map<String, String> fuelTypesMap = FuelPriceAPI.getFuelTypes(mockConnection);

        // Check fuelTypesMap is empty following error
        assertTrue(fuelTypesMap.isEmpty());
    }

    @Test
    public void testGetFuelTypes_InvalidJson() throws Exception {
        // Simulate a successful HTTP response
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);

        // Simulate 200 response
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        // Simulate invalid JSON response
        String invalidJsonResponse = "INVALID_JSON";
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(invalidJsonResponse.getBytes()));

        // Call getFuelTypes with the mocked connection
        Map<String, String> fuelTypesMap = FuelPriceAPI.getFuelTypes(mockConnection);

        // Check fuelTypesMap is empty after JSON parsing error
        assertTrue(fuelTypesMap.isEmpty());
    }



    @Test
    public void testGetFullSiteDetails_NetworkError() throws Exception {
        // Mock HttpURLConnection to simulate network error
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);

        // Simulate 500 error
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);

        // Call getFullSiteDetails with the mocked connection
        Map<String, FuelPriceAPI.StationDetails> stationsMap = FuelPriceAPI.getFullSiteDetails(mockConnection);


        // Check stationMap is empty following error
        assertTrue(stationsMap.isEmpty());
    }

    @Test
    public void testGetFullSiteDetails_InvalidJson() throws Exception {
        // Simulate a successful HTTP response
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);

        // Simulate 200 response
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        // Simulate invalid JSON response
        String invalidJsonResponse = "INVALID_JSON";
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(invalidJsonResponse.getBytes()));

        // Call getFuelTypes with the mocked connection
        Map<String, FuelPriceAPI.StationDetails> stationsMap = FuelPriceAPI.getFullSiteDetails(mockConnection);
        // Check fuelTypesMap is empty after JSON parsing error
        assertTrue(stationsMap.isEmpty());
    }

    @Test
    public void testGetSitesPrices_NetworkError() throws Exception {
        // Mock HttpURLConnection to simulate network error
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);

        // Simulate 500 error
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);

        // Create test maps
        Map<String, FuelPriceAPI.StationDetails> stationsMap = new HashMap<>();
        stationsMap.put("61290151", new FuelPriceAPI.StationDetails("Eg Ampol Ormeau", "8/19-21 Peachey Rd", "-27.151627", "149.067712"));

        Map<String, String> fuelTypesMap = new HashMap<>();
        fuelTypesMap.put("2", "Unleaded");

        // Print the map before the mocked getSitesPrices call
        System.out.println("Before Network Error:");
        printStationsMap(stationsMap);

        // Call getSitesPrices with the mocked connection
        FuelPriceAPI.getSitesPrices(mockConnection, stationsMap, fuelTypesMap);

        // Print the map after the mocked call
        System.out.println("After Network Error:");
        printStationsMap(stationsMap);

        // Check that the station details remain unchanged (no prices or fuel types added)
        FuelPriceAPI.StationDetails station = stationsMap.get("61290151");
        assertEquals("N/A", station.getFuelType());
        assertEquals("N/A", station.getPrice());
    }

    @Test
    public void testGetSitesPrices_InvalidJson() throws Exception {
        // Mock HttpURLConnection to simulate an invalid JSON response
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);

        // Simulate 200 response
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        // Simulate invalid JSON response
        String invalidJsonResponse = "INVALID_JSON";
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(invalidJsonResponse.getBytes()));

        // Create test maps
        Map<String, FuelPriceAPI.StationDetails> stationsMap = new HashMap<>();
        stationsMap.put("61290151", new FuelPriceAPI.StationDetails("Eg Ampol Ormeau", "Eg Ampol Ormeau", "-27.151627", "149.067712"));

        Map<String, String> fuelTypesMap = new HashMap<>();
        fuelTypesMap.put("2", "Unleaded");

        // Print the map before getSitesPrices mocked call
        System.out.println("Before Invalid JSON Response:");
        printStationsMap(stationsMap);

        // Call getSitesPrices with the mocked connection
        FuelPriceAPI.getSitesPrices(mockConnection, stationsMap, fuelTypesMap);

        // Print the map after the mocked call
        System.out.println("After Invalid JSON Response:");
        printStationsMap(stationsMap);

        // Visual confirmation that station is unchanged by invalid JSON response
        FuelPriceAPI.StationDetails station = stationsMap.get("61290151");
        assertEquals("N/A", station.getFuelType());
        assertEquals("N/A", station.getPrice());
    }

    // Helper method for printing stationsMap entries
    private void printStationsMap(Map<String, FuelPriceAPI.StationDetails> stationsMap) {
        for (Map.Entry<String, FuelPriceAPI.StationDetails> entry : stationsMap.entrySet()) {
            FuelPriceAPI.StationDetails station = entry.getValue();
            System.out.println("Station ID: " + entry.getKey() + ", Name: " + station.getName() +
                    ", Fuel Type: " + station.getFuelType() + ", Price: " + station.getPrice());
        }
    }
}