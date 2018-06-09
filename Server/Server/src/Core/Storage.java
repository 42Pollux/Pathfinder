package Core;
import java.util.*;

import ExternalReference.ReferenceObject;

/**this class stores every relevant meta information for the vertices**/
public class Storage {
	
	//use Storage to map data from db into path finding 
	protected String Name;
	private int ID;
	protected double Longitude;
	protected double Latitude;
	protected double Height;
	protected ArrayList<ReferenceObject> AdditionalInformation = new ArrayList<ReferenceObject>();
	protected String InsertTime; 
	
	//meta objects as another field?
	
	public Storage(String name,int id,double longitude, double latitude, double height)
	{
		this.Name = name;
		this.setID(id);
		this.Longitude = longitude;
		this.Latitude = latitude;
		this.Height = height;
	}
	
	public Storage(String name,int id,double longitude, double latitude, double height, String time)
	{
		this.Name = name;
		this.setID(id);
		this.Longitude = longitude;
		this.Latitude = latitude;
		this.Height = height;
		this.InsertTime = time;
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
	
	public ArrayList<ReferenceObject> getAdditionalInformation()
	{
		return this.AdditionalInformation;
	}
	
	public void addAdditionalInformation(ReferenceObject info)
	{
		if(info != null)
			this.AdditionalInformation.add(info);
	}
	
	public String getTime()
	{
		return this.InsertTime;
	}
}
