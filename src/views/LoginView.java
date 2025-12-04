package views;

import controller.UserHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.User;

public class LoginView {
    private Stage stage;
    private UserHandler userHandler;

    public LoginView(Stage stage) {
        this.stage = stage;
        this.userHandler = new UserHandler();
    }

    public void show() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        Label lblTitle = new Label("JoyMarket Login");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Email Address");
        txtEmail.setMaxWidth(300);

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Password");
        txtPass.setMaxWidth(300);

        Button btnLogin = new Button("Login");
        btnLogin.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        btnLogin.setPrefWidth(100);

        Label lblRegister = new Label("Don't have an account?");
        Hyperlink linkRegister = new Hyperlink("Register Here");

        btnLogin.setOnAction(e -> {
            String email = txtEmail.getText();
            String pass = txtPass.getText();
            User user = userHandler.login(email, pass);

            if (user != null) {
                new DashboardView(stage, user).show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Email or Password");
            }
        });

        linkRegister.setOnAction(e -> {
            new RegisterView(stage).show();
        });

        root.getChildren().addAll(lblTitle, new Label("Email:"), txtEmail, new Label("Password:"), txtPass, btnLogin, lblRegister, linkRegister);

        Scene scene = new Scene(root, 400, 500);
        stage.setScene(scene);
        stage.setTitle("JoyMarket - Login");
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