package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Promo;
import utils.Connect;

public class PromoHandler {
    private Connect con = Connect.getInstance();

    public Promo getPromoByCode(String code) {
        if (code == null || code.isEmpty()) return null;
        
        String query = "SELECT * FROM promos WHERE code = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Promo(
                    String.valueOf(rs.getInt("idPromo")),
                    rs.getString("code"),
                    rs.getString("headline"),
                    rs.getDouble("discountPercentage")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createPromo(String code, String headline, double discount) {
        if (code.isEmpty()) return "Promo code cannot be empty";
        if (headline.isEmpty()) return "Headline cannot be empty";
        if (discount <= 0 || discount > 100) return "Discount must be between 1-100%";

        try {
            if (getPromoByCode(code) != null) return "Promo code already exists";

            String query = "INSERT INTO promos (code, headline, discountPercentage) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, code);
            ps.setString(2, headline);
            ps.setDouble(3, discount);
            
            int rows = ps.executeUpdate();
            return (rows > 0) ? "Success" : "Failed to create promo";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Database Error: " + e.getMessage();
        }
    }

    public List<Promo> getAllPromos() {
        List<Promo> promos = new ArrayList<>();
        String query = "SELECT * FROM promos";
        try {
            ResultSet rs = con.execQuery(query);
            while (rs.next()) {
                promos.add(new Promo(
                    String.valueOf(rs.getInt("idPromo")),
                    rs.getString("code"),
                    rs.getString("headline"),
                    rs.getDouble("discountPercentage")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promos;
    }
}