package test.fuelapp.sample;

public class StationDetails {
    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private String distance;
    private String fuelType = "N/A";
    private String price = "N/A";


    public StationDetails(String bpBrisbane, String string, String s, String name, String address, String latitude, String longitude) {
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





    // Setters
    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public void setPrice(String price) {
        this.price = price;
    }


}