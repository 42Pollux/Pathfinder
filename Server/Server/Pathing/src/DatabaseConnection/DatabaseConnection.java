package DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
public class DatabaseConnection {	

		private Connection Connection;
		private Statement Statement;
		
		private DatabaseConnection() {
			// TODO Auto-generated constructor stub
		}
		
		/**static constructor method to create a DatabaseConnection to the application server
		 * @param yourUsername:= your mysql user name
		 * yourPassowrd:= the password for this user; For further questions, please connect with your database admin**/
		public static DatabaseConnection ByUsernameAndPW (String yourUsername, String yourPassword) throws Exception	
		{
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				DatabaseConnection result = new DatabaseConnection();
																//			//IP to connect:port/databaseName
				result.Connection = DriverManager.getConnection("jdbc:mysql://62.113.206.126/Pathfinder", yourUsername, yourPassword);
				return result;
			}catch (Exception e)
			{
				System.out.println(e.getMessage());
				throw e;	
			}
			finally {
				
			}		
		}
		/**use this method to execute an sql-select statement
		 * @return an array list of array lists of strings. Each
		 * row is represented by an array list   **/
		public ArrayList<ArrayList<String>> executeSelectQuery (String yourQuery) throws SQLException
		{
			
			ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();			
			
			this.Statement = Connection.createStatement();
			ResultSet resultSet = this.Statement.executeQuery(yourQuery);
			
			// read out data
			while(resultSet.next())
			{
				ArrayList<String> thisRow = new ArrayList<String>();
				// pointer in resultSet
				int i = 1; 
				//this construction will throw an error each time the next courser has reached the last column of this row 
				//so we use this error to get into the next line
				try{
					while (true) {
						thisRow.add(resultSet.getString(i));
						i++;						
					}					
				}catch(Exception e){
					//signal word for end of line
					thisRow.add("EOL");
					result.add(thisRow);
				};
			}
			this.Statement.close();
			return result;			
		}
		
		/**this method provides any transaction into the database and gives you feedback via the console.
		 * Please make sure that your aimed transaction is within your privileges on the db.**/
		public void executeTransactionQuery (String yourQuery) throws SQLException
		{
			try{
				this.Statement = Connection.createStatement();
				this.Statement.executeUpdate(yourQuery);
				System.out.println("Transaction succeed");
				this.Statement.close();
			}catch(Exception e){
				System.out.println(e.getMessage());
				System.out.println("Transaction failed");
			}			
		}
}
