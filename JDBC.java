import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class JDBC {
	private String username;
	private String password;
	private String db;
	private Statement stmt;
	private Connection con;
	
	public JDBC() throws SQLException{
		username  = "user08";
		password  = "user08";
		db  = "user08";

		try {
			// load driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
		catch (Exception e) {
			System.err.println("Can't find driver");
			System.exit(1);
		}

		// format "jdbc:mysql://[hostname][:port]/[dbname]"
		//note: if connecting through an ssh tunnel make sure to use 127.0.0.1 and
		//also to that the ports are set up correctly
		String urlString = "jdbc:mysql://127.0.0.1/"+ db;
		Connection con = DriverManager.getConnection(urlString,
				username,
				password);
		
		//create a statement object
		stmt = con.createStatement();
		
	}
	
	
	public void signUp(String name, String username, String password) throws SQLException{
		//execute a query, which returns a ResultSet object
				ResultSet result = stmt.executeQuery (
						"SELECT * " + 
						"FROM accounts;");

				//iterate over the ResultSet
				while (result.next()) {
					//for each result, get the value of the column name
					String res = result.getString("name");
					System.out.println(res);
				}
				
				//reuse the statement to insert a new value into the table
				//stmt.executeUpdate("INSERT INTO user (name, id) VALUES (\"AJ\", \"2\")");

				stmt.executeUpdate("INSERT INTO accounts (name, username, password) VALUES (\"" + name 
						+ "\", \"" + username + "\", \"" + password + "\")");

						
				System.out.println("***");
				
				//print the updated table
				result = stmt.executeQuery (
						"SELECT * " + 
						"FROM accounts;");
				while (result.next()) {
					String res = result.getString("name");
					System.out.println(res);
				}

//				con.close();
	}
	
	public boolean verifyUser(String username, String password) throws SQLException{
		ResultSet result = stmt.executeQuery (
				"SELECT * " + 
				"FROM accounts;");
		
		
		result = stmt.executeQuery("SELECT name FROM accounts WHERE username ='" + username + "' and password = '" + password + "'");
		if (result.next()){
			//add the last login time
			//update tablename set LASTTOUCH=CURRENT_TIMESTAMP;
			
			stmt.executeUpdate("UPDATE accounts SET lastlogin = CURRENT_TIMESTAMP WHERE username ='" + username + "' "); 
			
			return true;
		}
		
		return false;
	}

	
	public String getLastLogin(String username) throws SQLException {
		ResultSet result = stmt.executeQuery("SELECT lastlogin FROM accounts WHERE username ='" + username + "'");
		result.next();
		return result.getString(1);
		
		
	}
	
	
	
	public String getName(String username) throws SQLException{
		ResultSet result = stmt.executeQuery (
				"SELECT * " + 
				"FROM accounts;");
		result = stmt.executeQuery("SELECT name FROM accounts WHERE username ='" + username + "'");
		result.next();
		return result.getString(1);
		
	}
		
	
	public void addSearchHistory(String username, String query) throws SQLException{
		ResultSet result = stmt.executeQuery (
				"SELECT * " + 
				"FROM searchhistory;");
		
		
		
		stmt.executeUpdate("INSERT INTO searchhistory (username, query) VALUES (\"" + username 
				+ "\", \"" + query + "\")");
		
	}
	
	public ArrayList<String> getSearchHistory(String username) throws SQLException{
		ArrayList <String> temp = new ArrayList<String>();
		ResultSet result = stmt.executeQuery (
				"SELECT * " + 
				"FROM searchhistory;");
		
		result = stmt.executeQuery("SELECT query FROM searchhistory WHERE username ='" + username + "'");
		
		
		
		while (result.next()) {
			//for each result, get the value of the column name
			String res = result.getString("query");
			temp.add(res);
		}
		
		return temp;
	}
	
	
	public ArrayList<String> getTimeStamps(String username) throws SQLException{
		ArrayList <String> temp = new ArrayList<String>();
		ResultSet result = stmt.executeQuery (
				"SELECT * " + 
				"FROM searchhistory;");
		
		result = stmt.executeQuery("SELECT timestamp FROM searchhistory WHERE username ='" + username + "'");
		
		
		
		while (result.next()) {
			//for each result, get the value of the column name
			String res = result.getString("timestamp");
			temp.add(res);
		}
		
		return temp;
	}
	
	
	public void clearSearchHistory(String username) throws SQLException{
		ResultSet result = stmt.executeQuery (
				"SELECT * " + 
				"FROM searchhistory;");
		
		stmt.executeUpdate("DELETE FROM searchhistory WHERE username ='" + username + "'");
	}
	
	
	public static void addLink(String username, String link) throws SQLException{
		System.out.println("in addlink");
		String urlString = "jdbc:mysql://127.0.0.1/"+ "user08";
		Connection con = DriverManager.getConnection(urlString,
				"user08",
				"user08");
		Statement stmt = con.createStatement();

		stmt.executeUpdate("INSERT INTO visitedresults (username, link) VALUES (\"" + username 
				+ "\", \"" + link + "\")");
	}
	
	
	public void changePassword(String username, String password) throws SQLException{
		//UPDATE Customers
//		SET ContactName='Alfred Schmidt', City='Hamburg'
//				WHERE CustomerName='Alfreds Futterkiste';
		
		stmt.executeUpdate("UPDATE accounts SET password ='" + password + "'WHERE username ='" + username + "'");
		
	}
	
	
	public ArrayList<String> lastLogs() throws SQLException{
//		SELECT column1, column2, hit_pages,...
//		FROM YourTable
//		ORDER BY hit_pages DESC
//		LIMIT 5
		ArrayList<String> temp = new ArrayList<String>();
		ResultSet result = stmt.executeQuery("SELECT name, lastlogin FROM accounts ORDER BY lastlogin DESC LIMIT 5");
		
		while (result.next()) {
			String res = result.getString("name");
			temp.add(res);
		}
		return temp;

	}
	
	
	
}
