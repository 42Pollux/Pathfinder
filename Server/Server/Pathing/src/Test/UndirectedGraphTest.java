package Test;
import java.util.*;
import org.junit.Test;
import Core.*;
import ErrorHandling.PathingException;

public class UndirectedGraphTest {

	@Test
	public void TestUndirectedGraph()
	{
		LinkedList<Vertex> vertices = new LinkedList<Vertex>();
		LinkedList<Edge> edges = new LinkedList<Edge>();
		
		for(int i = 0; i < 10; i++)
		{
			vertices.add(new Core.Vertex("" + i,null));
		}
		
		try {
			edges.add(new Edge(vertices.get(0),vertices.get(1),"e1",1));
			edges.add(new Edge(vertices.get(2),vertices.get(3),"e2",1));
			edges.add(new Edge(vertices.get(0),vertices.get(4),"e3",1));
			edges.add(new Edge(vertices.get(4),vertices.get(5),"e4",1));
			edges.add(new Edge(vertices.get(5),vertices.get(6),"e5",1));
			edges.add(new Edge(vertices.get(0),vertices.get(9),"e6",1));
			edges.add(new Edge(vertices.get(9),vertices.get(8),"e7",1));
			edges.add(new Edge(vertices.get(8),vertices.get(7),"e8",1));
			edges.add(new Edge(vertices.get(7),vertices.get(6),"e9",1));
		} catch (PathingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.message);
		}
		
		UndirectedGraph g = new UndirectedGraph(vertices, edges);
		LinkedList<Vertex> verticesFromV1 = g.areReachable(vertices.get(0));
		LinkedList<Vertex> verticesFromV3 = g.areReachable(vertices.get(2));
		
		if(!verticesFromV1.isEmpty() && verticesFromV3.isEmpty())
			System.out.println("Error");
	}
}
