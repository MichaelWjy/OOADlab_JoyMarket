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
import models.Courier;
import models.User;

public class EditProfileView {
    private Stage stage;
    private User currentUser;
    private UserHandler userHandler;

    public EditProfileView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.userHandler = new UserHandler();
    }

    public void show() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #fcfcfc;");

        Label lblTitle = new Label("Edit Profile");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitle.setStyle("-fx-text-fill: #333;");

        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField txtName = new TextField(currentUser.getName());
        txtName.setPromptText("Full Name");
        txtName.setPrefWidth(250);

        TextField txtEmail = new TextField(currentUser.getEmail());
        txtEmail.setDisable(true); 

        PasswordField txtPass = new PasswordField();
        txtPass.setText(currentUser.getPassword());
        txtPass.setPromptText("Password");

        TextField txtPhone = new TextField(currentUser.getPhone());
        txtPhone.setPromptText("Phone Number");

        TextArea txtAddress = new TextArea(currentUser.getAddress());
        txtAddress.setPrefHeight(60);
        txtAddress.setPrefWidth(250);
        txtAddress.setWrapText(true);

        TextField txtGender = new TextField(currentUser.getGender());
        txtGender.setDisable(true); 
        txtGender.setStyle("-fx-opacity: 0.8; -fx-background-color: #e0e0e0;");

        grid.add(new Label("Full Name:"), 0, 0);   grid.add(txtName, 1, 0);
        grid.add(new Label("Email:"), 0, 1);       grid.add(txtEmail, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);       grid.add(txtPhone, 1, 2);
        grid.add(new Label("Address:"), 0, 3);     grid.add(txtAddress, 1, 3);
        grid.add(new Label("Gender:"), 0, 4);      grid.add(txtGender, 1, 4); 

        VBox courierPane = new VBox(10);
        courierPane.setAlignment(Pos.CENTER);
        courierPane.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 15; -fx-background-color: #f9f9f9;");
        
        TextField txtVehicleType = new TextField();
        TextField txtVehiclePlate = new TextField();

        if (currentUser instanceof Courier) {
            Courier c = (Courier) currentUser;
            txtVehicleType.setText(c.getVehicleType());
            txtVehiclePlate.setText(c.getVehiclePlate());
            
            GridPane courierGrid = new GridPane();
            courierGrid.setVgap(10);
            courierGrid.setHgap(10);
            courierGrid.setAlignment(Pos.CENTER);
            
            courierGrid.add(new Label("Vehicle Type:"), 0, 0); courierGrid.add(txtVehicleType, 1, 0);
            courierGrid.add(new Label("Vehicle Plate:"), 0, 1); courierGrid.add(txtVehiclePlate, 1, 1);
            
            courierPane.getChildren().addAll(new Label("Courier Details"), courierGrid);
            
            courierPane.setVisible(true);
            courierPane.setManaged(true);
        } else {
            courierPane.setVisible(false);
            courierPane.setManaged(false);
        }
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnUpdate = new Button("Update Profile");
        btnUpdate.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnUpdate.setPrefWidth(120);
        
        Button btnBack = new Button("Cancel");
        btnBack.setStyle("-fx-background-color: #ccc; -fx-text-fill: black; -fx-cursor: hand;");
        btnBack.setPrefWidth(80);

        buttonBox.getChildren().addAll(btnBack, btnUpdate);

        btnUpdate.setOnAction(e -> {
            String vType = (currentUser instanceof Courier) ? txtVehicleType.getText() : null;
            String vPlate = (currentUser instanceof Courier) ? txtVehiclePlate.getText() : null;

            String result = userHandler.editProfile(
                currentUser.getId(), 
                txtName.getText(),
                txtEmail.getText(),     
                txtPhone.getText(),
                txtAddress.getText(),
                currentUser.getGender(), 
                vType,
                vPlate
            );

            if (result.equals("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile Updated Successfully!");
                currentUser.setName(txtName.getText());
                currentUser.setPassword(txtPass.getText());
                currentUser.setPhone(txtPhone.getText());
                currentUser.setAddress(txtAddress.getText());
                
                if (currentUser instanceof Courier) {
                    ((Courier) currentUser).setVehicleType(vType);
                    ((Courier) currentUser).setVehiclePlate(vPlate);
                }
                
                new DashboardView(stage, currentUser).show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Failed", result);
            }
        });

        btnBack.setOnAction(e -> {
            new DashboardView(stage, currentUser).show();
        });

        root.getChildren().addAll(lblTitle, grid, courierPane, buttonBox);

        Scene scene = new Scene(root, 500, 750);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Edit Profile");
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}