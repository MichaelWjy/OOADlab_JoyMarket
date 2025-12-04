package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Connect {
	public final String HOST = "localhost:3306";
	public final String USERNAME = "root";
	public final String PASSWORD = "";
	public final String DBNAME = "jmTest";
	
	private final String URL = String.format("jdbc:mysql://%s/%s", HOST, DBNAME);
	
	
	Connection con;
	
	private static Connect connect;
	
	public static Connect getInstance() {
		if (connect == null) connect = new Connect();
		return connect;
	}
	
	public Connection getConnection() {
        return con;
    }
	
	public Connect() {
		try {
			con = DriverManager.getConnection(URL,USERNAME,PASSWORD);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ResultSet execQuery (String query) {
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(query);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void execUpdate (String query) {
		try {
			Statement st = con.createStatement();
			st.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public PreparedStatement prepareStatement(String query) {
        try {
            return con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	
	
}
