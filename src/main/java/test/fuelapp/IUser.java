package test.fuelapp;

public interface IUser {

    String getName();

    void setName(String name);

    double getMaxTravelDistance();
    void setMaxTravelDistance(double maxTravelDistance);

    double getFuelEfficiency();
    void setFuelEfficiency(double fuelEfficiency);

    String getFuelType();
    void setFuelType(String fuelType);

    double getLatitude();
    void setLatitude(double latitude);

    double getLongitude();
    void setLongitude(double longitude);

    String getBookmark();
}
