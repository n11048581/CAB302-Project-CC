package com.example.cab302_project.distanceTest;

import java.util.ArrayList;
import java.util.List;

public class MockLocationDAO implements ILocationDAO {

    /**
     * A static list of locations to be used as a mock database.
     */
    public static final ArrayList<Location> locations = new ArrayList<>();
    private static int autoIncrementedId = 0;

    public MockLocationDAO() {
        // Add some initial contacts to the mock database
        addLocation(new Location("SHELL COLES EXPRESS ANNERLEY", -27.50389657004053, 153.03481992714404));
        addLocation(new Location("QUT GARDENS POINT",-27.47711253360433, 153.02698892395478));
    }
    // https://api.distancematrix.ai/maps/api/distancematrix/json?origins=-27.47711253360433,153.02698892395478&destinations=-27.50389657004053,153.03481992714404&key=pmhrz1WllpjiUIg7RZnjhTGZ2C1MMDm2tMKVu3gHWm2x2XFNEwBD1f9CyGGyniwN

    @Override
    public void addLocation(Location location) {
        location.setId(autoIncrementedId);
        autoIncrementedId++;
        locations.add(location);
    }

    @Override
    public void updateLocation(Location location) {
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).getId() == location.getId()) {
                locations.set(i, location);
                break;
            }
        }
    }

    @Override
    public void deleteLocation(Location location) {
        locations.remove(location);
    }

    @Override
    public Location getLocation(int id) {
        for (Location location : locations) {
            if (location.getId() == id) {
                return location;
            }
        }
        return null;
    }

    @Override
    public List<Location> getAllLocation() {
        return new ArrayList<>(locations);
    }
}


