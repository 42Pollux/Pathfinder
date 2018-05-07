package Core;
import java.util.*;
import ErrorHandling.PathingException;

public class Path {
	protected Graph InGraph;
	public LinkedList<Vertex> Vertices;
	public LinkedList<Edge> Edges;
	protected int Distance;
	
	private Path()
	{
		
	}
	
	public static Path ByGraphVerticesAndEdges(Graph graph, LinkedList<Vertex> vertices,LinkedList<Edge> edges )
	{
		return null;
	}
	
	/**@return returns the shortest path through a given graph, based on the shortest length. It is a necessary condition that all edges are weighted.
	 *  Uses Dijkstra algorithm to obtain**/
	public static Path ShortestPathByGraphAndVertices(Graph graph, Vertex startpoint, Vertex endpoint) throws PathingException
	{
		LinkedList<Vertex> areReachable = graph.areReachable(startpoint);
		if(!areReachable.contains(endpoint))
		{
			throw new ErrorHandling.PathingException(2);
		}
	}
}
