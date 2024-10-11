package test.fuelapp;

public class ApiUpdateImplementation implements IApiUpdate {
    private int id;
    private String stationName;
    private String stationAddress;
    private String fuelType;
    private double price;
    private String stationLatitude;
    private String stationLongitude;


    public ApiUpdateImplementation(int id, String stationName, String stationAddress, String fuelType, double price, String stationLatitude, String stationLongitude) {
        this.id = id;
        this.stationName = stationName;
        this.stationAddress = stationAddress;
        this.fuelType = fuelType;
        this.price = price;
        this.stationLatitude = stationLatitude;
        this.stationLongitude = stationLongitude;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getStationName() {
        return stationName;
    }

    @Override
    public String getStationAddress() {
        return stationAddress;
    }

    @Override
    public String getFuelType() {
        return fuelType;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public String getStationLatitude() {
        return stationLatitude;
    }

    @Override
    public String getStationLongitude() {
        return stationLongitude;
    }
}
