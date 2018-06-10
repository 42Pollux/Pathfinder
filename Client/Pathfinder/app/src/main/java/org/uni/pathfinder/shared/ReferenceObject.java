package org.uni.pathfinder.shared;
import java.util.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**this class provides the access to several external references, like pictures or text files, from the server's database for the client.
 /**Let other classes inherit from this to implement new representable features for the client**/
public abstract class ReferenceObject implements Serializable {

	private static final long serialVersionUID = 2;
	/**ReferenceID saves an id to identify an object on the server**/
	protected int ReferenceID;
	/**DatabaseID saves an id to identify an object in the database**/
	protected int DatabaseID;
	protected String ClassName;
	protected ArrayList<String> dataList;

	public ReferenceObject(int referenceID, int databaseID){
		this.ReferenceID = referenceID;
		this.DatabaseID = databaseID;
		this.ClassName = this.getClass().toString();
		this.dataList = new ArrayList<String>();
	}

	public ReferenceObject(int referenceID, int databaseID, ArrayList<String> dataList)
	{
		this.ReferenceID = referenceID;
		this.DatabaseID = databaseID;
		this.dataList = dataList;
	}

	public int getReferenceID()
	{
		return this.ReferenceID;
	}

	public int getDatabaseID()
	{
		return this.DatabaseID;
	}

	public String getClassName()
	{
		return this.ClassName;
	}
}
