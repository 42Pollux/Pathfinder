package Core;
import java.util.Comparator;
import java.util.*;

public class VertexWeightComparator implements Comparator <KeyValue<Vertex, Integer>>{
	
	@Override
	public int compare(KeyValue<Vertex, Integer> kv1, KeyValue<Vertex, Integer> kv2)
	{
		return this.compareTo(kv1) - this.compareTo(kv2);
	}
	
	public int compareTo(KeyValue<Vertex, Integer> kv)
	{
		return kv.Value;
	}

}
