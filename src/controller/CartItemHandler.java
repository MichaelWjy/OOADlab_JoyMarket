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

    public List<CartItem> getUserCart(String idUser) {
        List<CartItem> cart = new ArrayList<>();
        String query = "SELECT * FROM carts WHERE idCustomer = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(idUser));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cart.add(new CartItem(
                    String.valueOf(rs.getInt("idCustomer")),
                    String.valueOf(rs.getInt("idProduct")),
                    rs.getInt("qty")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cart;
    }

    public String addToCart(String idUser, String idProduct, int qty) {
        Product p = productHandler.getProduct(idProduct);
        if (p == null) return "Product not found";
        if (qty < 1) return "Quantity must be at least 1";
        if (qty > p.getStock()) return "Insufficient stock";

        try {
            String checkQuery = "SELECT qty FROM carts WHERE idCustomer = ? AND idProduct = ?";
            PreparedStatement psCheck = con.prepareStatement(checkQuery);
            psCheck.setInt(1, Integer.parseInt(idUser));
            psCheck.setInt(2, Integer.parseInt(idProduct));
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                int oldQty = rs.getInt("qty");
                int newQty = oldQty + qty;
                if (newQty > p.getStock()) return "Total quantity exceeds stock";
                
                String updateQuery = "UPDATE carts SET qty = ? WHERE idCustomer = ? AND idProduct = ?";
                PreparedStatement psUpdate = con.prepareStatement(updateQuery);
                psUpdate.setInt(1, newQty);
                psUpdate.setInt(2, Integer.parseInt(idUser));
                psUpdate.setInt(3, Integer.parseInt(idProduct));
                psUpdate.executeUpdate();
            } else {
                String insertQuery = "INSERT INTO carts (idCustomer, idProduct, qty) VALUES (?, ?, ?)";
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

    public String updateCartItem(String idUser, String idProduct, int newQty) {
        Product p = productHandler.getProduct(idProduct);
        if (p == null) return "Product not found";
        if (newQty < 1) return "Quantity must be at least 1";
        if (newQty > p.getStock()) return "Insufficient stock (Stock: " + p.getStock() + ")";

        try {
            String query = "UPDATE carts SET qty = ? WHERE idCustomer = ? AND idProduct = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, newQty);
            ps.setInt(2, Integer.parseInt(idUser));
            ps.setInt(3, Integer.parseInt(idProduct));
            
            int rows = ps.executeUpdate();
            return (rows > 0) ? "Success" : "Item not found in cart";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database Error";
        }
    }

    public String deleteCartItem(String idUser, String idProduct) {
        String query = "DELETE FROM carts WHERE idCustomer = ? AND idProduct = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(idUser));
            ps.setInt(2, Integer.parseInt(idProduct));
            int rows = ps.executeUpdate();
            return (rows > 0) ? "Success" : "Failed to delete";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database Error";
        }
    }
    
    public void clearCart(String idUser) {
        String query = "DELETE FROM carts WHERE idCustomer = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(idUser));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}