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
        System.out.println(String.valueOf(values[0]) + " meters, " + String.valueOf(values[1]) + " seconds" );


    }


    /**
     * Uses Distancematrix.ai API to get the travel distance between two locations via coordinates.
     * Currently has no error detection!
     * @param start The starting location
     * @param end The ending location
     */

    public static int[] getDistance(Location start, Location end) throws IOException {

        //create url
        URL url = new URL(createURL(start, end));
        //set up connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //set up buffered reader to read input stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        //empty string to input stream into
        String inputLine;
        //prepare string builder to receive string
        StringBuilder response = new StringBuilder();
        //while there is still values in the reader
        while ((inputLine = reader.readLine()) != null)
            //append the stream to the strin builder
            response.append(inputLine);
        //close reader
        reader.close();
        //convert response to string
        String finalResponse = response.toString();
        int distance = 0;
        int time = 0;

        //regex to read the string
        Pattern pattern = Pattern.compile("value\":([^}]+)}");
        Matcher matcher = pattern.matcher(finalResponse);
        //theres gotta be a better way than this
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

    ///**
     //* Uses Distancematrix.ai API to get the travel distance between a set of coordinates and a location.
     //* @param latitude The latitude coordinate input for the start
     //* @param longitude The longitude coordinate input for the start
     //* @param end the ending location
     //*/

    //public static void getDistance(Double latitude, Double longitude, Location end) {}

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

/* d = 2R × sin⁻¹(√[sin²((θ₂ - θ₁)/2) + cosθ₁ × cosθ₂ × sin²((φ₂ - φ₁)/2)]) */
/* where d = distance, R = radius of earth (6371km), θ = latitude, φ = longitude*/

