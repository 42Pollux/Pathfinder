package Core;
import java.util.*;

public class MinimalSpanningTree extends UndirectedGraph{
	
	private MinimalSpanningTree()
	{
		
	}
	
	public static MinimalSpanningTree byPrim(UndirectedGraph graph)
	{
		MinimalSpanningTree result = new MinimalSpanningTree();		
		Vertex vertexPointer = graph.vertices.get(new Random().nextInt(graph.vertices.size())); //algorithm starts with any vertex in the graph
		//implement a comparator class, which compares the different weights for a vertex to reach 
		PriorityQueue <Vertex> prioQueue = new PriorityQueue <Vertex>(graph.vertices.size());		
		for(Vertex v: graph.vertices)
		{
			prioQueue.add(v);
		}
		prioQueue.size();
		return result;
	}
	
	public static Edge getCheapestEdge (LinkedList<Edge> edges)
	{
		Edge result = edges.getFirst();
		
		for(Edge comparator: edges)
		{
			if(result.Weight > comparator.Weight)
				result = comparator;
		}
		return result;
	}
	
	public static KeyValue reachVertexThroughCheapestEdge(Vertex v,LinkedList<Edge> adjacentEdges)
	{
		KeyValue result = new KeyValue(v,getCheapestEdge(adjacentEdges));
		return result;
	}

}
