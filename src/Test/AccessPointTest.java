package Test;
import org.junit.*;



import Core.*;
import Access.*;

public class AccessPointTest {
	@Test
	public void TestEuclideanDistance()
	{
		Vertex<Storage> v1 = new Vertex<Storage>("v1", new Storage("v1",0,11, 22, 33));
		Vertex<Storage> v2 = new Vertex<Storage>("v2", new Storage("v2",1,55, 66, 77));
		double distance = Access.AccessPoint.getEuclideanDistance(v1, v2);
		
		System.out.println("expected distance >= 76.21 and calculated distance is: " + distance);		
	}
	@Test
	public void TestGetAccessPoint()
	{
		AccessPoint accessPoint = AccessPoint.ByGUIInput(3, 2, 0);
		
		System.out.println("Input is: "+ accessPoint.getInputPoint().Name +
				" with (Lo,La,Hei): (" + accessPoint.getInputPoint().Storage.getLongitude() +","+
				accessPoint.getInputPoint().Storage.getLatitude()+","+
				accessPoint.getInputPoint().Storage.getHeight()+") \n"+
				"closest point is: " +accessPoint.getClosestPoint().Name +
				" with (Lo,La,Hei): (" + accessPoint.getClosestPoint().Storage.getLongitude() +","+
				accessPoint.getClosestPoint().Storage.getLatitude()+","+
				accessPoint.getClosestPoint().Storage.getHeight()+")"
				);
		
	}
}
