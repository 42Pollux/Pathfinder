package org.uni.pathfinder;

import android.util.Log;

import java.util.ArrayList;

public class LongitudeLatitude {

    private  LongitudeLatitude(){}

    public static double[] MaxMinByLongitudeLatitudeRadius(double latitude, double longitude, double radiusInM)
    {
        //result[0] := minLat
        //result[1] := minLon
        //result[2] := maxLat
        //result[3] := maxLon
        double[] result = new double[4];

        double minLat = latitude - (radiusInM / 111130); //one degree of latitude := 111,13 km
        double maxLat = latitude + (radiusInM / 111130);
        double minLon = longitude - (radiusInM / (Math.cos(Math.toRadians(latitude))*111130)); //the degree of longitude depends on the current latitude
        double maxLon = longitude + (radiusInM / (Math.cos(Math.toRadians(latitude))*111130));

        //consider if values above/below limits
       minLat = considerLimitsLatitude(minLat);
       maxLat = considerLimitsLatitude(maxLat);
       minLon = considerLimitsLongitude(minLon);
       maxLon = considerLimitsLongitude(maxLon);

        result[0] = minLat;
        result[1] = minLon;
        result[2] = maxLat;
        result[3] = maxLon;

        return  result;
    }

    public static double[] getRouteSurroundingBoundingBox(ArrayList<String> path_data, int edge_offset) {
        int actual_path_data = (path_data.size())/2;
        double minLat = 180.0, minLon = 180.0, maxLat = 0.0, maxLon = 0.0;
        for(int i=0; i<actual_path_data; i++) {
            String[] latlon = path_data.get(i).split(",");
            double l1 = Double.parseDouble(latlon[0].replace(" ", ""));
            double l2 = Double.parseDouble(latlon[1].replace(" ", ""));
            if(l1>maxLat) maxLat = l1;
            if(l1<minLat) minLat = l1;
            if(l2>maxLon) maxLon = l2;
            if(l2<minLon) minLon = l2;
        }
        Log.d("LongLat", "Actual Path Data: " + actual_path_data +"\nMinLat " + minLat + "\nMinLon " + minLon + "\nMaxLat " + maxLat + "\nMaxLon " + maxLon);

        double[] result = new double[4];

        double edge_offset_lon = (double)edge_offset / 111130;
        double edge_offset_lat = (double)edge_offset / (Math.cos(Math.toRadians(maxLat))*111130);
        Log.d("LongLat", "\noffset_lat " + edge_offset_lat + "\noffset_lon " + edge_offset_lon);
        result[0] = minLat - edge_offset_lat;
        result[1] = minLon - edge_offset_lon;
        result[2] = maxLat + edge_offset_lat;
        result[3] = maxLon + edge_offset_lon;

        Log.d("LongLat", "\nMinLat " + result[0] + "\nMinLon " + result[1] + "\nMaxLat " + result[2] + "\nMaxLon " + result[3]);
        return result;
    }

    private static Double considerLimitsLatitude(Double latitudeValue)
    {
        if(latitudeValue > 90.0)
        {
            return 90.0;
        }
        else if(latitudeValue < -90.0)
        {
            return -90.0;
        }
        else
            return latitudeValue;
    }

    private static Double considerLimitsLongitude(Double longitudeValue)
    {
        if(longitudeValue > 180.0)
        {
            return  180.0;
        }
        else if(longitudeValue < -180.0)
        {
            return  -180.0;
        }
        else
            return longitudeValue;
    }
}
