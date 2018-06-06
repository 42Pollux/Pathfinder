package Test;
import java.util.*;

import org.junit.*;
import Core.*;
import PathingInterface.*;

public class PathingInterfaceTest {
	@Test
	public void testNewRoutes() throws Exception
	{
		//first test object are three input points in one cluster 
		//find way from v0->v8->v15 (correspond the test cluster manual for more informations)
		ArrayList<ArrayList<String>>inputsForObject1 = new ArrayList<ArrayList<String>>();
		ArrayList<String>pointDescription = new ArrayList<String>();
		pointDescription.add("0.0");
		pointDescription.add("0.0");
		pointDescription.add("0.0");
		pointDescription.add("WeightTime");
		inputsForObject1.add(pointDescription);
		
		pointDescription = new ArrayList<String>();		
		pointDescription.add("31.0");
		pointDescription.add("28.0");
		pointDescription.add("27.0");
		pointDescription.add("WeightTime");
		inputsForObject1.add(pointDescription);

		pointDescription = new ArrayList<String>();		
		pointDescription.add("91");		//lon
		pointDescription.add("88");		//lat
		pointDescription.add("86");		//height /ele
		pointDescription.add("WeightTime");
		inputsForObject1.add(pointDescription);		
		
		ArrayList<Path> testobject1= PathingInterface.getNewRoutes(inputsForObject1, "testuser0"); 
		testobject1.forEach(path->{
			path.getEdges().forEach(edge->{
				System.out.println("from " +edge.U.Name + " to " +edge.V.Name + " via "+ edge.Name);
			});
		});
	}
	
	@Test 
	public void testGetStoredRoutes() throws Exception {
		ArrayList<Path> paths = PathingInterface.getStoredRoutes("testuser0");
		
		paths.forEach(path->{
			path.getEdges().forEach(edge->{
				System.out.println("from " +edge.U.Name + " to " + edge.V.Name + " via " + edge.Name);
			});
		});
	}
}


