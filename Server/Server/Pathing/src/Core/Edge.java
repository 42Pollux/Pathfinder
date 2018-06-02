package Core;
import ErrorHandling.PathingException;

public class Edge<T> {
	public Vertex<T> U;
	public Vertex<T> V;
	public String Name;
	public int Weight;
	public int ID = -1; //Initially -1 to flag that this edge is not part of a graph
	private int MatchID; //store the database matchID for an edge to identify the route
	
	public Edge(Vertex<T> u, Vertex<T> v, String name, int weight) throws PathingException
	{
		if(u == v)
		{			
			throw new ErrorHandling.PathingException(1);
		}
		else
		{
			this.U = u;
			this.V = v;
			this.Name = name;
			this.Weight = weight;	
		}		
	}
	
	public int getMatchID()
	{
		return this.MatchID;
	}
	
	public void setMatchID(int matchID)
	{
		this.MatchID = matchID;
	}
	
	
}
