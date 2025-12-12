package views;

import controller.OrderHandler;
import entitymodel.OrderHeader;
import entitymodel.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class AdminAllOrdersView {
    private Stage stage;
    private User currentUser;
    private OrderHandler orderHandler;

    public AdminAllOrdersView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.orderHandler = new OrderHandler();
    }

    public void show() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        Label lblTitle = new Label("All System Orders");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        ListView<HBox> listView = new ListView<>();
        List<OrderHeader> allOrders = orderHandler.getAllOrders();
        if (allOrders.isEmpty()) {
            listView.setPlaceholder(new Label("No Order Available"));
        } else {
            for (OrderHeader o : allOrders) {
                HBox row = new HBox(15);
                row.setPadding(new Insets(10));
                
                VBox infoBox = new VBox(5);
                Label lblId = new Label("Order #" + o.getIdOrder() + " (Cust ID: " + o.getIdCustomer() + ")");
                lblId.setStyle("-fx-font-weight: bold;");
                
                Label lblDetails = new Label(String.format("Date: %s | Total: Rp %.2f", o.getOrderedAt(), o.getTotalAmount()));
                Label lblStatus = new Label("Status: " + o.getStatus());
                if (o.getStatus().equalsIgnoreCase("Pending")) lblStatus.setStyle("-fx-text-fill: red; font-weight: bold;");
                else if (o.getStatus().equalsIgnoreCase("Processed")) lblStatus.setStyle("-fx-text-fill: orange; font-weight: bold;");
                else if (o.getStatus().equalsIgnoreCase("Delivered")) lblStatus.setStyle("-fx-text-fill: green; font-weight: bold;");

                infoBox.getChildren().addAll(lblId, lblDetails, lblStatus);
                row.getChildren().add(infoBox);
                
                listView.getItems().add(row);
            }
        }

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> new DashboardView(stage, currentUser).show());

        root.getChildren().addAll(lblTitle, listView, btnBack);
        
        Scene scene = new Scene(root, 650, 500);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - All Orders");
        stage.show();
    }
}