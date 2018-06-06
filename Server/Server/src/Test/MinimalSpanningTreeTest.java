package Test;
import java.util.*;
import org.junit.Test;
import Core.*;
import ErrorHandling.PathingException;

public class MinimalSpanningTreeTest {

	@Test
	public void TestCheapestEdge() throws PathingException
	{
		LinkedList<Edge> edges = new LinkedList<Edge>();
		
		for(int i = 10; i > 0; i--)
		{
			Vertex u = new Vertex("u"+i,null);
			Vertex v = new Vertex("v"+i,null);
			
			Edge e = new Edge(u,v,"e" +i,i);
			edges.add(e);
		}
		
		Edge cheapestEdge = MinimalSpanningTree.getCheapestEdge(edges);
		
		if(cheapestEdge.Weight != 10)
			System.out.println("Error");		
	}
	
	@Test 
	public void TestMinimalSpanningTreeByPrim() throws PathingException
	{
		LinkedList<Vertex> vertices = new LinkedList<Vertex>();
		LinkedList<Edge> edges = new LinkedList<Edge>();
		
		for(int i = 1; i<=7;i++)
		{
			Vertex v = new Vertex("v"+i,null);
			vertices.add(v);
		}
		
		edges.add(new Edge(vertices.get(0),vertices.get(1),"e1",2));
		edges.add(new Edge(vertices.get(0),vertices.get(4),"e2",5));
		edges.add(new Edge(vertices.get(1),vertices.get(2),"e3",2));
		edges.add(new Edge(vertices.get(1),vertices.get(3),"e4",3));
		edges.add(new Edge(vertices.get(3),vertices.get(4),"e5",10));
		edges.add(new Edge(vertices.get(2),vertices.get(6),"e6",8));
		edges.add(new Edge(vertices.get(3),vertices.get(6),"e7",5));
		edges.add(new Edge(vertices.get(3),vertices.get(5),"e8",9));
		edges.add(new Edge(vertices.get(4),vertices.get(5),"e9",4));
		edges.add(new Edge(vertices.get(5),vertices.get(6),"e10",7));
		
		UndirectedGraph graph = new UndirectedGraph(vertices,edges);
		MinimalSpanningTree minTree = MinimalSpanningTree.byPrim(graph);
		
		minTree.toString();
		
	}
}
