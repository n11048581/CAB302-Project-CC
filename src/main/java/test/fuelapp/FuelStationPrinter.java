package test.fuelapp;

import test.fuelapp.sample.FuelPriceAPI;
import test.fuelapp.sample.FuelPriceAPI.StationDetails;
public class FuelStationPrinter {


    // THIS FILE IS FOR TESTING PURPOSES ONLY
    public static void main(String[] args) {
        // Create an instance of the FuelPriceAPI
        FuelPriceAPI api = new FuelPriceAPI();

        // Call the method to get station data
        api.getStationsData(); // This will fetch and populate the data

        // Access the stationsMap using the getter
        for (FuelPriceAPI.StationDetails station : api.getStationsMap().values()) {
            System.out.println("Station Name: " + station.getName());
            System.out.println("Address: " + station.getAddress());
            System.out.println("Fuel Type: " + station.getFuelType());
            System.out.println("Price: " + station.getPrice());
            System.out.println("Distance: " + station.getDistance());
            System.out.println("Estimated Travel Cost: $" + String.format("%.2f", station.getTravelCost()));
            System.out.println("-------------------------------------");
        }
    }
}