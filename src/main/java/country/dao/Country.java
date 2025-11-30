package country.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Country {
	private int countryId;
	private String country;
    private Date lastUpdate;

	
	public Country() {
	}
	
	public Country(int countryId, String country, Date lastUpdate) {
		this.countryId = countryId;
		this.country = country;
		this.lastUpdate = lastUpdate;
	}
	
	public int getCountryId() {
		return countryId;
	}
	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}
	
	public String getCountry(){
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	
	public static List<Country> get(){
		
		List<Country> countries = new ArrayList<>();
		
		Connection conn = null;
        Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = DB.getConnection();
			
            stmt = conn.createStatement();

            String sql = "SELECT country_id, country, last_update FROM country ORDER BY country";
			
            rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				
				Country c = new Country();
				
				c.setCountryId(rs.getInt("country_id"));
				c.setCountry(rs.getString("country"));
                c.setLastUpdate(rs.getDate("last_update"));
				
				countries.add(c);				
			}
			
		}catch(SQLException e) {
			
            e.printStackTrace();
            
		}finally {
			try {
				if (rs != null) rs.close();
                if (stmt != null) stmt.close();
				if (conn != null) conn.close();
				
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		
		return countries;
	}
	
	public static boolean add(String countryName) {
		
		Connection conn = null;
		
        PreparedStatement pstmt = null;
		
		try {
			conn = DB.getConnection();
			
            String sql = "INSERT INTO country (country, last_update) VALUES (?, NOW())";
            pstmt = conn.prepareStatement(sql);
			
            pstmt.setString(1, countryName);
			
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

		}catch(SQLException e) {
			
            e.printStackTrace();
            return false;
            
		}finally {
			
			try {
                if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
}
