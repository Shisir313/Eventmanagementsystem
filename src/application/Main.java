package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Pos;


/**
 * Main entry point for the College Event Management System JavaFX application.
 * Shows the AuthUI at startup; portal buttons are available from AuthUI after login.
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Open authentication window first
            AuthUI.show(primaryStage);

            // Optionally keep a simple launcher window available (hidden by default)
            primaryStage.setTitle("College Event Management System - Launcher");

            Label title = new Label("College Event Management System (Launcher)");
            title.setStyle("-fx-font-size:16px; -fx-padding:10px;");

            Button studentBtn = new Button("Student Portal");
            studentBtn.setOnAction(e -> StudentUI.show());

            Button organizerBtn = new Button("Organizer Portal");
            organizerBtn.setOnAction(e -> OrganizerUI.show());

            Button adminBtn = new Button("Admin Portal");
            adminBtn.setOnAction(e -> AdminUI.show());

            VBox root = new VBox(10, title, studentBtn, organizerBtn, adminBtn);
            root.setAlignment(Pos.CENTER);
            root.setStyle("-fx-padding:20px;");

            Scene scene = new Scene(root, 400, 250);
            primaryStage.setScene(scene);
            // don't show the launcher by default; user will interact via AuthUI
            // primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}