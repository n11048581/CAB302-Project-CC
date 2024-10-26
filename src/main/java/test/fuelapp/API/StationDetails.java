package test.fuelapp.API;

/**
 * Class which can be instantiated to create an instance of a petrol station, including its name, address, coordinates,
 * distance, fuel type, price, and travel cost.
 * Getter and setter methods present for most station attributes.
 * Contains an isValid() method for checking validity of fuel information.
 */
public class StationDetails {

    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private String distance;
    private String fuelType = "N/A";
    private String price = "N/A";
    private double travelCost;

    /**
     * Constructs a StationDetails object with specified name, address, latitude, and longitude.
     *
     * @param name      the name of the fuel station
     * @param address   the address of the fuel station
     * @param latitude  the latitude coordinate of the station
     * @param longitude the longitude coordinate of the station
     */
    public StationDetails(String name, String address, String latitude, String longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Checks if the station has real fuel information.
     *
     * @return true if both fuelType and price are not default values.
     */
    public boolean isValid() {
        return !fuelType.equals("N/A") && !price.equals("N/A");
    }

    // Getters

    /**
     * @return the latitude of the station
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * @return the longitude of the station
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * @return the distance to the station
     */
    public String getDistance() {
        return distance;
    }

    /**
     * Sets the distance to the station.
     *
     * @param distance the distance to set
     */
    public void setDistance(String distance) {
        this.distance = distance;
    }

    /**
     * @return the fuel type available at the station
     */
    public String getFuelType() {
        return fuelType;
    }

    /**
     * @return the price of the fuel at the station
     */
    public String getPrice() {
        return price;
    }

    /**
     * @return the name of the station
     */
    public String getName() {
        return name;
    }

    /**
     * @return the address of the station
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return the travel cost to the station
     */
    public double getTravelCost() {
        return travelCost;
    }

    /**
     * Sets the travel cost to the station.
     *
     * @param travelCost the travel cost to set
     */
    public void setTravelCost(double travelCost) {
        this.travelCost = travelCost;
    }

    /**
     * Sets the type of fuel available at the station.
     *
     * @param fuelType the type of fuel to set
     */
    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    /**
     * Sets the price of the fuel at the station.
     *
     * @param price the price to set
     */
    public void setPrice(String price) {
        this.price = price;
    }
}










