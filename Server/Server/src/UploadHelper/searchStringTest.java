package UploadHelper;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;
import DatabaseConnection.DatabaseConnection;
import org.junit.jupiter.api.Test;

public class searchStringTest {

	public static ArrayList<ArrayList<String>> searchStringTest(String filePath, String phrase, String RteName)
			throws IOException {
		ArrayList<ArrayList<String>> result = Search.searchString(filePath, phrase, RteName);

		result.stream().forEach(lineList -> {
			System.out.println("name:" + lineList.get(4) + "lat:=" + lineList.get(0) + "\t lat:=" + lineList.get(1)
					+ "\t ele:=" + lineList.get(2));
		});
		return result;
	}

	/**
	 * This method uploads Values out of a given Arraylist into the Entity Vertices
	 * on the Server
	 * 
	 * @param lineList
	 * @throws Exception
	 */
	@Test
	public static void uploadVerticesToServer(ArrayList<ArrayList<String>> lineList) throws Exception {
		String user = "DataFinderLukas";
		String passwort = "pr!v3tB0tn3t";
		//
		String query = "insert into Vertices (Name,Latitude,Longitude,Height,InsertTime) values(' " + lineList.get(4)
				+ "'," + lineList.get(1) + ", " + lineList.get(2) + ", " + lineList.get(3) + ",Now());";
		// System.out.println(query);
		// DatabaseConnection conn = DatabaseConnection.ByUsernameAndPW(user, passwort);
		// conn.executeTransactionQuery(query);
	}

	/**
	 * This method uploads Values out of a given Arraylist into the Entity Edges on
	 * the Server
	 * 
	 * @param lineList
	 * @throws Exception
	 */
	public static void uploadEdgesToServer(ArrayList<ArrayList<String>> lineList) throws Exception {
		String user = "DataFinderLukas";
		String passwort = "pr!v3tB0tn3t";
		// now() out of sql function?
		double z = Math.random() + 1;
		double t = Math.random() + 10;
		String WeightDifficulty = String.valueOf(z);
		String WeightTime = String.valueOf(z);
		String query = "insert into Edges (Name,WeightTime,WeightDifficulty,InsertTime) values(" + lineList.get(4)
				+ ", " + WeightDifficulty + ", " + WeightTime + ",Now());";
		System.out.println(query);
		// DatabaseConnection conn = DatabaseConnection.ByUsernameAndPW(user, passwort);
		// conn.executeTransactionQuery(query);
	}

	public static void uploadUVToEdge(ArrayList<ArrayList<String>> lineListU, ArrayList<ArrayList<String>> lineListV,
			ArrayList<ArrayList<String>> lineListE) throws Exception {
		String user = "DataFinderLukas";
		String passwort = "pr!v3tB0tn3t";

		// where statement needs adjustement
		String query = "insert into UVToEdge (U,V,Edge) select t1.ID, t2.ID, t3.ID from Vertices t1, Vertices t2, Edges t3 where t1.ID =2, t2.ID=3, t3.ID =2";
		DatabaseConnection conn = DatabaseConnection.ByUsernameAndPW(user, passwort);
		conn.executeTransactionQuery(query);

		// : insert into UVToEdge (U,V,Edge) select t1.ID, t2.ID, t3.ID from Vertices
		// t1, Vertices t2, Edges t3 where t1.ID =2, t2.ID=3, t3.ID =2
	}

	public static void main(String[] args) throws Exception {

		// set wanted filepath and phrases to look for and upload

		String filePath = "J:\\Eclipse\\Workspace\\GPXtoString\\resources\\test.txt";
		String phrase1 = "wpt";
		// String phrase2 = "trck";
		String RteName = "";

		ArrayList<ArrayList<String>> lineList = searchStringTest.searchStringTest(filePath, phrase1, RteName);
		uploadVerticesToServer(lineList);
		lineList.forEach(list -> {
			System.out.println("");
			list.forEach(line -> {
				System.out.print(line + "\t");
			});
		});
		// ArrayList<ArrayList<String>> lineList2 =
		// searchStringTest.searchStringTest(filePath, phrase2);
		// uploadEdgesToServer(lineList2);

	}

	public void SelectStatement() throws Exception {
		String user = "DataFinderGen";
		String passwort = "D@t@F!nd3rG3n";
		DatabaseConnection conn = DatabaseConnection.ByUsernameAndPW(user, passwort);

		ArrayList<ArrayList<String>> result = conn.executeSelectQuery("Select U, V, Edge, MatchID from UVToEdge;");
		result.forEach(list -> {
			System.out.println("");
			list.forEach(line -> {
				System.out.print(line + "\t");
			});
		});
	}

	@Test
	public void TestList() throws Exception {
		String filePath = "J:\\Eclipse\\Workspace\\GPXtoString\\resources\\test.txt";
		String phrase1 = "lon";
		String RteName = "Darsser Weg";
		ArrayList<ArrayList<String>> lineList = searchStringTest.searchStringTest(filePath, phrase1, RteName);
		String query = "insert into Vertices (Name,Latitude,Longitude,Height,InsertTime) values(' " + lineList.get(4)
				+ "'," + lineList.get(1) + ", " + lineList.get(2) + ", " + lineList.get(3) + ",Now());";
		System.out.println(query);
		double z = Math.random() + 1;
		double t = Math.random() + 10;
		String WeightDifficulty = String.valueOf(z);
		String WeightTime = String.valueOf(t);
		String query2 = "insert into Edges (Name,WeightTime,WeightDifficulty,InsertTime) values(" + lineList.get(4)
				+ ", " + WeightDifficulty + ", " + WeightTime + ",Now());";
		System.out.println(query2);
		lineList.forEach(list -> {
			System.out.println("");
			list.forEach(line -> {
				System.out.print(line + "\t");
			});
		});

	}

	// insert into Vertices (Name,Latitude,Longitude,Height,InsertTime)
	// values('"+lineList.get(4) +"'," +lineList.get(1) +", " +lineList.get(2) +", "
	// +lineList.get(3) + Now());
}
