package org.uni.pathfinder;

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
