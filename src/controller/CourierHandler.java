package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Courier;
import models.OrderHeader;
import utils.Connect;

public class CourierHandler {
    private Connect con = Connect.getInstance();

    // ==========================================
    // ADMIN FEATURES
    // ==========================================

    public Courier getCourier(String idCourier) {
        String query = "SELECT u.*, c.vehicleType, c.vehiclePlate " +
                       "FROM users u " +
                       "JOIN couriers c ON u.idUser = c.idUser " +
                       "WHERE u.idUser = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(idCourier));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Courier(
                    String.valueOf(rs.getInt("idUser")),
                    rs.getString("fullName"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("gender"),
                    rs.getString("role"),
                    rs.getString("vehicleType"),
                    rs.getString("vehiclePlate")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Use Case: View All Couriers (Untuk ditampilkan di Admin Dashboard saat assign order)
    public List<Courier> getAllCouriers() {
        List<Courier> couriers = new ArrayList<>();
        // Query Join Users & Couriers untuk dapat nama & detail kendaraan
        String query = "SELECT u.idUser, u.fullName, u.email, u.phone, u.address, u.gender, c.vehicleType, c.vehiclePlate " +
                       "FROM users u " +
                       "JOIN couriers c ON u.idUser = c.idUser " +
                       "WHERE u.role = 'Courier'";
        
        try {
            ResultSet rs = con.execQuery(query);
            while (rs.next()) {
                Courier c = new Courier(
                    String.valueOf(rs.getInt("idUser")),
                    rs.getString("fullName"),
                    rs.getString("email"),
                    "PROTECTED", // Password tidak perlu ditampilkan
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("gender"),
                    "Courier",
                    rs.getString("vehicleType"),
                    rs.getString("vehiclePlate")
                );
                couriers.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return couriers;
    }

    // Use Case: Assign Order to Courier (Admin)
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

    // ==========================================
    // COURIER FEATURES
    // ==========================================

    // Use Case: View Assigned Deliveries (Courier)
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

    // Use Case: Edit Delivery Status (Courier)
    // Status Flow: Processed -> Picked Up -> Delivered
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