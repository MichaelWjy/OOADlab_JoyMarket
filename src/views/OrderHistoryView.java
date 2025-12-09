package views;

import controller.OrderHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.OrderHeader;
import models.User;
import java.util.List;

public class OrderHistoryView {
    private Stage stage;
    private User currentUser;
    private OrderHandler orderHandler;

    public OrderHistoryView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.orderHandler = new OrderHandler();
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label lblTitle = new Label("My Order History");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        ListView<VBox> listView = new ListView<>();
        List<OrderHeader> history = orderHandler.getOrderHistory(currentUser.getId());

        if (history.isEmpty()) {
            listView.setPlaceholder(new Label("No Order History Available"));
        } else {
            for (OrderHeader o : history) {
                VBox row = new VBox(5);
                row.setPadding(new Insets(10));
                row.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5;");
                
                Label lblId = new Label("Order ID: #" + o.getIdOrder());
                lblId.setStyle("-fx-font-weight: bold;");
                
                Label lblDetails = new Label(String.format("Date: %s | Total: Rp %.2f", 
                    o.getOrderedAt().toString(), o.getTotalAmount()));
                
                Label lblStatus = new Label("Status: " + o.getStatus());
                if(o.getStatus().equals("Pending")) lblStatus.setStyle("-fx-text-fill: orange;");
                else if(o.getStatus().equals("Delivered")) lblStatus.setStyle("-fx-text-fill: green;");
                
                row.getChildren().addAll(lblId, lblDetails, lblStatus);
                listView.getItems().add(row);
            }
        }

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> new DashboardView(stage, currentUser).show());

        root.getChildren().addAll(lblTitle, listView, btnBack);
        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Order History");
        stage.show();
    }
}