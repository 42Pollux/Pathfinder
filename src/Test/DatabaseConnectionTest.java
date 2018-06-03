package Test;
import DatabaseConnection.*;
import org.junit.*;

import java.sql.Connection;
import java.util.*;

public class DatabaseConnectionTest {
	
	@Test
	public void TestExecuteSelectStatement() throws Exception
	{
		String user = "DataFinderTom";
		String pw = "pr!3vtB0tn3t";		
		
		DatabaseConnection connection = DatabaseConnection.ByUsernameAndPW(user, pw);
		ArrayList<ArrayList<String>> queryResult = connection.executeSelectQuery("Select * from Edges;");
		
		queryResult.stream().forEach(row->{row.stream().forEach(string->{
			System.out.println(string);
		});});
	}
	
	@Test
	public void TestExecuteTransactionStatement()throws Exception
	{
		String user = "DataFinderTom";
		String pw = "pr!3vtB0tn3t";		
		
		DatabaseConnection connection = DatabaseConnection.ByUsernameAndPW(user, pw);
		connection.executeTransactionQuery("Update Edges Set Name = 'e2_test' where ID=3;");
	}
}
