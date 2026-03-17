package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.AuthenticationService;

/**
 * Simple authentication UI with login and registration tabs.
 * Uses AuthenticationService to interact with the database.
 */
public class AuthUI {
    private static AuthenticationService authService = new AuthenticationService();

    public static void show(Stage owner) {
        Stage stage = new Stage();
        stage.setTitle("Login / Register");

        TabPane tabs = new TabPane();

        // Login Tab
        GridPane loginGrid = new GridPane();
        loginGrid.setPadding(new Insets(10));
        loginGrid.setVgap(8);
        loginGrid.setHgap(10);

        Label roleLabel = new Label("Role:");
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Student", "Organizer", "Admin");
        roleBox.setValue("Student");

        Label idLabel = new Label("ID:");
        TextField idField = new TextField();

        Button loginBtn = new Button("Login");

        loginGrid.add(roleLabel, 0, 0);
        loginGrid.add(roleBox, 1, 0);
        loginGrid.add(idLabel, 0, 1);
        loginGrid.add(idField, 1, 1);
        loginGrid.add(loginBtn, 1, 2);

        loginBtn.setOnAction(e -> {
            String role = roleBox.getValue();
            try {
                int id = Integer.parseInt(idField.getText().trim());
                boolean ok = false;
                if ("Student".equals(role)) ok = authService.loginStudent(id);
                else if ("Organizer".equals(role)) ok = authService.loginOrganizer(id);
                else if ("Admin".equals(role)) ok = authService.loginAdmin(id);

                if (ok) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Login successful", ButtonType.OK);
                    a.showAndWait();
                    stage.close();
                    // open respective portal
                    if ("Student".equals(role)) StudentUI.show();
                    else if ("Organizer".equals(role)) OrganizerUI.show();
                    else AdminUI.show();
                } else {
                    String err = AuthenticationService.getLastError();
                    if (err != null) err = " (" + err + ")";
                    else err = "";
                    Alert a = new Alert(Alert.AlertType.ERROR, "Login failed - check ID" + err, ButtonType.OK);
                    a.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Enter a valid numeric ID", ButtonType.OK);
                a.showAndWait();
            }
        });

        Tab loginTab = new Tab("Login", loginGrid);
        loginTab.setClosable(false);

        // Register Tab
        GridPane regGrid = new GridPane();
        regGrid.setPadding(new Insets(10));
        regGrid.setVgap(8);
        regGrid.setHgap(10);

        Label regRoleLabel = new Label("Role:");
        ComboBox<String> regRoleBox = new ComboBox<>();
        regRoleBox.getItems().addAll("Student", "Organizer", "Admin");
        regRoleBox.setValue("Student");

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label contactLabel = new Label("Email / Contact:");
        TextField contactField = new TextField();

        Button registerBtn = new Button("Register");

        regGrid.add(regRoleLabel, 0, 0);
        regGrid.add(regRoleBox, 1, 0);
        regGrid.add(nameLabel, 0, 1);
        regGrid.add(nameField, 1, 1);
        regGrid.add(contactLabel, 0, 2);
        regGrid.add(contactField, 1, 2);
        regGrid.add(registerBtn, 1, 3);

        registerBtn.setOnAction(e -> {
            String role = regRoleBox.getValue();
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            if (name.isEmpty() || contact.isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Fill in all fields", ButtonType.OK);
                a.showAndWait();
                return;
            }

            int newId = -1;
            if ("Student".equals(role)) newId = authService.registerStudent(name, contact);
            else if ("Organizer".equals(role)) newId = authService.registerOrganizer(name, contact);
            else newId = authService.registerAdmin(name, contact);

            if (newId > 0) {
                // copy to clipboard
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(String.valueOf(newId));
                clipboard.setContent(content);

                // inform user and offer to open portal
                ButtonType openPortal = new ButtonType("Open Portal");
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Registered successfully. Your ID: " + newId + " (copied to clipboard)", ButtonType.OK, openPortal);
                a.setHeaderText("Registration complete");
                a.showAndWait().ifPresent(bt -> {
                    if (bt == openPortal) {
                        stage.close();
                        if ("Student".equals(role)) StudentUI.show();
                        else if ("Organizer".equals(role)) OrganizerUI.show();
                        else AdminUI.show();
                    }
                });

                // clear fields
                nameField.clear();
                contactField.clear();
            } else {
                String err = AuthenticationService.getLastError();
                if (err == null) err = "Unknown error (check DB and table schema).";
                Alert a = new Alert(Alert.AlertType.ERROR, "Registration failed: " + err, ButtonType.OK);
                a.showAndWait();
            }
        });

        Tab regTab = new Tab("Register", regGrid);
        regTab.setClosable(false);

        tabs.getTabs().addAll(loginTab, regTab);

        VBox root = new VBox(10, tabs);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 400, 250);
        stage.setScene(scene);
        stage.initOwner(owner);
        stage.show();
    }
}
