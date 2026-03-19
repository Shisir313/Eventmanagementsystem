package application;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
        content.setStyle("-fx-background-color: #F5F6FA; -fx-padding: 24px;");
        HBox.setHgrow(content, Priority.ALWAYS);

        // ── Sidebar ────────────────────────────────────────────
        VBox sidebar = new VBox(0);
        sidebar.setStyle("-fx-background-color: #1A237E; -fx-pref-width: 220px;");

        VBox header = new VBox(4);
        header.setStyle("-fx-background-color: #12175E; -fx-padding: 24px 16px;");
        header.setAlignment(Pos.CENTER);
        Label nameL = new Label(admin.getName());
        nameL.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label roleL = new Label("Admin");
        roleL.setStyle("-fx-text-fill: #9FA8DA; -fx-font-size: 12px;");
        header.getChildren().addAll(nameL, roleL);

        Button btnApprovals = StudentUI.navBtn("📋  Registrations", "navApprovals");
        Button btnAllEvents = StudentUI.navBtn("📅  All Events",    "navAllEvents");
        Region spacer       = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Button btnLogout    = StudentUI.navBtn("🚪  Logout",        "navLogout");

        sidebar.getChildren().addAll(header, btnApprovals, btnAllEvents, spacer, btnLogout);

        btnApprovals.setOnAction(e -> { StudentUI.show(approvalsPanel); StudentUI.hide(eventsPanel); });
        btnAllEvents.setOnAction(e -> { StudentUI.show(eventsPanel);    StudentUI.hide(approvalsPanel); });
        btnLogout.setOnAction(e    -> { admin.logout(); stage.close(); });

        HBox root = new HBox(sidebar, content);
        Scene scene = new Scene(root, 1100, 650);
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.show();
    }

    private static VBox buildApprovalsPanel(Admin admin,
                                             ApprovalService approvalSvc,
                                             RegistrationService regSvc) {
        VBox panel = new VBox(14);

        Label heading = new Label("Pending Registrations");
        heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");
        Label sub = new Label("Approve or reject student event registrations.");
        sub.setStyle("-fx-text-fill: #757575;");

        TableView<Registration> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        StudentUI.styleTable(table);

        StudentUI.addCol(table, "Reg ID",     "registrationId");
        StudentUI.addCol(table, "Student ID", "studentId");
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
            approvalSvc.approve(sel.getRegistrationId(), admin.getAdminId());
            sel.setStatus("Approved");
            table.refresh();
            StudentUI.setSuccess(feedback, "Registration #" + sel.getRegistrationId() + " approved.");
        });

        rejectBtn.setOnAction(e -> {
            Registration sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { StudentUI.setError(feedback, "Please select a registration."); return; }
            approvalSvc.reject(sel.getRegistrationId(), admin.getAdminId(), "Rejected by admin");
            sel.setStatus("Rejected");
            table.refresh();
            StudentUI.setError(feedback, "Registration #" + sel.getRegistrationId() + " rejected.");
        });

        HBox btnRow = new HBox(12, approveBtn, rejectBtn);
        panel.getChildren().addAll(heading, sub, table, btnRow, feedback);
        return panel;
    }

    private static VBox buildAllEventsPanel(EventService eventSvc) {
        VBox panel = new VBox(14);

        Label heading = new Label("All Events");
        heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");

        TableView<Event> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        StudentUI.styleTable(table);

        StudentUI.addCol(table, "ID",           "eventId");
        StudentUI.addCol(table, "Event Name",   "eventName");
        StudentUI.addCol(table, "Date",         "eventDate");
        StudentUI.addCol(table, "Location",     "location");
        StudentUI.addCol(table, "Organizer ID", "organizerId");
        StudentUI.addCol(table, "Status",       "status");

        table.setItems(FXCollections.observableArrayList(eventSvc.getAllEvents()));
        panel.getChildren().addAll(heading, table);
        return panel;
    }
}