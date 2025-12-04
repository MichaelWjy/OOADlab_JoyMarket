package views;

import controller.CourierHandler;
import controller.DeliveryHandler;
import controller.OrderHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Courier;
import models.OrderHeader;
import models.User;

public class AdminOrderView {
    private Stage stage;
    private User currentUser;
    private OrderHandler orderHandler = new OrderHandler();
    private CourierHandler courierHandler = new CourierHandler();
    private DeliveryHandler deliveryHandler = new DeliveryHandler();

    public AdminOrderView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        ListView<HBox> listView = new ListView<>();
        for (OrderHeader o : orderHandler.getPendingOrders()) {
            HBox row = new HBox(10);
            Label lblId = new Label("Order #" + o.getIdOrder() + " | Status: " + o.getStatus());
            
            ComboBox<Courier> cmbCourier = new ComboBox<>();
            cmbCourier.getItems().addAll(courierHandler.getAllCouriers());
            
            // Perlu override toString() di model Courier agar tampil nama
            // Atau setCellFactory (tapi untuk simpel, asumsikan toString user friendly atau tambah logic di sini)
            
            Button btnAssign = new Button("Assign");
            btnAssign.setOnAction(e -> {
                Courier c = cmbCourier.getValue();
                if(c == null) return;
                
                String res = deliveryHandler.assignCourierToOrder(o.getIdOrder(), c.getId());
                if(res.equals("Success")) {
                    new Alert(Alert.AlertType.INFORMATION, "Assigned!").showAndWait();
                    show(); // Refresh
                } else {
                    new Alert(Alert.AlertType.ERROR, res).show();
                }
            });
            
            row.getChildren().addAll(lblId, cmbCourier, btnAssign);
            listView.getItems().add(row);
        }

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> new DashboardView(stage, currentUser).show());
        
        root.getChildren().addAll(new Label("Assign Orders"), listView, btnBack);
        stage.setScene(new Scene(root, 600, 500));
        stage.show();
    }
}