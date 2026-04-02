import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class History {

    // Colors
    private static final String BG_PRIMARY    = "#1A1D20";
    private static final String BG_SECONDARY  = "#0D0F12";
    private static final String ACCENT        = "#E63946";
    private static final String TEXT_PRIMARY  = "#FFFFFF";
    private static final String TEXT_SECONDARY = "#A8B2C1";
    private static final String CARD_BG       = "#1e2428";
    private static final String CARD_HOVER    = "#252a2e";
    private static final String CARD_BORDER   = "#2a2d30";
    private static final String DIVIDER       = "#2a2d30";

    private Stage primaryStage;

    // ===== Data Models =====
    private static class Session {
        int id, duration, exerciseCount, setCount;
        String date, split;

        Session(int id, String date, String split, int duration,
                int exerciseCount, int setCount) {
            this.id            = id;
            this.date          = date;
            this.split         = split;
            this.duration      = duration;
            this.exerciseCount = exerciseCount;
            this.setCount      = setCount;
        }
    }

    private static class WorkoutSet {
        String exercise, set, weight, reps;

        WorkoutSet(String exercise, String set, String weight, String reps) {
            this.exercise = exercise;
            this.set      = set;
            this.weight   = weight;
            this.reps     = reps;
        }
    }

    // ===== Entry Point =====
    public Scene buildScene(Stage stage) {
        this.primaryStage = stage;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PRIMARY + ";");

        VBox topSection = new VBox(0);
        topSection.getChildren().addAll(buildTopBar(), buildNavBar());
        root.setTop(topSection);

        StackPane center = new StackPane();
        center.setStyle("-fx-background-color: " + BG_PRIMARY + ";");
        center.getChildren().add(buildHistoryList(center));
        root.setCenter(center);

        return new Scene(root, 900, 620);
    }

    // ===== Top Bar =====
    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(14, 20, 14, 20));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + BG_SECONDARY + ";");

        Label title = new Label("TaraGym");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 20));
        title.setTextFill(Color.web(ACCENT));

        Label sep = new Label("  |  ");
        sep.setTextFill(Color.web(TEXT_SECONDARY));

        Label screen = new Label("History");
        screen.setFont(Font.font("SansSerif", FontWeight.NORMAL, 14));
        screen.setTextFill(Color.web(TEXT_PRIMARY));

        bar.getChildren().addAll(title, sep, screen);
        return bar;
    }

    // ===== Nav Bar =====
    private HBox buildNavBar() {
        HBox nav = new HBox(0);
        nav.setAlignment(Pos.CENTER);
        nav.setStyle("-fx-background-color: " + BG_SECONDARY + ";");
        nav.setPadding(new Insets(8, 20, 8, 20));

        nav.getChildren().addAll(
                navButton("🏠 Home",     "home"),
                navButton("📋 History",  "history"),
                navButton("💪 Workout",  "workout"),
                navButton("📅 Schedule", "schedule"),
                navButton("📈 Progress", "progress"),
                navButton("⚙ Settings", "settings")
        );
        return nav;
    }

    private Button navButton(String text, String screen) {
        boolean isActive = screen.equals("history");
        Button btn = new Button(text);

        String activeStyle  = "-fx-background-color: " + ACCENT + "; -fx-text-fill: white;"
                + "-fx-font-size: 12px; -fx-padding: 8 16;"
                + "-fx-background-radius: 6; -fx-cursor: hand;";
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: " + TEXT_SECONDARY + ";"
                + "-fx-font-size: 12px; -fx-padding: 8 16;"
                + "-fx-background-radius: 6; -fx-cursor: hand;";
        String hoverStyle   = "-fx-background-color: #2a2d30; -fx-text-fill: white;"
                + "-fx-font-size: 12px; -fx-padding: 8 16;"
                + "-fx-background-radius: 6; -fx-cursor: hand;";

        btn.setStyle(isActive ? activeStyle : defaultStyle);

        if (!isActive) {
            btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
            btn.setOnMouseExited(e  -> btn.setStyle(defaultStyle));
        }

        btn.setOnAction(e -> Navigator.go(primaryStage, screen));
        return btn;
    }

    // ===== History List View =====
    private VBox buildHistoryList(StackPane container) {
        VBox view = new VBox(16);
        view.setPadding(new Insets(20));

        Label instruction = new Label("Click on a session to view its details.");
        instruction.setTextFill(Color.web(TEXT_SECONDARY));
        instruction.setFont(Font.font("SansSerif", 13));

        VBox cardsBox = new VBox(8);
        cardsBox.setPadding(new Insets(2, 0, 2, 0));

        List<Session> sessions = loadSessions();

        if (sessions.isEmpty()) {
            Label empty = new Label("No workout sessions yet! Start a workout to see your history.");
            empty.setTextFill(Color.web(TEXT_SECONDARY));
            empty.setFont(Font.font("SansSerif", 13));
            cardsBox.getChildren().add(empty);
        } else {
            for (Session s : sessions) {
                cardsBox.getChildren().add(buildSessionCard(s, container));
            }
        }

        ScrollPane scroll = new ScrollPane(cardsBox);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background: " + BG_PRIMARY + ";" +
                        "-fx-background-color: " + BG_PRIMARY + ";" +
                        "-fx-border-color: transparent;"
        );
        VBox.setVgrow(scroll, Priority.ALWAYS);

        view.getChildren().addAll(instruction, scroll);
        return view;
    }

    // ===== Session Card =====
    private HBox buildSessionCard(Session s, StackPane container) {
        HBox card = new HBox();
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 16, 14, 16));

        String baseStyle  =
                "-fx-background-color: " + CARD_BG + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + CARD_BORDER + ";" +
                        "-fx-border-radius: 10; -fx-border-width: 1; -fx-cursor: hand;";
        String hoverStyle =
                "-fx-background-color: " + CARD_HOVER + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + CARD_BORDER + ";" +
                        "-fx-border-radius: 10; -fx-border-width: 1; -fx-cursor: hand;";

        card.setStyle(baseStyle);

        // Left — info
        VBox left = new VBox(4);
        HBox.setHgrow(left, Priority.ALWAYS);

        Label dateLabel   = new Label(s.date);
        dateLabel.setTextFill(Color.web(TEXT_SECONDARY));
        dateLabel.setFont(Font.font("SansSerif", 11));

        Label splitLabel  = new Label(s.split);
        splitLabel.setTextFill(Color.web(TEXT_PRIMARY));
        splitLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));

        Label detailLabel = new Label(s.exerciseCount + " exercises  ·  " + s.setCount + " sets");
        detailLabel.setTextFill(Color.web(TEXT_SECONDARY));
        detailLabel.setFont(Font.font("SansSerif", 11));

        left.getChildren().addAll(dateLabel, splitLabel, detailLabel);

        // Right — duration badge
        Label badge = new Label(s.duration + " mins");
        badge.setTextFill(Color.web(ACCENT));
        badge.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        badge.setPadding(new Insets(4, 12, 4, 12));
        badge.setStyle(
                "-fx-background-color: rgba(230,57,70,0.15);" +
                        "-fx-background-radius: 6;"
        );

        card.getChildren().addAll(left, badge);
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e  -> card.setStyle(baseStyle));
        card.setOnMouseClicked(e ->
                container.getChildren().setAll(buildSessionDetail(container, s)));

        return card;
    }

    // ===== Session Detail View =====
    private VBox buildSessionDetail(StackPane container, Session s) {
        VBox view = new VBox(16);
        view.setPadding(new Insets(20));

        // Header
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        String btnBase  = "-fx-background-color: #2a2d30; -fx-text-fill: white;"
                + "-fx-font-size: 13px; -fx-padding: 8 20;"
                + "-fx-background-radius: 6; -fx-cursor: hand;";
        String btnHover = "-fx-background-color: #35383c; -fx-text-fill: white;"
                + "-fx-font-size: 13px; -fx-padding: 8 20;"
                + "-fx-background-radius: 6; -fx-cursor: hand;";

        Button backBtn = new Button("← Back");
        backBtn.setStyle(btnBase);
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(btnHover));
        backBtn.setOnMouseExited(e  -> backBtn.setStyle(btnBase));
        backBtn.setOnAction(e ->
                container.getChildren().setAll(buildHistoryList(container)));

        VBox titleBlock = new VBox(4);

        Label titleLbl = new Label("Session  —  " + s.split);
        titleLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
        titleLbl.setTextFill(Color.web(TEXT_PRIMARY));

        Label infoLbl = new Label(
                s.date + "   ·   " + s.duration + " mins   ·   " + s.setCount + " sets total");
        infoLbl.setFont(Font.font("SansSerif", 12));
        infoLbl.setTextFill(Color.web(TEXT_SECONDARY));

        titleBlock.getChildren().addAll(titleLbl, infoLbl);
        header.getChildren().addAll(backBtn, titleBlock);

        // Table
        TableView<WorkoutSet> table = buildDetailTable(s);
        VBox.setVgrow(table, Priority.ALWAYS);

        view.getChildren().addAll(header, table);
        return view;
    }

    // ===== Detail Table =====
    private TableView<WorkoutSet> buildDetailTable(Session s) {
        TableView<WorkoutSet> table = new TableView<>();
        table.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                        "-fx-border-color: " + DIVIDER + ";" +
                        "-fx-border-radius: 8; -fx-background-radius: 8;" +
                        "-fx-table-cell-border-color: " + DIVIDER + ";"
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(34);

        // Apply CSS
        table.getStylesheets().add("data:text/css," +
                ".table-view { -fx-background-color: #1e2428; }" +
                ".table-view .column-header { -fx-background-color: #0D0F12; }" +
                ".table-view .column-header .label { -fx-text-fill: " + TEXT_SECONDARY + ";" +
                "-fx-font-size: 12px; -fx-font-weight: bold; }" +
                ".table-view .table-cell { -fx-text-fill: white; -fx-font-size: 12px;" +
                "-fx-alignment: center; }" +
                ".table-view .table-row-cell { -fx-background-color: #1e2428; }" +
                ".table-view .table-row-cell:odd { -fx-background-color: #22272b; }" +
                ".table-view .table-row-cell:hover { -fx-background-color: #252a2e; }" +
                ".table-view .table-row-cell:selected { -fx-background-color: #2a2d30; }" +
                ".table-view .column-header-background { -fx-background-color: #0D0F12; }" +
                ".table-view .corner { -fx-background-color: #0D0F12; }"
        );

        TableColumn<WorkoutSet, String> colExercise = new TableColumn<>("Exercise");
        colExercise.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().exercise));

        TableColumn<WorkoutSet, String> colSet = new TableColumn<>("Set");
        colSet.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().set));
        colSet.setMaxWidth(80); colSet.setMinWidth(80);

        TableColumn<WorkoutSet, String> colWeight = new TableColumn<>("Weight");
        colWeight.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().weight));
        colWeight.setMaxWidth(120); colWeight.setMinWidth(120);

        TableColumn<WorkoutSet, String> colReps = new TableColumn<>("Reps");
        colReps.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().reps));
        colReps.setMaxWidth(100); colReps.setMinWidth(100);

        table.getColumns().addAll(colExercise, colSet, colWeight, colReps);
        table.getItems().addAll(loadWorkoutSets(s.id));
        return table;
    }

    // ===== Load Sessions from DB =====
    private List<Session> loadSessions() {
        List<Session> sessions = new ArrayList<>();
        int userId = DatabaseManager.getCurrentUserId();
        if (userId == -1) return sessions;

        String sql = "SELECT id, date, split, duration_mins FROM session " +
                "WHERE user_id = " + userId + " ORDER BY date DESC";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id       = rs.getInt("id");
                String date  = rs.getString("date");
                String split = rs.getString("split");
                int duration = rs.getInt("duration_mins");

                int[] counts = DatabaseManager.getSessionCounts(id);
                sessions.add(new Session(id, date, split, duration,
                        counts[0], counts[1]));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    // ===== Load Workout Sets from DB =====
    private List<WorkoutSet> loadWorkoutSets(int sessionId) {
        List<WorkoutSet> sets = new ArrayList<>();

        String sql = "SELECT exercise_name, set_number, weight_lbs, reps " +
                "FROM workout_log WHERE session_id = ? " +
                "ORDER BY exercise_name, set_number";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sessionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                sets.add(new WorkoutSet(
                        rs.getString("exercise_name"),
                        "Set " + rs.getInt("set_number"),
                        String.format("%.1f kg", rs.getDouble("weight_lbs")),
                        rs.getInt("reps") + " reps"
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sets;
    }
}