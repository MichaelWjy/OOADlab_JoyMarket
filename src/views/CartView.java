package views;

import controller.CartItemHandler;
import controller.OrderHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.CartItem;
import models.User;
import java.util.List;

public class CartView {
    private Stage stage;
    private User currentUser;
    private CartItemHandler cartHandler = new CartItemHandler();
    private OrderHandler orderHandler = new OrderHandler();

    public CartView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        
        ListView<String> listView = new ListView<>();
        List<CartItem> cart = cartHandler.getUserCart(currentUser.getId());
        
        if (cart.isEmpty()) listView.getItems().add("Cart is empty");
        else {
            for (CartItem item : cart) {
                // Di real case, join dengan product untuk dapat nama
                listView.getItems().add("Product ID: " + item.getIdProduct() + " | Qty: " + item.getCount());
            }
        }
        
        TextField txtPromo = new TextField();
        txtPromo.setPromptText("Promo Code");
        
        Button btnCheckout = new Button("Checkout");
        btnCheckout.setOnAction(e -> {
            String res = orderHandler.checkout(currentUser, txtPromo.getText());
            if(res.equals("Success")) {
                 new Alert(Alert.AlertType.INFORMATION, "Order Placed Successfully!").showAndWait();
                 new DashboardView(stage, currentUser).show();
            } else {
                 new Alert(Alert.AlertType.ERROR, res).show();
            }
        });

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> new DashboardView(stage, currentUser).show());
        
        root.getChildren().addAll(new Label("My Cart"), listView, txtPromo, btnCheckout, btnBack);
        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }
}