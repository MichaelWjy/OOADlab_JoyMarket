package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import entitymodel.OrderHeader;
import utils.Connect;

public class DeliveryModel {
    private Connect con = Connect.getInstance();
    private OrderModel orderModel = new OrderModel();
    
    public boolean assignCourier(int idOrder, int idCourier) {
        try {
            String insertDelivery = "INSERT INTO deliveries (idOrder, idCourier, status) VALUES (?, ?, 'Pending')";
            PreparedStatement psDelivery = con.prepareStatement(insertDelivery);
            psDelivery.setInt(1, idOrder);
            psDelivery.setInt(2, idCourier);
            psDelivery.executeUpdate();

            orderModel.updateOrderStatus(idOrder, "In Progress");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<OrderHeader> getCourierDeliveries(String idCourier) {
        List<OrderHeader> tasks = new ArrayList<>();
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
                tasks.add(mapToOrderHeader(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public boolean checkDeliveryOwnership(int idOrder, int idCourier) {
        try {
            String checkQuery = "SELECT idOrder FROM deliveries WHERE idOrder = ? AND idCourier = ?";
            PreparedStatement psCheck = con.prepareStatement(checkQuery);
            psCheck.setInt(1, idOrder);
            psCheck.setInt(2, idCourier);
            return psCheck.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateDeliveryStatus(int idOrder, String newStatus) {
        try {
            String updateDelivery = "UPDATE deliveries SET status = ? WHERE idOrder = ?";
            PreparedStatement psDel = con.prepareStatement(updateDelivery);
            psDel.setString(1, newStatus);
            psDel.setInt(2, idOrder);
            psDel.executeUpdate();

            orderModel.updateOrderStatus(idOrder, newStatus);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<OrderHeader> getAllDeliveries() {
        List<OrderHeader> list = new ArrayList<>();
        String query = "SELECT o.idOrder, o.idCustomer, o.idPromo, d.status, o.orderedAt, o.totalAmount " +
                       "FROM order_headers o " +
                       "JOIN deliveries d ON o.idOrder = d.idOrder";
        try {
            ResultSet rs = con.execQuery(query);
            while(rs.next()) {
                list.add(mapToOrderHeader(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private OrderHeader mapToOrderHeader(ResultSet rs) throws SQLException {
        return new OrderHeader(
            String.valueOf(rs.getInt("idOrder")),
            String.valueOf(rs.getInt("idCustomer")),
            String.valueOf(rs.getInt("idPromo")),
            rs.getString("status"),
            rs.getTimestamp("orderedAt"),
            rs.getDouble("totalAmount")
        );
    }
}