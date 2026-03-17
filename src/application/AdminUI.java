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
import service.ApprovalService;
import service.AuthenticationService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin UI for approving/rejecting registrations.
 */
public class AdminUI {
    private static AuthenticationService authService = new AuthenticationService();
    private static ApprovalService approvalService = new ApprovalService();

    public static class PendingRegistration {
        private int registrationId;
        private int studentId;
        private int eventId;
        private String registrationDate;

        public PendingRegistration(int registrationId, int studentId, int eventId, String registrationDate) {
            this.registrationId = registrationId;
            this.studentId = studentId;
            this.eventId = eventId;
            this.registrationDate = registrationDate;
        }

        public int getRegistrationId() { return registrationId; }
        public int getStudentId() { return studentId; }
        public int getEventId() { return eventId; }
        public String getRegistrationDate() { return registrationDate; }
    }

    public static void show() {
        Stage stage = new Stage();
        stage.setTitle("Admin Portal");

        Label idLabel = new Label("Admin ID:");
        TextField idField = new TextField();
        Button loginBtn = new Button("Login");

        HBox loginBox = new HBox(10, idLabel, idField, loginBtn);
        loginBox.setAlignment(Pos.CENTER_LEFT);

        TableView<PendingRegistration> table = new TableView<>();
        TableColumn<PendingRegistration, Integer> regCol = new TableColumn<>("Registration ID");
        regCol.setCellValueFactory(new PropertyValueFactory<>("registrationId"));
        TableColumn<PendingRegistration, Integer> studentCol = new TableColumn<>("Student ID");
        studentCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        TableColumn<PendingRegistration, Integer> eventCol = new TableColumn<>("Event ID");
        eventCol.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        TableColumn<PendingRegistration, String> dateCol = new TableColumn<>("Registration Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));

        table.getColumns().addAll(regCol, studentCol, eventCol, dateCol);

        Button refreshBtn = new Button("Refresh Pending");
        Button approveBtn = new Button("Approve");
        Button rejectBtn = new Button("Reject");

        HBox actions = new HBox(10, approveBtn, rejectBtn);
        actions.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, loginBox, refreshBtn, table, actions);
        root.setPadding(new Insets(10));

        refreshBtn.setOnAction(e -> {
            try {
                ResultSet rs = approvalService.getPendingRegistrations();
                List<PendingRegistration> list = new ArrayList<>();
                while (rs.next()) {
                    int rid = rs.getInt("RegistrationID");
                    int sid = rs.getInt("StudentID");
                    int eid = rs.getInt("EventID");
                    String rdate = rs.getDate("RegistrationDate").toString();
                    list.add(new PendingRegistration(rid, sid, eid, rdate));
                }
                ObservableList<PendingRegistration> obs = FXCollections.observableArrayList(list);
                table.setItems(obs);
                rs.getStatement().getConnection().close(); // close connection after read
            } catch (SQLException ex) {
                ex.printStackTrace();
                Alert a = new Alert(Alert.AlertType.ERROR, "Failed to fetch pending registrations", ButtonType.OK);
                a.showAndWait();
            }
        });

        loginBtn.setOnAction(e -> {
            try {
                int adminId = Integer.parseInt(idField.getText().trim());
                boolean ok = authService.loginAdmin(adminId);
                if (ok) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Login successful", ButtonType.OK);
                    a.showAndWait();
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Login failed", ButtonType.OK);
                    a.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Enter a valid numeric admin ID", ButtonType.OK);
                a.showAndWait();
            }
        });

        approveBtn.setOnAction(e -> {
            PendingRegistration pr = table.getSelectionModel().getSelectedItem();
            if (pr == null) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Select a registration to approve", ButtonType.OK);
                a.showAndWait();
                return;
            }
            try {
                int adminId = Integer.parseInt(idField.getText().trim());
                boolean ok = approvalService.approveRegistration(pr.getRegistrationId(), adminId);
                if (ok) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Approved", ButtonType.OK);
                    a.showAndWait();
                    refreshBtn.fire();
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Failed to approve", ButtonType.OK);
                    a.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Enter a valid numeric admin ID", ButtonType.OK);
                a.showAndWait();
            }
        });

        rejectBtn.setOnAction(e -> {
            PendingRegistration pr = table.getSelectionModel().getSelectedItem();
            if (pr == null) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Select a registration to reject", ButtonType.OK);
                a.showAndWait();
                return;
            }
            try {
                int adminId = Integer.parseInt(idField.getText().trim());
                boolean ok = approvalService.rejectRegistration(pr.getRegistrationId(), adminId);
                if (ok) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Rejected", ButtonType.OK);
                    a.showAndWait();
                    refreshBtn.fire();
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Failed to reject", ButtonType.OK);
                    a.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Enter a valid numeric admin ID", ButtonType.OK);
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
