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
        
        CourierHandler ch = new CourierHandler();
        if (ch.getCourier(idCourier) == null) return "Courier does not exist";

        try {
            String insertDelivery = "INSERT INTO deliveries (idOrder, idCourier, status) VALUES (?, ?, 'Pending')";
            PreparedStatement psDelivery = con.prepareStatement(insertDelivery);
            psDelivery.setInt(1, Integer.parseInt(idOrder));
            psDelivery.setInt(2, Integer.parseInt(idCourier));
            psDelivery.executeUpdate();

            String updateOrder = "UPDATE order_headers SET status = 'In Progress' WHERE idOrder = ?";
            PreparedStatement psOrder = con.prepareStatement(updateOrder);
            psOrder.setInt(1, Integer.parseInt(idOrder));
            psOrder.executeUpdate();
            
            return "Success";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database Error: " + e.getMessage();
        }
    }
      
    public List<OrderHeader> getAssignedDeliveries(String idCourier) {
        List<OrderHeader> tasks = new ArrayList<>();
        if (idCourier == null || idCourier.isEmpty()) return tasks;
        String query = "SELECT o.idOrder, o.idCustomer, o.idPromo, d.status, o.orderedAt, o.totalAmount " +
                       "FROM order_headers o " +
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
        }
        return tasks;
    }
    
    public String editDeliveryStatus(String idOrder, String idCourier, String newStatus) {
        if (!newStatus.equals("Pending") && !newStatus.equals("In Progress") && !newStatus.equals("Delivered")) {
            return "Invalid status";
        }

        try {
            String checkQuery = "SELECT idOrder FROM deliveries WHERE idOrder = ? AND idCourier = ?";
            PreparedStatement psCheck = con.prepareStatement(checkQuery);
            psCheck.setInt(1, Integer.parseInt(idOrder));
            psCheck.setInt(2, Integer.parseInt(idCourier));
            
            if (!psCheck.executeQuery().next()) return "Unauthorized";
            String updateDelivery = "UPDATE deliveries SET status = ? WHERE idOrder = ?";
            PreparedStatement psDel = con.prepareStatement(updateDelivery);
            psDel.setString(1, newStatus);
            psDel.setInt(2, Integer.parseInt(idOrder));
            psDel.executeUpdate();
            String updateOrder = "UPDATE order_headers SET status = ? WHERE idOrder = ?";
            PreparedStatement psOrd = con.prepareStatement(updateOrder);
            psOrd.setString(1, newStatus);
            psOrd.setInt(2, Integer.parseInt(idOrder));
            psOrd.executeUpdate();

            return "Success";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Database Error: " + e.getMessage();
        }
    }
    
    public List<OrderHeader> getAllDeliveries() {
        List<OrderHeader> list = new ArrayList<>();
        String query = "SELECT o.* FROM order_headers o JOIN deliveries d ON o.idOrder = d.idOrder";
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