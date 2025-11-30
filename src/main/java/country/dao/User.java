package country.dao;

import java.sql.*;

public class User {
	
	private int id;
	private String email;
	private String password;
	
	public User() {
	}
	
	public User(int id, String email, String password) {
		this.id = id;
		this.email = email;
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public static User authenticate(String email, String password) {
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
        	conn = DB.getConnection();
        	
            String sql = "SELECT id, email, password FROM user WHERE email = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            rs = pstmt.executeQuery();
            
            if(rs.next()) {
            	User user = new User();
            	
            	user.setId(rs.getInt("id"));
            	user.setEmail(rs.getString("email"));
            	user.setPassword(rs.getString("password"));
            	
            	return user;
            }else {
            	return null;
            }
        }catch(SQLException e) {
        	e.printStackTrace();
        	return null;
        }finally {
        	try {
        		if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
        	
        	}catch(SQLException e){
        		e.printStackTrace();
        	}
        	
        }
	}
	
	
	
}





















