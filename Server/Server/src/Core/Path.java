package Core;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

import ErrorHandling.PathingException;
import helper.OSDepPrint;

public class Path {
	protected Graph<Storage> InGraph;
	protected LinkedList<Vertex<Storage>> Vertices = new LinkedList<Vertex<Storage>>();
	protected LinkedList<Edge<Storage>> Edges = new LinkedList<Edge<Storage>>();
	protected int Distance=0;
	protected String InsertTime;
	
	/**@return returns the shortest path through a given graph, based on the shortest length. It is a necessary condition that all edges are weighted.
	 *  Uses Dijkstra's algorithm to obtain**/
	public Path (Graph<Storage> graph, Vertex<Storage> startpoint, Vertex<Storage> endpoint) throws PathingException
	{
		this.InGraph = graph;
		ArrayList <KeyValue<Vertex<Storage>, KeyValue<Vertex<Storage>,Integer>>> verticesAncestorsAndCosts = initialize(graph, startpoint);
		
		ArrayList<KeyValue<Vertex<Storage>, KeyValue<Vertex<Storage>,Integer>>> permanentMarked =
				new ArrayList<KeyValue<Vertex<Storage>, KeyValue<Vertex<Storage>,Integer>>>();
		
		PriorityQueue<KeyValue<Vertex<Storage>, KeyValue<Vertex<Storage>,Integer>>> priorityQueue =
				new PriorityQueue<KeyValue<Vertex<Storage>,KeyValue<Vertex<Storage>,Integer>>>(verticesAncestorsAndCosts.size(),new ShortestPathComparator());
		
		priorityQueue.offer(verticesAncestorsAndCosts.get(0));
		
		while(permanentMarked.size() != verticesAncestorsAndCosts.size())
		{
			KeyValue<Vertex<Storage>, KeyValue<Vertex<Storage>,Integer>> examineVertex = priorityQueue.poll();
			permanentMarked.add(examineVertex);
			LinkedList<Vertex<Storage>> adajacentVertices = examineVertex.Key.getAdjacentVertices();
			LinkedList<Vertex<Storage>> verticesInPermanentMarked = new LinkedList<Vertex<Storage>>();
			permanentMarked.stream().filter(kv -> { 
				verticesInPermanentMarked.add(kv.Key);
				return true;
			});
			LinkedList<Vertex<Storage>> adjacentVerticesWithoutPermanentMarked =
					 adajacentVertices.stream().filter(vertex -> !verticesInPermanentMarked.contains(vertex)).collect(Collectors.toCollection(LinkedList::new));
			
			//adjacentVerticesWithoutPermanentMarked.forEach(
			//		vertex -> System.out.println("for Vertex " + examineVertex.Key.Name + " is Vertex: " + vertex.Name + " in adjacent without in permanent marked"));
			
			for(Vertex<Storage>adjacentVertex : adjacentVerticesWithoutPermanentMarked)
			{
				//filter verticesAncestorsAndCosts for the current observed vertex 
				KeyValue<Vertex<Storage>, KeyValue<Vertex<Storage>,Integer>> vertexInVerticesAncestorsAndCost = 
						verticesAncestorsAndCosts.stream().filter(kv->kv.Key.equals(adjacentVertex)).findFirst().orElse(null);
				
				//there is no vertex to observe  
				if(vertexInVerticesAncestorsAndCost == null)
					continue;
				
				//initial step
				if(vertexInVerticesAncestorsAndCost.Value.Value == Integer.MAX_VALUE)
					priorityQueue.offer(vertexInVerticesAncestorsAndCost);
				
				//true if there is a edge with lesser weight
				if(decreaseDistance(examineVertex, vertexInVerticesAncestorsAndCost))
				{
					//this should decrease the element in the queue 
					priorityQueue.remove(vertexInVerticesAncestorsAndCost);
					priorityQueue.offer(vertexInVerticesAncestorsAndCost);					
				}
			}			
		}
		//now, rebuild a shortest path
		//therefore take the target out of verticesAndCosts
		KeyValue<Vertex<Storage>, KeyValue<Vertex<Storage>,Integer>> endVertex = verticesAncestorsAndCosts.stream().filter(kv -> kv.Key == endpoint).findFirst().orElse(null);		
		this.Vertices.addFirst(endVertex.Key);
		while(true)
		{		
			final KeyValue<Vertex<Storage>, KeyValue<Vertex<Storage>,Integer>> vertex =
					verticesAncestorsAndCosts.stream().filter(kv-> kv.Key == Vertices.getFirst()).findFirst().orElse(null);
			if(!this.Vertices.contains(vertex.Value.Key))
				this.Vertices.addFirst(vertex.Value.Key);
			//find the cheapest edge between vertex.Key and vertex.Value.Key (its ancestor)
			Edge<Storage> edgeBetweenThisVertexAndItsAncestor= vertex.Key.AdjacentEdges.stream().filter(edge-> 
					edge.U == vertex.Key && edge.V == vertex.Value.Key
				||	edge.V == vertex.Key && edge.U == vertex.Value.Key).min(new EdgeWeightComparator()).orElse(null);
			//if it couldn't found a cheapest edge, catch...
			try{
				this.Edges.addFirst(edgeBetweenThisVertexAndItsAncestor);				
				this.Distance += edgeBetweenThisVertexAndItsAncestor.Weight;
			}catch(Exception e){}
			
			
			//true if end is reached
			if(vertex.Value.Key == null)
			{
				//remove first elements from Vertices and Edges because the are null
				this.Edges.removeFirst();
				this.Vertices.removeFirst();
				//now, we are done
				break;
			}				
		}
	}
	
	//creates a new path following exactly the sequence of vertices and edges from the input. Necessary condition is |edges| = |vertices|-1  
	public Path(Graph<Storage> graph,LinkedList<Vertex<Storage>> vertices,LinkedList<Edge<Storage>> edges) throws PathingException
	{
		this.InGraph = graph;		
		//take those edges e's where U or V have only one adjacent edge -> this is always an start/end point
		this.Vertices = vertices;
		this.Edges = edges;
		for(Edge<Storage> edge :this.Edges)
		{
			this.Distance += edge.Weight;
		}		
	}
	
	private static ArrayList <KeyValue<Vertex<Storage>, KeyValue<Vertex<Storage>,Integer>>> initialize(Graph<Storage> graph, Vertex<Storage> startpoint)
	{		
		//KeyValue<Vertex,KeyValue<Vertex,Integer>> :=  key = vertex, value = KeyValue where key = ancestorVertex and value is cost 
		KeyValue<Vertex<Storage>,KeyValue<Vertex<Storage>, Integer>> start= 
				new KeyValue<Vertex<Storage>,KeyValue<Vertex<Storage>,Integer>>(startpoint,new KeyValue<Vertex<Storage>,Integer>(null,0));
		//
		ArrayList <KeyValue<Vertex<Storage>, KeyValue<Vertex<Storage>,Integer>>> result = 
				new ArrayList<KeyValue<Vertex<Storage>,KeyValue<Vertex<Storage>,Integer>>>();
		result.add(start);
		//set for each vertex in graph ancestor vertex = null and costs = maximum as a limit, but the start vertex 
		for(Vertex<Storage> v :graph.vertices)
		{
			if(v.equals(startpoint))
				continue;
			else
				result.add(
						new KeyValue<Vertex<Storage>, KeyValue<Vertex<Storage>,Integer>>(v, new KeyValue<Vertex<Storage>, Integer>(null, Integer.MAX_VALUE)));						
		}
		return result;
	}
	
	private static boolean decreaseDistance(KeyValue<Vertex<Storage>, KeyValue<Vertex<Storage>, Integer>> u,KeyValue<Vertex<Storage>, KeyValue<Vertex<Storage>, Integer>> v)
	{
		//find the cheapest edge between u and v
		ArrayList<Edge<Storage>> edgesBetweenUandV = new ArrayList<Edge<Storage>>();
		//to find cheapest edge, filter for those edges how are relevant 
		u.Key.AdjacentEdges.forEach(edge -> {
			//maintain purpose
			//System.out.println("edge.U is: " + edge.U.Name + " and edge.V is: " +edge.V.Name);
			if(edge.U == (u.Key) && edge.V == (v.Key))
			{				
				edgesBetweenUandV.add(edge);
			}				
			else if(edge.V == (u.Key) && edge.U == (v.Key))
			{
				edgesBetweenUandV.add(edge);
			}});
		
		Edge<Storage> cheapestEdge = edgesBetweenUandV.stream().filter(edge -> edge.Weight + u.Value.Value < v.Value.Value).min(new EdgeWeightComparator()).orElse(null);		
		
		if(cheapestEdge == null)
		{
			//there is no edge with cheaper costs between u and v
			return false;
		}
		//true if there is an edge with lesser weight and costs until u
		if(v.Value.Value > u.Value.Value + cheapestEdge.Weight)
		{
			//for maintain and debug purposes
			OSDepPrint.debug("Cheapest edge is: " + cheapestEdge.Name +" between " + u.Key.Name + " and " + v.Key.Name + " while weight is: " + cheapestEdge.Weight);
			//set new weight
			v.Value.Value = u.Value.Value + cheapestEdge.Weight;
			//set ancestor to u
			v.Value.Key = u.Key;
			return true;
		}			
		return false;		
	}
	
	public String getTime()
	{
		return this.InsertTime;
	}
	
	public void setTime(String time)
	{
		this.InsertTime = time;
	}
	
	public LinkedList<Vertex<Storage>> getVertices()
	{
		return this.Vertices;
	}
	
	public LinkedList<Edge<Storage>> getEdges()
	{
		return this.Edges;
	}
}
