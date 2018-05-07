package Core;
import ErrorHandling.PathingException;

public class Edge {
	public Vertex U;
	public Vertex V;
	public String Name;
	public int Weight;
	public int ID;
	
	public Edge(Vertex u, Vertex v, String name, int weight) throws PathingException
	{
		if(u == v)
		{			
			throw new ErrorHandling.PathingException(1);
		}
		else
		{
			this.U = u;
			this.V = v;
			this.Name = name;
			this.Weight = weight;	
		}		
	}
	
	
}
