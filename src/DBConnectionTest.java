import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DBConnectionTest{
	
	public static void main(String[] args) {
		Properties config = new Properties();
		try(FileInputStream in = new FileInputStream("config.properties")){
			config.load(in);
		} catch (IOException e) {
			System.out.println("Could not read config.properties. Is it in the project folder?");
			return;
		}
		
		String url = config.getProperty("db.url");
		String user = config.getProperty("db.user");
		String password = config.getProperty("db.password");
		
		try (Connection conn = DriverManager.getConnection(url, user, password)){
			if (conn != null) {
				System.out.println("Connected to fleet_management database successfully!");
			}
		} catch (SQLException e){
			System.out.println("Connection failed.");
			e.printStackTrace();
		}
				
		
	}
}