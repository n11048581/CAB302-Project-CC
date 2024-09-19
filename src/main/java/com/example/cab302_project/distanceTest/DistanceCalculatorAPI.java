package com.example.cab302_project.distanceTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DistanceCalculatorAPI {

    public static void main(String[] args) throws IOException {
        MockLocationDAO test = new MockLocationDAO();

        Location end = test.getLocation(0);
        Location start = test.getLocation(1);
        int[] values = getDistance(start,end);

        //print values for testing
        System.out.println((values[0]) + " meters, " + (values[1]) + " seconds" );


    }

    //Things to do
    //Add error handling for bad inputs
    //Add error handling for no connection
    //Make sure it confirms a HTTP OK before continuing
    //Overload the function for just flat out coordinates instead of location
    //Add a UI to this thing

    /**
     * Uses distancematrix.ai API to get the travel distance and time between two locations via coordinates.
     * Currently, no error detection!
     * @param start The starting location
     * @param end The ending location
     */

    //probably should rename the function!
    public static int[] getDistance(Location start, Location end) throws IOException {

        //create url. deprecated? figure out how to stop it complaining
        URL url = new URL(createURL(start, end));
        //set up connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //set up buffered reader to read input stream
        String finalResponse = getString(connection);

        int distance = 0;
        int time = 0;

        //regex to read the string
        Pattern pattern = Pattern.compile("value\":([^}]+)}");
        Matcher matcher = pattern.matcher(finalResponse);
        //there's gotta be a better way than this
        if (matcher.find())
        {
            distance = Integer.parseInt(matcher.group(1));
        }
        if (matcher.find())
        {
            time = Integer.parseInt(matcher.group(1));
        }

        int[] calculated = new int[2];
        calculated[0] = distance;
        calculated[1] = time;

        //disconnect connection
        if (connection != null) {
            connection.disconnect();
        }

        return calculated;
    }

    /**
     * Takes a GET request connection to the API, returning its response in a string
     * @param connection The get request connection
     * @return  a string of the API JSON response
     * @throws IOException something has gone wrong, probably with the connection? will improve errors later
     */
    private static String getString(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        //empty string to input stream into
        String inputLine;
        //prepare string builder to receive string
        StringBuilder response = new StringBuilder();
        //while there is still values in the reader
        while ((inputLine = reader.readLine()) != null)
            //append the stream to the string builder
            response.append(inputLine);
        //close reader
        reader.close();
        //convert response to string
        return response.toString();
    }

    ///**
     //* Uses Distancematrix.ai API to get the travel distance between a set of coordinates and a location.
     //* @param latitude The latitude coordinate input for the start
     //* @param longitude The longitude coordinate input for the start
     //* @param end the ending location
     //*/

    //public static void getDistance(Double latitude, Double longitude, Location end) {}


    /**
     * Takes in the start and end location objects and returns a completed URL.
     * The API is limited to 1000 requests a month - may have to make another account if we go over limit
     * @param start The starting location
     * @param end The ending location
     * @return returns a completed url get request to the api
     */
    private static String createURL(Location start, Location end) {
        String url = "https://api.distancematrix.ai/maps/api/distancematrix/json?origins=START&destinations=END&key=APIKEY";
        String apiKey = "pmhrz1WllpjiUIg7RZnjhTGZ2C1MMDm2tMKVu3gHWm2x2XFNEwBD1f9CyGGyniwN";

        url = url.replace("START", start.getCoordinates());
        url = url.replace("END", end.getCoordinates());
        url = url.replace("APIKEY", apiKey);

        return url;
    }


}

//https://api.distancematrix.ai/maps/api/distancematrix/json?origins=latitude,longitude&destinations=latitude,longitude&key=key
//key = pmhrz1WllpjiUIg7RZnjhTGZ2C1MMDm2tMKVu3gHWm2x2XFNEwBD1f9CyGGyniwN

