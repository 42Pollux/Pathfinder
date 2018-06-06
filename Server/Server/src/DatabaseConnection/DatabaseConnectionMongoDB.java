package DatabaseConnection;

import java.net.UnknownHostException;
import java.util.*;

import com.mongodb.*;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.sun.xml.internal.ws.api.message.StreamingSOAP;

public class DatabaseConnectionMongoDB {
		protected MongoClient Client;	
		protected com.mongodb.DB Database;
		//private DatabaseConnectionMongoDB(){}
		
		public DatabaseConnectionMongoDB(String username, String pwd) throws UnknownHostException {			
			
			try{
				Client=new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
				Database= Client.getDB("Pathfinder");
				MongoCredential credential = MongoCredential.createMongoCRCredential(username, "Pathfinder", pwd.toCharArray());
				System.out.println("Connection established");
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
		
		public String queryToDB(String datafield, int indexOf)
		{
			ArrayList<String> temp = new ArrayList<String>();
			BasicDBObject basicDBObject = new BasicDBObject();
			basicDBObject.put(datafield, indexOf);
			
			DBCollection table = Database.getCollection("Informations");
			DBCursor cursor = table.find(basicDBObject);
			
			System.out.println("start to read out\n ");
			while(cursor.hasNext())
			{						
				temp.add("" + cursor.next());
			}
			for(String string:temp)
			{
				if(string.startsWith("{"+datafield+" : "+indexOf+""));
				{					
					cursor.close();
					return string;
				}
					
			}
			return "no such element";
		}
}
