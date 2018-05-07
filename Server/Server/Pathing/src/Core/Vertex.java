package Core;
import java.util.*;

public class Vertex {
	public int ID ;				// id in a graph. the number comes from the graph, this vertex is part of
	public Object Storage;		// to store something
	public String Name;			//a human-readable name
	public LinkedList<Edge> AdjacentEdges; //save the adjacenting edges to this vertex
	
	public Vertex(String name, Object storage)
	{
		this.Storage = storage;
		this.Name = name;
		this.AdjacentEdges = new LinkedList<Edge>();
	}
	
	public int getDegree(){
		int result = AdjacentEdges.size();		
		return result;
	}
	
	public int getInDegree()
	{
		int result = 0;
		for(Edge e: AdjacentEdges)
		{
			if(e.V == this)
				result ++;			
		}
		return result;		
	}
	/**get the outdegree of this vertex **/
	public int getOutDegree()
	{
		int result = 0;
		for(Edge e: AdjacentEdges)
		{
			if(e.U == this)
				result ++;
		}
		return result;
	}
}
