package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Product;
import utils.Connect;

public class ProductHanlder {
    private Connect con = Connect.getInstance();

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        try {
            ResultSet rs = con.execQuery(query);
            while (rs.next()) {
                products.add(new Product(
                    String.valueOf(rs.getInt("idProduct")),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public Product getProduct(String idProduct) {
        String query = "SELECT * FROM products WHERE idProduct = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(idProduct));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Product(
                    String.valueOf(rs.getInt("idProduct")),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String updateStock(String idProduct, int newStock) {
        if (newStock < 0) return "Stock cannot be negative";

        String query = "UPDATE products SET stock = ? WHERE idProduct = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, newStock);
            ps.setInt(2, Integer.parseInt(idProduct));
            ps.executeUpdate();
            return "Success";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database Error";
        }
    }
}