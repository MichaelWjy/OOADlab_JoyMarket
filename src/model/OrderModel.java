package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import entitymodel.CartItem;
import entitymodel.OrderHeader;
import utils.Connect;

public class OrderModel {
    private Connect con = Connect.getInstance();
    private ProductModel productModel = new ProductModel();

    public boolean createOrder(String idCustomer, String idPromo, double totalPrice, List<CartItem> cart) {
        try {
            String headerQuery = "INSERT INTO order_headers (idCustomer, idPromo, status, orderedAt, totalAmount) VALUES (?, ?, 'Pending', NOW(), ?)";
            PreparedStatement psHeader = con.prepareStatement(headerQuery);
            psHeader.setInt(1, Integer.parseInt(idCustomer));
            
            if (idPromo == null) psHeader.setNull(2, java.sql.Types.INTEGER);
            else psHeader.setInt(2, Integer.parseInt(idPromo));
            
            psHeader.setDouble(3, totalPrice);
            psHeader.executeUpdate();

            ResultSet rsKey = psHeader.getGeneratedKeys();
            int orderId = 0;
            if (rsKey.next()) orderId = rsKey.getInt(1);
            else return false;

            String detailQuery = "INSERT INTO order_details (idOrder, idProduct, qty) VALUES (?, ?, ?)";
            PreparedStatement psDetail = con.prepareStatement(detailQuery);
            
            for (CartItem item : cart) {
                psDetail.setInt(1, orderId);
                psDetail.setInt(2, Integer.parseInt(item.getIdProduct()));
                psDetail.setInt(3, item.getCount());
                psDetail.executeUpdate();

                productModel.decreaseStock(item.getCount(), Integer.parseInt(item.getIdProduct()));
            }

            String balanceQuery = "UPDATE customers SET balance = balance - ? WHERE idUser = ?";
            PreparedStatement psBal = con.prepareStatement(balanceQuery);
            psBal.setDouble(1, totalPrice);
            psBal.setInt(2, Integer.parseInt(idCustomer));
            psBal.executeUpdate();
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<OrderHeader> getOrdersByStatus(String status) {
        List<OrderHeader> orders = new ArrayList<>();
        String query = "SELECT * FROM order_headers WHERE status = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                orders.add(mapRowToOrderHeader(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<OrderHeader> getOrderHistory(String idCustomer) {
        List<OrderHeader> orders = new ArrayList<>();
        String query = "SELECT * FROM order_headers WHERE idCustomer = ? ORDER BY orderedAt DESC";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(idCustomer));
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                orders.add(mapRowToOrderHeader(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    public List<OrderHeader> getAllOrders() {
        List<OrderHeader> orders = new ArrayList<>();
        String query = "SELECT * FROM order_headers ORDER BY orderedAt DESC";
        try {
            ResultSet rs = con.execQuery(query);
            while(rs.next()){
                orders.add(mapRowToOrderHeader(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    private OrderHeader mapRowToOrderHeader(ResultSet rs) throws SQLException {
        return new OrderHeader(
            String.valueOf(rs.getInt("idOrder")),
            String.valueOf(rs.getInt("idCustomer")),
            rs.getString("idPromo"),
            rs.getString("status"),
            rs.getTimestamp("orderedAt"),
            rs.getDouble("totalAmount")
        );
    }
    
    public void updateOrderStatus(int idOrder, String status) throws SQLException {
        String updateOrder = "UPDATE order_headers SET status = ? WHERE idOrder = ?";
        PreparedStatement psOrder = con.prepareStatement(updateOrder);
        psOrder.setString(1, status);
        psOrder.setInt(2, idOrder);
        psOrder.executeUpdate();
    }
}