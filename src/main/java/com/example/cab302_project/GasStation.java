package com.example.cab302_project;

public class GasStation {
    private String name;
    private double distance;
    private double price;

    public GasStation(String name, double distance, double price) {
        this.name = name;
        this.distance = distance;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }

    public double getPrice() {
        return price;
    }
}