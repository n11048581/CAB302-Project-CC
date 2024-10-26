package test.fuelapp;
import test.fuelapp.API.FuelPriceAPI.StationDetails;

public class StationCalculations {

    public static double calculateTravelCost(StationDetails station, double fuelEfficiency, double distance) {
        double fuelRequired = (distance / 100) * fuelEfficiency;
        double pricePerLiter = Double.parseDouble(station.getPrice()) / 1000.0; // Convert price to dollars
        double costOfTravel = fuelRequired * pricePerLiter;
        return Double.parseDouble(String.format("%.2f", costOfTravel));
    }

    public static double calculateDistance(String distanceString) {
        return Double.parseDouble(distanceString.replace(" km", ""));
    }
}
