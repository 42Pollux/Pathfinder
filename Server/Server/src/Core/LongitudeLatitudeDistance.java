package Core;

public class LongitudeLatitudeDistance {
	
	public static Double Distance(Vertex<Storage> v1, Vertex<Storage> v2)
    {
      Storage v1Storage = v1.Storage;
      Storage v2Storage = v2.Storage;
      
//       double dist = 6378.388 * Math.acos(Math.sin(Math.toRadians(v1Storage.getLatitude())) * Math.sin(Math.toRadians(v2Storage.getLatitude())) 
//    		   + Math.cos(Math.toRadians(v1Storage.getLatitude())) * Math.cos(Math.toRadians(v2Storage.getLatitude())) * Math.cos(Math.toRadians(v2Storage.getLongitude()) - Math.toRadians(v1Storage.getLongitude())));
//       
//       return  dist;

      double R = 6378.388; // km
      double dLat = Math.toRadians(v2Storage.getLatitude()-v1Storage.getLatitude());
      double dLon = Math.toRadians((v2Storage.getLongitude()-v1Storage.getLongitude())); 
      double a = Math.sin(dLat/2) * Math.sin(dLat/2) + 
              Math.cos(Math.toRadians(v1Storage.getLatitude())) * Math.cos(Math.toRadians(v2Storage.getLatitude())) * Math.sin(dLon/2) * Math.sin(dLon/2); 
      double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
      double d = R * c;
      return d;
    }

}
