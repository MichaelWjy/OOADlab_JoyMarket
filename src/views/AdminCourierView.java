package views;

import controller.CourierHandler;
import entitymodel.Courier;
import entitymodel.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AdminCourierView {
    private Stage stage;
    private User currentUser;
    private CourierHandler courierHandler;

    public AdminCourierView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.courierHandler = new CourierHandler();
    }

    public void show() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label lblTitle = new Label("List of Couriers");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ListView<HBox> listView = new ListView<>();
        
        for (Courier c : courierHandler.getAllCouriers()) {
            HBox row = new HBox(15);
            row.setPadding(new Insets(10));
            row.setAlignment(Pos.CENTER_LEFT);
            
            VBox infoBox = new VBox(5);
            Label lblName = new Label("Name: " + c.getName());
            lblName.setStyle("-fx-font-weight: bold;");
            
            Label lblDetails = new Label(String.format("Email: %s | Phone: %s", c.getEmail(), c.getPhone()));
            Label lblVehicle = new Label(String.format("Vehicle: %s | Plate: %s", c.getVehicleType(), c.getVehiclePlate()));
            lblVehicle.setStyle("-fx-text-fill: #555;");

            infoBox.getChildren().addAll(lblName, lblDetails, lblVehicle);
            row.getChildren().add(infoBox);
            
            listView.getItems().add(row);
        }
        
        if (listView.getItems().isEmpty()) {
            listView.setPlaceholder(new Label("No couriers registered."));
        }

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> new DashboardView(stage, currentUser).show());

        root.getChildren().addAll(lblTitle, listView, btnBack);
        
        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Couriers");
        stage.show();
    }
}