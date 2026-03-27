package application;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;
import service.EventService;
import service.RegistrationService;

public class StudentUI {

    public static void show(User user) {
        Student student = (Student) user;

        Stage stage = new Stage();
        stage.setTitle("Student Portal — " + student.getName());

        EventService        eventSvc = new EventService();
        RegistrationService regSvc   = new RegistrationService();

        // ── Sidebar ────────────────────────────────────────────
        VBox sidebar = buildSidebar(student.getName(), "Student");

        // ── Content panels ─────────────────────────────────────
        VBox eventsPanel = buildEventsPanel(student, eventSvc, regSvc);
        VBox myRegPanel  = buildMyRegPanel(student, regSvc);
        myRegPanel.setVisible(false);
        myRegPanel.setManaged(false);

        StackPane content = new StackPane(eventsPanel, myRegPanel);
        // changed light background to soft cream to match dark-green/gold theme
        content.setStyle("-fx-background-color: #F5F3E7; -fx-padding: 24px;");
        HBox.setHgrow(content, Priority.ALWAYS);

        // ── Nav buttons ────────────────────────────────────────
        Button btnEvents = (Button) sidebar.lookup("#navEvents");
        Button btnMyReg  = (Button) sidebar.lookup("#navMyReg");
        Button btnLogout = (Button) sidebar.lookup("#navLogout");

        // Refresh data when panels are shown so status updates (approved/rejected)
        btnEvents.setOnAction(e -> {
            TableView<Event> tbl = (TableView<Event>) eventsPanel.lookup("#tblEvents");
            if (tbl != null) {
                tbl.setItems(FXCollections.observableArrayList(eventSvc.getAllEvents()));
            }
            show(eventsPanel); hide(myRegPanel);
        });
        btnMyReg.setOnAction(e  -> {
            TableView<Registration> tbl = (TableView<Registration>) myRegPanel.lookup("#tblMyReg");
            if (tbl != null) {
                tbl.setItems(FXCollections.observableArrayList(
                    regSvc.getRegistrationsByStudent(student.getStudentId())
                ));
            }
            show(myRegPanel);  hide(eventsPanel);
        });
        btnLogout.setOnAction(e -> { student.logout();  stage.close(); });

        HBox root = new HBox(sidebar, content);
        Scene scene = new Scene(root, 1000, 620);
        scene.getStylesheets().add(StudentUI.class.getResource("application.css").toExternalForm());
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.show();
    }

    // ── Events Panel ──────────────────────────────────────────
    private static VBox buildEventsPanel(Student student,
                                          EventService eventSvc,
                                          RegistrationService regSvc) {
        VBox panel = new VBox(14);

        Label heading = new Label("Upcoming Events");
        // use deep green for headings
        heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0B3D2E;");

        Label sub = new Label("Select an event and click Register.");
        sub.setStyle("-fx-text-fill: #757575;");

        TableView<Event> table = new TableView<>();
        table.setId("tblEvents");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        styleTable(table);

        addCol(table, "ID",         "eventId");
        addCol(table, "Event Name", "eventName");
        addCol(table, "Date",       "eventDate");
        addCol(table, "Location",   "location");
        addCol(table, "Status",     "status");

        table.setItems(FXCollections.observableArrayList(eventSvc.getAllEvents()));

        Label feedback = new Label();
        feedback.setWrapText(true);

        Button registerBtn = new Button("✅  Register for Selected Event");
        stylePrimaryBtn(registerBtn);
        registerBtn.setOnAction(e -> {
            Event selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                setError(feedback, "Please select an event first."); return;
            }
            boolean ok = regSvc.registerStudent(student.getStudentId(), selected.getEventId());
            if (ok) setSuccess(feedback, "Registered for: " + selected.getEventName());
            else    setError(feedback, "Already registered for this event.");

            // If the My Registrations panel is visible, refresh its data so student sees the
            // new registration immediately.
            StackPane parent = (StackPane) table.getParent();
            if (parent != null) {
                VBox myReg = (VBox) parent.lookup("#tblMyReg") != null ? (VBox) parent.lookup("#tblMyReg").getParent() : null;
                // above attempt is defensive; UI refresh on navigation is the primary mechanism
            }
        });

        panel.getChildren().addAll(heading, sub, table, registerBtn, feedback);
        return panel;
    }

    // ── My Registrations Panel ────────────────────────────────
    private static VBox buildMyRegPanel(Student student, RegistrationService regSvc) {
        VBox panel = new VBox(14);

        Label heading = new Label("My Registrations");
        heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0B3D2E;");

        TableView<Registration> table = new TableView<>();
        table.setId("tblMyReg");
        // Attach the student id so other components (admin) can refresh this specific table
        table.setUserData(student.getStudentId());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        styleTable(table);

        addCol(table, "Reg ID",   "registrationId");
        addCol(table, "Event ID", "eventId");
        addCol(table, "Date",     "registrationDate");
        addCol(table, "Status",   "status");

        table.setItems(FXCollections.observableArrayList(
            regSvc.getRegistrationsByStudent(student.getStudentId())
        ));

        Label feedback = new Label();
        Button cancelBtn = new Button("❌  Cancel Registration");
        styleDangerBtn(cancelBtn);
        cancelBtn.setOnAction(e -> {
            Registration sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { setError(feedback, "Please select a registration."); return; }
            regSvc.cancelRegistration(sel.getRegistrationId());
            table.getItems().remove(sel);
            setSuccess(feedback, "Registration cancelled.");
        });

        panel.getChildren().addAll(heading, table, cancelBtn, feedback);
        return panel;
    }

    // ── Sidebar ────────────────────────────────────────────────
    static VBox buildSidebar(String name, String role) {
        VBox sidebar = new VBox(0);
        // use dark green for sidebar background
        sidebar.setStyle("-fx-background-color: #083C29; -fx-pref-width: 220px; -fx-min-width: 200px;");

        VBox header = new VBox(4);
        // slightly darker header shade
        header.setStyle("-fx-background-color: #05291E; -fx-padding: 24px 16px;");
        header.setAlignment(Pos.CENTER);
        Label nameL = new Label(name);
        nameL.setStyle("-fx-text-fill: #F5F3E7; -fx-font-size: 14px; -fx-font-weight: bold;");
        nameL.setWrapText(true);
        Label roleL = new Label(role);
        roleL.setStyle("-fx-text-fill: #BFD6C6; -fx-font-size: 12px;");
        header.getChildren().addAll(nameL, roleL);

        Button btnEvents = navBtn("📅  Browse Events", "navEvents");
        Button btnMyReg  = navBtn("📋  My Registrations", "navMyReg");
        Region spacer    = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Button btnLogout = navBtn("🚪  Logout", "navLogout");

        sidebar.getChildren().addAll(header, btnEvents, btnMyReg, spacer, btnLogout);
        return sidebar;
    }

    // ── Shared helpers ─────────────────────────────────────────
    static Button navBtn(String text, String id) {
        Button b = new Button(text);
        b.setId(id);
        b.setMaxWidth(Double.MAX_VALUE);
        // use cream as label color, hover uses dark-green accent
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: #BFD6C6; " +
                   "-fx-font-size: 13px; -fx-padding: 12px 20px; -fx-alignment: center-left;");
        b.setOnMouseEntered(e -> b.setStyle(
            "-fx-background-color: #0F4A3B; -fx-text-fill: #F5F3E7; " +
            "-fx-font-size: 13px; -fx-padding: 12px 20px; -fx-alignment: center-left;"));
        b.setOnMouseExited(e -> b.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #BFD6C6; " +
            "-fx-font-size: 13px; -fx-padding: 12px 20px; -fx-alignment: center-left;"));
        return b;
    }

    static <T> void addCol(TableView<T> table, String title, String property) {
        TableColumn<T, Object> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        table.getColumns().add(col);
    }

    static void styleTable(TableView<?> table) {
        // use cream background and subtle green/gold border
        table.setStyle("-fx-background-color: #F5F3E7; -fx-border-color: rgba(212,175,55,0.14); " +
                       "-fx-border-radius: 8px;");
    }

    static void stylePrimaryBtn(Button b) {
        // change primary buttons to gold gradient with dark text to match application.css
        b.setStyle("-fx-background-color: linear-gradient(#D4AF37, #B8860B); -fx-text-fill: #072E22; " +
                   "-fx-font-weight: bold; -fx-background-radius: 8px; " +
                   "-fx-padding: 10px 20px; -fx-cursor: hand;");
    }

    static void styleDangerBtn(Button b) {
        b.setStyle("-fx-background-color: #C62828; -fx-text-fill: white; " +
                   "-fx-font-weight: bold; -fx-background-radius: 8px; " +
                   "-fx-padding: 10px 20px; -fx-cursor: hand;");
    }

    static void setSuccess(Label l, String msg) {
        l.setText("✅  " + msg);
        // keep green success color
        l.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13px;");
    }

    static void setError(Label l, String msg) {
        l.setText("⚠  " + msg);
        l.setStyle("-fx-text-fill: #C62828; -fx-font-size: 13px;");
    }

    static void show(VBox p) { p.setVisible(true);  p.setManaged(true); }
    static void hide(VBox p) { p.setVisible(false); p.setManaged(false); }
}