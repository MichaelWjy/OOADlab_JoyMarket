package views;

import controller.CartItemHandler;
import controller.OrderHandler;
import controller.ProductHanlder;
import controller.PromoHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import models.CartItem;
import models.Product;
import models.Promo;
import models.User;
import java.util.List;

public class CartView {
    private Stage stage;
    private User currentUser;
    private CartItemHandler cartHandler = new CartItemHandler();
    private OrderHandler orderHandler = new OrderHandler();
    private ProductHanlder productHandler = new ProductHanlder();
    private PromoHandler promoHandler = new PromoHandler();
    
    private Label lblTotalAmount; 
    private ComboBox<Promo> cmbPromo;
    private Button btnClearPromo;

    public CartView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
    }

    public void show() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label lblTitle = new Label("My Cart");
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        ListView<HBox> listView = new ListView<>();
        List<CartItem> cart = cartHandler.getUserCart(currentUser.getId());
        
        lblTotalAmount = new Label("Total: Rp 0.00");
        lblTotalAmount.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        if (cart.isEmpty()) {
            listView.getItems().add(new HBox(new Label("Your cart is empty.")));
            lblTotalAmount.setText("Total: Rp 0.00");
        } else {
            for (CartItem item : cart) {
                Product p = productHandler.getProduct(item.getIdProduct());
                if (p == null) continue;

                HBox row = new HBox(15);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(5));

                VBox infoBox = new VBox(2);
                infoBox.setMinWidth(200);
                Label lblName = new Label(p.getName());
                lblName.setStyle("-fx-font-weight: bold;");
                Label lblPrice = new Label("Price: Rp " + p.getPrice());
                infoBox.getChildren().addAll(lblName, lblPrice);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Spinner<Integer> spinQty = new Spinner<>(1, p.getStock(), item.getCount());
                spinQty.setPrefWidth(70);

                spinQty.valueProperty().addListener((obs, oldVal, newVal) -> {
                    item.setCount(newVal); 
                    calculateAndSetTotal(cart, cmbPromo.getValue());
                });

                Button btnUpdate = new Button("Update");
                btnUpdate.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white;");
                btnUpdate.setOnAction(e -> {
                    String result = cartHandler.updateCartItem(currentUser.getId(), p.getIdProduct(), spinQty.getValue());
                    if (result.equals("Success")) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Item updated successfully!");
                        calculateAndSetTotal(cart, cmbPromo.getValue());
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", result);
                    }
                });

                Button btnDelete = new Button("Delete");
                btnDelete.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white;");
                btnDelete.setOnAction(e -> {
                    String result = cartHandler.deleteCartItem(currentUser.getId(), p.getIdProduct());
                    if (result.equals("Success")) {
                        showAlert(Alert.AlertType.INFORMATION, "Deleted", "Item removed from cart.");
                        show();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", result);
                    }
                });

                row.getChildren().addAll(infoBox, spacer, new Label("Qty:"), spinQty, btnUpdate, btnDelete);
                listView.getItems().add(row);
            }
        }
        
        HBox promoBox = new HBox(10);
        promoBox.setAlignment(Pos.CENTER);
        
        cmbPromo = new ComboBox<>();
        cmbPromo.setPromptText("Select Promo Code");
        cmbPromo.setPrefWidth(200);
        cmbPromo.getItems().addAll(promoHandler.getAllPromos());
        
        cmbPromo.setConverter(new StringConverter<Promo>() {
            @Override
            public String toString(Promo p) {
                if (p == null) return null;
                return p.getCode() + " (" + p.getDiscountPercentage() + "% Off)";
            }
            @Override
            public Promo fromString(String string) {
                return null;
            }
        });

        btnClearPromo = new Button("X");
        btnClearPromo.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold;");
        btnClearPromo.setVisible(false); 
        btnClearPromo.setOnAction(e -> {
            cmbPromo.setValue(null);
        });

        cmbPromo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                btnClearPromo.setVisible(true);
            } else {
                btnClearPromo.setVisible(false);
            }
            calculateAndSetTotal(cart, newVal);
        });

        calculateAndSetTotal(cart, null);

        promoBox.getChildren().addAll(new Label("Promo:"), cmbPromo, btnClearPromo);

        Button btnCheckout = new Button("Checkout All");
        btnCheckout.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnCheckout.setMinWidth(300);
        
        btnCheckout.setOnAction(e -> {
            String promoCode = "";
            if (cmbPromo.getValue() != null) {
                promoCode = cmbPromo.getValue().getCode();
            }

            String res = orderHandler.checkout(currentUser, promoCode);
            if(res.equals("Success")) {
                 showAlert(Alert.AlertType.INFORMATION, "Success", "Order Placed Successfully!");
                 new DashboardView(stage, currentUser).show();
            } else {
                 showAlert(Alert.AlertType.ERROR, "Checkout Failed", res);
            }
        });

        if (cart.isEmpty()) {
            btnCheckout.setDisable(true);
            cmbPromo.setDisable(true);
        }

        Button btnBack = new Button("Back to Dashboard");
        btnBack.setOnAction(e -> new DashboardView(stage, currentUser).show());
        
        root.getChildren().addAll(
            lblTitle, 
            listView, 
            new Separator(),
            lblTotalAmount,
            promoBox,
            new Separator(),
            btnCheckout, 
            btnBack
        );
        
        Scene scene = new Scene(root, 650, 700);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - My Cart");
        stage.show();
    }

    private void calculateAndSetTotal(List<CartItem> cart, Promo selectedPromo) {
        if (cart == null || cart.isEmpty()) {
            lblTotalAmount.setText("Total: Rp 0.00");
            return;
        }

        double subtotal = 0.0;
        for (CartItem item : cart) {
            Product p = productHandler.getProduct(item.getIdProduct());
            if (p != null) {
                subtotal += (p.getPrice() * item.getCount());
            }
        }

        double discountAmount = 0.0;
        double finalTotal = subtotal;

        if (selectedPromo != null) {
            // JIKA ADA VOUCHER
            discountAmount = subtotal * (selectedPromo.getDiscountPercentage() / 100.0);
            finalTotal = subtotal - discountAmount;
            
            lblTotalAmount.setText(String.format("Total: Rp %.2f (Disc: -Rp %.2f)", finalTotal, discountAmount));
            lblTotalAmount.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #E91E63;");
        } else {
            lblTotalAmount.setText(String.format("Total: Rp %.2f", finalTotal));
            lblTotalAmount.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}