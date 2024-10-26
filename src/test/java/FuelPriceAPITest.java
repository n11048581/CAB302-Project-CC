import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import test.fuelapp.API.FuelPriceAPI;
import test.fuelapp.API.StationDetails;

public class FuelPriceAPITest {

    @Test
    public void testGetFuelTypes_NetworkError() throws IOException {
        // Mock HttpURLConnection to simulate network error
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);

        // Simulate 500 error
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);

        // Verify that IOException is thrown
        assertThrows(IOException.class, () -> FuelPriceAPI.getFuelTypes(mockConnection));
    }

    @Test
    public void testGetFuelTypes_InvalidJson() throws IOException {
        // Simulate a successful HTTP response
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);

        // Simulate 200 response
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        // Simulate invalid JSON response
        String invalidJsonResponse = "INVALID_JSON";
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(invalidJsonResponse.getBytes()));

        // Verify that IllegalStateException is thrown due to invalid JSON structure
        assertThrows(IllegalStateException.class, () -> FuelPriceAPI.getFuelTypes(mockConnection));
    }

    @Test
    public void testGetFullSiteDetails_NetworkError() throws IOException {
        // Mock HttpURLConnection to simulate network error
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);

        // Simulate 500 error
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);

        // Verify that IOException is thrown
        assertThrows(IOException.class, () -> FuelPriceAPI.getFullSiteDetails(mockConnection));
    }

    @Test
    public void testGetFullSiteDetails_InvalidJson() throws IOException {
        // Simulate a successful HTTP response
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);

        // Simulate 200 response
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        // Simulate invalid JSON response
        String invalidJsonResponse = "INVALID_JSON";
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(invalidJsonResponse.getBytes()));

        // Verify that IllegalStateException is thrown due to invalid JSON structure
        assertThrows(IllegalStateException.class, () -> FuelPriceAPI.getFullSiteDetails(mockConnection));
    }

    @Test
    public void testGetSitesPrices_NetworkError() throws IOException {
        // Mock HttpURLConnection to simulate network error
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);

        // Simulate 500 error
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);

        // Create test maps
        Map<String, StationDetails> stationsMap = new HashMap<>();
        StationDetails station = new StationDetails("Eg Ampol Ormeau", "8/19-21 Peachey Rd", "-27.151627", "149.067712");
        stationsMap.put("61290151", station);

        Map<String, String> fuelTypesMap = new HashMap<>();
        fuelTypesMap.put("2", "Unleaded");

        // Verify that IOException is thrown
        assertThrows(IOException.class, () -> FuelPriceAPI.getSitesPrices(mockConnection, stationsMap, fuelTypesMap));
    }

    @Test
    public void testGetSitesPrices_InvalidJson() throws IOException {
        // Mock HttpURLConnection to simulate an invalid JSON response
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);

        // Simulate 200 response
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        // Simulate invalid JSON response
        String invalidJsonResponse = "INVALID_JSON";
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(invalidJsonResponse.getBytes()));

        // Create test maps
        Map<String, StationDetails> stationsMap = new HashMap<>();
        StationDetails station = new StationDetails("Eg Ampol Ormeau", "Eg Ampol Ormeau", "-27.151627", "149.067712");
        stationsMap.put("61290151", station);

        Map<String, String> fuelTypesMap = new HashMap<>();
        fuelTypesMap.put("2", "Unleaded");

        // Verify that IllegalStateException is thrown due to invalid JSON structure
        assertThrows(IllegalStateException.class, () -> FuelPriceAPI.getSitesPrices(mockConnection, stationsMap, fuelTypesMap));
    }
}
