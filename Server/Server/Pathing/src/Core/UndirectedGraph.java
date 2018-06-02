package Core;
import java.util.*;


public class UndirectedGraph <T> extends Graph <T> {

	protected UndirectedGraph(){}
	
	public UndirectedGraph (LinkedList<Vertex<T>> vertices, LinkedList<Edge<T>> edges){
		this.vertices = new LinkedList<Vertex<T>>();
		this.edges = new LinkedList<Edge<T>>();
		
		this.insertVertices(vertices);
		this.insertEdges(edges);
				
		for(Edge<T> e:this.edges)
		{
			Vertex<T>u=this.vertices.stream().filter(vertex->vertex.ID==e.U.ID).findFirst().get();
			Vertex<T>v=this.vertices.stream().filter(vertex->vertex.ID==e.V.ID).findFirst().get();
			u.AdjacentEdges.add(e);
			v.AdjacentEdges.add(e);
		}
	}	
	
	/*@Override public UndirectedGraph byVerticesAndEdges(List<Vertex> vertices, List<Edge> edges)
	{
		UndirectedGraph result = new UndirectedGraph();
		result.insertEdges(edges);
		result.insertVertices(vertices);
		
		return result;		
	}*/
		
	@Override public LinkedList<Vertex<T>> areReachable(Vertex<T> v)
	{		
		LinkedList<Vertex<T>> result = new LinkedList<Vertex<T>>();
		int[] visited = new int[vertices.size()];
		
		for(Vertex<T> ve: vertices)
		{
			visited[vertices.indexOf(ve)] = 0; //0 := not visited jet
		}
		
		result.add(v);
		
		while(!result.isEmpty())
		{
			Vertex<T> pointer = result.getFirst();		
			
			visited[vertices.indexOf(pointer)] = 1; //1 := visited			
			for(Edge<T> e: pointer.AdjacentEdges)
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
		
		for(Vertex<T> ver: vertices) //now rebuild, comparing to visited
		{
			if(visited[vertices.indexOf(ver)] == 1)
			{
				result.add(ver);
			}
		}
		return result;		
	}
	
	/**@param a linkedList of edges @return a linkedList of edges, without any parallels for an undirected graph**/
	@Override protected LinkedList<Edge<T>> removeAllParallels(LinkedList<Edge<T>> edges)
	{
		LinkedList<Edge<T>> result = new LinkedList<Edge<T>>();
		
		Edge<T> pointer = edges.get(0);
		
		while(!edges.isEmpty())
		{
			boolean flag = false;
			pointer = edges.pop();
			for(Edge<T> comparator : edges)
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
