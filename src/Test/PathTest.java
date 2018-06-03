package Test;
import Core.*;
import ErrorHandling.PathingException;

import java.util.LinkedList;

import org.junit.Test;

import junit.*;

public class PathTest {
	public LinkedList<Vertex<Storage>> Vertices = new LinkedList<Vertex<Storage>>();
	public LinkedList<Edge<Storage>> Edges = new LinkedList<Edge<Storage>>();
	public UndirectedGraph<Storage> Graph = null;
	
	public PathTest() throws PathingException {
		for(int i = 0 ; i<7;i++)
		{
			Vertices.add(new Vertex<Storage>("v"+i, null));
		}		
		Edges.add(new Edge<Storage>(Vertices.get(0), Vertices.get(1), "e1",2));
		Edges.add(new Edge<Storage>(Vertices.get(0), Vertices.get(4), "e2",5));
		Edges.add(new Edge<Storage>(Vertices.get(1), Vertices.get(2), "e3",2));
		Edges.add(new Edge<Storage>(Vertices.get(1), Vertices.get(3), "e4",3));
		Edges.add(new Edge<Storage>(Vertices.get(3), Vertices.get(4), "e5",10));
		Edges.add(new Edge<Storage>(Vertices.get(2), Vertices.get(6), "e6",8));
		Edges.add(new Edge<Storage>(Vertices.get(3), Vertices.get(6), "e7",5));
		Edges.add(new Edge<Storage>(Vertices.get(3), Vertices.get(5), "e8",9));
		Edges.add(new Edge<Storage>(Vertices.get(4), Vertices.get(5), "e9",4));
		Edges.add(new Edge<Storage>(Vertices.get(5), Vertices.get(6), "e10",7));
		this.Graph = new UndirectedGraph<Storage>(this.Vertices,this.Edges);
	}
	
	@Test
	public void TestShortestPath() throws PathingException{
		PathTest testobject = new PathTest();
		
		Path path = new Path(testobject.Graph,testobject.Vertices.get(0),testobject.Vertices.get(5));
		System.out.println("From ");
		path.getVertices().forEach(vertex->{			
			System.out.print(vertex.Name + " to: ");
		});
		path.getEdges().forEach(edge ->{			
				System.out.println("\n via: " + edge.Name);
			});
	}
}
