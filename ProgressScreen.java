import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class ProgressScreen {

    // Colors
    private static final String BG_PRIMARY    = "#13161A";
    private static final String BG_SECONDARY  = "#0D0F12";
    private static final String BG_CARD       = "#1A1D22";
    private static final String BG_ROW        = "#22252a";
    private static final String ACCENT        = "#E63946";
    private static final String TEXT_PRIMARY  = "#F0F2F5";
    private static final String TEXT_SECONDARY = "#A8B2C1";
    private static final String TEXT_MUTED    = "#6B7685";
    private static final String BORDER        = "#2a2d32";

    // Bar colors
    private static final String C_RED   = "#E63946";
    private static final String C_BLUE  = "#378ADD";
    private static final String C_GREEN = "#639922";
    private static final String C_AMBER = "#BA7517";
    private static final String C_PINK  = "#D4537E";

    private Stage primaryStage;

    public Scene buildScene(Stage stage) {
        this.primaryStage = stage;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PRIMARY + ";");

        VBox topSection = new VBox(0);
        topSection.getChildren().addAll(buildTopBar(), buildNavBar());
        root.setTop(topSection);

        ScrollPane scroll = new ScrollPane(buildContent());
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_PRIMARY +
                "; -fx-background-color: " + BG_PRIMARY + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setCenter(scroll);

        return new Scene(root, 900, 620);
    }

    // ===== Top Bar =====
    private HBox buildTopBar() {
        HBox bar = new HBox(6);
        bar.setPadding(new Insets(14, 20, 14, 20));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + BG_SECONDARY + ";");

        Label logo = new Label("TaraGym");
        logo.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
        logo.setTextFill(Color.web(ACCENT));

        Label sep = new Label("|");
        sep.setTextFill(Color.web(TEXT_MUTED));

        Label screen = new Label("Progress");
        screen.setFont(Font.font("SansSerif", 13));
        screen.setTextFill(Color.web(TEXT_PRIMARY));

        bar.getChildren().addAll(logo, sep, screen);
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

    private Button navButton(String text, String id) {
        Button btn = new Button(text);
        boolean active = id.equals("progress");

        String activeStyle  = "-fx-background-color: " + ACCENT + "; -fx-text-fill: white;"
                + "-fx-font-size: 12px; -fx-padding: 8 16;"
                + "-fx-background-radius: 6; -fx-cursor: hand;";
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: " + TEXT_SECONDARY + ";"
                + "-fx-font-size: 12px; -fx-padding: 8 16;"
                + "-fx-background-radius: 6; -fx-cursor: hand;";
        String hoverStyle   = "-fx-background-color: #2a2d30; -fx-text-fill: white;"
                + "-fx-font-size: 12px; -fx-padding: 8 16;"
                + "-fx-background-radius: 6; -fx-cursor: hand;";

        btn.setStyle(active ? activeStyle : defaultStyle);

        if (!active) {
            btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
            btn.setOnMouseExited(e  -> btn.setStyle(defaultStyle));
            btn.setOnAction(e -> Navigator.go(primaryStage, id));
        }
        return btn;
    }

    // ===== Main Content =====
    private VBox buildContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(22));

        content.getChildren().addAll(
                buildStatCards(),
                buildMidRow(),
                buildBottomRow()
        );
        return content;
    }

    // ===== Stat Cards — from DB =====
    private HBox buildStatCards() {
        HBox row = new HBox(10);

        // Load from DB
        int workouts     = DatabaseManager.getWorkoutsThisMonth();
        int avgDuration  = DatabaseManager.getAvgDuration();
        String mostTrained = DatabaseManager.getMostTrainedMuscle();
        String lastDate  = DatabaseManager.getLastWorkout();

        String lastWorkout = "N/A";
        if (lastDate != null) {
            try {
                LocalDate last  = LocalDate.parse(lastDate);
                LocalDate today = LocalDate.now();
                long daysAgo    = today.toEpochDay() - last.toEpochDay();
                if      (daysAgo == 0) lastWorkout = "Today";
                else if (daysAgo == 1) lastWorkout = "Yesterday";
                else                   lastWorkout = daysAgo + " days ago";
            } catch (Exception ignored) {}
        }

        row.getChildren().addAll(
                statCard("🔥", C_RED,   String.valueOf(workouts),  "Workouts this month"),
                statCard("⏱",  C_BLUE,  avgDuration + " min",      "Avg duration"),
                statCard("💪",  C_GREEN, mostTrained,                "Most trained muscle"),
                statCard("📅",  C_AMBER, lastWorkout,                "Last workout")
        );

        for (javafx.scene.Node n : row.getChildren()) {
            HBox.setHgrow(n, Priority.ALWAYS);
        }
        return row;
    }

    private VBox statCard(String icon, String iconColor,
                          String value, String labelText) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color: " + BG_CARD +
                "; -fx-background-radius: 10;" +
                "-fx-border-color: " + BORDER +
                "; -fx-border-radius: 10; -fx-border-width: 0.5;");
        card.setMaxWidth(Double.MAX_VALUE);

        StackPane iconBadge = new StackPane();
        iconBadge.setPrefSize(30, 30);
        iconBadge.setMaxSize(30, 30);
        iconBadge.setStyle("-fx-background-color: " + hexToRgba(iconColor, 0.15) +
                "; -fx-background-radius: 8;");
        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font(14));
        iconBadge.getChildren().add(iconLbl);

        Label val = new Label(value);
        val.setFont(Font.font("SansSerif", FontWeight.BOLD, 20));
        val.setTextFill(Color.web(TEXT_PRIMARY));

        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("SansSerif", 11));
        lbl.setTextFill(Color.web(TEXT_MUTED));

        card.getChildren().addAll(iconBadge, val, lbl);
        return card;
    }

    // ===== Mid Row: Weekly Summary + Personal Records =====
    private HBox buildMidRow() {
        HBox row = new HBox(14);

        VBox weekly = buildWeeklyPanel();
        VBox pr     = buildPRPanel();

        HBox.setHgrow(weekly, Priority.ALWAYS);
        HBox.setHgrow(pr,     Priority.ALWAYS);

        row.getChildren().addAll(weekly, pr);
        return row;
    }

    // ===== Weekly Panel — from DB =====
    private VBox buildWeeklyPanel() {
        VBox panel = panel();
        panel.getChildren().add(panelHeader("This week", "Mon – Sun"));

        int userId = DatabaseManager.getCurrentUserId();
        if (userId == -1) {
            panel.getChildren().add(sLabel("Not logged in."));
            return panel;
        }

        LocalDate today  = LocalDate.now();
        LocalDate monday = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);

        String sql = "SELECT date, split, duration_mins FROM session " +
                "WHERE user_id = " + userId +
                " AND date >= '" + monday + "' AND date <= '" + sunday + "'" +
                " ORDER BY date ASC";

        int totalWorkouts = 0;
        int totalMins     = 0;

        // Map each day of the week
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        String[] splitForDay    = new String[7];
        int[]    minsForDay     = new int[7];

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String date     = rs.getString("date");
                String split    = rs.getString("split");
                int    duration = rs.getInt("duration_mins");

                LocalDate sessionDate = LocalDate.parse(date);
                int dayIndex = sessionDate.getDayOfWeek().getValue() - 1; // 0=Mon
                splitForDay[dayIndex] = split;
                minsForDay[dayIndex]  = duration;
                totalWorkouts++;
                totalMins += duration;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Max mins for bar scaling
        int maxMins = 0;
        for (int m : minsForDay) if (m > maxMins) maxMins = m;
        if (maxMins == 0) maxMins = 1;

        for (int i = 0; i < 7; i++) {
            boolean isRest = splitForDay[i] == null;
            int     pct    = isRest ? 0 : (int)((minsForDay[i] / (double) maxMins) * 100);
            String  meta   = isRest ? "Rest" : splitForDay[i];
            String  mins   = isRest ? "" : String.valueOf(minsForDay[i]);
            panel.getChildren().add(weekRow(dayNames[i], meta, mins, pct));
        }

        // Footer
        Region spacer = new Region();
        spacer.setPrefHeight(6);
        HBox footer = new HBox();
        footer.setPadding(new Insets(10, 0, 0, 0));
        footer.setStyle("-fx-border-color: " + BG_ROW + "; -fx-border-width: 1 0 0 0;");
        footer.setAlignment(Pos.CENTER_LEFT);

        Label left  = sLabel(totalWorkouts + " workouts");
        Label right = sLabel(totalMins + " total mins");
        Region gap  = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        footer.getChildren().addAll(left, gap, right);

        panel.getChildren().addAll(spacer, footer);
        return panel;
    }

    private HBox weekRow(String day, String split, String mins, int pct) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(3, 0, 3, 0));

        Label dayLbl = new Label(day);
        dayLbl.setFont(Font.font("SansSerif", 10));
        dayLbl.setTextFill(Color.web(TEXT_MUTED));
        dayLbl.setMinWidth(28);

        StackPane barBg = new StackPane();
        barBg.setMinHeight(6);
        barBg.setMaxHeight(6);
        barBg.setPrefHeight(6);
        barBg.setStyle("-fx-background-color: " + BG_ROW + "; -fx-background-radius: 3;");
        HBox.setHgrow(barBg, Priority.ALWAYS);

        if (pct > 0) {
            Rectangle fill = new Rectangle();
            fill.setHeight(6);
            fill.setArcWidth(6);
            fill.setArcHeight(6);
            fill.setFill(Color.web(ACCENT));
            fill.widthProperty().bind(barBg.widthProperty().multiply(pct / 100.0));
            barBg.setAlignment(Pos.CENTER_LEFT);
            barBg.getChildren().add(fill);
        }

        String metaText = split.equals("Rest")
                ? "Rest"
                : split + (mins.isEmpty() ? "" : " · " + mins + "m");
        Label meta = new Label(metaText);
        meta.setFont(Font.font("SansSerif", 10));
        meta.setTextFill(split.equals("Rest")
                ? Color.web("#444") : Color.web(TEXT_MUTED));
        meta.setMinWidth(100);
        meta.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(dayLbl, barBg, meta);
        return row;
    }

    // ===== Personal Records Panel — from DB =====
    private VBox buildPRPanel() {
        VBox panel = panel();
        panel.getChildren().add(panelHeader("Personal records", "All time"));

        // Table header
        HBox th = new HBox();
        th.setPadding(new Insets(0, 0, 8, 0));
        th.setStyle("-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 0.5 0;");
        Label thEx   = colHeader("Exercise"); HBox.setHgrow(thEx, Priority.ALWAYS);
        Label thBest = colHeader("Best");
        thBest.setMinWidth(90); thBest.setAlignment(Pos.CENTER);
        Label thDate = colHeader("Date");
        thDate.setMinWidth(60); thDate.setAlignment(Pos.CENTER_RIGHT);
        th.getChildren().addAll(thEx, thBest, thDate);
        panel.getChildren().add(th);

        String[] colors = {C_RED, C_BLUE, C_GREEN, C_AMBER, C_PINK};
        int colorIdx    = 0;
        boolean hasRecords = false;

        try (Connection conn = DatabaseManager.connect();
             ResultSet rs    = DatabaseManager.getPersonalRecords()) {

            while (rs != null && rs.next()) {
                hasRecords = true;
                String exercise   = rs.getString("exercise_name");
                double bestWeight = rs.getDouble("best_weight");
                String date       = rs.getString("date");
                String color      = colors[colorIdx % colors.length];
                colorIdx++;

                HBox rowBox = new HBox();
                rowBox.setAlignment(Pos.CENTER_LEFT);
                rowBox.setPadding(new Insets(9, 0, 9, 0));
                rowBox.setStyle("-fx-border-color: #1e2226; -fx-border-width: 0 0 0.5 0;");

                Circle dot = new Circle(3.5, Color.web(color));
                Label name = new Label(exercise);
                name.setFont(Font.font("SansSerif", 12));
                name.setTextFill(Color.web(TEXT_SECONDARY));
                name.setPadding(new Insets(0, 0, 0, 7));
                HBox nameBox = new HBox(0);
                nameBox.setAlignment(Pos.CENTER_LEFT);
                nameBox.getChildren().addAll(dot, name);
                HBox.setHgrow(nameBox, Priority.ALWAYS);

                Label badge = new Label(String.format("%.1f kg", bestWeight));
                badge.setFont(Font.font("SansSerif", FontWeight.BOLD, 11));
                badge.setTextFill(Color.web(ACCENT));
                badge.setStyle("-fx-background-color: rgba(230,57,70,0.12);" +
                        "-fx-background-radius: 20; -fx-padding: 2 9;");
                badge.setMinWidth(90);
                badge.setAlignment(Pos.CENTER);

                Label dateLbl = new Label(date != null ? date : "");
                dateLbl.setFont(Font.font("SansSerif", 11));
                dateLbl.setTextFill(Color.web(TEXT_MUTED));
                dateLbl.setMinWidth(60);
                dateLbl.setAlignment(Pos.CENTER_RIGHT);

                rowBox.getChildren().addAll(nameBox, badge, dateLbl);
                panel.getChildren().add(rowBox);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!hasRecords) {
            Label empty = new Label("No personal records yet! Complete a workout first.");
            empty.setFont(Font.font("SansSerif", 12));
            empty.setTextFill(Color.web(TEXT_MUTED));
            panel.getChildren().add(empty);
        }

        return panel;
    }

    // ===== Bottom Row: Muscle Focus + Monthly Activity =====
    private HBox buildBottomRow() {
        HBox row = new HBox(14);

        VBox muscle  = buildMusclePanel();
        VBox monthly = buildMonthlyPanel();

        HBox.setHgrow(muscle,  Priority.ALWAYS);
        HBox.setHgrow(monthly, Priority.ALWAYS);

        row.getChildren().addAll(muscle, monthly);
        return row;
    }

    // ===== Muscle Focus Panel — from DB =====
    private VBox buildMusclePanel() {
        VBox panel = panel();
        panel.getChildren().add(panelHeader("Muscle group focus", null));

        int userId = DatabaseManager.getCurrentUserId();
        if (userId == -1) {
            panel.getChildren().add(sLabel("Not logged in."));
            return panel;
        }

        String sql = "SELECT muscle_group, COUNT(*) as count FROM workout_log " +
                "WHERE session_id IN " +
                "(SELECT id FROM session WHERE user_id = " + userId + ") " +
                "GROUP BY muscle_group ORDER BY count DESC LIMIT 5";

        String[] colors = {C_RED, C_BLUE, C_GREEN, C_AMBER, C_PINK};
        int colorIdx    = 0;
        int maxCount    = 1;
        boolean hasData = false;

        // First pass — get max count for scaling
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int count = rs.getInt("count");
                if (count > maxCount) maxCount = count;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Second pass — build rows
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                hasData = true;
                String muscle = rs.getString("muscle_group");
                int    count  = rs.getInt("count");
                int    pct    = (int)((count / (double) maxCount) * 100);
                String color  = colors[colorIdx % colors.length];
                colorIdx++;
                panel.getChildren().add(
                        muscleRow(muscle, pct, color, count + "x"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!hasData) {
            panel.getChildren().add(sLabel("No muscle data yet!"));
        }

        return panel;
    }

    private HBox muscleRow(String name, int pct, String color, String count) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(4, 0, 4, 0));

        Label nameLbl = new Label(name);
        nameLbl.setFont(Font.font("SansSerif", 11));
        nameLbl.setTextFill(Color.web(TEXT_SECONDARY));
        nameLbl.setMinWidth(72);

        StackPane barBg = new StackPane();
        barBg.setMinHeight(5);
        barBg.setMaxHeight(5);
        barBg.setPrefHeight(5);
        barBg.setStyle("-fx-background-color: " + BG_ROW + "; -fx-background-radius: 3;");
        HBox.setHgrow(barBg, Priority.ALWAYS);

        Rectangle fill = new Rectangle();
        fill.setHeight(5);
        fill.setArcWidth(5);
        fill.setArcHeight(5);
        fill.setFill(Color.web(color));
        fill.widthProperty().bind(barBg.widthProperty().multiply(pct / 100.0));
        barBg.setAlignment(Pos.CENTER_LEFT);
        barBg.getChildren().add(fill);

        Label cnt = new Label(count);
        cnt.setFont(Font.font("SansSerif", 11));
        cnt.setTextFill(Color.web(TEXT_MUTED));
        cnt.setMinWidth(28);
        cnt.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(nameLbl, barBg, cnt);
        return row;
    }

    // ===== Monthly Activity Panel — from DB =====
    private VBox buildMonthlyPanel() {
        VBox panel = panel();

        String monthName = LocalDate.now()
                .getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                + " " + LocalDate.now().getYear();
        panel.getChildren().add(panelHeader("Monthly activity", monthName));

        int userId = DatabaseManager.getCurrentUserId();

        // Get workouts per week this month
        int[] weekCounts = new int[4];
        int[] weekMins   = new int[4];

        if (userId != -1) {
            LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
            String sql = "SELECT date, duration_mins FROM session " +
                    "WHERE user_id = " + userId +
                    " AND strftime('%Y-%m', date) = strftime('%Y-%m', 'now')";

            try (Connection conn = DatabaseManager.connect();
                 Statement stmt = conn.createStatement();
                 ResultSet rs   = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    String date   = rs.getString("date");
                    int    mins   = rs.getInt("duration_mins");
                    LocalDate d   = LocalDate.parse(date);
                    int weekIndex = Math.min((d.getDayOfMonth() - 1) / 7, 3);
                    weekCounts[weekIndex]++;
                    weekMins[weekIndex] += mins;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Bar chart
        int maxCount = 1;
        for (int c : weekCounts) if (c > maxCount) maxCount = c;

        HBox bars = new HBox(6);
        bars.setAlignment(Pos.BOTTOM_CENTER);
        bars.setPrefHeight(100);
        bars.setPadding(new Insets(0, 0, 8, 0));

        for (int i = 0; i < 4; i++) {
            VBox barWrap = new VBox(4);
            barWrap.setAlignment(Pos.BOTTOM_CENTER);
            HBox.setHgrow(barWrap, Priority.ALWAYS);

            Label val = new Label(String.valueOf(weekCounts[i]));
            val.setFont(Font.font("SansSerif", 9));
            val.setTextFill(Color.web(TEXT_MUTED));

            int h = (int)((weekCounts[i] / (double) maxCount) * 75);
            if (h < 4) h = 4;

            boolean isCurrent = (i == (LocalDate.now().getDayOfMonth() - 1) / 7);

            Rectangle bar = new Rectangle();
            bar.setWidth(28);
            bar.setHeight(h);
            bar.setArcWidth(4);
            bar.setArcHeight(4);
            bar.setFill(Color.web(isCurrent ? ACCENT : BORDER));

            Label weekLbl = new Label("W" + (i + 1));
            weekLbl.setFont(Font.font("SansSerif", 9));
            weekLbl.setTextFill(Color.web(TEXT_MUTED));

            barWrap.getChildren().addAll(val, bar, weekLbl);
            bars.getChildren().add(barWrap);
        }

        panel.getChildren().add(bars);

        // Divider
        Region div = new Region();
        div.setPrefHeight(0.5);
        div.setStyle("-fx-background-color: " + BG_ROW + ";");

        // Streak
        Label streakTitle = sectionLabel("Streak");

        int streakDays = calculateStreak(userId);

        FlowPane dots = new FlowPane(5, 5);
        for (int i = 0; i < 21; i++) {
            Rectangle dot = new Rectangle(22, 22);
            dot.setArcWidth(5);
            dot.setArcHeight(5);
            if      (i < streakDays && i < 21) dot.setFill(Color.web(ACCENT));
            else if (i == streakDays)          {
                dot.setFill(Color.web(ACCENT));
                dot.setStroke(Color.web(ACCENT, 0.4));
                dot.setStrokeWidth(2);
            }
            else dot.setFill(Color.web(BORDER));
            dots.getChildren().add(dot);
        }

        Label streakNote = sLabel(streakDays + "-day streak this month");
        streakNote.setPadding(new Insets(4, 0, 0, 0));

        panel.getChildren().addAll(div, streakTitle, dots, streakNote);
        return panel;
    }

    // ===== Calculate Streak =====
    private int calculateStreak(int userId) {
        if (userId == -1) return 0;

        String sql = "SELECT DISTINCT date FROM session WHERE user_id = " + userId +
                " AND strftime('%Y-%m', date) = strftime('%Y-%m', 'now')" +
                " ORDER BY date DESC";

        int streak = 0;
        LocalDate check = LocalDate.now();

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LocalDate sessionDate = LocalDate.parse(rs.getString("date"));
                if (sessionDate.equals(check) || sessionDate.equals(check.minusDays(1))) {
                    streak++;
                    check = sessionDate.minusDays(1);
                } else {
                    break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return streak;
    }

    // ===== Shared Helpers =====
    private VBox panel() {
        VBox p = new VBox(10);
        p.setPadding(new Insets(16));
        p.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 10;" +
                "-fx-border-color: " + BORDER + "; -fx-border-radius: 10; -fx-border-width: 0.5;");
        p.setMaxWidth(Double.MAX_VALUE);
        return p;
    }

    private HBox panelHeader(String title, String badge) {
        HBox h = new HBox(8);
        h.setAlignment(Pos.CENTER_LEFT);

        Label t = new Label(title);
        t.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
        t.setTextFill(Color.web(TEXT_PRIMARY));
        h.getChildren().add(t);

        if (badge != null) {
            Label b = new Label(badge);
            b.setFont(Font.font("SansSerif", 10));
            b.setTextFill(Color.web(TEXT_MUTED));
            b.setStyle("-fx-background-color: " + BG_ROW +
                    "; -fx-background-radius: 20; -fx-padding: 2 8;" +
                    "-fx-border-color: " + BORDER +
                    "; -fx-border-radius: 20; -fx-border-width: 0.5;");
            h.getChildren().add(b);
        }
        return h;
    }

    private Label colHeader(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("SansSerif", FontWeight.BOLD, 10));
        l.setTextFill(Color.web(TEXT_MUTED));
        return l;
    }

    private Label sLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("SansSerif", 11));
        l.setTextFill(Color.web(TEXT_MUTED));
        return l;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text.toUpperCase());
        l.setFont(Font.font("SansSerif", FontWeight.BOLD, 10));
        l.setTextFill(Color.web(TEXT_MUTED));
        l.setPadding(new Insets(6, 0, 4, 0));
        return l;
    }

    private String hexToRgba(String hex, double alpha) {
        int r = Integer.parseInt(hex.substring(1, 3), 16);
        int g = Integer.parseInt(hex.substring(3, 5), 16);
        int b = Integer.parseInt(hex.substring(5, 7), 16);
        return String.format("rgba(%d,%d,%d,%.2f)", r, g, b, alpha);
    }
}