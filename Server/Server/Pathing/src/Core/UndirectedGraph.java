package Core;
import java.util.*;


public class UndirectedGraph extends Graph {

	protected UndirectedGraph(){}
	
	public UndirectedGraph(LinkedList<Vertex> vertices, LinkedList<Edge> edges){
		this.vertices = new LinkedList<Vertex>();
		this.edges = new LinkedList<Edge>();
		
		this.insertEdges(edges);
		this.insertVertices(vertices);
		
		for(Edge e:this.edges)
		{
			e.U.AdjacentEdges.add(e);
			e.V.AdjacentEdges.add(e);
		}
	}	
	
	/*@Override public UndirectedGraph byVerticesAndEdges(List<Vertex> vertices, List<Edge> edges)
	{
		UndirectedGraph result = new UndirectedGraph();
		result.insertEdges(edges);
		result.insertVertices(vertices);
		
		return result;		
	}*/
		
	@Override public LinkedList<Vertex> areReachable(Vertex v)
	{		
		LinkedList<Vertex> result = new LinkedList<Vertex>();
		int[] visited = new int[vertices.size()];
		
		for(Vertex ve: vertices)
		{
			visited[vertices.indexOf(ve)] = 0; //0 := not visited jet
		}
		
		result.add(v);
		
		while(!result.isEmpty())
		{
			Vertex pointer = result.getFirst();			
			visited[vertices.indexOf(pointer)] = 1; //1 := visited
			for(Edge e: pointer.AdjacentEdges)
			{
				if(e.U == pointer && !result.contains(e.V) && visited[vertices.indexOf(e.V)] == 0)
				{
					result.add(e.V);
					visited[vertices.indexOf(e.V)] = 1;
				}
				if(e.V == pointer && !result.contains(e.U) && visited[vertices.indexOf(e.V)] == 0)
				{
					result.add(e.U);
					visited[vertices.indexOf(e.U)] = 1;
				}
			}
			result.removeFirst();
		}		
		
		for(Vertex ver: vertices) //now rebuild, comparing to visited
		{
			if(visited[vertices.indexOf(ver)] == 1)
			{
				result.add(ver);
			}
		}
		return result;		
	}
	
	/**@param a linkedList of edges @return a linkedList of edges, without any parallels for an undirected graph**/
	@Override protected LinkedList<Edge> removeAllParallels(LinkedList<Edge> edges)
	{
		LinkedList<Edge> result = new LinkedList<Edge>();
		
		Edge pointer = edges.get(0);
		
		while(!edges.isEmpty())
		{
			boolean flag = false;
			pointer = edges.pop();
			for(Edge comparator : edges)
			{
				if(pointer.U == comparator.U && pointer.V == comparator.V 
						|| pointer.U == comparator.V && pointer.V == comparator.U) //parallel founded
				{
					flag = true;
					break;
				}		
			}	
			if(!flag)
			{
				result.add(pointer);
			}
		}
		return result;
	}
}
