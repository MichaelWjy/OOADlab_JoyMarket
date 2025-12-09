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
import java.util.List;

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
        Label lblTitle = new Label("Assign Orders to Courier");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        List<Courier> courierList = courierHandler.getAllCouriers(); 

        ListView<HBox> listView = new ListView<>();
        List<OrderHeader> pendingOrders = orderHandler.getPendingOrders();
        
        if (pendingOrders.isEmpty()) {
            listView.setPlaceholder(new Label("No pending orders available."));
        }
        for (OrderHeader o : pendingOrders) {
            HBox row = new HBox(10);
            row.setStyle("-fx-alignment: center-left;");
            
            Label lblId = new Label("Order #" + o.getIdOrder());
            lblId.setMinWidth(80);
            lblId.setStyle("-fx-font-weight: bold;");
            
            Label lblDetail = new Label("Rp " + o.getTotalAmount());
            lblDetail.setMinWidth(100);
            ComboBox<Courier> cmbCourier = new ComboBox<>();
            cmbCourier.getItems().addAll(courierList);
            cmbCourier.setPromptText("Select Courier");
            
            cmbCourier.setCellFactory(param -> new ListCell<Courier>() {
                @Override
                protected void updateItem(Courier item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) setText(null);
                    else setText(item.getName() + " (" + item.getVehiclePlate() + ")");
                }
            });
            cmbCourier.setButtonCell(cmbCourier.getCellFactory().call(null)); 
            
            Button btnAssign = new Button("Assign");
            btnAssign.setStyle("-fx-background-color: #009688; -fx-text-fill: white;");
            
            btnAssign.setOnAction(e -> {
                Courier c = cmbCourier.getValue();
                
                if(c == null) {
                    new Alert(Alert.AlertType.WARNING, "Please select a courier first!").show();
                    return;
                }
                
                String res = deliveryHandler.assignCourierToOrder(o.getIdOrder(), c.getId());
                
                if(res.equals("Success")) {
                    new Alert(Alert.AlertType.INFORMATION, "Courier Assigned Successfully!").showAndWait();
                    show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed: " + res).show();
                }
            });
            
            row.getChildren().addAll(lblId, lblDetail, cmbCourier, btnAssign);
            listView.getItems().add(row);
        }

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> new DashboardView(stage, currentUser).show());
        
        root.getChildren().addAll(lblTitle, listView, btnBack);
        stage.setScene(new Scene(root, 700, 500));
        stage.setTitle("JoyMarket - Assign Orders");
        stage.show();
    }
}