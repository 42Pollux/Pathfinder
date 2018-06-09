package ExternalReference;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TextReference extends ReferenceObject {

	public TextReference(int referenceID, int databaseID) {
		super(referenceID, databaseID);
	}

	
	 private void writeObject(ObjectOutputStream out) throws IOException {          
        out.defaultWriteObject();  
        out.writeInt(this.ReferenceID);
        out.writeInt(this.DatabaseID);        
     }	
	 
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.ReferenceID = in.readInt();
        this.DatabaseID = in.readInt();
    }  
}
