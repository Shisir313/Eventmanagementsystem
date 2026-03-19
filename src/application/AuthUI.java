package application;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.User;
import service.AuthenticationService;

public class AuthUI {

    public static void show(Stage stage) {

        // ── LEFT: Brand panel ──────────────────────────────────
        VBox sidebar = new VBox(20);
        sidebar.setAlignment(Pos.CENTER);
        sidebar.setPrefWidth(340);
        sidebar.setMinWidth(340);
        sidebar.setStyle("-fx-background-color: #1A237E; -fx-padding: 40px;");

        Label icon = new Label("🎓");
        icon.setStyle("-fx-font-size: 52px;");

        Label appName = new Label("College Event\nManagement System");
        appName.setStyle("-fx-text-fill: white; -fx-font-size: 24px; " +
                         "-fx-font-weight: bold; -fx-text-alignment: center;");
        appName.setTextAlignment(TextAlignment.CENTER);
        appName.setWrapText(true);

        Label tagline = new Label("Stay connected.\nNever miss a campus event.");
        tagline.setStyle("-fx-text-fill: #C5CAE9; -fx-font-size: 13px; " +
                         "-fx-text-alignment: center;");
        tagline.setTextAlignment(TextAlignment.CENTER);
        tagline.setWrapText(true);

        VBox features = new VBox(10);
        features.setAlignment(Pos.CENTER_LEFT);
        features.setPadding(new Insets(20, 0, 0, 0));
        for (String f : new String[]{
                "📅  Browse upcoming events",
                "✅  Register with one click",
                "🔔  Get instant notifications",
                "📊  Manage & track attendance"}) {
            Label l = new Label(f);
            l.setStyle("-fx-text-fill: #C5CAE9; -fx-font-size: 13px;");
            features.getChildren().add(l);
        }
        sidebar.getChildren().addAll(icon, appName, tagline, features);

        // ── RIGHT: Login form ──────────────────────────────────
        VBox formArea = new VBox();
        formArea.setAlignment(Pos.CENTER);
        formArea.setStyle("-fx-background-color: #F5F6FA;");
        HBox.setHgrow(formArea, Priority.ALWAYS);

        VBox card = new VBox(14);
        card.setMaxWidth(370);
        card.setStyle("-fx-background-color: white; -fx-padding: 32px; " +
                      "-fx-background-radius: 12px; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 16, 0, 0, 4);");

        Label title = new Label("Welcome Back");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");

        Label sub = new Label("Sign in to your account to continue");
        sub.setStyle("-fx-text-fill: #757575; -fx-font-size: 13px;");

        // Role
        Label roleLabel = fieldLabel("Sign in as");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Student", "Organizer", "Admin");
        roleCombo.setValue("Student");
        roleCombo.setMaxWidth(Double.MAX_VALUE);
        styleCombo(roleCombo);

        // Email
        Label emailLabel = fieldLabel("Email Address");
        TextField emailField = new TextField();
        emailField.setPromptText("you@university.edu");
        emailField.setMaxWidth(Double.MAX_VALUE);
        styleField(emailField);

        // Password
        Label passLabel = fieldLabel("Password");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter your password");
        passField.setMaxWidth(Double.MAX_VALUE);
        styleField(passField);

        // Error label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #C62828; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setWrapText(true);

        // Login button
        Button loginBtn = new Button("Sign In →");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(42);
        stylePrimaryBtn(loginBtn);

        Label hint = new Label("New user? Ask your administrator to create an account.");
        hint.setStyle("-fx-font-size: 11px; -fx-text-fill: #9E9E9E;");
        hint.setWrapText(true);

        card.getChildren().addAll(
            title, sub, new Separator(),
            roleLabel, roleCombo,
            emailLabel, emailField,
            passLabel, passField,
            errorLabel, loginBtn, hint
        );
        formArea.getChildren().add(card);

        // Register button
        Button registerBtn = new Button("New User? Register Here");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setPrefHeight(38);
        registerBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #1A237E; " +
            "-fx-font-size: 13px; -fx-border-color: #1A237E; " +
            "-fx-border-width: 1.5px; -fx-border-radius: 8px; -fx-cursor: hand;");

        registerBtn.setOnAction(e -> showRegisterDialog(roleCombo.getValue()));

        // Add register button to the layout under the card
        formArea.getChildren().add(registerBtn);

        // ── Login action ───────────────────────────────────────
        loginBtn.setOnAction(e -> {
            String email    = emailField.getText().trim();
            String password = passField.getText();
            String role     = roleCombo.getValue();

            if (email.isEmpty() || password.isEmpty()) {
                showError(errorLabel, "Please fill in all fields."); return;
            }
            if (!email.contains("@")) {
                showError(errorLabel, "Please enter a valid email address."); return;
            }

            AuthenticationService auth = new AuthenticationService();
            User user = auth.authenticate(email, password, role);

            if (user != null) {
                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
                user.showDashboard();  // Polymorphism
                switch (role) {
                    case "Student"   -> StudentUI.show(user);
                    case "Organizer" -> OrganizerUI.show(user);
                    case "Admin"     -> AdminUI.show(user);
                }
            } else {
                showError(errorLabel, "Invalid email, password, or role.");
            }
        });

        passField.setOnAction(e -> loginBtn.fire());

        // ── Assemble ───────────────────────────────────────────
        HBox root = new HBox(sidebar, formArea);
        root.setMinSize(900, 580);

        Scene scene = new Scene(root, 900, 580);
        stage.setScene(scene);
        stage.setTitle("College Event Management System");
        stage.setResizable(true);
        stage.show();
    }

    // ── Helpers ────────────────────────────────────────────────
    private static void showError(Label label, String msg) {
        label.setText("⚠  " + msg);
        label.setVisible(true);
        label.setManaged(true);
    }

    private static Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-text-fill: #424242; -fx-font-size: 13px;");
        return l;
    }

    private static void styleField(Control field) {
        field.setStyle("-fx-background-color: #FAFAFA; -fx-border-color: #BDBDBD; " +
                       "-fx-border-width: 1.5px; -fx-border-radius: 8px; " +
                       "-fx-background-radius: 8px; -fx-padding: 10px 14px; " +
                       "-fx-font-size: 14px; -fx-pref-height: 42px;");
    }

    private static void styleCombo(ComboBox<?> combo) {
        combo.setStyle("-fx-background-color: #FAFAFA; -fx-border-color: #BDBDBD; " +
                       "-fx-border-width: 1.5px; -fx-border-radius: 8px; " +
                       "-fx-background-radius: 8px; -fx-pref-height: 42px;");
    }

    private static void stylePrimaryBtn(Button btn) {
        btn.setStyle("-fx-background-color: #1A237E; -fx-text-fill: white; " +
                     "-fx-font-size: 14px; -fx-font-weight: bold; " +
                     "-fx-background-radius: 8px; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #3949AB; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; " +
            "-fx-background-radius: 8px; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: #1A237E; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; " +
            "-fx-background-radius: 8px; -fx-cursor: hand;"));
    }

    // Registration dialog shown when user clicks Register
    private static void showRegisterDialog(String defaultRole) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Register New Account");

        // Buttons
        ButtonType registerType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Full name");
        styleField(nameField);

        TextField emailField = new TextField();
        emailField.setPromptText("email@university.edu");
        styleField(emailField);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        styleField(passField);

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Student", "Organizer", "Admin");
        roleCombo.setValue(defaultRole != null ? defaultRole : "Student");
        styleCombo(roleCombo);

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: #C62828;");
        msg.setVisible(false);

        grid.add(new Label("Full name"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Password"), 0, 2);
        grid.add(passField, 1, 2);
        grid.add(new Label("Role"), 0, 3);
        grid.add(roleCombo, 1, 3);
        grid.add(msg, 1, 4);

        // Enable/Disable register button based on validation
        Node registerButton = dialog.getDialogPane().lookupButton(registerType);
        registerButton.setDisable(true);

        Runnable validate = () -> {
            boolean ok = !nameField.getText().trim().isEmpty()
                      && !emailField.getText().trim().isEmpty()
                      && emailField.getText().contains("@")
                      && passField.getText().length() >= 4;
            registerButton.setDisable(!ok);
            if (!ok) {
                msg.setText("Please complete all fields. Password must be at least 4 characters.");
                msg.setVisible(true);
            } else {
                msg.setVisible(false);
            }
        };

        nameField.textProperty().addListener((s,o,n) -> validate.run());
        emailField.textProperty().addListener((s,o,n) -> validate.run());
        passField.textProperty().addListener((s,o,n) -> validate.run());

        dialog.getDialogPane().setContent(grid);

        // When register pressed, attempt registration
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerType) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String password = passField.getText();
                String role = roleCombo.getValue();

                AuthenticationService auth = new AuthenticationService();
                boolean ok = auth.register(name, email, password, role);
                if (!ok) {
                    msg.setText("Registration failed: email may already exist or database error.");
                    msg.setVisible(true);
                    return false;
                }
                return true;
            }
            return null;
        });

        // Show and wait
        dialog.showAndWait();
    }
}
