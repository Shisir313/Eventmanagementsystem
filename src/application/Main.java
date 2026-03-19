package application;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import database.DBConnection;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        // Ensure database can be reached early and show a friendly alert if not.
        try {
            if (DBConnection.getInstance().getConnection() == null) {
                showDbErrorAndExit("Unable to obtain a database connection.\nPlease check your database server and credentials.");
                return;
            }
        } catch (Exception ex) {
            showDbErrorAndExit("Database initialization failed: " + ex.getMessage());
            return;
        }

        try {
            AuthUI.show(primaryStage);
        } catch (Exception ex) {
            // Show an alert so the developer/user sees what went wrong instead of a silent crash.
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Startup Error");
            a.setHeaderText("Failed to start application");
            a.setContentText(ex.getMessage());
            a.showAndWait();
            ex.printStackTrace();
        }
    }

    private void showDbErrorAndExit(String msg) {
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle("Database Error");
        a.setHeaderText("Database connection problem");
        a.setContentText(msg);
        a.showAndWait();
        // Exit the JavaFX application cleanly
        System.exit(1);
    }

    @Override
    public void stop() throws Exception {
        // Close DB connection when app stops
        try {
            DBConnection.getInstance().close();
        } catch (Exception ignored) {}
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}