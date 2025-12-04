package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.OrderHeader;
import utils.Connect;

public class DeliveryHandler {
	  private Connect con = Connect.getInstance();
	  
	  public String assignCourierToOrder(String idOrder, String idCourier) {
	        if (idOrder == null || idCourier == null) return "Invalid selection";

	        try {
	            // Update tabel order_headers
	            // Set status menjadi 'Processed' atau 'On The Way' saat kurir di-assign
	            String query = "UPDATE order_headers SET courier_id = ?, status = 'Processed' WHERE idOrder = ?";
	            PreparedStatement ps = con.prepareStatement(query);
	            ps.setInt(1, Integer.parseInt(idCourier));
	            ps.setInt(2, Integer.parseInt(idOrder));
	            
	            int rows = ps.executeUpdate();
	            return (rows > 0) ? "Success" : "Failed to assign courier";
	            
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return "Database Error: " + e.getMessage();
	        }
	  }
	  
	  public List<OrderHeader> getAssignedDeliveries(String idCourier) {
	        List<OrderHeader> tasks = new ArrayList<>();
	        if (idCourier == null || idCourier.isEmpty()) return tasks;
	        String query = "SELECT * FROM order_headers WHERE courier_id = ? ORDER BY orderedAt DESC";
	        
	        try {
	            PreparedStatement ps = con.prepareStatement(query);
	            ps.setInt(1, Integer.parseInt(idCourier));
	            
	            ResultSet rs = ps.executeQuery();
	            
	            while(rs.next()) {
	                OrderHeader o = new OrderHeader(
	                    String.valueOf(rs.getInt("idOrder")),
	                    String.valueOf(rs.getInt("idCustomer")),
	                    String.valueOf(rs.getInt("idPromo")),
	                    rs.getString("status"),
	                    rs.getTimestamp("orderedAt"),
	                    rs.getDouble("totalAmount")
	                );
	                tasks.add(o);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } catch (NumberFormatException e) {
	            System.out.println("Error: Courier ID invalid.");
	        }
	        
	        return tasks;
	  }
	  
	  public String editDeliveryStatus(String idOrder, String newStatus) {
	        // Validasi Status yang diperbolehkan
	        if (!newStatus.equals("Picked Up") && !newStatus.equals("Delivered") && !newStatus.equals("Pending")) {
	            return "Invalid status";
	        }

	        try {
	            String query = "UPDATE order_headers SET status = ? WHERE idOrder = ?";
	            PreparedStatement ps = con.prepareStatement(query);
	            ps.setString(1, newStatus);
	            ps.setInt(2, Integer.parseInt(idOrder));
	            
	            int rows = ps.executeUpdate();
	            return (rows > 0) ? "Success" : "Failed to update status";

	        } catch (SQLException e) {
	            e.printStackTrace();
	            return "Database Error: " + e.getMessage();
	        }
	    }
}
