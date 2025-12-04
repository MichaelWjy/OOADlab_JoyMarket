package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.User;
import models.Customer;
import models.Admin;
import models.Courier;

public class DashboardView {
    private Stage stage;
    private User currentUser;

    public DashboardView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
    }

    public void show() {
        BorderPane root = new BorderPane();

        // ==========================================
        // 1. HEADER (Welcome & Logout)
        // ==========================================
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
        header.setAlignment(Pos.CENTER_LEFT);

        VBox welcomeBox = new VBox(3);
        Label lblWelcome = new Label("Welcome, " + currentUser.getName());
        lblWelcome.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label lblRole = new Label("Role: " + currentUser.getRole());
        lblRole.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        if (currentUser instanceof Customer) {
            Label lblBalance = new Label("Balance: Rp " + ((Customer)currentUser).getBalance());
            lblBalance.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;"); // Styling Balance
            
            // Jika Customer, tambahkan 3 label
            welcomeBox.getChildren().addAll(lblWelcome, lblRole, lblBalance);
       } else {
            // Jika bukan Customer (Admin/Courier), tambahkan 2 label saja
            welcomeBox.getChildren().addAll(lblWelcome, lblRole);
       }

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold;");
        btnLogout.setOnAction(e -> {
            new LoginView(stage).show();
        });

        header.getChildren().addAll(welcomeBox, spacer, btnLogout);
        root.setTop(header);

        // ==========================================
        // 2. CENTER (Menu Grid based on Use Case)
        // ==========================================
        VBox centerBox = new VBox(25);
        centerBox.setPadding(new Insets(30));
        centerBox.setAlignment(Pos.CENTER);

        Label lblMenuTitle = new Label("Main Menu");
        lblMenuTitle.setFont(new Font("Arial", 22));

        GridPane menuGrid = new GridPane();
        menuGrid.setHgap(20);
        menuGrid.setVgap(20);
        menuGrid.setAlignment(Pos.CENTER);

        // --- LOGIC MENU DINAMIS BERDASARKAN ROLE ---
        
        if (currentUser instanceof Customer) {
            // [Use Case: Customer]
            // 1. Shop (View Products)
            menuGrid.add(createMenuButton("Product", "View products & add to cart", "#4CAF50"), 0, 0);
            
            // 2. Cart (View Cart, Edit Cart, Checkout)
            menuGrid.add(createMenuButton("My Cart", "Checkout & Place Order", "#2196F3"), 1, 0);
            
            // 3. History (View Order History)
            menuGrid.add(createMenuButton("Order History", "Track your orders", "#FF9800"), 0, 1);
            
            // 4. Top Up (Top Up Balance)
            Button btnTopUp = createMenuButton("Top Up Balance", "Add funds to wallet", "#9C27B0");
            btnTopUp.setOnAction(e -> {
            	 new TopUpView(stage, currentUser).show();
            });
            menuGrid.add(btnTopUp, 1, 1);

        } else if (currentUser instanceof Admin) {
            // [Use Case: Admin]
            // 1. Products (Edit Product Stock)
            menuGrid.add(createMenuButton("Manage Products", "Edit Stock & Details", "#3F51B5"), 0, 0);
            
            // 2. Orders (View All Orders, Assign Courier)
            menuGrid.add(createMenuButton("Manage Orders", "Assign Courier to Order", "#009688"), 1, 0);
            
            // 3. Couriers (View All Couriers)
            menuGrid.add(createMenuButton("View Couriers", "List of all couriers", "#795548"), 0, 1);

        } else if (currentUser instanceof Courier) {
            // [Use Case: Courier]
            // 1. Tasks (View Assigned Deliveries, Edit Status)
            menuGrid.add(createMenuButton("My Tasks", "Update Delivery Status", "#673AB7"), 0, 0);
            
            // 2. History
            menuGrid.add(createMenuButton("Delivery History", "Completed jobs", "#607D8B"), 1, 0);
        }

        // ==========================================
        // 3. FOOTER (Edit Profile - All Roles)
        // ==========================================
        // Sesuai Use Case: Semua user (User -> Admin/Cust/Courier) bisa Edit Profile
        
        Button btnEditProfile = new Button("Edit My Profile");
        btnEditProfile.setMinWidth(250);
        btnEditProfile.setMinHeight(40);
        btnEditProfile.setStyle("-fx-background-color: #546E7A; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        btnEditProfile.setOnAction(e -> {
            // Pindah ke Halaman Edit Profile
            new EditProfileView(stage, currentUser).show();
        });

        centerBox.getChildren().addAll(lblMenuTitle, menuGrid, new Separator(), btnEditProfile);
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 900, 650);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Dashboard");
        stage.show();
    }

    // Helper untuk membuat tombol menu yang seragam
    private Button createMenuButton(String title, String subtitle, String colorHex) {
        VBox btnContent = new VBox(5);
        btnContent.setAlignment(Pos.CENTER);
        
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white;");
        
        Label lblSub = new Label(subtitle);
        lblSub.setStyle("-fx-font-size: 10px; -fx-text-fill: white;");
        
        btnContent.getChildren().addAll(lblTitle, lblSub);
        
        Button btn = new Button();
        btn.setGraphic(btnContent);
        btn.setMinWidth(180);
        btn.setMinHeight(100);
        btn.setStyle(String.format("-fx-background-color: %s; -fx-cursor: hand;", colorHex));
        
        // Hover Effect
        btn.setOnMouseEntered(e -> btn.setStyle(String.format("-fx-background-color: derive(%s, 20%%);", colorHex)));
        btn.setOnMouseExited(e -> btn.setStyle(String.format("-fx-background-color: %s;", colorHex)));
        
        return btn;
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}