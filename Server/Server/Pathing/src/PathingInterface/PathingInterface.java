package PathingInterface;
import java.sql.SQLException;
import java.util.*;
import Core.*;
import DatabaseConnection.DatabaseConnection;
import ErrorHandling.*;
import Access.*;

/**This class provides the hole interaction between the pathing module and the server.**/
public class PathingInterface {
	private PathingInterface(){}
	
	/**this method returns new routes bases @
	 * @throws Exception **/
	public static ArrayList<Path> getNewRoutes(ArrayList<ArrayList<String>> pathThroughPoints, String userID) throws Exception
	{
		if(pathThroughPoints.size() <= 1)
			throw new PathingInterfaceException(0);
		
		ArrayList<Path> result = new ArrayList<Path>();
		ArrayList<AccessPoint> throughThisPoints = new ArrayList<AccessPoint>();
		//for each pointDescription in pathThroughPoints make an analysis and add it 
		pathThroughPoints.stream().forEach(pointList->{
			try {
				throughThisPoints.add(analyzeInput(pointList));
			} catch (Exception e) {}});
		
		//now find a graph which contains all points from throughThisPoints
		//need the transitive closure for all vertices in throughThisPoints  
		DatabaseConnection con = DatabaseConnection.ByUsernameAndPW("DataFinderGen", "D@t@F!nd3rG3n");
		String query = "";
		//look for difficulty
		switch (pathThroughPoints.get(0).get(pathThroughPoints.get(0).size()-1))
		{
			//
			case "WeightDifficulty":
						//UName (0), ULon(1),ULat(2),UHeight(3),U(4)
				query = "Select u.Name as UName, u.Longitude as ULon,u.Latitude as ULat ,u.Height as UHeight, uv.U,"
						//V(5), VName(6),VLon(7), VLat(8), VHeight(9)
						+ " uv.V,v.Name as VName, v.Longitude as VLon,v.Latitude as VLat,v.Height as VHeight,"
						//Edge(10), WeightDifficulty(11), MatchID(12), EdgeName(13)
						+ " uv.Edge, e.WeightDifficulty, uv.MatchID, e.Name as EdgeName"
						+ " from UVToEdge uv, Vertices u, Vertices v, Edges e "
						+ "where u.ID = uv.U and v.ID = uv.V and uv.Edge = e.ID;";
						break;
			case "WeightTime":
						//UName (0), ULon(1),ULat(2),UHeight(3),U(4)
				query= "Select u.Name as UName, u.Longitude as ULon,u.Latitude as ULat ,u.Height as UHeight, uv.U,"
						//V(5), VName(6),VLon(7), VLat(8), VHeight(9)
						+ " uv.V, v.Name as VName, v.Longitude as VLon,v.Latitude as VLat,v.Height as VHeight,"
						//Edge(10), WeightTime(11), MatchID(12), EdgeName(13)
						+ " uv.Edge, e.WeightTime,uv.MatchID, e.Name as EdgeName"
						+ " from UVToEdge uv, Vertices u, Vertices v, Edges e"
						+ " where u.ID = uv.U and v.ID = uv.V and uv.Edge = e.ID;";
				break;				
		}
		//than make a graph
		ArrayList<ArrayList<String>> transitiveClosureQueryResults = con.executeSelectQuery(query);
		//this is the hole database graph TODO: write an sql-procedure to find the transitive closure for a particular input vertex
		UndirectedGraph<Storage> graph = byQueryResults(transitiveClosureQueryResults);
		//and check if all points from the input are in this graph
		UndirectedGraph<Storage> clusterGraph = clusterGraph(graph,throughThisPoints);	
		//get shortest path through points
		ArrayList<Path> shortestPaths = new ArrayList<Path>();
		
		for(int i = 0; i<throughThisPoints.size()-1;i++)
		{
			// make n/2 times a new path, because partly shortest paths are shortest paths
			shortestPaths.add(new Path(clusterGraph,throughThisPoints.get(i).getClosestPoint(),throughThisPoints.get(i+1).getClosestPoint()));
		}
		LinkedList<Vertex<Storage>> vertices = new LinkedList<Vertex<Storage>>();
		LinkedList<Edge<Storage>> edges = new LinkedList<Edge<Storage>>(); 
				
		shortestPaths.stream().forEach(path->{
			try{
				//do an induction-> take always the last vertex out to avoid duplicates
				//in try catch block, because of the NoSuchElementException for the first step
				vertices.removeLast();
			}catch(Exception e)
			{
				
			}			
			vertices.addAll(path.getVertices());
			edges.addAll(path.getEdges());
		});
		//now result contains the shortest path through the input points 
		result.add(new Path(clusterGraph,vertices,edges));
		//add an entry into db for that route
		notifyPathInDB(con,result.get(0),userID);
		
		return result;
		
	}
	
	//** this method provides access to a list of paths which are stored in the database for the given userID**//
	public static ArrayList<Path> getStoredRoutes (String userID) throws Exception
	{
		ArrayList<Path> result = new ArrayList<Path>();
		DatabaseConnection connection = DatabaseConnection.ByUsernameAndPW("DataFinderGen", "D@t@F!nd3rG3n");
					//	UName (0), ULon(1),ULat(2),UHeight(3),U(4)
		String query = "Select u.Name as UName, u.Longitude as ULon, u.Latitude as ULat, u.Height as UHei, uv.U,"+
				//V(5), VName(6),VLon(7), VLat(8), VHeight(9)
				"uv.V, v.Name as VName, v.Longitude as VLon,v.Latitude as VLat, v.Height as VHei,"+
				//Edge(10), WeightDifficulty(11) //has no use, MatchID(12), EdgeName(13), RouteID (14)
				"uv.Edge, e.WeightDifficulty,uv.MatchID, e.Name as EdgeName "
				+"from UVToEdge uv,"+ 
				"(Select c.RouteID,c.EdgeID from (Select us.ID as usID, rou.ID as rouID, rou.IDOfUser  from PathfinderUser us, Routes rou where us.UserID = '"+userID+"') t1,"+
				"Contains c where t1.rouID = c.RouteID)t2,"+
				"Vertices u, Vertices v, Edges e "+ 
				"where t2.EdgeID = uv.MatchID and uv.U = u.ID and uv.V = v.ID and uv.Edge = e.ID ;";
		ArrayList<ArrayList<String>> queryResults = connection.executeSelectQuery(query);
		ArrayList<UndirectedGraph<Storage>> graphs = new ArrayList<UndirectedGraph<Storage>>();
		
		//make a new graph for every different route id and add it to graphs
		for(int i = 1; i<queryResults.size()||!queryResults.isEmpty(); i++)
		{
			if(queryResults.get(i).get(queryResults.get(i-1).size()-1) != queryResults.get(i-1).get(queryResults.get(i-1).size()-1)
					||queryResults.size()-1 == i) //is true if the there is only only route in this queryResult
			{
				//split queryResult and make new graph out of the first list
				List<ArrayList<String>> sublist = queryResults.subList(0, i+1);
				ArrayList<ArrayList<String>> sublistAsArrayList = new ArrayList<ArrayList<String>>();
				sublist.forEach(list->{
					sublistAsArrayList.add(list);
				});
				
				UndirectedGraph<Storage> graph = byQueryResults(sublistAsArrayList);
				LinkedList<Vertex<Storage>> vertices = graph.getVertices();
				LinkedList<Edge<Storage>> edges = graph.getEdges();				
				graphs.add(graph);
				result.add(new Path(graph,vertices,edges));
				//reduce list if i is not the same like size
				if(i==queryResults.size()-1)
				{
					break;
				}
				else
				{
					for(int j=0;j<i;j++)
					{
						queryResults.remove(j);
					}
					i=1;
				}				
			}			
		}
		return result;
	}
	
	private static AccessPoint analyzeInput(ArrayList<String> pointDescription) throws AccessPointException, PathingException
	{
		AccessPoint result = null;
		
		//the size of pointDescription gives a hint, how the point is described
		switch(pointDescription.size())
		{
			// there is just a name given and a difficulty
			case 2:
				result = AccessPoint.ByVertexName(pointDescription.get(0));
			//there are coordinates given and a difficulty
			case 4:
				result = AccessPoint.ByGUIInput(
						Double.parseDouble(pointDescription.get(0)), //longitude
						Double.parseDouble(pointDescription.get(1)), //latitude
						Double.parseDouble(pointDescription.get(2))); //height/ elevation				
		}
		return result;
	}
	
	/**this method creates a new undirected graph by a list of lists of strings. The strings are query results from a databases, which is a description of vertices and edges**/
	private static UndirectedGraph<Storage> byQueryResults(ArrayList<ArrayList<String>> queryResults) throws Exception
	{
		LinkedList<Vertex<Storage>> vertices = new LinkedList<Vertex<Storage>>();
		LinkedList<Edge<Storage>> edges = new LinkedList<Edge<Storage>>();
		
		queryResults.stream().forEach(verticeEdgeTable ->{
			Vertex<Storage> u = new Vertex<Storage>(verticeEdgeTable.get(0), new Storage(
					verticeEdgeTable.get(0),							//vertex name
					Integer.parseInt(verticeEdgeTable.get(4)),		//U is id from db			
					Double.parseDouble(verticeEdgeTable.get(1)),		//longitude 
					Double.parseDouble(verticeEdgeTable.get(2)),		//latitude
					Double.parseDouble(verticeEdgeTable.get(3))));	//height
			
			//there is no vertex in vertices where the id is the same like u's
			if(vertices.stream().filter(vertex->vertex.Storage.getID() == u.Storage.getID()).findFirst().orElse(null)==null)
				vertices.add(u);
			
			Vertex<Storage> v = new Vertex<Storage>(verticeEdgeTable.get(6), new Storage(
					verticeEdgeTable.get(6),							//vertex name
					Integer.parseInt(verticeEdgeTable.get(5)),		//V is id from db			
					Double.parseDouble(verticeEdgeTable.get(7)),		//longitude 
					Double.parseDouble(verticeEdgeTable.get(8)),		//latitude
					Double.parseDouble(verticeEdgeTable.get(9))));	//height
			
			// there is no vertex in vertices where the id is the same like v's
			if(vertices.stream().filter(vertex->vertex.Storage.getID() == v.Storage.getID()).findFirst().orElse(null)==null)
				vertices.add(v);			
						
			Edge<Storage> edge;
			try {
				//couldn't create edge with(u,v,storage) because there was the oddest site effect I ever saw
				edge = new Edge<Storage>(vertices.stream().filter(vertex->
					vertex.Storage.getID() == u.Storage.getID()
						).findFirst().get(), //get vertex u out of vertices
						vertices.stream().filter(vertex->vertex.Storage.getID() == v.Storage.getID()
						).findFirst().get() //get vertex v out of vertices 
				, verticeEdgeTable.get(13), Integer.parseInt(verticeEdgeTable.get(11)));
				edge.setMatchID(Integer.parseInt(verticeEdgeTable.get(12)));
				edges.add(edge);				
			} catch (PathingException e) {
				e.printStackTrace();
			}		
			});//end of lambda
			
		UndirectedGraph<Storage> result = new UndirectedGraph<>(vertices, edges);
		return result;
	}
	
	/**this method finds a cluster (subgraph) in which all inputPoints are reachable. Initially unreachable InputPoints will be mapped into the cluster. **/
	private static UndirectedGraph<Storage> clusterGraph(UndirectedGraph<Storage> graph,ArrayList<AccessPoint> inputPoints) 
	{		
		if(graph.getVertices().isEmpty()|| graph.getEdges().isEmpty())
			return null;
		
		//get the cluster of the start point
		LinkedList<Vertex<Storage>> startCluster = graph.areReachable(
				//this obtains that we find literally the same object 
				graph.getVertices().stream().filter(vertex->vertex.Storage.getID() == inputPoints.get(0).getClosestPoint().
				Storage.getID()).findFirst().get()				
				);	
				
		inputPoints.stream().forEach(accessPoint->{	
			//if closestPoint is not in the cluster search for another point with the shortest euclidean distance (to closestPoint) within the cluster
			if(!startCluster.contains(accessPoint.getClosestPoint()))			
			{
				//map the euclidean distance to every point in the cluster and the current closestPoint
				ArrayList<KeyValue<Vertex<Storage>,Double>> mappedDistances = new ArrayList<KeyValue<Vertex<Storage>,Double>>();
				startCluster.stream().forEach(vertex->{
					mappedDistances.add(new KeyValue<Vertex<Storage>, Double>(vertex, AccessPoint.getEuclideanDistance(vertex, accessPoint.getClosestPoint())));
				});
				Vertex<Storage> resetClosestPoint = mappedDistances.stream().min(new EuclideanDistanceComparator()).orElse(null).Key;
				System.out.println("Your input point:"+accessPoint.getInputPoint().Name +" has no connection to your startpoint (according to our database).\n"
						+ "So we remapped this point to a point within our database");
				accessPoint.setClosestPoint(resetClosestPoint);
			}				
		}); 
		
		LinkedList<Edge<Storage>> edges = new LinkedList<Edge<Storage>>();
		startCluster.stream().forEach(vertex ->{
			LinkedList<Edge<Storage>> tempEdge = vertex.getEdges();
			tempEdge.forEach(edge->{
				if(!edges.contains(edge))
				{
					edges.add(edge);
				}
			});
		});
		//reset edges and vertices graph id back to = -1
		startCluster.forEach(vertex->{
			vertex.ID =-1;
		});
		edges.forEach(edge->{
			edge.ID =-1;
		});
		
		UndirectedGraph<Storage> result = new UndirectedGraph<Storage>(startCluster, edges);
		return result;
	}
	
	private static boolean notifyPathInDB(DatabaseConnection connection,Path pathToNotify, String userID) throws SQLException
	{
		try{
		//make a new entry in table Routes
		String query = "insert into Routes(IDOfUser,Name,InsertTime) values((select pu.ID from PathfinderUser pu where pu.UserID ='" + userID +"'),'isRoute',Now());";		
		connection.executeTransactionQuery(query);
		query = "Select r.ID from Routes r, PathfinderUser pu where pu.UserID ='"+userID+"' order by r.ID;";
		//the last row is the id of the added route
		ArrayList<ArrayList<String>> idsOfRoutes = connection.executeSelectQuery(query);
		LinkedList<String> range = new LinkedList<String>(); 
		pathToNotify.getEdges().stream().forEach(edge->{
			range.add(""+edge.getMatchID());
		});
		String idRange= "("+String.join(",", range) +")";
		query = "insert into Contains(RouteID,EdgeID) select r.ID, uv.MatchID from Routes r, UVToEdge uv where r.ID =" +idsOfRoutes.get(idsOfRoutes.size()-1).get(0) +
				" and uv.MatchID in " +idRange+";";
		connection.executeTransactionQuery(query);
		return true;
		}catch(Exception e){
			return false;
		}
	}	
	
	//**this method maps additional information from the NoSQL database to the vertices of a given path**//
	public static Path getFurtherInformations(Path path)
	{
		path.getVertices().forEach(vertex->
		{
			ArrayList<String> informationToThatVertex = queryAdditionalInformationByVertexID(vertex.Storage.getID());
			informationToThatVertex.forEach(info-> vertex.Storage.addAdditionalInformation(info));			
		});
		return path;
	}
	
	private static ArrayList<String> queryAdditionalInformationByVertexID(int idOfVertexInDB)
	{
		ArrayList<String>result = new ArrayList<String>();
		
		return result;
	}
}
