package PathingInterface;
import java.sql.SQLException;
import java.util.*;

import com.mysql.cj.xdevapi.Result;

import Core.*;
import DatabaseConnection.DatabaseConnection;
import DatabaseConnection.DatabaseConnectionMongoDB;
import ErrorHandling.*;
import ExternalReference.PictureReference;
import ExternalReference.ReferenceObject;
import ExternalReference.TextReference;
import helper.OSDepPrint;
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
		//UndirectedGraph<Storage> clusterGraph = clusterGraph(graph,throughThisPoints);	
		//get shortest path through points
		//map each accessPoint to a current graph. use closestPoints -> the are part of the graph
		throughThisPoints.forEach(accessPoint->{ 
			accessPoint.getClosestPoint().ID = graph.getVertices().stream().filter(vertex->vertex.Storage.getID()==
					accessPoint.getClosestPoint().Storage.getID()).findFirst().get().ID;
			accessPoint.setClosestPoint(graph.getVertices().stream().filter(vertex->vertex.Storage.getID()==
					accessPoint.getClosestPoint().Storage.getID()).findFirst().get());
			});
		
		ArrayList<Path> shortestPaths = new ArrayList<Path>();
		
		for(int i = 0; i<throughThisPoints.size()-1;i++)
		{
			// make n/2 times a new path, because partly shortest paths are shortest paths			
			shortestPaths.add(new Path(graph,throughThisPoints.get(i).getClosestPoint(),throughThisPoints.get(i+1).getClosestPoint()));
		}
		LinkedList<Vertex<Storage>> vertices = new LinkedList<Vertex<Storage>>();
		LinkedList<Edge<Storage>> edges = new LinkedList<Edge<Storage>>(); 
				
		shortestPaths.stream().forEach(path->{
			path.getEdges().forEach(e->System.out.println("from " +e.U.Name + " to " + e.V.Name +" via " + e.Name));
			
			path.getVertices().stream().forEach(vertex->{
				if(!vertices.contains(vertex))
				{
					vertices.add(vertex);
				}}); //end of vertex lambda
			path.getEdges().stream().forEach(edge-> {
				if(!edges.contains(edge))
				{
					edges.add(edge);
				}				
			});	//end of edge lambda
			});		//end of path lambda	
				
		//now result contains the shortest path through the input points 
		result.add(new Path(graph,vertices,edges));
		//add an entry into db for that route
		/*for(Path p: shortestPaths)
		{
			notifyPathInDB(con,p,userID);
		}*/
		notifyPathInDB(con, result.get(0), userID);
		
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
				" uv.V, v.Name as VName, v.Longitude as VLon, v.Latitude as VLat, v.Height as VHei,"+
				//Edge(10), WeightDifficulty(11) //has no use, MatchID(12), EdgeName(13),InsertTime(14), RouteID (15)
				" uv.Edge,e.WeightDifficulty, uv.MatchID, e.Name as EdgeName, t3.InsertTime , t3.IDOfRoute "
				+"from (select t2.IDOfRoute, c.EdgeID, t2.InsertTime from "+ 
				"(select r.ID as IDOfRoute, r.IDOfUser, t1.UserID,r.InsertTime from  "
				+ "(select UserID, ID as IDOfUser from PathfinderUser where UserID = '"+userID+"') t1,"+
				"Routes r where t1.IDOfUser = r.IDOfUser)t2, Contains c where c.RouteID =t2.IDOfRoute)t3,"+
				"Vertices u, Vertices v, Edges e, UVToEdge uv"+ 
				" where t3.EdgeID = uv.MatchID and uv.U = u.ID and uv.V = v.ID and uv.Edge = e.ID Order By t3.IDOfRoute;";
				     
		ArrayList<ArrayList<String>> queryResults = connection.executeSelectQuery(query);
		ArrayList<UndirectedGraph<Storage>> graphs = new ArrayList<UndirectedGraph<Storage>>();
		
		//if a user has no routes, the query result will be empty
		if(queryResults.isEmpty())
		{
			return null;
		}
		
		//make a new graph for every different route id and add it to graphs
		for(int i = 1; i<queryResults.size()||!queryResults.isEmpty(); i++)
			
		{	//take the last element of row i and compare it with the last element of row i-1  (size()-2 is last interesting element)
			String a = queryResults.get(i).get(queryResults.get(i).size()-2);
			String b= queryResults.get(i-1).get(queryResults.get(i-1).size()-2);
			if(!a.equals(b) || 
					queryResults.size()-1 == i ) //the last entries 
			{//if the are different -> different routes detected -> rebuild path
				//split queryResult and make new graph out of the first list
				List<ArrayList<String>> sublist = queryResults.subList(0, i);
				ArrayList<ArrayList<String>> sublistAsArrayList = new ArrayList<ArrayList<String>>();
				sublist.forEach(list->{
					sublistAsArrayList.add(list);
				});
				
				UndirectedGraph<Storage> graph = byQueryResults(sublistAsArrayList);
				LinkedList<Vertex<Storage>> vertices = graph.getVertices();
				LinkedList<Edge<Storage>> edges = graph.getEdges();
				
				graphs.add(graph);
				Path p = new Path(graph,vertices,edges);
				p.setTime(queryResults.get(0).get(queryResults.get(0).size()-3));
				result.add(p);
				//reduce list if i is not the same like size
				if(i==queryResults.size()-1)
				{
					break;
				}
				else
				{
					//remove the rebuild route from the list 
					for(int j=0;j<i;j++)
					{
						//pop head
						queryResults.remove(0);
					}
					//set i = 0 -> next loop it is i=1
					i=0;
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
					Double.parseDouble(verticeEdgeTable.get(3)))		//height
					);	
			
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
				edge.setMatchID(Integer.parseInt(verticeEdgeTable.get(11)));
				edges.add(edge);				
			} catch (PathingException e) {
				e.printStackTrace();
			}		
			});//end of lambda
			
		UndirectedGraph<Storage> result = new UndirectedGraph<>(vertices, edges);
		return result;
	}
	
	private static boolean notifyPathInDB(DatabaseConnection connection,Path pathToNotify, String userID) throws SQLException
	{
		try{
		//make a new entry in table Routes
		String query = "insert into Routes(IDOfUser,Name,InsertTime) values((select pu.ID from PathfinderUser pu where pu.UserID ='" + userID +"'),'isRoute',Now());";
		OSDepPrint.debug(query);
		connection.executeTransactionQuery(query);
		query = "Select r.ID from Routes r, PathfinderUser pu where pu.UserID ='"+userID+"' order by r.ID;";
		OSDepPrint.debug(query);
		//the last row is the id of the added route
		ArrayList<ArrayList<String>> idsOfRoutes = connection.executeSelectQuery(query);
		LinkedList<String> range = new LinkedList<String>(); 
		pathToNotify.getEdges().stream().forEach(edge->{
			range.add(""+edge.getMatchID());			
		});
		String idRange= "("+String.join(",", range) +")";
		query = "insert into Contains(RouteID,EdgeID) select r.ID, uv.MatchID from Routes r, UVToEdge uv where r.ID =" +idsOfRoutes.get(idsOfRoutes.size()-1).get(0) +
				" and uv.MatchID in " +idRange+";";
		OSDepPrint.debug(query);
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
			try{
				ArrayList<String> informationToThatVertex = queryAdditionalInformationByVertexID(vertex.Storage.getID());
				informationToThatVertex.forEach(infoString->{
					ReferenceObject refObject = analyzeInfo(infoString);
					if(refObject != null)
						vertex.Storage.addAdditionalInformation(refObject);
				});				
			}
			catch(Exception e){
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
			
		});
		return path;
	}
	
	private static ArrayList<String> queryAdditionalInformationByVertexID(int idOfVertexInDB) throws Exception
	{
		ArrayList<String>result = new ArrayList<String>();
		DatabaseConnection connection = DatabaseConnection.ByUsernameAndPW("DataFinderGen", "D@t@F!nd3rG3n");
		DatabaseConnectionMongoDB mongoDbConn = new DatabaseConnectionMongoDB("DataFinderGen", "D@t@F!nd3rG3n");
		
		String query =  "select i.LinkIntoMongo as Link, mp.InformationID from Information i, MappedTo mp "
				+ "where mp.VertexID = "+ idOfVertexInDB  +" and mp.InformationID = i.ID;" ;
		ArrayList<ArrayList<String>> queryResultsFromRNDB=connection.executeSelectQuery(query);
		queryResultsFromRNDB.forEach(line->{
			String [] newQueryArray = line.get(0).split(" : ");
			String queryResultFromNoSQL= mongoDbConn.queryToDB(newQueryArray[0], Integer.parseInt(newQueryArray[1]));
			if(queryResultFromNoSQL != "no such element")
			{
				result.add(queryResultFromNoSQL);
			}
		});
		
		return result;
	}
	
	/**this method checks if a userID exists in the db**/
	public static boolean checkValidUserID(String userIDToCheckFor) throws Exception
	{
		DatabaseConnection connection = DatabaseConnection.ByUsernameAndPW("DataFinderGen", "D@t@F!nd3rG3n");
		
		String query = "Select Distinct UserID from PathfinderUser where UserID='" + userIDToCheckFor +"';";
		System.out.println(query);
		ArrayList<ArrayList<String>> queryResult=connection.executeSelectQuery(query);
		
		//userId doesn't exists
		if(queryResult.isEmpty())
		{
			return false;
		}
		//userId doesn't exists
		if(queryResult.get(0).isEmpty())
		{
			return false;
		}	
		//userId exists
		else if(queryResult.get(0).get(0)==userIDToCheckFor){			
			return true;
		}
		return false;
			
	}
	
	//**this method creates a new user into the db**//
	public static boolean notifyUserIDIntoDB(String userIDToAdd) throws Exception
	{
		if(!checkValidUserID(userIDToAdd))
		{
			DatabaseConnection connection = DatabaseConnection.ByUsernameAndPW("DataFinderGen", "D@t@F!nd3rG3n");
			String query ="insert ignore into PathfinderUser(UserID,InsertTime) values('"+userIDToAdd+"',Now());";
			System.out.println(query);
			connection.executeTransactionQuery(query);
			return true;
		}
		else
			return false;		
	}
	
	private static ReferenceObject analyzeInfo(String infoString)
	{
		ReferenceObject result = null; 
		
		// infoString looks like this {"_id" : 34, "pic": 5, "description": "non"}
		//or like {"_id":1 , "info": "testinfo"}
		char[]tempCharArray=infoString.toCharArray();
		LinkedList<String> infoFields = new LinkedList<String>();
		String aInfoField ="";
		
		for(int i = 0; i<tempCharArray.length-1;i++)
		{	
			char c = tempCharArray[i];
			if(c=='{'||c=='"'|| c=='}')
				continue;
			// read . means go two steps ahead
			else if(c=='.')
				i++;
			//new field reached
			else if(c==',')
			{
				infoFields.add(aInfoField);
				aInfoField ="";
			}
			else
				aInfoField += c;
		}
		infoFields.add(aInfoField);
		String flag = infoFields.get(1).split(" : ")[0];
		
		switch(flag)
		{		
			case " pic":					
				result = new PictureReference(
						Integer.parseInt( infoFields.get(1).split(":")[1].split(" ")[1]), 
						Integer.parseInt(infoFields.get(0).split(":")[1].split(" ")[1]),
						infoFields.get(2).split(": ")[1]);
				break;
			case " info":
				result = new TextReference(
						Integer.parseInt(infoFields.get(1).split(":")[1].split(" ")[1]),
						Integer.parseInt(infoFields.get(0).split(":")[1].split(" ")[1]));
				break;
			default:
				OSDepPrint.debug("couldn't interpret db entry");
		}
		
		return result;		
	}
	
}
