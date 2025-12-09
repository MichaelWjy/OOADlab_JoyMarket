package views;

import controller.UserHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class RegisterView {
    private Stage stage;
    private UserHandler userHandler;

    public RegisterView(Stage stage) {
        this.stage = stage;
        this.userHandler = new UserHandler();
    }

    public void show() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label lblTitle = new Label("Register JoyMarket Account");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField txtName = new TextField(); txtName.setPromptText("Full Name");
        TextField txtEmail = new TextField(); txtEmail.setPromptText("Email (@gmail.com)");
        PasswordField txtPass = new PasswordField(); txtPass.setPromptText("Password (min 6 chars)");
        TextField txtPhone = new TextField(); txtPhone.setPromptText("Phone Number");
        
        TextArea txtAddress = new TextArea();
        txtAddress.setPromptText("Full Address");
        txtAddress.setPrefHeight(60);
        txtAddress.setPrefWidth(200);

        Label lblGender = new Label("Gender:");
        ToggleGroup genderGroup = new ToggleGroup();
        RadioButton rbMale = new RadioButton("Male");
        rbMale.setToggleGroup(genderGroup);
        RadioButton rbFemale = new RadioButton("Female");
        rbFemale.setToggleGroup(genderGroup);
        
        HBox genderBox = new HBox(10, rbMale, rbFemale);
        grid.add(new Label("Full Name:"), 0, 0);   grid.add(txtName, 1, 0);
        grid.add(new Label("Email:"), 0, 1);       grid.add(txtEmail, 1, 1);
        grid.add(new Label("Password:"), 0, 2);    grid.add(txtPass, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);       grid.add(txtPhone, 1, 3);
        grid.add(new Label("Address:"), 0, 4);     grid.add(txtAddress, 1, 4);
        grid.add(lblGender, 0, 5);                 grid.add(genderBox, 1, 5);

        HBox roleBox = new HBox(15);
        Label lblRole = new Label("Register as:");
        ToggleGroup roleGroup = new ToggleGroup();
        RadioButton rbCustomer = new RadioButton("Customer");
        rbCustomer.setToggleGroup(roleGroup);
        rbCustomer.setSelected(true);
        RadioButton rbCourier = new RadioButton("Courier");
        rbCourier.setToggleGroup(roleGroup);
        roleBox.getChildren().addAll(lblRole, rbCustomer, rbCourier);
        
        grid.add(roleBox, 0, 6, 2, 1);
        VBox courierPane = new VBox(10);
        courierPane.setAlignment(Pos.CENTER);
        courierPane.setStyle("-fx-border-color: #ccc; -fx-padding: 10; -fx-background-color: #f9f9f9;");
        
        TextField txtVehicleType = new TextField(); txtVehicleType.setPromptText("Vehicle Type");
        TextField txtPlate = new TextField(); txtPlate.setPromptText("Vehicle Plate");

        courierPane.getChildren().addAll(new Label("Vehicle Type:"), txtVehicleType, new Label("Vehicle Plate:"), txtPlate);
        courierPane.setVisible(false);
        courierPane.setManaged(false);

        roleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (rbCourier.isSelected()) {
                courierPane.setVisible(true); courierPane.setManaged(true);
            } else {
                courierPane.setVisible(false); courierPane.setManaged(false);
                txtVehicleType.clear(); txtPlate.clear();
            }
        });

        Button btnRegister = new Button("Register Now");
        Button btnBack = new Button("Back to Login");

        btnRegister.setOnAction(e -> {
            String selectedRole = rbCourier.isSelected() ? "Courier" : "Customer";
            String vType = rbCourier.isSelected() ? txtVehicleType.getText() : "";
            String vPlate = rbCourier.isSelected() ? txtPlate.getText() : "";
            String selectedGender = null;
            if (rbMale.isSelected()) selectedGender = "Male";
            else if (rbFemale.isSelected()) selectedGender = "Female";
            String result = userHandler.registerAccount(
                txtName.getText(),
                txtEmail.getText(),
                txtPass.getText(),
                txtPhone.getText(),
                txtAddress.getText(),
                selectedGender,
                selectedRole,
                vType,
                vPlate
            );

            if (result.equals("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Registered successfully!");
                new LoginView(stage).show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", result);
            }
        });

        btnBack.setOnAction(e -> new LoginView(stage).show());

        root.getChildren().addAll(lblTitle, grid, courierPane, btnRegister, btnBack);
        stage.setScene(new Scene(root, 500, 700));
        stage.setTitle("JoyMarket - Register");
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}