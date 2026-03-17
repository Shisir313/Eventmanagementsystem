package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Event;
import service.AuthenticationService;
import service.EventService;

import java.time.LocalDate;
import java.util.List;

/**
 * Organizer UI: login, create and update events.
 */
public class OrganizerUI {
    private static AuthenticationService authService = new AuthenticationService();
    private static EventService eventService = new EventService();

    public static void show() {
        Stage stage = new Stage();
        stage.setTitle("Organizer Portal");

        Label idLabel = new Label("Organizer ID:");
        TextField idField = new TextField();
        Button loginBtn = new Button("Login");

        HBox loginBox = new HBox(10, idLabel, idField, loginBtn);
        loginBox.setAlignment(Pos.CENTER_LEFT);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        Label nameLabel = new Label("Event Name:");
        TextField nameField = new TextField();
        Label dateLabel = new Label("Event Date:");
        DatePicker datePicker = new DatePicker();

        Button createBtn = new Button("Create Event");
        Button updateBtn = new Button("Update Event");

        form.add(nameLabel, 0, 0);
        form.add(nameField, 1, 0);
        form.add(dateLabel, 0, 1);
        form.add(datePicker, 1, 1);
        form.add(new HBox(10, createBtn, updateBtn), 1, 2);

        TableView<Event> table = new TableView<>();
        TableColumn<Event, Integer> idCol = new TableColumn<>("Event ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        TableColumn<Event, String> nameCol = new TableColumn<>("Event Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        TableColumn<Event, LocalDate> dateCol = new TableColumn<>("Event Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        table.getColumns().addAll(idCol, nameCol, dateCol);

        Button refreshBtn = new Button("Refresh Events");

        VBox root = new VBox(10, loginBox, form, refreshBtn, table);
        root.setPadding(new Insets(10));

        refreshBtn.setOnAction(e -> {
            List<Event> events = eventService.getAllEvents();
            ObservableList<Event> obs = FXCollections.observableArrayList(events);
            table.setItems(obs);
        });

        loginBtn.setOnAction(e -> {
            try {
                int organizerId = Integer.parseInt(idField.getText().trim());
                boolean ok = authService.loginOrganizer(organizerId);
                if (ok) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Login successful", ButtonType.OK);
                    a.showAndWait();
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Login failed", ButtonType.OK);
                    a.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Enter a valid numeric organizer ID", ButtonType.OK);
                a.showAndWait();
            }
        });

        createBtn.setOnAction(e -> {
            try {
                int organizerId = Integer.parseInt(idField.getText().trim());
                Event ev = new Event();
                ev.setEventName(nameField.getText().trim());
                ev.setEventDate(datePicker.getValue());
                ev.setOrganizerId(organizerId);
                int id = eventService.createEvent(ev);
                if (id > 0) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Event created (ID: " + id + ")", ButtonType.OK);
                    a.showAndWait();
                    refreshBtn.fire();
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Failed to create event", ButtonType.OK);
                    a.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Enter a valid numeric organizer ID", ButtonType.OK);
                a.showAndWait();
            }
        });

        updateBtn.setOnAction(e -> {
            Event selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Select an event to update", ButtonType.OK);
                a.showAndWait();
                return;
            }
            selected.setEventName(nameField.getText().trim());
            selected.setEventDate(datePicker.getValue());
            boolean ok = eventService.updateEvent(selected);
            if (ok) {
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Event updated", ButtonType.OK);
                a.showAndWait();
                refreshBtn.fire();
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR, "Failed to update event", ButtonType.OK);
                a.showAndWait();
            }
        });

        // initial load
        refreshBtn.fire();

        Scene scene = new Scene(root, 700, 500);
        stage.setScene(scene);
        stage.show();
    }
}
