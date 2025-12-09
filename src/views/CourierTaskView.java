package views;

import controller.DeliveryHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.OrderHeader;
import models.User;
import java.util.List;

public class CourierTaskView {
    private Stage stage;
    private User currentUser;
    private DeliveryHandler deliveryHandler;

    public CourierTaskView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.deliveryHandler = new DeliveryHandler();
    }

    public void show() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label lblTitle = new Label("My Active Deliveries");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ListView<VBox> listView = new ListView<>();
        // Mengambil tasks dengan status dari tabel 'deliveries'
        List<OrderHeader> tasks = deliveryHandler.getAssignedDeliveries(currentUser.getId());

        boolean hasActiveTask = false;

        for (OrderHeader o : tasks) {
            // Filter: Hanya tampilkan order yang belum selesai (Delivered)
            if (o.getStatus().equals("Delivered")) continue; 
            
            hasActiveTask = true;
            
            VBox card = new VBox(8);
            card.setStyle("-fx-border-color: #ccc; -fx-padding: 15; -fx-background-color: #f9f9f9; -fx-border-radius: 5;");
            
            Label lblHeader = new Label("Order #" + o.getIdOrder());
            lblHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            
            Label lblStatus = new Label("Current Status: " + o.getStatus());
            lblStatus.setStyle("-fx-text-fill: #2196F3; font-weight: bold;");
            
            Label lblDetail = new Label(String.format("Total: Rp %.2f | Date: %s", o.getTotalAmount(), o.getOrderedAt()));

            HBox actionBox = new HBox(10);
            actionBox.setAlignment(Pos.CENTER_RIGHT);
            
            Button btnAction = new Button();
            btnAction.setPrefWidth(150);
            
            // --- LOGIKA STATUS BARU (Pending -> In Progress -> Delivered) ---
            
            if (o.getStatus().equals("Pending")) {
                // Status awal saat baru di-assign oleh Admin
                btnAction.setText("Start Delivery");
                btnAction.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-cursor: hand;");
                btnAction.setOnAction(e -> updateStatus(o.getIdOrder(), "In Progress"));
                
            } else if (o.getStatus().equals("In Progress")) {
                // Status saat kurir sedang mengantar
                btnAction.setText("Complete Delivery");
                btnAction.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
                btnAction.setOnAction(e -> updateStatus(o.getIdOrder(), "Delivered"));
                
            } else {
                // Fallback jika ada status lain
                btnAction.setText("Waiting...");
                btnAction.setDisable(true);
            }

            actionBox.getChildren().add(btnAction);
            card.getChildren().addAll(lblHeader, lblStatus, lblDetail, new Separator(), actionBox);
            listView.getItems().add(card);
        }

        if (!hasActiveTask) {
            listView.setPlaceholder(new Label("No active delivery tasks."));
        }

        Button btnBack = new Button("Back to Dashboard");
        btnBack.setOnAction(e -> new DashboardView(stage, currentUser).show());

        root.getChildren().addAll(lblTitle, listView, btnBack);

        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Courier Tasks");
        stage.show();
    }

    private void updateStatus(String idOrder, String newStatus) {
        // Panggil controller untuk update status di database
        String result = deliveryHandler.editDeliveryStatus(idOrder, currentUser.getId(), newStatus);
        
        if (result.equals("Success")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Status Updated to: " + newStatus);
            alert.showAndWait();
            show(); // Refresh halaman untuk melihat perubahan
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed: " + result);
            alert.showAndWait();
        }
    }
}