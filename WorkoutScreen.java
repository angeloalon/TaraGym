import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class WorkoutScreen {

    // Colors
    private final String BG_PRIMARY     = "#1A1D20";
    private final String BG_SECONDARY   = "#0D0F12";
    private final String ACCENT         = "#E63946";
    private final String TEXT_PRIMARY   = "#FFFFFF";
    private final String TEXT_SECONDARY = "#A8B2C1";
    private final String CELL_BG        = "#1e2428";

    // Pull from UserSession
    private final String level      = UserSession.getInstance().getLevel();
    private final String split      = UserSession.getInstance().getWorkoutSplit();
    private final String goal       = UserSession.getInstance().getGoal();
    private final String lbsPerWeek = UserSession.getInstance().getLbsPerWeek();
    private final String todayLabel = getTodayWorkoutLabel();

    // Unit
    private String unit = "lbs";

    // Table data
    private ObservableList<ExerciseRow> tableData = FXCollections.observableArrayList();

    // Table
    private TableView<ExerciseRow> table;

    // Stage reference for navigation after finish
    private Stage primaryStage;

    // ===== Data Model =====
    public static class ExerciseRow {
        private final String exercise;
        private String set;
        private String reps;
        private String weight;

        public ExerciseRow(String exercise, String set, String reps, String weight) {
            this.exercise = exercise;
            this.set      = set;
            this.reps     = reps;
            this.weight   = weight;
        }

        public String getExercise() { return exercise; }
        public String getSet()      { return set;      }
        public String getReps()     { return reps;     }
        public String getWeight()   { return weight;   }
        public void setWeight(String weight) { this.weight = weight; }
        public void setReps(String reps) { this.reps = reps; }
        public void setSets(String set) { this.set = set; }


    }

    private String getTodayWorkoutLabel() {
        List<String> availableDays = UserSession.getInstance().getAvailableDays();
        String level = UserSession.getInstance().getLevel();
        String split = UserSession.getInstance().getWorkoutSplit();
        String goal  = UserSession.getInstance().getGoal();

        // Lose Fat users always get "Cardio Day" — no complex label needed
        if ("Lose Fat".equals(goal) || "Lose Weight".equals(split)) {
            if (availableDays == null || availableDays.isEmpty()) {
                return "Cardio Day";
            }
            String today = java.time.LocalDate.now()
                    .getDayOfWeek()
                    .getDisplayName(java.time.format.TextStyle.FULL,
                            java.util.Locale.ENGLISH);
            return availableDays.contains(today) ? "Cardio Day" : "Rest Day";
        }

        // If schedule not generated yet, fall back
        if (availableDays == null || availableDays.isEmpty()) {
            return "now";
        }

        String today = java.time.LocalDate.now()
                .getDayOfWeek()
                .getDisplayName(java.time.format.TextStyle.FULL,
                        java.util.Locale.ENGLISH);

        int dayIndex = availableDays.indexOf(today);
        if (dayIndex == -1) return "Rest Day";

        List<String> workoutLabels = Schedule.getWorkoutLabels(level, split);
        if (dayIndex >= workoutLabels.size()) return "Rest Day";

        return workoutLabels.get(dayIndex);
    }

    // ===== Build Scene =====
    public Scene buildScene(Stage stage) {
        this.primaryStage = stage;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PRIMARY + ";");

        VBox topSection = new VBox(0);
        topSection.getChildren().addAll(buildTopBar(), buildNavBar(stage));

        root.setTop(topSection);
        root.setCenter(buildContent());
        root.setBottom(buildBottomBar(stage));

        return new Scene(root, 900, 660);
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

        Label screen = new Label("Workout");
        screen.setFont(Font.font("SansSerif", FontWeight.NORMAL, 14));
        screen.setTextFill(Color.web(TEXT_PRIMARY));

        Label sep2 = new Label("  |  ");
        sep2.setTextFill(Color.web(TEXT_SECONDARY));

        Label sessionLabel = new Label(todayLabel);
        sessionLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        sessionLabel.setTextFill(Color.web(ACCENT));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String day = LocalDate.now().getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        Label dateLabel = new Label("Today: " + day);
        dateLabel.setFont(Font.font("SansSerif", 12));
        dateLabel.setTextFill(Color.web(TEXT_SECONDARY));

        bar.getChildren().addAll(title, sep, screen, sep2, sessionLabel, spacer, dateLabel);
        return bar;
    }

    // ===== Nav Bar =====
    private HBox buildNavBar(Stage stage) {
        HBox nav = new HBox(0);
        nav.setAlignment(Pos.CENTER);
        nav.setStyle("-fx-background-color: " + BG_SECONDARY + ";");
        nav.setPadding(new Insets(8, 20, 8, 20));

        nav.getChildren().addAll(
                navButton("🏠 Home",     stage, "home"),
                navButton("📋 History",  stage, "history"),
                navButton("💪 Workout",  stage, "workout"),
                navButton("📅 Schedule", stage, "schedule"),
                navButton("📈 Progress", stage, "progress"),
                navButton("⚙ Settings", stage, "settings")
        );
        return nav;
    }

    private Button navButton(String text, Stage stage, String screen) {
        Button btn = new Button(text);
        boolean isActive = screen.equals("workout");

        String activeStyle  = "-fx-background-color: " + ACCENT + "; -fx-text-fill: white;"
                + "-fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;";
        String normalStyle  = "-fx-background-color: transparent; -fx-text-fill: " + TEXT_SECONDARY + ";"
                + "-fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;";
        String hoverStyle   = "-fx-background-color: #2a2d30; -fx-text-fill: white;"
                + "-fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;";

        btn.setStyle(isActive ? activeStyle : normalStyle);

        if (!isActive) {
            btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
            btn.setOnMouseExited(e  -> btn.setStyle(normalStyle));
        }

        btn.setOnAction(e -> Navigator.go(stage, screen));
        return btn;
    }

    // ===== Content =====
    private VBox buildContent() {
        VBox content = new VBox(12);
        content.setPadding(new Insets(20));

        HBox unitRow = buildUnitSelector();
        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        loadRoutine();

        HBox addRow = buildAddExerciseRow();
        content.getChildren().addAll(unitRow, table, addRow);
        return content;
    }

    // ===== Unit Selector =====
    private HBox buildUnitSelector() {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label("Weight Unit:");
        label.setTextFill(Color.web(TEXT_SECONDARY));
        label.setFont(Font.font("SansSerif", 12));

        ToggleGroup group = new ToggleGroup();

        ToggleButton lbsBtn = new ToggleButton("lbs");
        lbsBtn.setToggleGroup(group);
        lbsBtn.setSelected(true);
        styleToggleBtn(lbsBtn, true);

        ToggleButton kgBtn = new ToggleButton("kg");
        kgBtn.setToggleGroup(group);
        styleToggleBtn(kgBtn, false);

        lbsBtn.setOnAction(e -> {
            unit = "lbs";
            styleToggleBtn(lbsBtn, true);
            styleToggleBtn(kgBtn, false);
        });

        kgBtn.setOnAction(e -> {
            unit = "kg";
            styleToggleBtn(kgBtn, true);
            styleToggleBtn(lbsBtn, false);
        });

        row.getChildren().addAll(label, lbsBtn, kgBtn);
        return row;
    }

    private void styleToggleBtn(ToggleButton btn, boolean active) {
        if (active) {
            btn.setStyle("-fx-background-color: " + ACCENT +
                    "; -fx-text-fill: white; -fx-font-weight: bold;" +
                    "-fx-padding: 6 16; -fx-background-radius: 6; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: #2a2d30;" +
                    "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                    "-fx-padding: 6 16; -fx-background-radius: 6; -fx-cursor: hand;");
        }
    }

    // ===== Table =====
    private TableView<ExerciseRow> buildTable() {
        TableView<ExerciseRow> tv = new TableView<>();
        tv.setEditable(true);
        tv.setStyle("-fx-background-color: " + BG_SECONDARY + "; -fx-border-color: transparent;");

        TableColumn<ExerciseRow, String> exerciseCol = new TableColumn<>("Exercise");
        exerciseCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getExercise()));
        exerciseCol.setStyle("-fx-alignment: CENTER;");
        exerciseCol.setPrefWidth(400);
        exerciseCol.setMinWidth(200);

        TableColumn<ExerciseRow, String> setCol = new TableColumn<>("Set");
        setCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getSet()));
        setCol.setCellFactory(TextFieldTableCell.forTableColumn());

        setCol.setOnEditCommit(e -> e.getRowValue().setSets(e.getNewValue()));

        setCol.setStyle("-fx-alignment: CENTER;");
        setCol.setMaxWidth(80); setCol.setMinWidth(80);

        TableColumn<ExerciseRow, String> repsCol = new TableColumn<>("Reps");
        repsCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getReps()));
                repsCol.setCellFactory(TextFieldTableCell.forTableColumn());

        repsCol.setOnEditCommit(e -> e.getRowValue().setReps(e.getNewValue()));

        repsCol.setStyle("-fx-alignment: CENTER;");
        repsCol.setMaxWidth(100); repsCol.setMinWidth(100);

        TableColumn<ExerciseRow, String> weightCol = new TableColumn<>("Weight");
        weightCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getWeight()));
        weightCol.setCellFactory(TextFieldTableCell.forTableColumn());
        weightCol.setOnEditCommit(e -> e.getRowValue().setWeight(e.getNewValue()));
        weightCol.setStyle("-fx-alignment: CENTER;");
        weightCol.setMaxWidth(120); weightCol.setMinWidth(120);

        tv.setRowFactory(row -> {
            TableRow<ExerciseRow> tableRow = new TableRow<>();
            tableRow.setStyle("-fx-background-color: " + CELL_BG + ";");
            tableRow.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                tableRow.setStyle(isSelected
                        ? "-fx-background-color: #2a2d30;"
                        : "-fx-background-color: " + CELL_BG + ";");
            });
            return tableRow;
        });

        tv.setItems(tableData);
        tv.getColumns().addAll(exerciseCol, setCol, repsCol, weightCol);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        styleTable(tv);
        return tv;
    }

    private void styleTable(TableView<ExerciseRow> tv) {
        tv.getStylesheets().add("data:text/css," +
                ".table-view { -fx-background-color: #0D0F12; }" +
                ".table-view .column-header { -fx-background-color: #0D0F12; }" +
                ".table-view .column-header .label { -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; }" +
                ".table-view .table-cell { -fx-text-fill: white; -fx-font-size: 12px; -fx-alignment: center; }" +
                ".table-view .table-row-cell { -fx-background-color: #1e2428; }" +
                ".table-view .table-row-cell:selected { -fx-background-color: #2a2d30; }" +
                ".table-view .table-row-cell:odd { -fx-background-color: #1a1d20; }" +
                ".table-view .table-row-cell:hover { -fx-background-color: #252a2e; }" +
                ".table-view .scroll-bar { -fx-background-color: #0D0F12; }" +
                ".table-view .corner { -fx-background-color: #0D0F12; }" +
                ".table-view .column-header-background { -fx-background-color: #0D0F12; }" +
                ".text-field-table-cell { -fx-background-color: transparent; }" +
                ".text-field-table-cell .text-field { -fx-background-color: #2a2d30; -fx-text-fill: white; }"
        );
    }

    // ===== Load Routine =====
    private void loadRoutine() {
        if (todayLabel.equals("Rest Day")) {
            tableData.add(new ExerciseRow("Today is a Rest Day!", "", "", ""));
            tableData.add(new ExerciseRow("Recover and come back stronger tomorrow.", "", "", ""));
            return;
        }

        // Route to Lose Fat cardio exercises if goal is Lose Fat
        if ("Lose Fat".equals(goal) || "Lose Weight".equals(split)) {

            String intensity = (lbsPerWeek != null && !lbsPerWeek.isEmpty())
                    ? lbsPerWeek : level;
            List<String[]> exercises = getLoseFatExercises(intensity);
            if (exercises.isEmpty()) {
                tableData.add(new ExerciseRow("No exercises found for " + todayLabel, "", "", ""));
                return;
            }
            for (String[] ex : exercises) {
                String name = ex[0];
                int sets = Integer.parseInt(ex[1]);
                String repsOrDuration = ex[2];
                for (int i = 1; i <= sets; i++) {
                    tableData.add(new ExerciseRow(name, "Set " + i, repsOrDuration, ""));
                }
            }
            return;
        }

        List<String[]> exercises = getExercises(level, split, todayLabel);

        if (exercises.isEmpty()) {
            tableData.add(new ExerciseRow("No exercises found for " + todayLabel, "", "", ""));
            return;
        }

        for (String[] ex : exercises) {
            String name = ex[0];
            int sets = Integer.parseInt(ex[1]);
            int reps = Integer.parseInt(ex[2]);
            for (int i = 1; i <= sets; i++) {
                tableData.add(new ExerciseRow(name, "Set " + i, reps + " reps", ""));
            }
        }
    }

    // ===== LOSE FAT EXERCISES (from CardioPanel exercises, scaled by intensity) =====
    // Format: { exerciseName, sets, reps/duration }
    // 0.5 lbs/week → Light intensity (2 sets, lower reps/duration)
    // 1.0 lbs/week → Moderate intensity (3–4 sets, standard reps/duration)
    // 1.5 lbs/week → High intensity (5 sets, higher reps/duration)
    private List<String[]> getLoseFatExercises(String lbs) {
        List<String[]> exercises = new ArrayList<>();

        if (lbs == null || lbs.isEmpty()) lbs = "1.0 lbs/week";

        switch (lbs) {
            case "0.5 lbs/week" -> {
                // Light & Steady — Low intensity, fewer sets
                exercises.add(new String[]{"Jumping Jacks",     "2", "12 reps"});
                exercises.add(new String[]{"Treadmill Walk",    "2", "10 min"});
                exercises.add(new String[]{"Bodyweight Squats", "2", "15 reps"});
                exercises.add(new String[]{"High Knees",        "2", "20 reps"});
                exercises.add(new String[]{"Mountain Climbers", "2", "15 reps"});
                exercises.add(new String[]{"Elliptical — Low Resistance", "1", "15 min"});
                exercises.add(new String[]{"Plank",             "2", "20 sec"});
            }
            case "1.0 lbs/week" -> {
                // Moderate — Balanced cardio
                exercises.add(new String[]{"Jumping Jacks",     "3", "12 reps"});
                exercises.add(new String[]{"Treadmill Jog",     "1", "15 min"});
                exercises.add(new String[]{"Jump Squat",        "3", "12 reps"});
                exercises.add(new String[]{"High Knees",        "3", "30 reps"});
                exercises.add(new String[]{"Mountain Climbers", "3", "20 reps"});
                exercises.add(new String[]{"Burpee",            "3", "10 reps"});
                exercises.add(new String[]{"Stationary Bike — Steady Pace", "1", "15 min"});
                exercises.add(new String[]{"Plank",             "3", "30 sec"});
            }
            case "1.5 lbs/week" -> {
                // Aggressive — High intensity, more sets
                exercises.add(new String[]{"Jumping Jacks",       "5", "12 reps"});
                exercises.add(new String[]{"Sprint Intervals",    "1", "20 min"});
                exercises.add(new String[]{"Jump Squat",          "5", "15 reps"});
                exercises.add(new String[]{"High Knees",          "5", "40 reps"});
                exercises.add(new String[]{"Mountain Climbers",   "5", "30 reps"});
                exercises.add(new String[]{"Burpee",              "5", "12 reps"});
                exercises.add(new String[]{"Kettlebell Swing",    "4", "15 reps"});
                exercises.add(new String[]{"Battle Ropes",        "4", "30 sec"});
                exercises.add(new String[]{"Plank",               "5", "45 sec"});
            }
            default -> {
                // Fallback: same as 1.0 lbs/week
                exercises.add(new String[]{"Jumping Jacks",     "3", "12 reps"});
                exercises.add(new String[]{"Treadmill Jog",     "1", "15 min"});
                exercises.add(new String[]{"Jump Squat",        "3", "12 reps"});
                exercises.add(new String[]{"High Knees",        "3", "30 reps"});
                exercises.add(new String[]{"Mountain Climbers", "3", "20 reps"});
                exercises.add(new String[]{"Burpee",            "3", "10 reps"});
                exercises.add(new String[]{"Plank",             "3", "30 sec"});
            }
        }

        return exercises;
    }

    // ===== Add Exercise Row =====
    private HBox buildAddExerciseRow() {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 0, 0, 0));

        TextField exerciseField = new TextField();
        exerciseField.setPromptText("Exercise name");
        styleField(exerciseField);
        HBox.setHgrow(exerciseField, Priority.ALWAYS);

        TextField repsField = new TextField();
        repsField.setPromptText("Reps");
        repsField.setPrefWidth(80);
        styleField(repsField);

        TextField weightField = new TextField();
        weightField.setPromptText("Weight");
        weightField.setPrefWidth(80);
        styleField(weightField);

        Button addBtn = new Button("+ Add Exercise");
        addBtn.setStyle("-fx-background-color: " + ACCENT +
                "; -fx-text-fill: white; -fx-font-weight: bold;" +
                "-fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");

        addBtn.setOnAction(e -> {
            String exercise = exerciseField.getText().trim();
            String reps     = repsField.getText().trim();
            String weight   = weightField.getText().trim();

            if (exercise.isEmpty() || reps.isEmpty()) {
                showAlert("Please fill in at least Exercise and Reps!");
                return;
            }

            int setNum = 1;
            for (ExerciseRow r : tableData) {
                if (r.getExercise().equalsIgnoreCase(exercise)) setNum++;
            }

            tableData.add(new ExerciseRow(exercise, "Set " + setNum, reps + " reps", weight));
            exerciseField.clear();
            repsField.clear();
            weightField.clear();
            exerciseField.requestFocus();
        });

        row.getChildren().addAll(exerciseField, repsField, weightField, addBtn);
        return row;
    }

    // ===== Bottom Bar =====
    private HBox buildBottomBar(Stage stage) {
        HBox bar = new HBox(10);
        bar.setPadding(new Insets(14, 20, 14, 20));
        bar.setAlignment(Pos.CENTER_RIGHT);
        bar.setStyle("-fx-background-color: " + BG_SECONDARY + ";");

        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: #2a2d30; -fx-text-fill: white;" +
                "-fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");
        backBtn.setOnAction(e -> Navigator.go(stage, "home"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button finishBtn = new Button("Finish Session ✓");
        finishBtn.setStyle("-fx-background-color: " + ACCENT +
                "; -fx-text-fill: white; -fx-font-weight: bold;" +
                "-fx-padding: 10 24; -fx-background-radius: 6; -fx-cursor: hand;");
        finishBtn.setOnAction(e -> finishSession(stage));

        bar.getChildren().addAll(backBtn, spacer, finishBtn);
        return bar;
    }

    // ===== Finish Session =====
    private void finishSession(Stage stage) {
        boolean hasWeight = tableData.stream().anyMatch(r -> !r.getWeight().isEmpty());

        if (!hasWeight) {
            showAlert("Please enter at least one weight before finishing!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Session Duration");
        dialog.setHeaderText(null);
        dialog.setContentText("How long was your workout? (minutes)");
        dialog.getDialogPane().setStyle("-fx-background-color: " + BG_SECONDARY + ";");
        dialog.getDialogPane().getStylesheets().add("data:text/css," +
                ".dialog-pane .content.label { -fx-text-fill: white; }" +
                ".dialog-pane { -fx-background-color: #0D0F12; }" +
                ".dialog-pane .label { -fx-text-fill: white; }"
        );

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(durationStr -> {
            try {
                int mins = Integer.parseInt(durationStr.trim());

                String date = LocalDate.now().toString();
                int sessionId = DatabaseManager.saveSession(date, mins, todayLabel);

                if (sessionId == -1) {
                    showAlert("Failed to save session. Are you logged in?");
                    return;
                }

                String currentUnit = unit;
                for (ExerciseRow row : tableData) {
                    if (row.getWeight().isEmpty()) continue;
                    try {
                        int setNum = Integer.parseInt(
                                row.getSet().replace("Set ", "").trim());
                        // reps field may contain "reps", "sec", "min" — extract leading number
                        String repsRaw = row.getReps().replaceAll("[^0-9]", "").trim();
                        int reps = repsRaw.isEmpty() ? 0 : Integer.parseInt(repsRaw);
                        double weight = Double.parseDouble(row.getWeight().trim());
                        double weightLbs = currentUnit.equals("lbs") ? weight / 2.205 : weight;

                        DatabaseManager.saveWorkoutLog(
                                sessionId,
                                row.getExercise(),
                                getMuscleGroup(row.getExercise()),
                                setNum,
                                reps,
                                weightLbs
                        );
                    } catch (NumberFormatException ignored) {}
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Session Complete!");
                alert.setHeaderText("Great workout! 💪");
                alert.setContentText(
                        "Session: " + todayLabel + "\n" +
                                "Duration: " + mins + " mins\n" +
                                "Exercises: " + countUniqueExercises() + "\n" +
                                "Sets logged: " + tableData.stream()
                                .filter(r -> !r.getWeight().isEmpty()).count()
                );
                alert.showAndWait();

                Navigator.go(stage, "history");

            } catch (NumberFormatException ex) {
                showAlert("Please enter a valid number for duration!");
            }
        });
    }

    // ===== Muscle Group Mapper =====
    private String getMuscleGroup(String exercise) {
        exercise = exercise.toLowerCase();
        if (exercise.contains("bench") || exercise.contains("chest") ||
                exercise.contains("fly")   || exercise.contains("dip"))    return "Chest";
        if (exercise.contains("squat") || exercise.contains("leg")    ||
                exercise.contains("lunge") || exercise.contains("calf"))   return "Legs";
        if (exercise.contains("deadlift") || exercise.contains("row") ||
                exercise.contains("pulldown") || exercise.contains("pull")||
                exercise.contains("lat"))                                  return "Back";
        if (exercise.contains("curl")   || exercise.contains("bicep") ||
                exercise.contains("hammer"))                               return "Biceps";
        if (exercise.contains("tricep") || exercise.contains("pushdown") ||
                exercise.contains("skull"))                                return "Triceps";
        if (exercise.contains("press")    || exercise.contains("shoulder") ||
                exercise.contains("lateral")  || exercise.contains("delt")     ||
                exercise.contains("overhead"))                             return "Shoulders";
        if (exercise.contains("plank")   || exercise.contains("crunch") ||
                exercise.contains("mountain") || exercise.contains("climber")) return "Core";
        if (exercise.contains("jumping") || exercise.contains("jacks") ||
                exercise.contains("burpee")   || exercise.contains("treadmill")||
                exercise.contains("sprint")   || exercise.contains("bike")     ||
                exercise.contains("elliptical")|| exercise.contains("kettlebell")||
                exercise.contains("battle")   || exercise.contains("high knees")) return "Cardio";
        return "Other";
    }

    private long countUniqueExercises() {
        return tableData.stream()
                .map(ExerciseRow::getExercise)
                .distinct().count();
    }

    private void styleField(TextField field) {
        field.setStyle("-fx-background-color: " + CELL_BG +
                "; -fx-text-fill: white; -fx-prompt-text-fill: " + TEXT_SECONDARY +
                "; -fx-padding: 8; -fx-background-radius: 6;" +
                "-fx-border-color: #2a2d30; -fx-border-radius: 6;");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("TaraGym");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ===== Exercise Database (Build Muscle & Strength) =====
    private List<String[]> getExercises(String level, String split, String label) {
        List<String[]> exercises = new ArrayList<>();

        switch (level) {
            case "Beginner" -> {
                switch (split) {
                    case "PPL" -> {
                        switch (label) {
                            case "Push Day" -> {
                                exercises.add(new String[]{"Bench Press", "3", "10"});
                                exercises.add(new String[]{"Incline Dumbbell Press", "3", "10"});
                                exercises.add(new String[]{"Overhead Shoulder Press", "3", "10"});
                                exercises.add(new String[]{"Lateral Raise", "3", "15"});
                                exercises.add(new String[]{"Tricep Pushdown", "3", "12"});
                            }
                            case "Pull Day" -> {
                                exercises.add(new String[]{"Deadlift", "3", "8"});
                                exercises.add(new String[]{"Pull Ups / Lat Pulldown", "3", "10"});
                                exercises.add(new String[]{"Barbell Row", "3", "10"});
                                exercises.add(new String[]{"Face Pull", "3", "15"});
                                exercises.add(new String[]{"Bicep Curl", "3", "12"});
                            }
                            case "Legs Day" -> {
                                exercises.add(new String[]{"Squat", "3", "10"});
                                exercises.add(new String[]{"Romanian Deadlift", "3", "10"});
                                exercises.add(new String[]{"Leg Press", "3", "12"});
                                exercises.add(new String[]{"Walking Lunges", "3", "12"});
                                exercises.add(new String[]{"Standing Calf Raise", "3", "15"});
                            }
                        }
                    }
                    case "Full Body" -> {
                        exercises.add(new String[]{"Barbell Squat", "3", "10"});
                        exercises.add(new String[]{"Bench Press", "3", "10"});
                        exercises.add(new String[]{"Bent Over Row", "3", "10"});
                        exercises.add(new String[]{"Dumbbell Shoulder Press", "3", "10"});
                        exercises.add(new String[]{"Bicep Curl", "3", "12"});
                        exercises.add(new String[]{"Tricep Pushdown", "3", "12"});
                    }
                    case "Upper Lower" -> {
                        switch (label) {
                            case "Upper Body" -> {
                                exercises.add(new String[]{"Bench Press", "3", "10"});
                                exercises.add(new String[]{"Barbell Row", "3", "10"});
                                exercises.add(new String[]{"Overhead Press", "3", "10"});
                                exercises.add(new String[]{"Lat Pulldown", "3", "10"});
                                exercises.add(new String[]{"Bicep Curl", "3", "12"});
                                exercises.add(new String[]{"Tricep Pushdown", "3", "12"});
                            }
                            case "Lower Body" -> {
                                exercises.add(new String[]{"Squat", "3", "10"});
                                exercises.add(new String[]{"Romanian Deadlift", "3", "10"});
                                exercises.add(new String[]{"Leg Press", "3", "12"});
                                exercises.add(new String[]{"Walking Lunges", "3", "12"});
                                exercises.add(new String[]{"Calf Raise", "3", "15"});
                            }
                        }
                    }
                }
            }
            case "Intermediate" -> {
                switch (split) {
                    case "PPL" -> {
                        switch (label) {
                            case "Push Day" -> {
                                exercises.add(new String[]{"Bench Press", "4", "8"});
                                exercises.add(new String[]{"Incline Dumbbell Press", "3", "10"});
                                exercises.add(new String[]{"Shoulder Press", "3", "8"});
                                exercises.add(new String[]{"Lateral Raise", "3", "15"});
                                exercises.add(new String[]{"Tricep Dips", "3", "10"});
                            }
                            case "Pull Day" -> {
                                exercises.add(new String[]{"Deadlift", "4", "5"});
                                exercises.add(new String[]{"Pull Ups", "3", "10"});
                                exercises.add(new String[]{"Barbell Row", "3", "8"});
                                exercises.add(new String[]{"Face Pull", "3", "12"});
                                exercises.add(new String[]{"Barbell Curl", "3", "10"});
                            }
                            case "Legs Day" -> {
                                exercises.add(new String[]{"Squat", "4", "8"});
                                exercises.add(new String[]{"Romanian Deadlift", "3", "8"});
                                exercises.add(new String[]{"Leg Press", "3", "10"});
                                exercises.add(new String[]{"Leg Extension", "3", "12"});
                                exercises.add(new String[]{"Calf Raise", "4", "15"});
                            }
                        }
                    }
                    case "Upper Lower" -> {
                        switch (label) {
                            case "Upper Body" -> {
                                exercises.add(new String[]{"Bench Press", "4", "8"});
                                exercises.add(new String[]{"Barbell Row", "4", "8"});
                                exercises.add(new String[]{"Incline Dumbbell Press", "3", "10"});
                                exercises.add(new String[]{"Pull Ups", "3", "10"});
                                exercises.add(new String[]{"Lateral Raise", "3", "12"});
                                exercises.add(new String[]{"Bicep Curl", "3", "10"});
                                exercises.add(new String[]{"Tricep Pushdown", "3", "10"});
                            }
                            case "Lower Body" -> {
                                exercises.add(new String[]{"Squat", "4", "8"});
                                exercises.add(new String[]{"Romanian Deadlift", "3", "8"});
                                exercises.add(new String[]{"Leg Press", "3", "10"});
                                exercises.add(new String[]{"Leg Curl", "3", "12"});
                                exercises.add(new String[]{"Calf Raise", "4", "15"});
                            }
                        }
                    }
                    case "Powerbuilding" -> {
                        switch (label) {
                            case "Chest & Triceps" -> {
                                exercises.add(new String[]{"Bench Press", "5", "5"});
                                exercises.add(new String[]{"Incline Bench Press", "4", "8"});
                                exercises.add(new String[]{"Chest Fly", "3", "12"});
                                exercises.add(new String[]{"Skull Crushers", "3", "10"});
                                exercises.add(new String[]{"Tricep Pushdown", "3", "12"});
                            }
                            case "Back & Biceps" -> {
                                exercises.add(new String[]{"Deadlift", "5", "5"});
                                exercises.add(new String[]{"Barbell Row", "4", "8"});
                                exercises.add(new String[]{"Lat Pulldown", "4", "10"});
                                exercises.add(new String[]{"Barbell Curl", "3", "10"});
                                exercises.add(new String[]{"Hammer Curl", "3", "12"});
                            }
                            case "Legs" -> {
                                exercises.add(new String[]{"Squat", "5", "5"});
                                exercises.add(new String[]{"Leg Press", "4", "10"});
                                exercises.add(new String[]{"Leg Curl", "3", "12"});
                                exercises.add(new String[]{"Walking Lunges", "3", "12"});
                                exercises.add(new String[]{"Calf Raises", "4", "15"});
                            }
                        }
                    }
                }
            }
            case "Advance" -> {
                switch (split) {
                    case "PPL" -> {
                        switch (label) {
                            case "Push Day" -> {
                                exercises.add(new String[]{"Bench Press", "5", "5"});
                                exercises.add(new String[]{"Incline Bench Press", "4", "8"});
                                exercises.add(new String[]{"Shoulder Press", "4", "8"});
                                exercises.add(new String[]{"Lateral Raise", "4", "15"});
                                exercises.add(new String[]{"Tricep Pushdown", "4", "12"});
                            }
                            case "Pull Day" -> {
                                exercises.add(new String[]{"Deadlift", "5", "5"});
                                exercises.add(new String[]{"Pull Ups", "4", "8"});
                                exercises.add(new String[]{"Barbell Row", "4", "8"});
                                exercises.add(new String[]{"Face Pull", "4", "12"});
                                exercises.add(new String[]{"Barbell Curl", "4", "10"});
                            }
                            case "Legs Day" -> {
                                exercises.add(new String[]{"Squat", "5", "5"});
                                exercises.add(new String[]{"Romanian Deadlift", "4", "8"});
                                exercises.add(new String[]{"Leg Press", "4", "10"});
                                exercises.add(new String[]{"Leg Extension", "4", "12"});
                                exercises.add(new String[]{"Calf Raise", "5", "15"});
                            }
                        }
                    }
                    case "Upper Lower" -> {
                        switch (label) {
                            case "Upper Body" -> {
                                exercises.add(new String[]{"Bench Press", "5", "5"});
                                exercises.add(new String[]{"Weighted Pull Ups", "4", "6"});
                                exercises.add(new String[]{"Incline Bench Press", "4", "8"});
                                exercises.add(new String[]{"Barbell Row", "4", "8"});
                                exercises.add(new String[]{"Lateral Raise", "4", "12"});
                                exercises.add(new String[]{"Tricep Extension", "3", "12"});
                                exercises.add(new String[]{"Bicep Curl", "3", "12"});
                            }
                            case "Lower Body" -> {
                                exercises.add(new String[]{"Squat", "5", "5"});
                                exercises.add(new String[]{"Deadlift", "4", "5"});
                                exercises.add(new String[]{"Leg Press", "4", "10"});
                                exercises.add(new String[]{"Leg Curl", "3", "12"});
                                exercises.add(new String[]{"Walking Lunges", "3", "12"});
                                exercises.add(new String[]{"Standing Calf Raise", "5", "15"});
                            }
                        }
                    }
                    case "5x5" -> {
                        switch (label) {
                            case "Day 1 - Chest" -> {
                                exercises.add(new String[]{"Bench Press", "5", "5"});
                                exercises.add(new String[]{"Incline Dumbbell Press", "4", "8"});
                                exercises.add(new String[]{"Chest Fly", "4", "12"});
                                exercises.add(new String[]{"Dips", "3", "10"});
                            }
                            case "Day 2 - Back" -> {
                                exercises.add(new String[]{"Deadlift", "5", "5"});
                                exercises.add(new String[]{"Pull Ups", "4", "8"});
                                exercises.add(new String[]{"Barbell Row", "4", "8"});
                                exercises.add(new String[]{"Lat Pulldown", "4", "10"});
                            }
                            case "Day 3 - Shoulders" -> {
                                exercises.add(new String[]{"Overhead Press", "5", "5"});
                                exercises.add(new String[]{"Lateral Raise", "4", "12"});
                                exercises.add(new String[]{"Rear Delt Fly", "4", "12"});
                            }
                            case "Day 4 - Arms" -> {
                                exercises.add(new String[]{"Barbell Curl", "4", "10"});
                                exercises.add(new String[]{"Hammer Curl", "4", "12"});
                                exercises.add(new String[]{"Skull Crushers", "4", "10"});
                                exercises.add(new String[]{"Tricep Pushdown", "4", "12"});
                            }
                            case "Day 5 - Legs" -> {
                                exercises.add(new String[]{"Squat", "5", "5"});
                                exercises.add(new String[]{"Leg Press", "4", "10"});
                                exercises.add(new String[]{"Romanian Deadlift", "4", "8"});
                                exercises.add(new String[]{"Leg Curl", "4", "12"});
                                exercises.add(new String[]{"Calf Raise", "5", "15"});
                            }
                        }
                    }
                }
            }
        }
        return exercises;
    }
}
