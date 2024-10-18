package test.fuelapp;

import test.fuelapp.sample.FuelPriceAPI.StationDetails;

public class StationCalculations {

    // Calculate the total refueling cost (based solely on the amount of fuel)
    public static double calculateTotalCost(StationDetails station, double fuelEfficiency, double distance, double fuelAmount) {
        double travelCost = calculateTravelCost(station, fuelEfficiency, distance);
        double refuelingCost = fuelAmount * (Double.parseDouble(station.getPrice()) / 1000.0);
        return Double.parseDouble(String.format("%.2f", travelCost + refuelingCost));
    }

    // Calculate the fuel cost for traveling to the gas station
    public static double calculateTravelCost(StationDetails station, double fuelEfficiency, double distance) {
        double fuelRequired = (distance / 100) * fuelEfficiency;
        double pricePerLiter = Double.parseDouble(station.getPrice()) / 1000.0; // Convert price to dollars
        double costOfTravel = fuelRequired * pricePerLiter;
        return Double.parseDouble(String.format("%.2f", costOfTravel));
    }

    // Parse the distance string and return it as a double, with error handling
    public static Double calculateDistance(String distanceString) {
        try {
            if (distanceString == null || distanceString.trim().isEmpty() ||
                    distanceString.equalsIgnoreCase("N/A") || distanceString.equalsIgnoreCase("An error has occurred")) {
                // If the distance is invalid, return null to indicate no valid distance
                return null;
            }

            // Remove commas and 'km' before parsing
            String cleanedDistance = distanceString.replace(",", "").replace(" km", "");

            // Check if the cleaned distance is a valid number
            if (!cleanedDistance.matches("\\d+(\\.\\d+)?")) {
                return null;  // Return null if the distance is not a valid number
            }

            return Double.parseDouble(cleanedDistance);
        } catch (Exception e) {
            return null;  // Return null if any parsing fails
        }
    }
}
