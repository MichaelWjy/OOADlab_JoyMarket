package views;

import controller.CartItemHandler;
import controller.ProductHanlder;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Product;
import models.User;

public class ProductListView {
    private Stage stage;
    private User currentUser;
    private ProductHanlder productHandler;
    private CartItemHandler cartHandler;
    
    public ProductListView(Stage stage, User currentUser, ProductHanlder productHandler, CartItemHandler cartHandler) {
		super();
		this.stage = stage;
		this.currentUser = currentUser;
		this.productHandler = productHandler;
		this.cartHandler = cartHandler;
	}
//    public ProductListView(Stage stage, User user) {
//        this.stage = stage;
//        this.currentUser = user;
//        this.productHandler = new ProductHandler();
//        this.cartHandler = new CartItemHandler();
//    }

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
            info.getChildren().addAll(
                new Label(p.getName() + " - " + p.getCategory()), 
                new Label("Rp " + p.getPrice() + " | Stock: " + p.getStock())
            );
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Spinner<Integer> spinQty = new Spinner<>(1, p.getStock(), 1);
            spinQty.setPrefWidth(70);
            
            Button btnAdd = new Button("Add to Cart");
            btnAdd.setOnAction(e -> {
                String res = cartHandler.addToCart(currentUser.getId(), p.getIdProduct(), spinQty.getValue());
                if(res.equals("Success")) {
                    new Alert(Alert.AlertType.INFORMATION, "Added to cart!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, res).show();
                }
            });

            if(p.getStock() <= 0) {
                btnAdd.setDisable(true);
                btnAdd.setText("Out of Stock");
            }

            row.getChildren().addAll(info, spacer, spinQty, btnAdd);
            listView.getItems().add(row);
        }

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> new DashboardView(stage, currentUser).show());

        root.getChildren().addAll(lblTitle, listView, btnBack);
        stage.setScene(new Scene(root, 600, 500));
        stage.show();
    }
}