package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import entitymodel.CartItem;
import utils.Connect;

public class CartItemModel {
    private Connect con = Connect.getInstance();

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

    public int checkItemInCart(String idUser, String idProduct) {
        try {
            String checkQuery = "SELECT qty FROM carts WHERE idCustomer = ? AND idProduct = ?";
            PreparedStatement psCheck = con.prepareStatement(checkQuery);
            psCheck.setInt(1, Integer.parseInt(idUser));
            psCheck.setInt(2, Integer.parseInt(idProduct));
            ResultSet rs = psCheck.executeQuery();
            if (rs.next()) return rs.getInt("qty");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateCartQty(String idUser, String idProduct, int newQty) {
        try {
            String updateQuery = "UPDATE carts SET qty = ? WHERE idCustomer = ? AND idProduct = ?";
            PreparedStatement psUpdate = con.prepareStatement(updateQuery);
            psUpdate.setInt(1, newQty);
            psUpdate.setInt(2, Integer.parseInt(idUser));
            psUpdate.setInt(3, Integer.parseInt(idProduct));
            return psUpdate.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addToCart(String idUser, String idProduct, int qty) {
        try {
            String insertQuery = "INSERT INTO carts (idCustomer, idProduct, qty) VALUES (?, ?, ?)";
            PreparedStatement psInsert = con.prepareStatement(insertQuery);
            psInsert.setInt(1, Integer.parseInt(idUser));
            psInsert.setInt(2, Integer.parseInt(idProduct));
            psInsert.setInt(3, qty);
            return psInsert.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCartItem(String idUser, String idProduct) {
        try {
            String query = "DELETE FROM carts WHERE idCustomer = ? AND idProduct = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(idUser));
            ps.setInt(2, Integer.parseInt(idProduct));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void clearCart(String idUser) {
        try {
            String query = "DELETE FROM carts WHERE idCustomer = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(idUser));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}