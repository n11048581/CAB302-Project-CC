package test.fuelapp;

public class User implements IUser {
    private String name;
    private double fuelEfficiency;
    private String[] fuelType;
    private double latitude;
    private double longitude;

    // Constructor
    public User(String name, double fuelEfficiency, String[] fuelType, double latitude, double longitude) {
        this.name = name;
        this.fuelEfficiency = fuelEfficiency;
        this.fuelType = fuelType;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Implementing IUser methods
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public double getFuelEfficiency() {
        return fuelEfficiency;
    }

    @Override
    public void setFuelEfficiency(double fuelEfficiency) {
        this.fuelEfficiency = fuelEfficiency;
    }

    @Override
    public String[] getFuelType() {
        return fuelType;
    }

    @Override
    public void setFuelType(String[] fuelType) {
        this.fuelType = fuelType;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
