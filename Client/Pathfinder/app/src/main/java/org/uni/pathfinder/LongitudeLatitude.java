package org.uni.pathfinder;

public class LongitudeLatitude {

    private  LongitudeLatitude(){}

    public static Double[] MaxMinByLongitudeLatitudeRadius(double latitude, double longitude, double radiusInM)
    {
        //result[0] := minLat
        //result[1] := minLon
        //result[2] := maxLat
        //result[3] := maxLon
        Double[] result = new Double[4];

        Double minLat = latitude - (radiusInM / 111130); //one degree of latitude := 111,13 km
        Double maxLat = latitude + (radiusInM / 111130);
        Double minLon = longitude - (radiusInM / (Math.cos(latitude)*111130)); //the degree of longitude depends on the current latitude
        Double maxLon = longitude - (radiusInM / (Math.cos(latitude)*111130));

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
