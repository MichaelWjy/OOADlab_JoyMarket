package views;

import controller.PromoHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.User;

public class AddPromoView {
    private Stage stage;
    private User currentUser;
    private PromoHandler promoHandler;

    public AddPromoView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.promoHandler = new PromoHandler();
    }

    public void show() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #fcfcfc;");

        Label lblTitle = new Label("Create New Promo");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblTitle.setStyle("-fx-text-fill: #333;");

        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        // Inputs
        TextField txtCode = new TextField();
        txtCode.setPromptText("e.g., SALE10");
        
        TextField txtHeadline = new TextField();
        txtHeadline.setPromptText("e.g., 10% Off All Items");
        
        TextField txtDiscount = new TextField();
        txtDiscount.setPromptText("1 - 100");

        grid.add(new Label("Promo Code:"), 0, 0);   grid.add(txtCode, 1, 0);
        grid.add(new Label("Headline:"), 0, 1);     grid.add(txtHeadline, 1, 1);
        grid.add(new Label("Discount (%):"), 0, 2); grid.add(txtDiscount, 1, 2);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnSave = new Button("Create Promo");
        btnSave.setStyle("-fx-background-color: #E91E63; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        
        Button btnBack = new Button("Cancel");
        btnBack.setStyle("-fx-background-color: #ccc; -fx-text-fill: black; -fx-cursor: hand;");

        buttonBox.getChildren().addAll(btnBack, btnSave);

        // Actions
        btnSave.setOnAction(e -> {
            String code = txtCode.getText();
            String headline = txtHeadline.getText();
            String discountStr = txtDiscount.getText();

            // Validasi Numeric
            if (!isNumeric(discountStr)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Discount must be a number.");
                return;
            }

            double discount = Double.parseDouble(discountStr);
            String result = promoHandler.createPromo(code, headline, discount);

            if (result.equals("Success")) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Promo Created Successfully!");
                new DashboardView(stage, currentUser).show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", result);
            }
        });

        btnBack.setOnAction(e -> new DashboardView(stage, currentUser).show());

        root.getChildren().addAll(lblTitle, grid, buttonBox);

        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Add Promo");
        stage.show();
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
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