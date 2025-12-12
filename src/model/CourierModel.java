package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import entitymodel.Courier;
import utils.Connect;

public class CourierModel {
    private Connect con = Connect.getInstance();

    public Courier getCourierById(String idCourier) {
        String query = "SELECT u.idUser, u.fullName, u.email, u.password, u.phone, u.address, u.gender, u.role, c.vehicleType, c.vehiclePlate " +
                       "FROM users u " +
                       "JOIN couriers c ON u.idUser = c.idUser " +
                       "WHERE u.idUser = ?";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(idCourier));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Courier(
                    String.valueOf(rs.getInt("idUser")),
                    rs.getString("fullName"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("gender"),
                    rs.getString("role"),
                    rs.getString("vehicleType"),
                    rs.getString("vehiclePlate")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Courier> getAllCouriers() {
        List<Courier> couriers = new ArrayList<>();
        String query = "SELECT u.idUser, u.fullName, u.email, u.phone, u.address, u.gender, c.vehicleType, c.vehiclePlate " +
                       "FROM users u " +
                       "JOIN couriers c ON u.idUser = c.idUser " +
                       "WHERE u.role = 'Courier'";
        try {
            ResultSet rs = con.execQuery(query);
            while (rs.next()) {
                couriers.add(new Courier(
                    String.valueOf(rs.getInt("idUser")),
                    rs.getString("fullName"),
                    rs.getString("email"),
                    "PROTECTED", 
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("gender"),
                    "Courier",
                    rs.getString("vehicleType"),
                    rs.getString("vehiclePlate")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return couriers;
    }
}