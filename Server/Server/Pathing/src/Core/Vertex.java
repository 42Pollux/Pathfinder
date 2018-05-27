package Core;
import java.util.*;

public class Vertex <T>{
	public int ID ;				// id in a graph. the number comes from the graph, this vertex is part of
	public T Storage;		// to store something
	public String Name;			//a human-readable name
	protected LinkedList<Edge<T>> AdjacentEdges; //save the adjacent edges to this vertex
	
	public Vertex(String name, T storage)
	{
		this.Storage = storage;
		this.Name = name;
		this.AdjacentEdges = new LinkedList<Edge<T>>();
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
		for(Edge<T> e: AdjacentEdges)
		{
			if(e.U == this)
				result ++;
		}
		return result;
	}
	
	/**@return a list of vertices which are adjacent to this vertex**/
	public LinkedList<Vertex<T>> getAdjacentVertices ()
	{
		LinkedList<Vertex<T>> result = new LinkedList<Vertex<T>>();
		for(Edge<T> edge: AdjacentEdges)
		{
			if(edge.U == this)
			{
				result.add(edge.V);
				continue;
			}
			else
			{
				result.add(edge.U);
			}			
		}
		return result;
	}
	
	public LinkedList<Edge<T>> getEdges()
	{
		return this.AdjacentEdges;		
	}
}
