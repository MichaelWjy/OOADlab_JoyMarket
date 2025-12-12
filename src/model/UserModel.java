package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import entitymodel.Admin;
import entitymodel.Courier;
import entitymodel.Customer;
import entitymodel.User;
import utils.Connect;

public class UserModel {
    private Connect con = Connect.getInstance();

    public User getUser(String userId) {
        User user = null;
        try {
            String query = "SELECT * FROM users WHERE idUser = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String id = String.valueOf(rs.getInt("idUser"));
                String name = rs.getString("fullName");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                String gender = rs.getString("gender");
                String role = rs.getString("role");

                if (role.equalsIgnoreCase("Customer")) {
                    ResultSet custRS = con.execQuery("SELECT * FROM customers WHERE idUser = " + id);
                    if (custRS.next()) {
                        user = new Customer(id, name, email, password, phone, address, gender, role, custRS.getDouble("balance"));
                    }
                } else if (role.equalsIgnoreCase("Courier")) {
                    ResultSet courierRS = con.execQuery("SELECT * FROM couriers WHERE idUser = " + id);
                    if (courierRS.next()) {
                        user = new Courier(id, name, email, password, phone, address, gender, role, courierRS.getString("vehicleType"), courierRS.getString("vehiclePlate"));
                    }
                } else if (role.equalsIgnoreCase("Admin")) {
                    ResultSet adminRS = con.execQuery("SELECT * FROM admins WHERE idUser = " + id);
                    if (adminRS.next()) {
                        user = new Admin(id, name, email, password, phone, address, gender, role, adminRS.getString("emergencyContact"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public boolean isEmailExists(String email) {
        try {
            String check = "SELECT idUser FROM users WHERE email = ?";
            PreparedStatement emailCheck = con.prepareStatement(check);
            emailCheck.setString(1, email);
            return emailCheck.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerUser(String name, String email, String pass, String phone, String address, String gender, String role) {
        try {
            String query = "INSERT INTO users (fullName, email, password, phone, address, gender, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, pass);
            ps.setString(4, phone);
            ps.setString(5, address);
            ps.setString(6, gender);
            ps.setString(7, role);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int newId = 0;
            if (rs.next()) newId = rs.getInt(1);
            else return false;

            if (role.equalsIgnoreCase("Customer")) {
                String queryCustomer = "INSERT INTO customers (idUser, balance) VALUES (?, 0)";
                PreparedStatement psCust = con.prepareStatement(queryCustomer);
                psCust.setInt(1, newId);
                psCust.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User checkLogin(String email, String password) {
        try {
            String query = "SELECT idUser FROM users WHERE email = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return getUser(String.valueOf(rs.getInt("idUser")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserProfile(String idUser, String name, String email, String password, String phone, String address, String gender, String role, String vType, String vPlate) {
        try {
            String updateUsers = "UPDATE users SET fullName = ?, email = ?, password = ?, phone = ?, address = ?, gender = ? WHERE idUser = ?";
            PreparedStatement psUser = con.prepareStatement(updateUsers);
            psUser.setString(1, name);
            psUser.setString(2, email);
            psUser.setString(3, password);
            psUser.setString(4, phone);
            psUser.setString(5, address);
            psUser.setString(6, gender);
            psUser.setString(7, idUser);
            psUser.executeUpdate();

            if (role.equalsIgnoreCase("Courier")) {
                String updateCourier = "UPDATE couriers SET vehicleType = ?, vehiclePlate = ? WHERE idUser = ?";
                PreparedStatement psCourier = con.prepareStatement(updateCourier);
                psCourier.setString(1, vType);
                psCourier.setString(2, vPlate);
                psCourier.setString(3, idUser);
                psCourier.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBalance(String idUser, double amount) {
        try {
            String query = "UPDATE customers SET balance = balance + ? WHERE idUser = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setDouble(1, amount);
            ps.setString(2, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}