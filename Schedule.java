import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.*;

public class Schedule extends Application {

    private final String[] DAYS       = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private final String[] SLOTS      = {"Morning", "Afternoon", "Evening"};
    private final String[] SLOT_TIMES = {"6AM - 12PM", "12PM - 6PM", "6PM - 10PM"};

    private Stage primaryStage;

    // Colors
    private final String BG_PRIMARY    = "#1A1D20";
    private final String BG_SECONDARY  = "#0D0F12";
    private final String ACCENT        = "#E63946";
    private final String TEXT_PRIMARY  = "#FFFFFF";
    private final String TEXT_SECONDARY = "#A8B2C1";
    private final String UNAVAILABLE   = "#2a2a2a";
    private final String AVAILABLE     = "#1e2428";
    private final String WORKOUT       = "#E63946";

    private boolean sameDayMode = true;

    private final int[][]      slotState     = new int[7][3];
    private final String[][]   workoutLabels = new String[7][3];
    private final StackPane[][] cells        = new StackPane[7][3];

    private Button modeBtn;
    private Label  modeDesc;

    private List<String> routine = new ArrayList<>();

    private TextArea workoutPlanArea;
    private VBox     workoutPlanPanel;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        routine = buildRoutine();
        restoreSlotStates();

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

        Scene scene = new Scene(root, 900, 680);
        stage.setTitle("TaraGym — Schedule");
        stage.setScene(scene);
        stage.show();
    }

    private void restoreSlotStates() {
        boolean[] unavailable = {
                UserSession.getInstance().isMonUnavailable(),
                UserSession.getInstance().isTueUnavailable(),
                UserSession.getInstance().isWedUnavailable(),
                UserSession.getInstance().isThuUnavailable(),
                UserSession.getInstance().isFriUnavailable(),
                UserSession.getInstance().isSatUnavailable(),
                UserSession.getInstance().isSunUnavailable()
        };
        for (int d = 0; d < 7; d++) {
            if (unavailable[d]) {
                for (int s = 0; s < 3; s++) {
                    slotState[d][s] = 1;
                }
            }
        }
    }

    private List<String> buildRoutine() {
        String goal  = UserSession.getInstance().getGoal();
        String level = UserSession.getInstance().getLevel();
        String split = UserSession.getInstance().getWorkoutSplit();

        if (level == null || level.isEmpty()) level = "Null";
        if (split == null || split.isEmpty()) split = "Null";

        // Lose Weight path: split stored as "Lose Fat", level stored as lbs/week
        List<String> labels;
        if ("Lose Fat".equals(goal) || "Lose Fat".equals(split)) {
            labels = getWorkoutLabels(level, "Lose Fat");
        } else {
            labels = getWorkoutLabels(level, split);
        }

        if (labels.isEmpty()) {
            labels.add("Null");
            labels.add("Null");
            labels.add("Null");
        }

        return labels;
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

        Label screen = new Label("Schedule");
        screen.setFont(Font.font("SansSerif", FontWeight.NORMAL, 14));
        screen.setTextFill(Color.web(TEXT_PRIMARY));

        bar.getChildren().addAll(title, sep, screen);

        String level = UserSession.getInstance().getLevel();
        String split = UserSession.getInstance().getWorkoutSplit();
        String goal  = UserSession.getInstance().getGoal();
        if (level != null && !level.isEmpty()) {
            Label sep2 = new Label("  |  ");
            sep2.setTextFill(Color.web(TEXT_SECONDARY));

            String displayText;
            if ("Lose Fat".equals(goal) || "Lose Fat".equals(split)) {
                displayText = "Lose Fat — " + level;
            } else {
                displayText = level + " — " + split;
            }

            Label splitLbl = new Label(displayText);
            splitLbl.setFont(Font.font("SansSerif", 12));
            splitLbl.setTextFill(Color.web(ACCENT));

            bar.getChildren().addAll(sep2, splitLbl);
        }

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
        Button btn = new Button(text);
        boolean isActive = screen.equals("schedule");

        String activeStyle = "-fx-background-color: " + ACCENT + "; -fx-text-fill: white;"
                + "-fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;";
        String normalStyle = "-fx-background-color: transparent; -fx-text-fill: " + TEXT_SECONDARY + ";"
                + "-fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;";
        String hoverStyle  = "-fx-background-color: #2a2d30; -fx-text-fill: white;"
                + "-fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;";

        btn.setStyle(isActive ? activeStyle : normalStyle);

        if (!isActive) {
            btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
            btn.setOnMouseExited(e  -> btn.setStyle(normalStyle));
        }

        btn.setOnAction(e -> Navigator.go(primaryStage, screen));
        return btn;
    }

    // ===== Content =====
    private VBox buildContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        String level = UserSession.getInstance().getLevel();
        String split = UserSession.getInstance().getWorkoutSplit();
        String goal2 = UserSession.getInstance().getGoal();

        String infoText;
        if (level == null || level.isEmpty()) {
            infoText = "Please complete your profile setup to generate a schedule.";
        } else if ("Lose Fat".equals(goal2) || "Lose Fat".equals(split)) {
            infoText = "Mark the time slots when you are UNAVAILABLE, then click Generate.  (Lose Fat — " + level + ")";
        } else {
            infoText = "Mark the time slots when you are UNAVAILABLE, then click Generate.  (" + level + " — " + split + ")";
        }

        Label instruction = new Label(infoText);
        instruction.setTextFill(Color.web(TEXT_SECONDARY));
        instruction.setFont(Font.font("SansSerif", 13));
        instruction.setWrapText(true);

        HBox legend  = buildLegend();
        VBox grid    = buildGrid();
        HBox buttons = buildButtons();

        workoutPlanPanel = buildWorkoutPlanPanel();
        workoutPlanPanel.setVisible(false);
        workoutPlanPanel.setManaged(false);

        content.getChildren().addAll(
                instruction, legend, grid, buttons, workoutPlanPanel);

        for (int d = 0; d < 7; d++)
            for (int s = 0; s < 3; s++)
                if (slotState[d][s] == 1) refreshCell(d, s);

        return content;
    }

    // ===== Workout Plan Panel =====
    private VBox buildWorkoutPlanPanel() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(16));
        panel.setStyle(
                "-fx-background-color: " + BG_SECONDARY + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #2a2d30;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = new Label("Your Workout Plan");
        titleLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));
        titleLbl.setTextFill(Color.web(TEXT_PRIMARY));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewDetailsBtn = new Button("View Full Details ↓");
        viewDetailsBtn.setStyle(
                "-fx-background-color: #2a2d30; -fx-text-fill: white;" +
                        "-fx-padding: 6 14; -fx-background-radius: 6; -fx-cursor: hand;"
        );

        header.getChildren().addAll(titleLbl, spacer, viewDetailsBtn);

        GridPane weeklyGrid = new GridPane();
        weeklyGrid.setHgap(8);
        weeklyGrid.setVgap(8);

        for (int i = 0; i < 2; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(50);
            weeklyGrid.getColumnConstraints().add(cc);
        }

        weeklyGrid.setId("weeklyGrid");

        workoutPlanArea = new TextArea();
        workoutPlanArea.setEditable(false);
        workoutPlanArea.setFont(Font.font("Monospaced", 12));
        workoutPlanArea.setPrefHeight(300);
        workoutPlanArea.setStyle(
                "-fx-background-color: #1e2428;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: #2a2d30;" +
                        "-fx-border-radius: 6;" +
                        "-fx-control-inner-background: #1e2428;"
        );
        workoutPlanArea.setVisible(false);
        workoutPlanArea.setManaged(false);

        final boolean[] detailsVisible = {false};
        viewDetailsBtn.setOnAction(e -> {
            detailsVisible[0] = !detailsVisible[0];
            workoutPlanArea.setVisible(detailsVisible[0]);
            workoutPlanArea.setManaged(detailsVisible[0]);
            viewDetailsBtn.setText(detailsVisible[0]
                    ? "Hide Details ↑" : "View Full Details ↓");
        });

        panel.getChildren().addAll(header, weeklyGrid, workoutPlanArea);
        return panel;
    }

    private void updateWorkoutPlanPanel(List<String> availableDays,
                                        List<String> workoutLabelsList) {
        GridPane weeklyGrid = (GridPane) workoutPlanPanel.getChildren().stream()
                .filter(n -> n instanceof GridPane)
                .findFirst().orElse(null);

        if (weeklyGrid == null) return;
        weeklyGrid.getChildren().clear();

        weeklyGrid.getColumnConstraints().clear();
        for (int i = 0; i < 2; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(50);
            weeklyGrid.getColumnConstraints().add(cc);
        }

        String level = UserSession.getInstance().getLevel();

        String split = UserSession.getInstance().getWorkoutSplit();
        String goal  = UserSession.getInstance().getGoal();

        String panelLabel;
        if ("Lose Fat".equals(goal) || "Lose Fat".equals(split)) {
            panelLabel = "Goal: Lose Fat   |   " + level;
        } else {
            panelLabel = "Level: " + level + "   |   Split: " + split;
        }
        Label levelLbl = new Label(panelLabel);
        levelLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        levelLbl.setTextFill(Color.web(ACCENT));
        GridPane.setColumnSpan(levelLbl, 2);
        weeklyGrid.add(levelLbl, 0, 0);

        int row = 1;
        for (int i = 0; i < availableDays.size(); i++) {
            String day     = availableDays.get(i);
            String workout = i < workoutLabelsList.size()
                    ? workoutLabelsList.get(i) : "Rest Day";

            HBox dayCard = buildDayCard(day, workout);
            weeklyGrid.add(dayCard, i % 2, row + i / 2);
        }

        String details = buildScheduleText(availableDays, workoutLabelsList, level, split);
        workoutPlanArea.setText(details);

        workoutPlanPanel.setVisible(true);
        workoutPlanPanel.setManaged(true);
    }

    private HBox buildDayCard(String day, String workout) {
        HBox card = new HBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10, 14, 10, 14));

        boolean isRest = workout.equals("Rest Day");

        card.setStyle(
                "-fx-background-color: " + (isRest ? "#1e2428" : "#2a1215") + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: " + (isRest ? "#2a2d30" : ACCENT) + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-width: 1;"
        );

        VBox info = new VBox(3);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label dayLbl = new Label(day);
        dayLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        dayLbl.setTextFill(Color.web(TEXT_PRIMARY));

        Label workoutLbl = new Label(workout);
        workoutLbl.setFont(Font.font("SansSerif", 11));
        workoutLbl.setTextFill(isRest
                ? Color.web(TEXT_SECONDARY) : Color.web(ACCENT));

        info.getChildren().addAll(dayLbl, workoutLbl);
        card.getChildren().add(info);

        return card;
    }

    private String buildScheduleText(List<String> availableDays,
                                     List<String> workoutLabelsList,
                                     String level, String split) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== YOUR WORKOUT SCHEDULE ===\n");

        String goal = UserSession.getInstance().getGoal();
        if ("Lose Fat".equals(goal) || "Lose Fat".equals(split)) {
            sb.append("Goal: Lose Fat  |  Target: ").append(level).append("\n");
        } else {
            sb.append("Level: ").append(level).append("  |  Split: ").append(split).append("\n");
        }
        sb.append("─".repeat(45)).append("\n\n");

        sb.append("WEEKLY PLAN:\n\n");
        for (int i = 0; i < availableDays.size(); i++) {
            if (i < workoutLabelsList.size()) {
                sb.append(String.format("  %-12s →  %s\n",
                        availableDays.get(i), workoutLabelsList.get(i)));
            } else {
                sb.append(String.format("  %-12s →  Rest Day\n", availableDays.get(i)));
            }
        }

        sb.append("\n").append("─".repeat(45)).append("\n\n");
        sb.append("WORKOUT DETAILS:\n\n");
        sb.append(getWorkoutDetails(level, split));

        return sb.toString();
    }

    // ===== Legend =====
    private HBox buildLegend() {
        HBox legend = new HBox(16);
        legend.setAlignment(Pos.CENTER_LEFT);
        legend.getChildren().addAll(
                legendItem(AVAILABLE,   "Available"),
                legendItem(UNAVAILABLE, "Unavailable"),
                legendItem(WORKOUT,     "Workout")
        );
        return legend;
    }

    private HBox legendItem(String color, String label) {
        HBox item = new HBox(6);
        item.setAlignment(Pos.CENTER_LEFT);

        StackPane dot = new StackPane();
        dot.setPrefSize(14, 14);
        dot.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 3;");

        Label lbl = new Label(label);
        lbl.setTextFill(Color.web(TEXT_SECONDARY));
        lbl.setFont(Font.font("SansSerif", 11));

        item.getChildren().addAll(dot, lbl);
        return item;
    }

    // ===== Grid =====
    private VBox buildGrid() {
        VBox grid = new VBox(0);
        grid.setStyle("-fx-background-color: " + BG_SECONDARY + "; -fx-background-radius: 10;");
        grid.setPadding(new Insets(12));

        // Use a single GridPane with ColumnConstraints so column widths are
        // governed by the constraints — not by cell content — preventing any
        // cell from resizing when children are added or removed.
        GridPane gp = new GridPane();
        gp.setHgap(0);
        gp.setVgap(0);

        // Column 0 – row-label column (fixed 100px)
        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setMinWidth(100);
        labelCol.setPrefWidth(100);
        labelCol.setMaxWidth(100);
        labelCol.setHgrow(Priority.NEVER);
        gp.getColumnConstraints().add(labelCol);

        // Columns 1–7 – one per day, equal share of remaining space
        for (int d = 0; d < DAYS.length; d++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setMinWidth(50);
            cc.setPrefWidth(80);
            cc.setMaxWidth(Double.MAX_VALUE);
            cc.setHgrow(Priority.ALWAYS);
            gp.getColumnConstraints().add(cc);
        }

        // ── Header row (GridPane row 0) ──
        StackPane cornerCell = new StackPane();
        cornerCell.setMinHeight(40); cornerCell.setMaxHeight(40); cornerCell.setPrefHeight(40);
        gp.add(cornerCell, 0, 0);

        for (int d = 0; d < DAYS.length; d++) {
            Label dayLabel = new Label(DAYS[d].substring(0, 3));
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            dayLabel.setMaxHeight(Double.MAX_VALUE);
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
            dayLabel.setTextFill(Color.web(TEXT_PRIMARY));
            StackPane headerCell = new StackPane(dayLabel);
            headerCell.setMinHeight(40); headerCell.setMaxHeight(40); headerCell.setPrefHeight(40);
            gp.add(headerCell, d + 1, 0);
        }

        // ── Slot rows ──
        for (int s = 0; s < SLOTS.length; s++) {
            int gpRow = s * 2 + 1; // rows 1,3,5 for slots; rows 2,4 for dividers

            VBox slotLabel = new VBox(2);
            slotLabel.setMinHeight(85); slotLabel.setMaxHeight(85); slotLabel.setPrefHeight(85);
            slotLabel.setAlignment(Pos.CENTER_RIGHT);
            slotLabel.setPadding(new Insets(0, 10, 0, 0));

            Label slotName = new Label(SLOTS[s]);
            slotName.setFont(Font.font("SansSerif", FontWeight.BOLD, 11));
            slotName.setTextFill(Color.web(TEXT_PRIMARY));

            Label slotTime = new Label(SLOT_TIMES[s]);
            slotTime.setFont(Font.font("SansSerif", 9));
            slotTime.setTextFill(Color.web(TEXT_SECONDARY));

            slotLabel.getChildren().addAll(slotName, slotTime);
            gp.add(slotLabel, 0, gpRow);

            for (int d = 0; d < DAYS.length; d++) {
                StackPane cell = buildCell(d, s);
                cells[d][s] = cell;
                gp.add(cell, d + 1, gpRow);
            }

            if (s < SLOTS.length - 1) {
                Region divider = new Region();
                divider.setMinHeight(2); divider.setMaxHeight(2); divider.setPrefHeight(2);
                divider.setStyle("-fx-background-color: #2a2d30;");
                GridPane.setColumnSpan(divider, DAYS.length + 1);
                gp.add(divider, 0, gpRow + 1);
            }
        }

        grid.getChildren().add(gp);
        return grid;
    }

    private StackPane buildCell(int day, int slot) {
        StackPane cell = new StackPane();
        // Lock all three size dimensions — the GridPane column width governs
        // the actual rendered width; min/pref/max prevent JavaFX from letting
        // child content override the layout constraint.
        cell.setMinHeight(85);  cell.setMaxHeight(85);  cell.setPrefHeight(85);
        cell.setMinWidth(50);   cell.setPrefWidth(80);  cell.setMaxWidth(Double.MAX_VALUE);
        cell.setPadding(new Insets(3));
        cell.setStyle("-fx-background-color: " + AVAILABLE +
                "; -fx-background-radius: 6; -fx-cursor: hand;" +
                "-fx-border-color: #1A1D20; -fx-border-width: 3; -fx-border-radius: 6;");

        cell.setOnMouseClicked(e -> toggleCell(day, slot));
        cell.setOnMouseEntered(e -> {
            if (slotState[day][slot] == 0) {
                cell.setStyle("-fx-background-color: #252a2e;" +
                        "-fx-background-radius: 6; -fx-cursor: hand;" +
                        "-fx-border-color: #1A1D20; -fx-border-width: 3; -fx-border-radius: 6;");
            }
        });
        cell.setOnMouseExited(e -> refreshCell(day, slot));
        return cell;
    }

    private void toggleCell(int day, int slot) {
        if (slotState[day][slot] == 2) return;
        slotState[day][slot] = slotState[day][slot] == 0 ? 1 : 0;
        saveSlotStatesToSession();
        refreshCell(day, slot);
    }

    private void saveSlotStatesToSession() {
        UserSession.getInstance().setMonUnavailable(
                slotState[0][0]==1 || slotState[0][1]==1 || slotState[0][2]==1);
        UserSession.getInstance().setTueUnavailable(
                slotState[1][0]==1 || slotState[1][1]==1 || slotState[1][2]==1);
        UserSession.getInstance().setWedUnavailable(
                slotState[2][0]==1 || slotState[2][1]==1 || slotState[2][2]==1);
        UserSession.getInstance().setThuUnavailable(
                slotState[3][0]==1 || slotState[3][1]==1 || slotState[3][2]==1);
        UserSession.getInstance().setFriUnavailable(
                slotState[4][0]==1 || slotState[4][1]==1 || slotState[4][2]==1);
        UserSession.getInstance().setSatUnavailable(
                slotState[5][0]==1 || slotState[5][1]==1 || slotState[5][2]==1);
        UserSession.getInstance().setSunUnavailable(
                slotState[6][0]==1 || slotState[6][1]==1 || slotState[6][2]==1);
    }

    private void refreshCell(int day, int slot) {
        StackPane cell = cells[day][slot];
        cell.getChildren().clear();

        switch (slotState[day][slot]) {
            case 0 -> {
                cell.setStyle("-fx-background-color: " + AVAILABLE +
                        "; -fx-background-radius: 6; -fx-cursor: hand;" +
                        "-fx-border-color: #1A1D20; -fx-border-width: 3; -fx-border-radius: 6;");
            }
            case 1 -> {
                cell.setStyle("-fx-background-color: " + UNAVAILABLE +
                        "; -fx-background-radius: 6; -fx-cursor: hand;" +
                        "-fx-border-color: #1A1D20; -fx-border-width: 3; -fx-border-radius: 6;");
                // FIX: label fills the fixed-size cell instead of resizing it
                Label lbl = new Label("✕");
                lbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));
                lbl.setTextFill(Color.web("#555555"));
                lbl.setMaxWidth(Double.MAX_VALUE);
                lbl.setMaxHeight(Double.MAX_VALUE);
                lbl.setAlignment(Pos.CENTER);
                StackPane.setAlignment(lbl, Pos.CENTER);
                cell.getChildren().add(lbl);
            }
            case 2 -> {
                cell.setStyle("-fx-background-color: " + WORKOUT +
                        "; -fx-background-radius: 6; -fx-cursor: hand;" +
                        "-fx-border-color: #1A1D20; -fx-border-width: 3; -fx-border-radius: 6;");

                VBox content = new VBox(2);
                content.setAlignment(Pos.CENTER);
                // Clamp content strictly inside the fixed cell — never let it push outward
                content.setMinWidth(0);
                content.setMaxWidth(Double.MAX_VALUE);
                content.setMinHeight(0);
                content.setMaxHeight(79);
                content.setPrefHeight(79);
                StackPane.setAlignment(content, Pos.CENTER);

                Label workoutLbl = new Label(workoutLabels[day][slot]);
                workoutLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 11));
                workoutLbl.setTextFill(Color.WHITE);
                workoutLbl.setWrapText(true);
                workoutLbl.setAlignment(Pos.CENTER);
                workoutLbl.setMinWidth(0);
                workoutLbl.setMaxWidth(Double.MAX_VALUE);

                Label slotLbl = new Label(SLOTS[slot]);
                slotLbl.setFont(Font.font("SansSerif", 10));
                slotLbl.setTextFill(Color.web("#ffcccc"));

                content.getChildren().addAll(workoutLbl, slotLbl);
                cell.getChildren().add(content);
            }
        }
    }

    // ===== Buttons =====
    private HBox buildButtons() {
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Button generateBtn = new Button("Generate Schedule");
        generateBtn.setStyle("-fx-background-color: " + ACCENT +
                "; -fx-text-fill: white; -fx-font-weight: bold;" +
                "-fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");
        generateBtn.setOnAction(e -> generateSchedule());

        Button clearBtn = new Button("Clear All");
        clearBtn.setStyle("-fx-background-color: #2a2d30; -fx-text-fill: white;" +
                "-fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> clearAll());

        modeBtn = new Button("Mode: Same Day");
        modeBtn.setStyle("-fx-background-color: " + ACCENT +
                "; -fx-text-fill: white;" +
                "-fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");

        modeDesc = new Label("Unavailable slot → suggest another slot same day");
        modeDesc.setTextFill(Color.web(TEXT_SECONDARY));
        modeDesc.setFont(Font.font("SansSerif", 11));

        modeBtn.setOnAction(e -> {
            sameDayMode = !sameDayMode;
            if (sameDayMode) {
                modeBtn.setText("Mode: Same Day");
                modeBtn.setStyle("-fx-background-color: " + ACCENT +
                        "; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");
                modeDesc.setText("Unavailable slot → suggest another slot same day");
            } else {
                modeBtn.setText("Mode: Other Day");
                modeBtn.setStyle("-fx-background-color: #2a2d30; -fx-text-fill: white;" +
                        "-fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");
                modeDesc.setText("Unavailable slot → move to next available day");
            }
        });

        Button workoutBtn = new Button("Go to Workout →");
        workoutBtn.setStyle("-fx-background-color: #2a2d30; -fx-text-fill: white;" +
                "-fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");
        workoutBtn.setOnAction(e -> Navigator.go(primaryStage, "workout"));

        buttons.getChildren().addAll(generateBtn, clearBtn, modeBtn, modeDesc, workoutBtn);
        return buttons;
    }

    // ===== Generate Schedule =====
    private void generateSchedule() {
        routine = buildRoutine();

        for (int d = 0; d < 7; d++)
            for (int s = 0; s < 3; s++)
                if (slotState[d][s] == 2) slotState[d][s] = 0;

        List<String> availableDays = new ArrayList<>();
        for (int d = 0; d < 7; d++) {
            if (sameDayMode) {
                boolean allUnavailable = slotState[d][0]==1
                        && slotState[d][1]==1 && slotState[d][2]==1;
                if (!allUnavailable) availableDays.add(DAYS[d]);
            } else {
                boolean anyUnavailable = slotState[d][0]==1
                        || slotState[d][1]==1 || slotState[d][2]==1;
                if (!anyUnavailable) availableDays.add(DAYS[d]);
            }
        }

        if (availableDays.isEmpty()) {
            showAlert("No available slots found! Please unmark some slots.");
            return;
        }

        UserSession.getInstance().setAvailableDays(availableDays);

        int routineIndex = 0;
        List<String> assignedWorkouts = new ArrayList<>();

        for (int d = 0; d < 7 && routineIndex < routine.size(); d++) {
            boolean morningUnavailable   = slotState[d][0] == 1;
            boolean afternoonUnavailable = slotState[d][1] == 1;
            boolean eveningUnavailable   = slotState[d][2] == 1;
            boolean allUnavailable = morningUnavailable && afternoonUnavailable && eveningUnavailable;

            if (allUnavailable) continue;

            if (sameDayMode) {
                int[] suggestionOrder;

                if (!morningUnavailable && !afternoonUnavailable && !eveningUnavailable) {
                    suggestionOrder = new int[]{0, 1, 2};
                } else if (morningUnavailable && !afternoonUnavailable && !eveningUnavailable) {
                    suggestionOrder = new int[]{2, 1};
                } else if (eveningUnavailable && !morningUnavailable && !afternoonUnavailable) {
                    suggestionOrder = new int[]{0, 1};
                } else if (afternoonUnavailable && !morningUnavailable && !eveningUnavailable) {
                    suggestionOrder = new int[]{0, 2};
                } else if (morningUnavailable && eveningUnavailable) {
                    suggestionOrder = new int[]{1};
                } else if (morningUnavailable && afternoonUnavailable) {
                    suggestionOrder = new int[]{2};
                } else if (afternoonUnavailable && eveningUnavailable) {
                    suggestionOrder = new int[]{0};
                } else {
                    suggestionOrder = new int[]{0, 1, 2};
                }

                for (int s : suggestionOrder) {
                    if (slotState[d][s] == 0) {
                        slotState[d][s] = 2;
                        workoutLabels[d][s] = routine.get(routineIndex);
                        routineIndex++;
                        break;
                    }
                }

            } else {
                boolean anyUnavailable = morningUnavailable || afternoonUnavailable || eveningUnavailable;
                if (!anyUnavailable) {
                    slotState[d][0] = 2;
                    workoutLabels[d][0] = routine.get(routineIndex);
                    routineIndex++;
                }
            }
        }

        if (routineIndex == 0) {
            showAlert("No available slots found! Please unmark some slots.");
            return;
        }

        for (int d = 0; d < 7; d++)
            for (int s = 0; s < 3; s++)
                refreshCell(d, s);

        for (int d = 0; d < 7; d++) {
            boolean hasWorkout = false;
            for (int s = 0; s < 3; s++) {
                if (slotState[d][s] == 2) {
                    assignedWorkouts.add(workoutLabels[d][s]);
                    hasWorkout = true;
                    break;
                }
            }
            if (!hasWorkout && availableDays.contains(DAYS[d])) {
                assignedWorkouts.add("Rest Day");
            }
        }

        availableDays.clear();
        for (int d = 0; d < 7; d++) {
            if (sameDayMode) {
                boolean allUnavailable = slotState[d][0]==1
                        && slotState[d][1]==1 && slotState[d][2]==1;
                if (!allUnavailable) availableDays.add(DAYS[d]);
            } else {
                boolean anyUnavailable = slotState[d][0]==1
                        || slotState[d][1]==1 || slotState[d][2]==1;
                if (!anyUnavailable) availableDays.add(DAYS[d]);
            }
        }

        updateWorkoutPlanPanel(availableDays, assignedWorkouts);
    }

    private void clearAll() {
        for (int d = 0; d < 7; d++) {
            for (int s = 0; s < 3; s++) {
                slotState[d][s] = 0;
                workoutLabels[d][s] = null;
                refreshCell(d, s);
            }
        }

        workoutPlanPanel.setVisible(false);
        workoutPlanPanel.setManaged(false);

        UserSession.getInstance().setMonUnavailable(false);
        UserSession.getInstance().setTueUnavailable(false);
        UserSession.getInstance().setWedUnavailable(false);
        UserSession.getInstance().setThuUnavailable(false);
        UserSession.getInstance().setFriUnavailable(false);
        UserSession.getInstance().setSatUnavailable(false);
        UserSession.getInstance().setSunUnavailable(false);
    }

    // ===== Workout Labels =====
    public static List<String> getWorkoutLabels(String level, String split) {
        List<String> labels = new ArrayList<>();
        if (level == null || level.isEmpty() || split == null || split.isEmpty())
            return labels;

        // ── LOSE FAT PATH ───────────────────────────────────────────────────
        // level = lbsPerWeek ("0.5 lbs/week", "1.0 lbs/week", "1.5 lbs/week")
        // split = "Lose Fat"
        if ("Lose Fat".equals(split)) {
            switch (level) {
                case "0.5 lbs/week" -> {
                    // Light & Steady — 3 cardio days/week
                    labels.add("Cardio Day 1");
                    labels.add("Cardio Day 2");
                    labels.add("Cardio Day 3");
                }
                case "1.0 lbs/week" -> {
                    // Moderate — 4 cardio days/week
                    labels.add("Cardio Day 1");
                    labels.add("Cardio Day 2");
                    labels.add("Cardio Day 3");
                    labels.add("Cardio Day 4");
                }
                case "1.5 lbs/week" -> {
                    // Aggressive — 5 cardio days/week
                    labels.add("Cardio Day 1");
                    labels.add("Cardio Day 2");
                    labels.add("Cardio Day 3");
                    labels.add("Cardio Day 4");
                    labels.add("Cardio Day 5");
                }
                default -> {
                    labels.add("Cardio Day 1");
                    labels.add("Cardio Day 2");
                    labels.add("Cardio Day 3");
                }
            }
            return labels;
        }

        // ── BUILD MUSCLE & STRENGTH PATH ────────────────────────────────────
        switch (level) {
            case "Beginner" -> {
                switch (split) {
                    case "Full Body" -> {
                        labels.add("Day 1 - Full Body");
                        labels.add("Day 2 - Full Body");
                        labels.add("Day 3 - Full Body");
                    }
                    case "PPL" -> {
                        labels.add("Push Day");
                        labels.add("Pull Day");
                        labels.add("Legs Day");
                    }
                }
            }
            case "Intermediate" -> {
                switch (split) {
                    case "Upper Lower" -> {
                        labels.add("Upper Body");
                        labels.add("Lower Body");
                        labels.add("Upper Body");
                        labels.add("Lower Body");
                    }
                    case "Powerbuilding" -> {
                        labels.add("Chest & Triceps");
                        labels.add("Back & Biceps");
                        labels.add("Legs");
                    }
                    case "PPL" -> {
                        labels.add("Push Day");
                        labels.add("Pull Day");
                        labels.add("Legs Day");
                    }
                }
            }
            case "Advance" -> {
                switch (split) {
                    case "Upper Lower" -> {
                        labels.add("Upper Body");
                        labels.add("Lower Body");
                        labels.add("Upper Body");
                        labels.add("Lower Body");
                    }
                    case "PPL" -> {
                        labels.add("Push Day");
                        labels.add("Pull Day");
                        labels.add("Legs Day");
                        labels.add("Push Day");
                        labels.add("Pull Day");
                        labels.add("Legs Day");
                    }
                    case "5x5" -> {
                        labels.add("Day 1 - Chest");
                        labels.add("Day 2 - Back");
                        labels.add("Day 3 - Shoulders");
                        labels.add("Day 4 - Arms");
                        labels.add("Day 5 - Legs");
                    }
                }
            }
        }
        return labels;
    }

    // ===== Workout Details =====
    public static String getWorkoutDetails(String level, String split) {
        if (level == null || level.isEmpty() || split == null || split.isEmpty())
            return "No workout details found. Please complete your profile setup first.";

        // ── LOSE FAT PATH ────────────────────────────────────────────────────
        if ("Lose Weight".equals(split)) {
            return switch (level) {
                case "0.5 lbs/week" -> """
                        LOSE FAT — Light & Steady (0.5 lbs/week)
                        3 Cardio Sessions/week | Low-Moderate Intensity
                        ──────────────────────────────────────────────
                        EACH CARDIO DAY:
                          Jumping Jacks              2×12 reps
                          Treadmill Walk             2×10 min
                          Bodyweight Squats          2×15 reps
                          High Knees                 2×20 reps
                          Mountain Climbers          2×15 reps
                          Elliptical — Low Resist.   1×15 min
                          Plank                      2×20 sec

                        DIET TIP: ~250 kcal daily deficit.
                        REST DAYS: Light walking or stretching.
                        """;
                case "1.0 lbs/week" -> """
                        LOSE FAT — Moderate (1.0 lbs/week)
                        4 Cardio Sessions/week | Moderate-High Intensity
                        ──────────────────────────────────────────────
                        EACH CARDIO DAY:
                          Jumping Jacks              3×12 reps
                          Treadmill Jog              1×15 min
                          Jump Squat                 3×12 reps
                          High Knees                 3×30 reps
                          Mountain Climbers          3×20 reps
                          Burpee                     3×10 reps
                          Stationary Bike            1×15 min
                          Plank                      3×30 sec

                        DIET TIP: ~500 kcal daily deficit.
                        REST DAYS: 20–30 min walk recommended.
                        """;
                case "1.5 lbs/week" -> """
                        LOSE FAT — Aggressive (1.5 lbs/week)
                        5 Cardio Sessions/week | High-Very High Intensity
                        ──────────────────────────────────────────────
                        EACH CARDIO DAY:
                          Jumping Jacks              5×12 reps
                          Sprint Intervals           1×20 min
                          Jump Squat                 5×15 reps
                          High Knees                 5×40 reps
                          Mountain Climbers          5×30 reps
                          Burpee                     5×12 reps
                          Kettlebell Swing           4×15 reps
                          Battle Ropes               4×30 sec
                          Plank                      5×45 sec

                        DIET TIP: ~750 kcal daily deficit.
                        REST DAYS: Active recovery only (yoga/walk).
                        """;
                default -> "No details found for your Lose Fat goal.";
            };
        }

        // ── BUILD MUSCLE & STRENGTH PATH ─────────────────────────────────────
        switch (level) {
            case "Beginner" -> {
                switch (split) {
                    case "Full Body" -> { return """
                            DAY 1:
                              Barbell Squat            3×8–10
                              Bench Press              3×8–10
                              Bent Over Row            3×8–10
                              Dumbbell Shoulder Press  3×10
                              Bicep Curl               3×10–12
                              Tricep Pushdown          3×10–12

                            DAY 2:
                              Deadlift                 3×6–8
                              Incline Dumbbell Press   3×8–10
                              Lat Pulldown             3×10
                              Lateral Raise            3×12
                              Hammer Curl              3×10–12
                              Tricep Dips              3×10–12

                            DAY 3:
                              Leg Press                3×10
                              Push Ups                 3×12
                              Seated Cable Row         3×10
                              Shoulder Press           3×10
                              Plank                    3×30–45 sec
                            """; }
                    case "PPL" -> { return """
                            PUSH DAY:
                              Bench Press              3×8–10
                              Incline Dumbbell Press   3×10
                              Overhead Shoulder Press  3×8–10
                              Lateral Raise            3×12–15
                              Tricep Pushdown          3×12

                            PULL DAY:
                              Deadlift                 3×5–8
                              Pull Ups / Lat Pulldown  3×8–10
                              Barbell Row              3×8–10
                              Face Pull                3×12–15
                              Bicep Curl               3×10–12

                            LEGS DAY:
                              Squat                    3×8–10
                              Romanian Deadlift        3×8–10
                              Leg Press                3×10–12
                              Walking Lunges           3×12
                              Standing Calf Raise      3×15
                            """; }
                }
            }
            case "Intermediate" -> {
                switch (split) {
                    case "Upper Lower" -> { return """
                            UPPER DAY:
                              Bench Press              4×6–8
                              Barbell Row              4×6–8
                              Incline Dumbbell Press   3×8–10
                              Pull Ups                 3×8–10
                              Lateral Raise            3×12
                              Bicep Curl               3×10
                              Tricep Pushdown          3×10

                            LOWER DAY:
                              Squat                    4×6–8
                              Romanian Deadlift        3×8
                              Leg Press                3×10
                              Leg Curl                 3×10–12
                              Calf Raise               4×12–15
                            """; }
                    case "Powerbuilding" -> { return """
                            DAY 1 – CHEST/TRICEPS:
                              Bench Press              5×5
                              Incline Bench Press      4×8
                              Chest Fly                3×12
                              Skull Crushers           3×10
                              Tricep Pushdown          3×12

                            DAY 2 – BACK/BICEPS:
                              Deadlift                 5×5
                              Barbell Row              4×8
                              Lat Pulldown             4×10
                              Barbell Curl             3×10
                              Hammer Curl              3×12

                            DAY 3 – LEGS:
                              Squat                    5×5
                              Leg Press                4×10
                              Leg Curl                 3×12
                              Walking Lunges           3×12
                              Calf Raises              4×15
                            """; }
                    case "PPL" -> { return """
                            PUSH DAY:
                              Bench Press              4×6–8
                              Incline Dumbbell Press   3×8–10
                              Shoulder Press           3×8
                              Lateral Raise            3×12–15
                              Tricep Dips              3×10

                            PULL DAY:
                              Deadlift                 4×5
                              Pull Ups                 3×8–10
                              Barbell Row              3×8
                              Face Pull                3×12
                              Barbell Curl             3×10

                            LEGS DAY:
                              Squat                    4×6–8
                              Romanian Deadlift        3×8
                              Leg Press                3×10
                              Leg Extension            3×12
                              Calf Raise               4×15
                            """; }
                }
            }
            case "Advance" -> {
                switch (split) {
                    case "Upper Lower" -> { return """
                            UPPER DAY:
                              Bench Press              5×5
                              Weighted Pull Ups        4×6
                              Incline Bench Press      4×8
                              Barbell Row              4×8
                              Lateral Raise            4×12
                              Tricep Extension         3×12
                              Bicep Curl               3×12

                            LOWER DAY:
                              Squat                    5×5
                              Deadlift                 4×5
                              Leg Press                4×10
                              Leg Curl                 3×12
                              Walking Lunges           3×12
                              Standing Calf Raise      5×15
                            """; }
                    case "PPL" -> { return """
                            PUSH DAY:
                              Bench Press              5×5
                              Incline Bench Press      4×8
                              Shoulder Press           4×8
                              Lateral Raise            4×15
                              Tricep Pushdown          4×12

                            PULL DAY:
                              Deadlift                 5×5
                              Pull Ups                 4×8
                              Barbell Row              4×8
                              Face Pull                4×12
                              Barbell Curl             4×10

                            LEGS DAY:
                              Squat                    5×5
                              Romanian Deadlift        4×8
                              Leg Press                4×10
                              Leg Extension            4×12
                              Calf Raise               5×15
                            """; }
                    case "5x5" -> { return """
                            DAY 1 – CHEST:
                              Bench Press              5×5
                              Incline Dumbbell Press   4×8
                              Chest Fly                4×12
                              Dips                     3×10

                            DAY 2 – BACK:
                              Deadlift                 5×5
                              Pull Ups                 4×8
                              Barbell Row              4×8
                              Lat Pulldown             4×10

                            DAY 3 – SHOULDERS:
                              Overhead Press           5×5
                              Lateral Raise            4×12
                              Rear Delt Fly            4×12

                            DAY 4 – ARMS:
                              Barbell Curl             4×10
                              Hammer Curl              4×12
                              Skull Crushers           4×10
                              Tricep Pushdown          4×12

                            DAY 5 – LEGS:
                              Squat                    5×5
                              Leg Press                4×10
                              Romanian Deadlift        4×8
                              Leg Curl                 4×12
                              Calf Raise               5×15
                            """; }
                }
            }
        }
        return "No workout details found for your current split.";
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("TaraGym");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}