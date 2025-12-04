package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.CartItem;
import models.Product;
import utils.Connect;

public class CartItemHandler {
    private Connect con = Connect.getInstance();
    private ProductHanlder productHandler = new ProductHanlder();

    // Get Cart Items milik user tertentu
    public List<CartItem> getUserCart(String idUser) {
        List<CartItem> cart = new ArrayList<>();
        String query = "SELECT * FROM cart_items WHERE idUser = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(idUser));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cart.add(new CartItem(
                    String.valueOf(rs.getInt("idCustomer")),
                    String.valueOf(rs.getInt("idProduct")),
                    rs.getInt("count")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cart;
    }

    // Add Item to Cart / Update Quantity jika sudah ada
    public String addToCart(String idUser, String idProduct, int qty) {
        Product p = productHandler.getProduct(idProduct);
        if (p == null) return "Product not found";
        if (qty < 1) return "Quantity must be at least 1";
        if (qty > p.getStock()) return "Insufficient stock";

        try {
            // Cek apakah produk sudah ada di cart user
            String checkQuery = "SELECT count FROM cart_items WHERE idUser = ? AND idProduct = ?";
            PreparedStatement psCheck = con.prepareStatement(checkQuery);
            psCheck.setInt(1, Integer.parseInt(idUser));
            psCheck.setInt(2, Integer.parseInt(idProduct));
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                // Update Qty
                int oldQty = rs.getInt("count");
                int newQty = oldQty + qty;
                if (newQty > p.getStock()) return "Total quantity exceeds stock";
                
                String updateQuery = "UPDATE cart_items SET count = ? WHERE idUser = ? AND idProduct = ?";
                PreparedStatement psUpdate = con.prepareStatement(updateQuery);
                psUpdate.setInt(1, newQty);
                psUpdate.setInt(2, Integer.parseInt(idUser));
                psUpdate.setInt(3, Integer.parseInt(idProduct));
                psUpdate.executeUpdate();
            } else {
                // Insert Baru
                String insertQuery = "INSERT INTO cart_items (idUser, idProduct, count) VALUES (?, ?, ?)";
                PreparedStatement psInsert = con.prepareStatement(insertQuery);
                psInsert.setInt(1, Integer.parseInt(idUser));
                psInsert.setInt(2, Integer.parseInt(idProduct));
                psInsert.setInt(3, qty);
                psInsert.executeUpdate();
            }
            return "Success";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database Error";
        }
    }

    // Delete Item from Cart
    public void deleteCartItem(String idUser, String idProduct) {
        String query = "DELETE FROM cart_items WHERE idUser = ? AND idProduct = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(idUser));
            ps.setInt(2, Integer.parseInt(idProduct));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Clear Cart (dipanggil saat checkout)
    public void clearCart(String idUser) {
        String query = "DELETE FROM cart_items WHERE idUser = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(idUser));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}