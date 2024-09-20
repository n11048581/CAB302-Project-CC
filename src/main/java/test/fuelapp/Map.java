package test.fuelapp;

public class Map {
    public String getMapHTML() {
        return "<!DOCTYPE html>" +
                "<html>" +
                "  <head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <title>Google Maps API Map</title>" +
                "    <style>" +
                "      #map {" +
                "        width: 100%;" +
                "        height: 100%;" +
                "        border: 1px solid black;" +
                "      }" +
                "    </style>" +
                "  </head>" +
                "  <body>" +
                "    <div id=\"map\"></div>" +
                "    <script>" +
                "      var options = {" +
                "           zoom: 12, " +
                "           center: { lat: -27.470125, lng: 153.021072}" +
                "      }" +
                "      function initMap() {" +
                "        var map = new google.maps.Map(document.getElementById('map'), options);" +
                "      }" +
                "    </script>" +
                "    <script src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyA4Eeiogc9Sqx4-w3tOPkQeEN16JAYl9Vk&callback=initMap\"></script>" +
                "  </body>" +
                "</html>";
    }

}
