package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import entitymodel.Promo;
import utils.Connect;

public class PromoModel {
    private Connect con = Connect.getInstance();

    public Promo getPromoByCode(String code) {
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

    public boolean insertPromo(String code, String headline, double discount) {
        try {
            String query = "INSERT INTO promos (code, headline, discountPercentage) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, code);
            ps.setString(2, headline);
            ps.setDouble(3, discount);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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