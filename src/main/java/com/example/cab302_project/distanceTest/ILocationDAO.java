package com.example.cab302_project.distanceTest;

import java.util.List;

/**
 * Interface for the Contact Data Access Object that handles
 * the CRUD operations for the Contact class with the database.
 */
public interface ILocationDAO {
    /**
     * Adds a new location to the database.
     * @param location The location to add.
     */
    public void addLocation(Location location);
    /**
     * Updates an existing location in the database.
     * @param location The location to update.
     */
    public void updateLocation(Location location);
    /**
     * Deletes a location from the database.
     * @param location The location to delete.
     */
    public void deleteLocation(Location location);
    /**
     * Retrieves a location from the database.
     * @param id The id of the location to retrieve.
     * @return The location with the given id, or null if not found.
     */
    public Location getLocation(int id);
    /**
     * Retrieves all locations from the database.
     * @return A list of all location in the database.
     */
    public List<Location> getAllLocation();
}
