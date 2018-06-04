/**
 * 
 * @author pollux
 *
 */
package map;

public class BoundingBox {
	public double minLat, minLon, maxLat, maxLon;
	
	public BoundingBox(int _minLat, int _minLon, int _maxLat, int _maxLon) {
		minLat = ((double)_minLat)/1000000;
		minLon = ((double)_minLon)/1000000;
		maxLat = ((double)_maxLat)/1000000;
		maxLon = ((double)_maxLon)/1000000;
		
	}
	
	public BoundingBox(double[] a) {
		minLat = a[0];
		minLon = a[1];
		maxLat = a[2];
		maxLon = a[3];
	}
	
	public double getWidth() {
		return maxLon-minLon;
	}
	
	public double getHeight() {
		return maxLat-minLat;
	}
}
