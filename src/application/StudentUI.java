package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Event;
import model.Registration;
import service.AuthenticationService;
import service.EventService;
import service.RegistrationService;

import java.time.LocalDate;
import java.util.List;

/**
 * Student UI window. Allows student login, viewing events and registering.
 */
public class StudentUI {
    private static AuthenticationService authService = new AuthenticationService();
    private static EventService eventService = new EventService();
    private static RegistrationService registrationService = new RegistrationService();

    public static void show() {
        Stage stage = new Stage();
        stage.setTitle("Student Portal");

        Label idLabel = new Label("Student ID:");
        TextField idField = new TextField();
        Button loginBtn = new Button("Login");

        HBox loginBox = new HBox(10, idLabel, idField, loginBtn);
        loginBox.setAlignment(Pos.CENTER_LEFT);

        TableView<Event> table = new TableView<>();
        TableColumn<Event, Integer> idCol = new TableColumn<>("Event ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        TableColumn<Event, String> nameCol = new TableColumn<>("Event Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        TableColumn<Event, LocalDate> dateCol = new TableColumn<>("Event Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("eventDate"));

        table.getColumns().addAll(idCol, nameCol, dateCol);
        table.setPlaceholder(new Label("No events available"));

        Button refreshBtn = new Button("Refresh Events");
        Button registerBtn = new Button("Register");

        HBox actions = new HBox(10, refreshBtn, registerBtn);
        actions.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, loginBox, table, actions);
        root.setPadding(new Insets(10));

        // Load events into table
        refreshBtn.setOnAction(e -> {
            List<Event> events = eventService.getAllEvents();
            ObservableList<Event> obs = FXCollections.observableArrayList(events);
            table.setItems(obs);
        });

        loginBtn.setOnAction(e -> {
            try {
                int studentId = Integer.parseInt(idField.getText().trim());
                boolean ok = authService.loginStudent(studentId);
                if (ok) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Login successful", ButtonType.OK);
                    a.showAndWait();
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Login failed", ButtonType.OK);
                    a.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Enter a valid numeric student ID", ButtonType.OK);
                a.showAndWait();
            }
        });

        registerBtn.setOnAction(e -> {
            Event selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Select an event to register", ButtonType.OK);
                a.showAndWait();
                return;
            }
            try {
                int studentId = Integer.parseInt(idField.getText().trim());
                Registration reg = new Registration();
                reg.setStudentId(studentId);
                reg.setEventId(selected.getEventId());
                reg.setRegistrationDate(LocalDate.now());
                int regId = registrationService.registerStudentForEvent(reg);
                if (regId > 0) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Registered (ID: " + regId + ")", ButtonType.OK);
                    a.showAndWait();
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Registration failed or already registered", ButtonType.OK);
                    a.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Enter a valid numeric student ID", ButtonType.OK);
                a.showAndWait();
            }
        });

        // initial load
        refreshBtn.fire();

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();
    }
}
