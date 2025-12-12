package controller;

import entitymodel.User;
import model.UserModel;

public class UserHandler {
    private UserModel userModel = new UserModel();
    
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (char c : str.toCharArray()) {
            if(!Character.isDigit(c)) return false;
        }
        return true;
    }
    
    public User getUser(String userId) {
        return userModel.getUser(userId);
    }
    
    public String registerAccount(String name, String email, String pass, String phone, String address, String gender, String role) {
        if (name.isEmpty()) return "Name Cannot be empty";
        if (!email.endsWith("@gmail.com")) return "Email must end with @gmail.com";
        if (pass.length() < 6) return "Password min 6 characters";
        if (!isNumeric(phone) || phone.length() < 10 || phone.length() > 13) return "Phone invalid (10-13 digits)";
        if (address.isEmpty()) return "Address must be filled";
        if (gender == null || gender.isEmpty()) return "Gender must be selected";
        
        if (userModel.isEmailExists(email)) return "Email Already Exist";
        
        boolean success = userModel.registerUser(name, email, pass, phone, address, gender, role);
        return success ? "Success" : "Database Error";
    }
    
    public User login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) return null;
        return userModel.checkLogin(email, password);
    }
    
    public String editProfile(String idUser, String name, String email, String password, String phone, String address, String gender, String vehicleType, String vehiclePlate) {
        if (name.isEmpty()) return "Name cannot be empty";
        if (password.isEmpty() || password.length() < 6) return "Password invalid (min 6 chars)";
        if (!isNumeric(phone) || phone.length() < 10 || phone.length() > 13) return "Phone invalid (10-13 digits)";
        if (address.isEmpty()) return "Address cannot be empty";
        User u = userModel.getUser(idUser);
        if (u == null) return "User not found";
        
        if (u.getRole().equalsIgnoreCase("Courier")) {
            if (vehicleType == null || vehicleType.isEmpty()) return "Vehicle Type cannot be empty";
            if (vehiclePlate == null || vehiclePlate.isEmpty()) return "Vehicle Plate cannot be empty";
        }

        boolean success = userModel.updateUserProfile(idUser, name, email, password, phone, address, gender, u.getRole(), vehicleType, vehiclePlate);
        return success ? "Success" : "Update Failed";
    }
    
    public boolean topUpBalance(String idUser, double amount) {
        if (amount < 10000) return false;
        return userModel.updateBalance(idUser, amount);
    }
}