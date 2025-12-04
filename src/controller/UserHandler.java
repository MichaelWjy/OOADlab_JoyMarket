package controller;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import models.Admin;
import models.Courier;
import models.Customer;
import models.User;
import utils.Connect;

public class UserHandler {
	private Connect con = Connect.getInstance();
	
	private boolean isNumeric(String str) {
		if (str == null || str.isEmpty()) return false;
		for (char c  : str.toCharArray()) {
			if(!Character.isDigit(c)) return false;
		}
		return true;
	}
	
	public User getUser(String userId) {
		if (userId.isEmpty()) return null;
		
		User user = null;
		
		try {
			String query = "SELECT * FROM users WHERE idUser = ? ";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				String id = String.valueOf(rs.getInt("idUser"));
				String name = rs.getString("fullName");
				String email = rs.getString("email");
				String password = rs.getString("password");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                String gender = rs.getString("gender");
                String role = rs.getString("role");
                
                if (role.equalsIgnoreCase("Customer")) {
					ResultSet custRS = con.execQuery("SELECT * FROM customers WHERE idUser = "+id);
					if (custRS.next()) {
						user = new Customer(id, name, email, password, phone, address, gender, role, custRS.getDouble("balance"));
					}
				}else if (role.equalsIgnoreCase("Courier")) {
					ResultSet courierRS = con.execQuery("SELECT * FROM couriers WHERE idUser = "+id);
					if (courierRS.next()) {
						user = new Courier(id, name, email, password, phone, address, gender, role, courierRS.getString("vehicleType"), courierRS.getString("vehiclePlate"));
					}
				}else if (role.equalsIgnoreCase("Admin")) {
					ResultSet adminRS = con.execQuery("SELECT * FROM admins WHERE idUser = "+id);
					if (adminRS.next()) {
						user = new Admin(id, name, email, password, phone, address, gender, role, adminRS.getString("emergencyContact"));
                    }
				}
                
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}
	
	public String registerAccount(String name, String email, String pass, String phone, String address, String gender, String role, String vehicleType, String vehiclePlate) {
        if (name.isEmpty()) return "Name Cannot be empty";
        if (!email.endsWith("@gmail.com")) return "Email must end with @gmail.com";
        if (pass.length() < 6) return "Password min 6 characters";
        if (!isNumeric(phone) || phone.length() < 10 || phone.length() > 13) return "Phone invalid (10-13 digits)";
        if (address.isEmpty()) return "Address must be filled";
        if (gender == null || gender.isEmpty()) return "Gender must be selected";
        if (role.equalsIgnoreCase("Courier")) {
            if (vehicleType == null || vehicleType.isEmpty()) return "Vehicle Type required for Courier";
            if (vehiclePlate == null || vehiclePlate.isEmpty()) return "Vehicle Plate required for Courier";
        }
        
        
        try {
			String check = "SELECT idUser FROM users WHERE email = ?";
			PreparedStatement emailCheck = con.prepareStatement(check);
			emailCheck.setString(1, email);
			if (emailCheck.executeQuery().next()) return "Email Already Exist";
			
			String query = "INSERT INTO users (fullName, email, password, phone, address, gender, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, pass);
            ps.setString(4, phone);
            ps.setString(5, address);
            ps.setString(6, gender);
            ps.setString(7, role);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            int newId = 0;
            if (rs.next()) {
                newId = rs.getInt(1);
            } else {
                return "Failed to generate User ID";
            }
			
			if (role.equalsIgnoreCase("Customer")) {
                String queryCustomer = "INSERT INTO customers (idUser, balance) VALUES (" + newId + ", 0)";
                con.execUpdate(queryCustomer); 
                
            } else if (role.equalsIgnoreCase("Courier")) {
                String queryCourier = "INSERT INTO couriers (idUser, vehicleType, vehiclePlate) VALUES (?, ?, ?)";
                PreparedStatement psCourier = con.prepareStatement(queryCourier);
                psCourier.setInt(1, newId);
                psCourier.setString(2, vehicleType);
                psCourier.setString(3, vehiclePlate);
                psCourier.executeUpdate();
            }

            return "Success";
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Failed";
	
	}
	
	public User login(String email, String password) {
	    if (email.isEmpty() || password.isEmpty()) return null;
	    
	    User user = null;
	    try {
	        String query = "SELECT idUser FROM users WHERE email = ? AND password = ?";
	        PreparedStatement ps = con.prepareStatement(query);
	        ps.setString(1, email);
	        ps.setString(2, password);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            int id = rs.getInt("idUser");
	            return getUser(String.valueOf(id));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	public String editProfile(String idUser, String name, String email, String phone, String address, String gender, String vehicleType, String vehiclePlate) {
        if (name.isEmpty()) return "Name cannot be empty";
        if (!isNumeric(phone) || phone.length() < 10 || phone.length() > 13) return "Phone invalid (10-13 digits)";
        if (address.isEmpty()) return "Address cannot be empty";
//        if (password.isEmpty() || password.length() < 6) return "Password invalid (min 6 chars)"; // Validasi Password

        try {
            String roleQuery = "SELECT role FROM users WHERE idUser = ?";
            PreparedStatement psRole = con.prepareStatement(roleQuery);
            psRole.setString(1, idUser);
            ResultSet rsRole = psRole.executeQuery();
            
            String role = "";
            if(rsRole.next()){
                role = rsRole.getString("role");
            } else {
                return "User not found";
            }

            if(role.equalsIgnoreCase("Courier")){
                if(vehicleType == null || vehicleType.isEmpty()) return "Vehicle Type cannot be empty";
                if(vehiclePlate == null || vehiclePlate.isEmpty()) return "Vehicle Plate cannot be empty";
            }
         
            String updateUsers = "UPDATE users SET fullName = ?, email = ?, phone = ?, address = ?, gender = ? WHERE idUser = ?";
            PreparedStatement psUser = con.prepareStatement(updateUsers);
            psUser.setString(1, name);
            psUser.setString(2, email); 
//            psUser.setString(3, password);
            psUser.setString(3, phone);
            psUser.setString(4, address);
            psUser.setString(5, gender);
            psUser.setString(6, idUser);
            psUser.executeUpdate();

            if(role.equalsIgnoreCase("Courier")){
                String updateCourier = "UPDATE couriers SET vehicleType = ?, vehiclePlate = ? WHERE idUser = ?";
                PreparedStatement psCourier = con.prepareStatement(updateCourier);
                psCourier.setString(1, vehicleType);
                psCourier.setString(2, vehiclePlate);
                psCourier.setString(3, idUser);
                psCourier.executeUpdate();
            }

            return "Success";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Update Failed: " + e.getMessage();
        }
    }
	
	public boolean topUpBalance(String idUser, double amount) {
        try {
            String query = "UPDATE customers SET balance = balance + ? WHERE idUser = ?";
            
            // Gunakan PreparedStatement biasa
            PreparedStatement ps = con.prepareStatement(query);
            ps.setDouble(1, amount);
            ps.setString(2, idUser);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
