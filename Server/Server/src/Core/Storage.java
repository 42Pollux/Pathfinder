package Core;
import java.util.*;

/**this class stores every relevant meta information for the vertices**/
public class Storage {
	
	//use Storage to map data from db into path finding 
	protected String Name;
	private int ID;
	protected double Longitude;
	protected double Latitude;
	protected double Height;
	protected ArrayList<String> AdditionalInformation = new ArrayList<String>();
	
	//meta objects as another field?
	
	public Storage(String name,int id,double longitude, double latitude, double height)
	{
		this.Name = name;
		this.setID(id);
		this.Longitude = longitude;
		this.Latitude = latitude;
		this.Height = height;
	}
	
	public double getLongitude()
	{
		return this.Longitude;
	}
	
	public double getLatitude()
	{
		return this.Latitude;
	}
	
	public double getHeight()
	{
		return this.Height;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}
	
	public ArrayList<String> getAdditionalInformation()
	{
		return this.AdditionalInformation;
	}
	
	public void addAdditionalInformation(String info)
	{
		if(info != "")
			this.AdditionalInformation.add(info);
	}
}
