package com.example.torsh.mymapsdemo1;

import android.widget.Toast;

/**
 * Created by torsh on 7/14/15.
 *
 * Validation class for the geographic coordinates
 *
 */

public class LatLngValidator {

    private String respond_msg;

    double lat;
    double lon;

    double validLat;
    double validLon;


    public String getStatusMessage(){
        return respond_msg;
    }

    public double getLat(){
        return validLat;
    }

    public double getLon(){
        return validLon;
    }

    public boolean validateLatLng(String lat_str, String lon_str) {

        double min_latitude = -90.0;
        double max_latitude = 90.0;
        double min_longitude = -180.0;
        double max_longitude = 180.0;

        // trim leading zeros of the inputs might be needed. //

        String pattern_lat = "^-{0,1}[0-9]{0,2}[.][0-9]{0,4}"; // regex for: lat: -90.2909
        String pattern_lon = "^-{0,1}[0-9]{1}[0-9]{0,2}[.][0-9]{0,4}"; // regex for lon: -222.0909

        if (lat_str.equals("") || lon_str.equals("") ||
                !lat_str.matches(pattern_lat) || !lon_str.matches(pattern_lon)) {
            respond_msg = "invalid input !";
            //Toast.makeText( getBaseContext() , "Invalid input !", Toast.LENGTH_SHORT).show();
            return false;


        } else {

            lat = Double.parseDouble(lat_str);
            lon = Double.parseDouble(lon_str);

            int compareIndexMinLat = Double.compare(lat, min_latitude);
            int compareIndexMaxLat = Double.compare(lat, max_latitude);

            int compareIndexMinLon = Double.compare(lon, min_longitude);
            int compareIndexMaxLon = Double.compare(lon, max_longitude);

            if (compareIndexMaxLat > 0) {
                respond_msg = "invalid max latitude !";
                return false;
            }
            if (compareIndexMaxLon > 0) {
                respond_msg = "invalid max longitude !";
                return false;
            }

            if (compareIndexMinLat < 0) {
                respond_msg = "invalid min latitude !";
                return false;
            }
            if (compareIndexMinLon < 0) {
                respond_msg = "invalid min longitude !";
                return false;
            }
        }

        respond_msg = "ok";
        validLat = lat;
        validLon = lon;
        return true;
        }

    }