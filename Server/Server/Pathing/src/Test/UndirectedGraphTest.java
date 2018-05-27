package Test;
import java.util.*;
import org.junit.Test;
import Core.*;
import ErrorHandling.PathingException;

public class UndirectedGraphTest {

	@Test
	public void TestUndirectedGraph()
	{
		LinkedList<Vertex<Storage>> vertices = new LinkedList<Vertex<Storage>>();
		LinkedList<Edge<Storage>> edges = new LinkedList<Edge<Storage>>();
		
		for(int i = 0; i < 10; i++)
		{
			vertices.add(new Core.Vertex<Storage>("" + i,null));
		}
		
		try {
			edges.add(new Edge<Storage>(vertices.get(0),vertices.get(1),"e1",1));
			edges.add(new Edge<Storage>(vertices.get(2),vertices.get(3),"e2",1));
			edges.add(new Edge<Storage>(vertices.get(0),vertices.get(4),"e3",1));
			edges.add(new Edge<Storage>(vertices.get(4),vertices.get(5),"e4",1));
			edges.add(new Edge<Storage>(vertices.get(5),vertices.get(6),"e5",1));
			edges.add(new Edge<Storage>(vertices.get(0),vertices.get(9),"e6",1));
			edges.add(new Edge<Storage>(vertices.get(9),vertices.get(8),"e7",1));
			edges.add(new Edge<Storage>(vertices.get(8),vertices.get(7),"e8",1));
			edges.add(new Edge<Storage>(vertices.get(7),vertices.get(6),"e9",1));
		} catch (PathingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.message);
		}
		
		UndirectedGraph<Storage> g = new UndirectedGraph<Storage>(vertices, edges);
		LinkedList<Vertex<Storage>> verticesFromV1 = g.areReachable(vertices.get(0));
		LinkedList<Vertex<Storage>> verticesFromV3 = g.areReachable(vertices.get(2));
		
		while(!verticesFromV1.isEmpty())
		{
			System.out.println("Vertex reachable from V1: " + verticesFromV1.poll().Name);
		}
		System.out.println();
		while(!verticesFromV3.isEmpty())
		{
			System.out.println("Vertex reachable from V3: " + verticesFromV3.poll().Name);
		}
	}
}
