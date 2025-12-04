package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.CartItem;
import models.Customer;
import models.OrderHeader;
import models.Product;
import models.Promo;
import models.User;
import utils.Connect;

public class OrderHandler {
    private Connect con = Connect.getInstance();
    private CartItemHandler cartHandler = new CartItemHandler();
    private ProductHanlder productHandler = new ProductHanlder();
    private PromoHandler promoHandler = new PromoHandler();

    // Use Case: Checkout
    public String checkout(User user, String promoCode) {
        if (!(user instanceof Customer)) return "Only customers can order";
        Customer customer = (Customer) user;

        List<CartItem> cart = cartHandler.getUserCart(customer.getId());
        if (cart.isEmpty()) return "Cart is empty";

        // 1. Hitung Total & Validasi Stok
        double totalPrice = 0;
        for (CartItem item : cart) {
            Product p = productHandler.getProduct(item.getIdProduct());
            if (p.getStock() < item.getCount()) {
                return "Stock changed for product: " + p.getName();
            }
            totalPrice += (p.getPrice() * item.getCount());
        }

        // 2. Cek Promo
        String idPromo = null; 
        if (promoCode != null && !promoCode.isEmpty()) {
            Promo promo = promoHandler.getPromoByCode(promoCode);
            if (promo == null) return "Invalid Promo Code";
            
            // Diskon logic
            double discount = totalPrice * (promo.getDiscountPercentage() / 100);
            totalPrice -= discount;
            idPromo = promo.getIdPromo();
        }

        // 3. Cek Saldo
        if (customer.getBalance() < totalPrice) {
            return "Insufficient Balance. Total: " + totalPrice;
        }

        // 4. Proses Transaksi
        try {
            // A. Insert Header
            String headerQuery = "INSERT INTO order_headers (idCustomer, idPromo, status, orderedAt, totalAmount) VALUES (?, ?, 'Pending', NOW(), ?)";
            PreparedStatement psHeader = con.prepareStatement(headerQuery);
            psHeader.setInt(1, Integer.parseInt(customer.getId()));
            
            if (idPromo == null) psHeader.setNull(2, java.sql.Types.INTEGER);
            else psHeader.setInt(2, Integer.parseInt(idPromo));
            
            psHeader.setDouble(3, totalPrice);
            psHeader.executeUpdate();

            ResultSet rsKey = psHeader.getGeneratedKeys();
            int orderId = 0;
            if (rsKey.next()) orderId = rsKey.getInt(1);
            else return "Failed to create order";

            // B. Insert Details & Update Stock
            String detailQuery = "INSERT INTO order_details (idOrder, idProduct, qty) VALUES (?, ?, ?)";
            PreparedStatement psDetail = con.prepareStatement(detailQuery);
            
            String stockQuery = "UPDATE products SET stock = stock - ? WHERE idProduct = ?";
            PreparedStatement psStock = con.prepareStatement(stockQuery);

            for (CartItem item : cart) {
                // Insert Detail
                psDetail.setInt(1, orderId);
                psDetail.setInt(2, Integer.parseInt(item.getIdProduct()));
                psDetail.setInt(3, item.getCount());
                psDetail.executeUpdate();

                // Update Stock
                psStock.setInt(1, item.getCount());
                psStock.setInt(2, Integer.parseInt(item.getIdProduct()));
                psStock.executeUpdate();
            }

            // C. Potong Saldo User
            String balanceQuery = "UPDATE customers SET balance = balance - ? WHERE idUser = ?";
            PreparedStatement psBal = con.prepareStatement(balanceQuery);
            psBal.setDouble(1, totalPrice);
            psBal.setInt(2, Integer.parseInt(customer.getId()));
            psBal.executeUpdate();
            
            // Update object di memori
            customer.setBalance(customer.getBalance() - totalPrice);

            // D. Clear Cart
            cartHandler.clearCart(customer.getId());

            return "Success";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Transaction Failed: " + e.getMessage();
        }
    }

    // Get Order History (Customer)
    public List<OrderHeader> getOrderHistory(String idCustomer) {
        List<OrderHeader> orders = new ArrayList<>();
        String query = "SELECT * FROM order_headers WHERE idCustomer = ? ORDER BY orderedAt DESC";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(idCustomer));
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                orders.add(new OrderHeader(
                    String.valueOf(rs.getInt("idOrder")),
                    String.valueOf(rs.getInt("idCustomer")),
                    rs.getString("idPromo"),
                    rs.getString("status"),
                    rs.getTimestamp("orderedAt"),
                    rs.getDouble("totalAmount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    // Get Pending Orders (Admin - untuk Assign Courier)
    // PERBAIKAN: Menghapus "OR courier_id IS NULL" karena kolom courier_id tidak ada
    public List<OrderHeader> getPendingOrders() {
        List<OrderHeader> orders = new ArrayList<>();
        // Query diperbaiki: Cukup ambil yang statusnya 'Pending'
        String query = "SELECT * FROM order_headers WHERE status = 'Pending'";
        try {
            ResultSet rs = con.execQuery(query);
            while(rs.next()){
                orders.add(new OrderHeader(
                    String.valueOf(rs.getInt("idOrder")),
                    String.valueOf(rs.getInt("idCustomer")),
                    rs.getString("idPromo"),
                    rs.getString("status"),
                    rs.getTimestamp("orderedAt"),
                    rs.getDouble("totalAmount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}