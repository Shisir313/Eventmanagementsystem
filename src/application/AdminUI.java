package application;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;
import service.ApprovalService;
import service.EventService;
import service.RegistrationService;

public class AdminUI {

    public static void show(User user) {
        Admin admin = (Admin) user;

        Stage stage = new Stage();
        stage.setTitle("Admin Portal — " + admin.getName());

        ApprovalService     approvalSvc = new ApprovalService();
        RegistrationService regSvc      = new RegistrationService();
        EventService        eventSvc    = new EventService();

        VBox approvalsPanel = buildApprovalsPanel(admin, approvalSvc, regSvc);
        VBox eventsPanel    = buildAllEventsPanel(eventSvc);
        eventsPanel.setVisible(false);
        eventsPanel.setManaged(false);

        StackPane content = new StackPane(approvalsPanel, eventsPanel);
        content.setStyle("-fx-background-color: #F5F3E7; -fx-padding: 24px;");
        HBox.setHgrow(content, Priority.ALWAYS);

        // ── Sidebar ────────────────────────────────────────────
        VBox sidebar = new VBox(0);
        sidebar.setStyle("-fx-background-color: #083C29; -fx-pref-width: 220px;");

        VBox header = new VBox(4);
        header.setStyle("-fx-background-color: #05291E; -fx-padding: 24px 16px;");
        header.setAlignment(Pos.CENTER);
        Label nameL = new Label(admin.getName());
        nameL.setStyle("-fx-text-fill: #F5F3E7; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label roleL = new Label("Admin");
        roleL.setStyle("-fx-text-fill: #BFD6C6; -fx-font-size: 12px;");
        header.getChildren().addAll(nameL, roleL);

        Button btnApprovals = StudentUI.navBtn("📋  Registrations", "navApprovals");
        Button btnAllEvents = StudentUI.navBtn("📅  All Events",    "navAllEvents");
        Region spacer       = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Button btnLogout    = StudentUI.navBtn("🚪  Logout",        "navLogout");

        sidebar.getChildren().addAll(header, btnApprovals, btnAllEvents, spacer, btnLogout);

        btnApprovals.setOnAction(e -> { StudentUI.show(approvalsPanel); StudentUI.hide(eventsPanel); });
        btnAllEvents.setOnAction(e -> {
            // Refresh events table items so admin sees latest statuses
            TableView<Event> tbl = (TableView<Event>) eventsPanel.lookup("#tblAllEvents");
            if (tbl != null) {
                tbl.setItems(FXCollections.observableArrayList(eventSvc.getAllEvents()));
            }
            StudentUI.show(eventsPanel);    StudentUI.hide(approvalsPanel);
        });
        btnLogout.setOnAction(e    -> { admin.logout(); stage.close(); });

        HBox root = new HBox(sidebar, content);
        Scene scene = new Scene(root, 1100, 650);
        scene.getStylesheets().add(AdminUI.class.getResource("application.css").toExternalForm());
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.show();
    }

    private static VBox buildApprovalsPanel(Admin admin,
                                             ApprovalService approvalSvc,
                                             RegistrationService regSvc) {
        VBox panel = new VBox(14);

        Label heading = new Label("Pending Registrations");
        heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0B3D2E;");
        Label sub = new Label("Approve or reject student event registrations.");
        sub.setStyle("-fx-text-fill: #757575;");

        TableView<Registration> table = new TableView<>();
        table.setId("tblApprovals");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        StudentUI.styleTable(table);

        StudentUI.addCol(table, "Reg ID",     "registrationId");
        // Replace Student ID with Student Name column using RegistrationService helper
        TableColumn<Registration, String> studentNameCol = new TableColumn<>("Student Name");
        studentNameCol.setCellValueFactory(cellData -> {
            Registration reg = cellData.getValue();
            String name = regSvc.getStudentName(reg.getStudentId());
            return new ReadOnlyStringWrapper(name != null ? name : "(unknown)");
        });
        table.getColumns().add(studentNameCol);

        StudentUI.addCol(table, "Event ID",   "eventId");
        StudentUI.addCol(table, "Date",       "registrationDate");
        StudentUI.addCol(table, "Status",     "status");

        ObservableList<Registration> data = FXCollections.observableArrayList(
            regSvc.getAllPendingRegistrations()
        );
        table.setItems(data);

        Label feedback = new Label();
        feedback.setWrapText(true);

        Button approveBtn = new Button("✅  Approve");
        StudentUI.stylePrimaryBtn(approveBtn);

        Button rejectBtn = new Button("❌  Reject");
        StudentUI.styleDangerBtn(rejectBtn);

        approveBtn.setOnAction(e -> {
            Registration sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { StudentUI.setError(feedback, "Please select a registration."); return; }
            boolean ok = approvalSvc.approve(sel.getRegistrationId());
            if (ok) {
                // remove from pending list
                table.getItems().remove(sel);
                StudentUI.setSuccess(feedback, "Registration #" + sel.getRegistrationId() + " approved.");

                // Refresh student and organizer views when they navigate. If admin also wants to
                // view the student's registrations or related event list, they can toggle views.
                // Additionally, if there's an events table visible, refresh it to reflect any
                // status changes related to events (not registration statuses).
                StackPane parent = (StackPane) panel.getParent();
                if (parent != null) {
                    TableView<Event> eventsTbl = (TableView<Event>) parent.lookup("#tblAllEvents");
                    if (eventsTbl != null) eventsTbl.setItems(FXCollections.observableArrayList(new EventService().getAllEvents()));
                }
            } else {
                StudentUI.setError(feedback, "Failed to approve registration. Please try again.");
            }
        });

        rejectBtn.setOnAction(e -> {
            Registration sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { StudentUI.setError(feedback, "Please select a registration."); return; }
            boolean ok = approvalSvc.reject(sel.getRegistrationId(), "Rejected by admin");
            if (ok) {
                table.getItems().remove(sel);
                StudentUI.setError(feedback, "Registration #" + sel.getRegistrationId() + " rejected.");

                StackPane parent = (StackPane) panel.getParent();
                if (parent != null) {
                    TableView<Event> eventsTbl = (TableView<Event>) parent.lookup("#tblAllEvents");
                    if (eventsTbl != null) eventsTbl.setItems(FXCollections.observableArrayList(new EventService().getAllEvents()));
                }
            } else {
                StudentUI.setError(feedback, "Failed to reject registration. Please try again.");
            }
        });

        HBox btnRow = new HBox(12, approveBtn, rejectBtn);
        panel.getChildren().addAll(heading, sub, table, btnRow, feedback);
        return panel;
    }

    private static VBox buildAllEventsPanel(EventService eventSvc) {
        VBox panel = new VBox(14);

        Label heading = new Label("All Events");
        heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0B3D2E;");

        TableView<Event> table = new TableView<>();
        table.setId("tblAllEvents");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        StudentUI.styleTable(table);

        StudentUI.addCol(table, "ID",           "eventId");
        StudentUI.addCol(table, "Event Name",   "eventName");
        StudentUI.addCol(table, "Date",         "eventDate");
        StudentUI.addCol(table, "Location",     "location");
        StudentUI.addCol(table, "Organizer ID", "organizerId");
        StudentUI.addCol(table, "Status",       "status");

        // Load all events
        table.setItems(FXCollections.observableArrayList(eventSvc.getAllEvents()));

        // Feedback label
        Label feedback = new Label(); feedback.setWrapText(true);

        // Approve / Reject buttons for selected event (admin action)
        Button approveEventBtn = new Button("✅  Approve Event");
        StudentUI.stylePrimaryBtn(approveEventBtn);
        Button rejectEventBtn = new Button("❌  Reject Event");
        StudentUI.styleDangerBtn(rejectEventBtn);

        approveEventBtn.setOnAction(e -> {
            Event sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { StudentUI.setError(feedback, "Please select an event."); return; }
            if (!"PENDING".equalsIgnoreCase(sel.getStatus())) {
                StudentUI.setError(feedback, "Only pending events can be approved."); return;
            }
            boolean ok = eventSvc.updateStatus(sel.getEventId(), "APPROVED");
            if (ok) {
                sel.setStatus("APPROVED");
                table.refresh();
                StudentUI.setSuccess(feedback, "Event #" + sel.getEventId() + " approved.");
            } else {
                StudentUI.setError(feedback, "Failed to approve event. Please try again.");
            }
        });

        rejectEventBtn.setOnAction(e -> {
            Event sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { StudentUI.setError(feedback, "Please select an event."); return; }
            if (!"PENDING".equalsIgnoreCase(sel.getStatus())) {
                StudentUI.setError(feedback, "Only pending events can be rejected."); return;
            }
            boolean ok = eventSvc.updateStatus(sel.getEventId(), "REJECTED");
            if (ok) {
                sel.setStatus("REJECTED");
                table.refresh();
                StudentUI.setError(feedback, "Event #" + sel.getEventId() + " rejected.");
            } else {
                StudentUI.setError(feedback, "Failed to reject event. Please try again.");
            }
        });

        HBox btnRow = new HBox(12, approveEventBtn, rejectEventBtn);
        panel.getChildren().addAll(heading, table, btnRow, feedback);
        return panel;
    }
}
