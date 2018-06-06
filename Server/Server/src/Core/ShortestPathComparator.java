package Core;
import java.util.*;

public class ShortestPathComparator implements Comparator<KeyValue<Vertex<Storage>,KeyValue<Vertex<Storage>,Integer>>>{
	
	@Override 
	public int compare(KeyValue<Vertex<Storage>,KeyValue<Vertex<Storage>,Integer>> instance1 ,KeyValue<Vertex<Storage>,KeyValue<Vertex<Storage>,Integer>> instance2)
	{
		return compareTo(instance1) - compareTo(instance2);
	}
	
	private static int compareTo(KeyValue<Vertex<Storage>,KeyValue<Vertex<Storage>,Integer>> instance)
	{
		return instance.Value.Value;
	}

}
