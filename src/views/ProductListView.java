package views;

import controller.CartItemHandler;
import controller.ProductHanlder;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Admin;
import models.Customer;
import models.Product;
import models.User;

public class ProductListView {
    private Stage stage;
    private User currentUser;
    private ProductHanlder productHandler;
    private CartItemHandler cartHandler;

    public ProductListView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.productHandler = new ProductHanlder();
        this.cartHandler = new CartItemHandler();
    }

    public void show() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        Label lblTitle = new Label("Shop Products");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        ListView<HBox> listView = new ListView<>();
        
        for (Product p : productHandler.getAllProducts()) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            
            VBox info = new VBox(2);
            info.setMinWidth(200);
            info.getChildren().addAll(
                new Label(p.getName()), 
                new Label("Cat: " + p.getCategory() + " | Price: Rp " + p.getPrice())
            );
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            if (currentUser instanceof Admin) {
                Label lblStock = new Label("Stock:");
                TextField txtStock = new TextField(String.valueOf(p.getStock()));
                txtStock.setPrefWidth(60);
                Button btnUpdate = new Button("Update");
                
                btnUpdate.setOnAction(e -> {
                    String res = productHandler.updateStock(p.getIdProduct(), Integer.parseInt(txtStock.getText()));
                    new Alert(Alert.AlertType.INFORMATION, res).show();
                });
                row.getChildren().addAll(info, spacer, lblStock, txtStock, btnUpdate);

            } else if (currentUser instanceof Customer) {
                Label lblStockInfo = new Label("Stock: " + p.getStock());
                Spinner<Integer> spinQty = new Spinner<>(1, p.getStock(), 1);
                spinQty.setPrefWidth(70);
                Button btnAdd = new Button("Add to Cart");
                btnAdd.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                
                btnAdd.setOnAction(e -> {
                    String res = cartHandler.addToCart(currentUser.getId(), p.getIdProduct(), spinQty.getValue());
                    if(res.equals("Success")) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Added to cart!");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", res);
                    }
                });

                if(p.getStock() <= 0) {
                    btnAdd.setDisable(true);
                    btnAdd.setText("Out of Stock");
                    spinQty.setDisable(true);
                }

                row.getChildren().addAll(info, spacer, lblStockInfo, spinQty, btnAdd);
            }

            listView.getItems().add(row);
        }

        Button btnBack = new Button("Back to Dashboard");
        btnBack.setOnAction(e -> new DashboardView(stage, currentUser).show());

        root.getChildren().addAll(lblTitle, listView, btnBack);
        stage.setScene(new Scene(root, 650, 500));
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