package test.fuelapp;

/**
 * An interface to handle changing data received from API
 */
public interface IApiUpdate {

    int getId();

    String getStationName();

    String getStationAddress();

    String getFuelType();

    double getPrice();

    String getStationLatitude();

    String getStationLongitude();


}
