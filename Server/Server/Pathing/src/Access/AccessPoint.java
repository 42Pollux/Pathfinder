package Access;
import java.sql.SQLException;
import java.util.*;

import com.mysql.cj.QueryResult;
import com.sun.org.apache.bcel.internal.generic.NEW;

import Core.*;
import DatabaseConnection.*;
import ErrorHandling.AccessPointException;

public class AccessPoint {
		protected Vertex<Storage> InputPoint; //this is the input from the gui
		protected Vertex<Storage> ClosestVertex; //this is a vertex of the database
		private int IterationInterval = 100; //stores the interval limits the accessPoint was found; hence, interval start with 100 m 
		
		private AccessPoint()
		{}
		
		/**creates a new access point bases on the input coordinates**/
		public static AccessPoint ByGUIInput(double longitude, double latitude, double height)
		{
			AccessPoint accessPoint = new AccessPoint();
			//make a new vertex, where its storage is a storage object with the input coordinates
			accessPoint.InputPoint = new Vertex<Storage>("input", new Storage("input",Integer.MIN_VALUE,longitude,latitude,height)); 
			//establish a db connection 
			DatabaseConnection connection = null;
			try {
				connection = DatabaseConnection.ByUsernameAndPW("DataFinderGen", "D@t@F!nd3rG3n");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Couldn't connect to db");
				return null;
			}
			
			if(connection != null)
			{
				ArrayList<Vertex<Storage>> closestPoints = new ArrayList<Vertex<Storage>>();
				do
				{
					closestPoints = accessPoint.queryDatabaseForAccessPointsByCoordinates(connection);
					//
					//iteration interval is to large
					if(closestPoints.size() >= 100)
						accessPoint.IterationInterval = accessPoint.IterationInterval / 2;
					//iteration interval is to small
					else if(closestPoints.isEmpty())
						accessPoint.IterationInterval = accessPoint.IterationInterval * 2; 
					
				}while(closestPoints.size() >= 100 && !closestPoints.isEmpty());	
				
				//map the distance to every vertex from closestPoints and inputPoint   
				ArrayList<KeyValue<Vertex<Storage>, Double>> matchedDistancesToPoints = new ArrayList<KeyValue<Vertex<Storage>,Double>>();
				closestPoints.stream().forEach(vertex->{
					matchedDistancesToPoints.add(new KeyValue<Vertex<Storage>, Double>(vertex, getEuclideanDistance(accessPoint.InputPoint, vertex)));
							});
					
				// take the point with the minimum distance and set it as the closest point into access point
				accessPoint.ClosestVertex = matchedDistancesToPoints.stream().min(new EuclideanDistanceComparator()).orElse(null).Key;
				return accessPoint;
			}
			
			return null;
		}
		
		/**@return returns the euclidean distance between to vertices**/
		public static double getEuclideanDistance(Vertex<Storage>v1,Vertex<Storage>v2)
		{
			double result = 0.0;
			
			Storage v1Storage = v1.Storage;
			Storage v2Storage = v2.Storage;
			
			result = Math.sqrt((	
						Math.pow((v2Storage.getLongitude() - v1Storage.getLongitude()),2.0) + 
						Math.pow((v2Storage.getLatitude() - v1Storage.getLatitude()),2.0) +
						Math.pow((v2Storage.getHeight() - v1Storage.getHeight()),2.0)));
			return result;
		}
		
		private ArrayList<Vertex<Storage>> queryDatabaseForAccessPointsByCoordinates(DatabaseConnection connection)
		{
			ArrayList<Vertex<Storage>> result = new ArrayList<Vertex<Storage>>();
			double x,y,z;
			x = this.InputPoint.Storage.getLongitude();
			y = this.InputPoint.Storage.getLatitude();
			z = this.InputPoint.Storage.getHeight();
			String query = "Select Name, ID, Longitude, Latitude, Height from Vertices"
					+ " where Longitude <=" + (x +this.IterationInterval) + "and Longitude>=" +(x-this.IterationInterval) +
						 "and Latitude <=" + (y +this.IterationInterval) + "and Latitude>=" +(y-this.IterationInterval) +
						 "and Height <=" + (z +this.IterationInterval) + "and Height>=" +(z-this.IterationInterval) + ";";
			
			ArrayList<ArrayList<String>> queryResult = null;
			try {
				queryResult = connection.executeSelectQuery(query);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
			if(queryResult != null)
			{
				queryResult.stream().forEach(row ->{					
					result.add(	new Vertex<Storage>(row.get(0), //name 
								new Storage(
										row.get(0), //name
										Integer.parseInt(row.get(1)), //id
										Double.parseDouble(row.get(2)), //longitude
										Double.parseDouble(row.get(3)), //latitude
										Double.parseDouble(row.get(4)) //height
										)));
				});
			}
			return result;			
		}
		
		public Vertex<Storage> getInputPoint()
		{
			return this.InputPoint;
		}
		
		public Vertex<Storage> getClosestPoint() {
			return this.ClosestVertex;				
		}
		
		public void setClosestPoint(Vertex<Storage> v)
		{
			this.ClosestVertex = v;
		}
		
		/**creates a new access point by the name of a particular vertex**/
		public static AccessPoint ByVertexName(String vertexName) throws AccessPointException
		{
			AccessPoint result = new AccessPoint();
			
			DatabaseConnection connection = null;
			try{
				connection = DatabaseConnection.ByUsernameAndPW("DataFinderGen", "D@t@F!nd3rG3n");
			}catch(Exception e)
			{
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
			if(connection != null)
			{
				result.ClosestVertex = result.queryDataBaseForAccessPointByVertexName(connection,vertexName);
				result.InputPoint = new Vertex<Storage>("Inputpoint", result.ClosestVertex.Storage);
			}
			return result;			
		}
		
		/**finds a vertex from the database with matches the given name
		 * @return the particular vertex **/
		private Vertex<Storage> queryDataBaseForAccessPointByVertexName(DatabaseConnection connection,String vertexName) throws AccessPointException
		{
			ArrayList<ArrayList<String>> queryResults = null;
			try {
				queryResults = connection.executeSelectQuery(
						"Select Name, ID, Longitude, Latitude, Height from Vertices where Name =" + vertexName);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
			if(!queryResults.isEmpty())
			{
				Vertex<Storage> result = new Vertex<Storage>(queryResults.get(0).get(0), new Storage
						(
								queryResults.get(0).get(0), //name
								Integer.parseInt(queryResults.get(0).get(1)), //Id
								Double.parseDouble(queryResults.get(0).get(2)), //longitude
								Double.parseDouble(queryResults.get(0).get(3)), //latitude
								Double.parseDouble(queryResults.get(0).get(4)) //height								
						));	
				return result;
			}
			else
			{
				throw new AccessPointException(0);
			}			
		}
}
