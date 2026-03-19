package application;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;
import service.EventService;
import java.time.LocalDate;

public class OrganizerUI {

    public static void show(User user) {
        Organizer organizer = (Organizer) user;

        System.out.println("OrganizerUI.show: opening organizer UI for id=" + organizer.getOrganizerId() + ", name=" + organizer.getName());

        Stage stage = new Stage();
        stage.setTitle("Organizer Portal — " + organizer.getName());

        EventService eventSvc = new EventService();

        VBox sidebar = StudentUI.buildSidebar(organizer.getName(), "Organizer");

        VBox eventsPanel = buildEventsPanel(organizer, eventSvc);
        VBox createPanel = buildCreatePanel(organizer, eventSvc, eventsPanel);
        createPanel.setVisible(false);
        createPanel.setManaged(false);

        StackPane content = new StackPane(eventsPanel, createPanel);
        content.setStyle("-fx-background-color: #F5F6FA; -fx-padding: 24px;");
        HBox.setHgrow(content, Priority.ALWAYS);

        Button btnMyEvents = StudentUI.navBtn("📅  My Events",     "navMyEvents");
        Button btnCreate   = StudentUI.navBtn("➕  Create Event",  "navCreate");
        Region spacer      = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Button btnLogout   = StudentUI.navBtn("🚪  Logout",        "navLogout");

        // Replace sidebar buttons
        sidebar.getChildren().clear();
        VBox header = new VBox(4);
        header.setStyle("-fx-background-color: #12175E; -fx-padding: 24px 16px;");
        header.setAlignment(Pos.CENTER);
        Label nameL = new Label(organizer.getName());
        nameL.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label roleL = new Label("Organizer");
        roleL.setStyle("-fx-text-fill: #9FA8DA; -fx-font-size: 12px;");
        header.getChildren().addAll(nameL, roleL);
        sidebar.getChildren().addAll(header, btnMyEvents, btnCreate, spacer, btnLogout);

        btnMyEvents.setOnAction(e -> { StudentUI.show(eventsPanel); StudentUI.hide(createPanel); });
        btnCreate.setOnAction(e   -> { StudentUI.show(createPanel); StudentUI.hide(eventsPanel); });
        btnLogout.setOnAction(e   -> { organizer.logout(); stage.close(); });

        HBox root = new HBox(sidebar, content);
        Scene scene = new Scene(root, 1000, 620);
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.show();
    }

    private static VBox buildEventsPanel(Organizer organizer, EventService eventSvc) {
        VBox panel = new VBox(14);

        Label heading = new Label("My Events");
        heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");

        TableView<Event> table = new TableView<>();
        table.setId("tblOrganizerEvents");
        // allow other components to identify the organizer that owns this table
        table.setUserData(organizer.getOrganizerId());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        StudentUI.styleTable(table);

        StudentUI.addCol(table, "ID",         "eventId");
        StudentUI.addCol(table, "Event Name", "eventName");
        StudentUI.addCol(table, "Date",       "eventDate");
        StudentUI.addCol(table, "Location",   "location");
        StudentUI.addCol(table, "Status",     "status");

        table.setItems(FXCollections.observableArrayList(
            eventSvc.getEventsByOrganizer(organizer.getOrganizerId())
        ));

        Label feedback = new Label();
        Button deleteBtn = new Button("🗑  Delete Selected Event");
        StudentUI.styleDangerBtn(deleteBtn);
        deleteBtn.setOnAction(e -> {
            Event sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { StudentUI.setError(feedback, "Please select an event."); return; }
            eventSvc.deleteEvent(sel.getEventId());
            table.getItems().remove(sel);
            StudentUI.setSuccess(feedback, "Event deleted.");
        });

        panel.getChildren().addAll(heading, table, deleteBtn, feedback);
        return panel;
    }

    private static VBox buildCreatePanel(Organizer organizer, EventService eventSvc, VBox eventsPanel) {
        VBox panel = new VBox(12);
        panel.setMaxWidth(460);

        Label heading = new Label("Create New Event");
        heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");

        TextField nameField = new TextField();
        nameField.setPromptText("Event name");

        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(7));

        TextField locationField = new TextField();
        locationField.setPromptText("Location / venue");

        TextArea descField = new TextArea();
        descField.setPromptText("Event description");
        descField.setPrefRowCount(3);

        Label feedback = new Label();
        feedback.setWrapText(true);

        Button createBtn = new Button("Create Event");
        StudentUI.stylePrimaryBtn(createBtn);

        createBtn.setOnAction(e -> {
            String name     = nameField.getText().trim();
            String location = locationField.getText().trim();
            LocalDate date  = datePicker.getValue();

            if (name.isEmpty() || location.isEmpty() || date == null) {
                StudentUI.setError(feedback, "Please fill in all required fields."); return;
            }

            Event newEvent = new Event(0, name, date, location,
                                       descField.getText().trim(),
                                       organizer.getOrganizerId());
            if (eventSvc.createEvent(newEvent)) {
                StudentUI.setSuccess(feedback, "Event \"" + name + "\" created!");
                nameField.clear(); locationField.clear(); descField.clear();
                // Refresh the organizer's events table (if visible) so the newly created event appears
                if (eventsPanel.getParent() instanceof StackPane) {
                    StackPane sp = (StackPane) eventsPanel.getParent();
                    @SuppressWarnings("unchecked")
                    TableView<Event> eventsTbl = (TableView<Event>) sp.lookup("#tblOrganizerEvents");
                    java.util.List<Event> fresh = eventSvc.getEventsByOrganizer(organizer.getOrganizerId());
                    System.out.println("OrganizerUI: created event, organizerId=" + organizer.getOrganizerId() + ", reloaded events=" + fresh.size());
                    if (eventsTbl != null) {
                        eventsTbl.setItems(FXCollections.observableArrayList(fresh));
                        eventsTbl.refresh();
                        System.out.println("OrganizerUI: events table refreshed in UI.");
                        // Switch to the events panel so organizer sees the new event immediately
                        if (eventsTbl.getParent() instanceof VBox) {
                            VBox eventsVBox = (VBox) eventsTbl.getParent();
                            StudentUI.show(eventsVBox);
                            StudentUI.hide(panel);
                        }
                    } else {
                        System.out.println("OrganizerUI: events table not found in parent StackPane (null).");
                    }
                }
            } else {
                StudentUI.setError(feedback, "Failed to create event. Try again.");
            }
        });

        panel.getChildren().addAll(
            heading, new Separator(),
            new Label("Event Name"), nameField,
            new Label("Event Date"), datePicker,
            new Label("Location"),   locationField,
            new Label("Description"), descField,
            createBtn, feedback
        );
        return panel;
    }
}