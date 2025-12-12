package views;

import controller.UserHandler;
import entitymodel.Customer;
import entitymodel.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class TopUpView {
    private Stage stage;
    private User currentUser;
    private UserHandler userHandler;
    
    public TopUpView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.userHandler = new UserHandler();
    }
    
    public void show() {
        BorderPane root = new BorderPane();
        VBox centerBox = new VBox(20);
        centerBox.setPadding(new Insets(30));
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setStyle("-fx-background-color: #fcfcfc;");
        Label lblTitle = new Label("Top Up Balance");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitle.setStyle("-fx-text-fill: #333;");

        double currentBal = 0.0;
        if (currentUser instanceof Customer) {
            currentBal = ((Customer) currentUser).getBalance();
        }
        
        Label lblCurrent = new Label("Current Balance:");
        Label lblBalanceVal = new Label("Rp " + String.format("%.2f", currentBal));
        lblBalanceVal.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblBalanceVal.setStyle("-fx-text-fill: #2E7D32;"); 
        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        Label lblAmount = new Label("Top Up Amount:");
        TextField txtAmount = new TextField();
        txtAmount.setPromptText("Min Rp 10,000");
        txtAmount.setPrefWidth(200);

        grid.add(lblAmount, 0, 0);
        grid.add(txtAmount, 1, 0);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnProcess = new Button("Confirm Top Up");
        btnProcess.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        
        Button btnBack = new Button("Cancel");
        btnBack.setStyle("-fx-background-color: #ccc; -fx-text-fill: black; -fx-cursor: hand;");

        buttonBox.getChildren().addAll(btnBack, btnProcess);
        btnProcess.setOnAction(e -> {
            String amountStr = txtAmount.getText();
            if (!isNumeric(amountStr)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Amount must be a numeric value.");
                return;
            }
            double amount = Double.parseDouble(amountStr);
            if (amount < 10000) {
                showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Minimum top up amount is Rp 10,000.");
                return;
            }
            boolean success = userHandler.topUpBalance(currentUser.getId(), amount);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Top Up Successful!");
                if (currentUser instanceof Customer) {
                    Customer c = (Customer) currentUser;
                    c.setBalance(c.getBalance() + amount);
                }
                new DashboardView(stage, currentUser).show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Transaction Failed. Please try again.");
            }
        });

        btnBack.setOnAction(e -> {
            new DashboardView(stage, currentUser).show();
        });
        centerBox.getChildren().addAll(lblTitle, lblCurrent, lblBalanceVal, grid, buttonBox);
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 500, 500);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Top Up Balance");
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