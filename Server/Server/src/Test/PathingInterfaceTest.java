package Test;
import java.util.*;

import org.junit.*;

import Access.AccessPoint;
import Core.*;
import ExternalReference.PictureReference;
import ExternalReference.ReferenceObject;
import PathingInterface.*;

public class PathingInterfaceTest {
	@Test
	public void testNewRoutes() throws Exception
	{
		//first test object are three input points in one cluster 
		//find way from v0->v8->v15 (correspond the test cluster manual for more informations)
		ArrayList<ArrayList<String>>inputsForObject1 = new ArrayList<ArrayList<String>>();
		ArrayList<String>pointDescription = new ArrayList<String>();
		pointDescription.add("11.977499"); //Lati
		pointDescription.add("53.899855");	//lon	
		pointDescription.add("0.0");
		pointDescription.add("WeightTime");
		inputsForObject1.add(pointDescription);
		
		pointDescription = new ArrayList<String>();		
		pointDescription.add("12.001915");
		pointDescription.add("53.903084");		
		pointDescription.add("0");
		pointDescription.add("WeightTime");
		inputsForObject1.add(pointDescription);

		pointDescription = new ArrayList<String>();		
		pointDescription.add("12.05234");
		pointDescription.add("53.895478");		
		pointDescription.add("0");
		pointDescription.add("WeightTime");
		inputsForObject1.add(pointDescription);		
		
		pointDescription = new ArrayList<String>();		
		pointDescription.add("12.11556");
		pointDescription.add("53.93717");		
		pointDescription.add("0");
		pointDescription.add("WeightTime");
		inputsForObject1.add(pointDescription);	
			
		
		ArrayList<Path> testobject1= PathingInterface.getNewRoutes(inputsForObject1, "testuser0"); 
		testobject1.forEach(path->{
			path.getEdges().forEach(edge->{
				System.out.println("from " +edge.U.Name + " to " +edge.V.Name + " via "+ edge.Name);
				
				System.out.println("Distance: " + AccessPoint.getEuclideanDistance(edge.U, edge.V));
			});
		});
	}
	
	@Test 
	public void testGetStoredRoutes() throws Exception {
		ArrayList<Path> paths = PathingInterface.getStoredRoutes("testuser0");
		
		if(paths.isEmpty())
			System.out.println("no paths stored for this user");
		
		paths.forEach(path->{
			System.out.println("New Path: ");
			path.getEdges().forEach(edge->{
				System.out.println("from " +edge.U.Name + " to " + edge.V.Name + " via " + edge.Name);
			});
		});
	}
	
	
	@Test
	public void testGetFurtherInformation()throws Exception{
		ArrayList<Path> paths = PathingInterface.getStoredRoutes("testuser0");
		
		paths.forEach(path->{
			PathingInterface.getFurtherInformations(path);
			path.getVertices().forEach(vertex->{
				vertex.Storage.getAdditionalInformation().forEach(info->{
					System.out.println("dbID: " + info.getDatabaseID() +" referenceID: " + info.getReferenceID() );
				});
			});
		});
	}  
}


