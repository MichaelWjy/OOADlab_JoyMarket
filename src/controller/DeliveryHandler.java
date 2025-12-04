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

    // Use Case: Admin Assign Order to Courier
    public String assignCourierToOrder(String idOrder, String idCourier) {
        if (idOrder == null || idCourier == null) return "Invalid selection";
        
        // Validasi: Courier harus ada
        CourierHandler ch = new CourierHandler();
        if (ch.getCourier(idCourier) == null) {
            return "Courier does not exist";
        }

        try {
            // Perbaikan: Insert ke tabel 'deliveries' karena order_headers tidak punya courier_id
            // Tabel deliveries merepresentasikan model Delivery (idOrder, idCourier, status)
            String insertDelivery = "INSERT INTO deliveries (idOrder, idCourier, status) VALUES (?, ?, 'Processed')";
            PreparedStatement psDelivery = con.prepareStatement(insertDelivery);
            psDelivery.setInt(1, Integer.parseInt(idOrder));
            psDelivery.setInt(2, Integer.parseInt(idCourier));
            psDelivery.executeUpdate();

            // Update status di order_headers agar sinkron
            String updateOrder = "UPDATE order_headers SET status = 'Processed' WHERE idOrder = ?";
            PreparedStatement psOrder = con.prepareStatement(updateOrder);
            psOrder.setInt(1, Integer.parseInt(idOrder));
            
            int rows = psOrder.executeUpdate();
            return (rows > 0) ? "Success" : "Failed to assign courier";
            
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database Error: " + e.getMessage();
        }
    }
      
    // Use Case: Courier View Assigned Deliveries
    public List<OrderHeader> getAssignedDeliveries(String idCourier) {
        List<OrderHeader> tasks = new ArrayList<>();
        if (idCourier == null || idCourier.isEmpty()) return tasks;
        
        // Perbaikan: Melakukan JOIN antara order_headers dan deliveries
        // untuk mendapatkan order yang ditugaskan ke kurir tersebut
        String query = "SELECT o.* FROM order_headers o " +
                       "JOIN deliveries d ON o.idOrder = d.idOrder " +
                       "WHERE d.idCourier = ? " +
                       "ORDER BY o.orderedAt DESC";
        
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(idCourier));
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                tasks.add(new OrderHeader(
                    String.valueOf(rs.getInt("idOrder")),
                    String.valueOf(rs.getInt("idCustomer")),
                    String.valueOf(rs.getInt("idPromo")),
                    rs.getString("status"),
                    rs.getTimestamp("orderedAt"),
                    rs.getDouble("totalAmount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error: Courier ID invalid.");
        }
        
        return tasks;
    }
    
    // Use Case: Courier Edit Delivery Status
    public String editDeliveryStatus(String idOrder, String idCourier, String newStatus) {
        // Validasi Status
        if (!newStatus.equals("Pending") && !newStatus.equals("Processed") && 
            !newStatus.equals("Picked Up") && !newStatus.equals("Delivered")) {
            return "Invalid status";
        }

        try {
            // Perbaikan: Cek otorisasi melalui tabel deliveries
            String checkQuery = "SELECT idOrder FROM deliveries WHERE idOrder = ? AND idCourier = ?";
            PreparedStatement psCheck = con.prepareStatement(checkQuery);
            psCheck.setInt(1, Integer.parseInt(idOrder));
            psCheck.setInt(2, Integer.parseInt(idCourier));
            
            if (!psCheck.executeQuery().next()) {
                return "Unauthorized: Order not assigned to this courier";
            }

            // Perbaikan: Update status di kedua tabel (deliveries & order_headers)
            
            // 1. Update tabel deliveries
            String updateDelivery = "UPDATE deliveries SET status = ? WHERE idOrder = ?";
            PreparedStatement psDel = con.prepareStatement(updateDelivery);
            psDel.setString(1, newStatus);
            psDel.setInt(2, Integer.parseInt(idOrder));
            psDel.executeUpdate();

            // 2. Update tabel order_headers (agar Customer/Admin bisa lihat update statusnya)
            String updateOrder = "UPDATE order_headers SET status = ? WHERE idOrder = ?";
            PreparedStatement psOrd = con.prepareStatement(updateOrder);
            psOrd.setString(1, newStatus);
            psOrd.setInt(2, Integer.parseInt(idOrder));
            
            int rows = psOrd.executeUpdate();
            return (rows > 0) ? "Success" : "Failed to update status";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Database Error: " + e.getMessage();
        }
    }
    
    // Use Case: Get All Deliveries (Optional: Jika Admin perlu lihat semua pengiriman aktif)
    public List<OrderHeader> getAllDeliveries() {
        List<OrderHeader> list = new ArrayList<>();
        // Mengambil order yang sudah ada di tabel deliveries
        String query = "SELECT o.* FROM order_headers o " +
                       "JOIN deliveries d ON o.idOrder = d.idOrder";
        try {
            ResultSet rs = con.execQuery(query);
            while(rs.next()) {
                list.add(new OrderHeader(
                    String.valueOf(rs.getInt("idOrder")),
                    String.valueOf(rs.getInt("idCustomer")),
                    String.valueOf(rs.getInt("idPromo")),
                    rs.getString("status"),
                    rs.getTimestamp("orderedAt"),
                    rs.getDouble("totalAmount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}