package test.fuelapp.API;

// Store station details
public class StationDetails {
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

    public boolean isValid() {
        return !fuelType.equals("N/A") && !price.equals("N/A");
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

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public void setPrice(String price) {
        this.price = price;
    }










}