package Core;
import java.util.*;
public abstract class Graph <T>{
	protected LinkedList<Vertex<T>> vertices;
	protected LinkedList<Edge<T>> edges;
	private int verticesID = 0;
	private int edgesID= 0;
	
	//public abstract Graph byVerticesAndEdges(List<Vertex> vertices, List<Edge> edges);
	
	/**@return a list of reachable vertices from this vertex @param v is a vertex in this graph**/
	public abstract LinkedList<Vertex<T>> areReachable (Vertex<T> v);
	
	protected void insertEdges(LinkedList<Edge<T>> edges)
	{
		edges = this.removeAllParallels(edges);
		for(Edge<T> e : edges)
		{
			e.ID = edgesID;
			edgesID ++;
			this.edges.add(e);
		}
	}
	
	protected void insertVertices(LinkedList<Vertex<T>> vertices)
	{
		{
			for(Vertex<T> v:vertices)
			{
				if(!this.vertices.contains(v))
				{
					v.ID = verticesID;
					verticesID ++;
					this.vertices.add(v);
				}				
			}
		}		
	}
	
	/**An essential condition for an exact path finding is a graph without parallels **/
	protected abstract LinkedList<Edge<T>> removeAllParallels(LinkedList<Edge<T>> edges);	
	
	//this method needs an make over because remove doesn't shrink the list
	protected void removeVertex(Vertex<T> v)
	{
		this.vertices.remove(v);		
	}
	
	protected void removeEdge(Edge<T> e)
	{
		this.edges.remove(e);
	}
	
	public LinkedList<Vertex<T>> getVertices()
	{
		return this.vertices;
	}
	
	public LinkedList<Edge<T>> getEdges()
	{
		return this.edges;
	}
}
