package org.uni.pathfinder.shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PictureReference extends ReferenceObject{
	
	protected String PictureDescription;	
		
	public PictureReference(int referenceID, int databaseID, String pictureDescription)
	{
		super(referenceID, databaseID);
		this.PictureDescription = pictureDescription;		
	}
	public String getDescription()
	{
		return PictureDescription;
	}
	
	// Serialization
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();  
        out.writeInt(this.ReferenceID);
        out.writeInt(this.DatabaseID);
        out.writeChars(this.PictureDescription+ "\n");
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.ReferenceID = in.readInt();
        this.DatabaseID = in.readInt();
        this.PictureDescription = in.readLine();
    }    
	
}
