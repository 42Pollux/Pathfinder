package Access;

import java.util.Comparator;
import Core.*;

public class EuclideanDistanceComparator implements Comparator <KeyValue<Vertex<Storage>,Double>> {
	
	public int compare(KeyValue<Vertex<Storage>, Double> instance1, KeyValue<Vertex<Storage>, Double> instance2)
	{
		return (int) (instance1.Value -  instance2.Value);
	}

}
