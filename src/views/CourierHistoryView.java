package views;

import controller.DeliveryHandler;
import entitymodel.OrderHeader;
import entitymodel.User;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class CourierHistoryView {
    private Stage stage;
    private User currentUser;
    private DeliveryHandler deliveryHandler;

    public CourierHistoryView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.deliveryHandler = new DeliveryHandler();
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label lblTitle = new Label("Completed Deliveries");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ListView<String> listView = new ListView<>();
        List<OrderHeader> tasks = deliveryHandler.getAssignedDeliveries(currentUser.getId());
        
        boolean empty = true;
        for (OrderHeader o : tasks) {
            if (o.getStatus().equals("Delivered")) {
                empty = false;
                String item = String.format("Order #%s - Completed on %s - Rp %.2f", 
                        o.getIdOrder(), o.getOrderedAt().toString(), o.getTotalAmount());
                listView.getItems().add(item);
            }
        }
        
        if (empty) listView.getItems().add("No completed deliveries yet.");

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> new DashboardView(stage, currentUser).show());

        root.getChildren().addAll(lblTitle, listView, btnBack);

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Delivery History");
        stage.show();
    }
}