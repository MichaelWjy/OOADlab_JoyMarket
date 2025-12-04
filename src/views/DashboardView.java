package views;

import controller.UserHandler;
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
    private UserHandler userHandler;

    public DashboardView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.userHandler = new UserHandler();
    }

    public void show() {
        // REFRESH USER DATA (Penting agar Balance selalu update setelah TopUp/Transaksi)
        User refreshedUser = userHandler.getUser(currentUser.getId());
        if (refreshedUser != null) {
            this.currentUser = refreshedUser;
        }
        
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
            Label lblBalance = new Label("Balance: Rp " + String.format("%.2f", ((Customer)currentUser).getBalance()));
            lblBalance.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;"); 
            welcomeBox.getChildren().addAll(lblWelcome, lblRole, lblBalance);
       } else {
            welcomeBox.getChildren().addAll(lblWelcome, lblRole);
       }

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
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
            // 1. Shop (Product List)
            Button btnShop = createMenuButton("Shop Products", "Browse & Add to Cart", "#4CAF50");
//            btnShop.setOnAction(e -> new ProductListView(stage, currentUser).show());
            menuGrid.add(btnShop, 0, 0);
            
            // 2. Cart
            Button btnCart = createMenuButton("My Cart", "Checkout & Place Order", "#2196F3");
            btnCart.setOnAction(e -> new CartView(stage, currentUser).show());
            menuGrid.add(btnCart, 1, 0);
            
            // 3. History
            Button btnHistory = createMenuButton("Order History", "Track your orders", "#FF9800");
//            btnHistory.setOnAction(e -> new OrderHistoryView(stage, currentUser).show());
            menuGrid.add(btnHistory, 0, 1);
            
            // 4. Top Up
            Button btnTopUp = createMenuButton("Top Up Balance", "Add funds to wallet", "#9C27B0");
            btnTopUp.setOnAction(e -> new TopUpView(stage, currentUser).show());
            menuGrid.add(btnTopUp, 1, 1);

        } else if (currentUser instanceof Admin) {
            // 1. Manage Products (Edit Stock)
            Button btnManageProd = createMenuButton("Manage Products", "Edit Stock & Details", "#3F51B5");
//            btnManageProd.setOnAction(e -> new AdminProductView(stage, currentUser).show());
            menuGrid.add(btnManageProd, 0, 0);
            
            // 2. Manage Orders (Assign Courier)
            Button btnManageOrder = createMenuButton("Manage Orders", "Assign Courier to Order", "#009688");
            btnManageOrder.setOnAction(e -> new AdminOrderView(stage, currentUser).show());
            menuGrid.add(btnManageOrder, 1, 0);
            
            // 3. View Couriers
            Button btnViewCouriers = createMenuButton("View Couriers", "List of all couriers", "#795548");
            // Optional: Buat View khusus list kurir jika diminta, atau tampilkan alert info
            btnViewCouriers.setOnAction(e -> showAlert("Info", "Use 'Manage Orders' to see couriers availability.")); 
            menuGrid.add(btnViewCouriers, 0, 1);
            
            Button btnAddPromo = createMenuButton("Add Promo", "Create New Promo Code", "#E91E63");
            btnAddPromo.setOnAction(e -> new AddPromoView(stage, currentUser).show());
            menuGrid.add(btnAddPromo, 1, 1);

        } else if (currentUser instanceof Courier) {
            // 1. Tasks
            Button btnTasks = createMenuButton("My Tasks", "Update Delivery Status", "#673AB7");
//            btnTasks.setOnAction(e -> new CourierTaskView(stage, currentUser).show());
            menuGrid.add(btnTasks, 0, 0);
            
            // 2. History
            Button btnDeliveryHist = createMenuButton("Delivery History", "Completed jobs", "#607D8B");
//            btnDeliveryHist.setOnAction(e -> new CourierHistoryView(stage, currentUser).show());
            menuGrid.add(btnDeliveryHist, 1, 0);
        }

        // ==========================================
        // 3. FOOTER (Edit Profile - All Roles)
        // ==========================================
        Button btnEditProfile = new Button("Edit My Profile");
        btnEditProfile.setMinWidth(250);
        btnEditProfile.setMinHeight(40);
        btnEditProfile.setStyle("-fx-background-color: #546E7A; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
        
        btnEditProfile.setOnAction(e -> {
            new EditProfileView(stage, currentUser).show();
        });

        centerBox.getChildren().addAll(lblMenuTitle, menuGrid, new Separator(), btnEditProfile);
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 900, 650);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Dashboard");
        stage.show();
    }

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