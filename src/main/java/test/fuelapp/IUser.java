package test.fuelapp;

public interface IUser {
    String getName();

    double getMaxTravelDistance();
    void setMaxTravelDistance(double maxTravelDistance);

    void setName(String name);

    double getFuelEfficiency();
    void setFuelEfficiency(double fuelEfficiency);

    String[] getFuelType();
    void setFuelType(String[] fuelType);

    double getLatitude();
    void setLatitude(double latitude);

    double getLongitude();
    void setLongitude(double longitude);
}
