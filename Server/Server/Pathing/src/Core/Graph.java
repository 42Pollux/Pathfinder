package Core;
import java.util.*;
public abstract class Graph {
	protected LinkedList<Vertex> vertices;
	protected LinkedList<Edge> edges;
	protected static int verticesID = 0;
	protected static int edgesID= 0;
	
	//public abstract Graph byVerticesAndEdges(List<Vertex> vertices, List<Edge> edges);
	
	/**@return a list of reachable vertices from this vertex @param v is a vertex in this graph**/
	public abstract LinkedList<Vertex> areReachable (Vertex v);
	
	protected void insertEdges(LinkedList<Edge> edges)
	{
		edges = this.removeAllParallels(edges);
		for(Edge e : edges)
		{
			e.ID = edgesID;
			edgesID ++;
			this.edges.add(e);
		}
	}
	
	protected void insertVertices(LinkedList<Vertex> vertices)
	{
		{
			for(Vertex v:vertices)
			{
				v.ID = verticesID;
				verticesID ++;
				this.vertices.add(v);
			}
		}		
	}
	
	/**An essential condition for an exact path finding is a graph without parallels **/
	protected abstract LinkedList<Edge> removeAllParallels(LinkedList<Edge> edges);	
	
	//this method needs an make over because remove doesn't shrink the list
	protected void removeVertex(Vertex v)
	{
		this.vertices.remove(v);		
	}
	
	protected void removeEdge(Edge e)
	{
		this.edges.remove(e);
	}
}
