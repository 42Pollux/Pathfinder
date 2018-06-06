package Core;

import java.util.Comparator;

public class EdgeWeightComparator implements Comparator <Edge<Storage>>{
	
	@Override 
	public int compare(Edge<Storage> o1, Edge<Storage> o2) {
		return o1.Weight -o2.Weight;
	}
	
}
