import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ProfileSetupScreen {

    // Colors
    private final String BG_PRIMARY     = "#1A1D20";
    private final String BG_SECONDARY   = "#0D0F12";
    private final String ACCENT         = "#E63946";
    private final String TEXT_PRIMARY   = "#FFFFFF";
    private final String TEXT_SECONDARY = "#A8B2C1";

    // State
    private final String username;
    private int currentStep = 1;

    private String selectedGender    = "Male";
    private int    selectedAge       = 0;
    private String selectedFrequency = "No Experience";
    private String selectedGoal      = "Build Muscle & Strength";
    private String selectedLevel     = "";
    private String selectedSplit     = "";
    private String selectedLbsPerWeek = "";  // NEW: for Lose Fat path

    private BorderPane root;
    private Stage stage;

    public ProfileSetupScreen(String username) {
        this.username = username;
    }

    public Scene buildScene(Stage stage) {
        this.stage = stage;

        root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PRIMARY + ";");
        root.setCenter(wrapInStack(buildStep1()));

        return new Scene(root, 900, 620);
    }

    // ===== Shared Helpers =====
    private StackPane wrapInStack(VBox card) {
        StackPane wrapper = new StackPane(card);
        wrapper.setStyle("-fx-background-color: " + BG_PRIMARY + ";");
        return wrapper;
    }

    private VBox buildCardShell(String stepLabel, int totalSteps) {
        VBox card = new VBox(0);
        card.setMaxWidth(460);
        card.setPadding(new Insets(40, 36, 36, 36));
        card.setStyle(
                "-fx-background-color: " + BG_SECONDARY + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #2a2d30;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;"
        );

        Label brand = new Label("TaraGym");
        brand.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));
        brand.setTextFill(Color.web(ACCENT));

        Label stepLbl = new Label(stepLabel);
        stepLbl.setFont(Font.font("SansSerif", 12));
        stepLbl.setTextFill(Color.web(TEXT_SECONDARY));
        stepLbl.setPadding(new Insets(3, 0, 10, 0));

        // Progress bar
        HBox progressBar = new HBox(4);
        progressBar.setPadding(new Insets(0, 0, 24, 0));
        for (int i = 1; i <= totalSteps; i++) {
            Region seg = new Region();
            seg.setMinHeight(4);
            seg.setMaxHeight(4);
            HBox.setHgrow(seg, Priority.ALWAYS);
            seg.setStyle("-fx-background-color: " +
                    (i <= currentStep ? ACCENT : "#2a2d30") +
                    "; -fx-background-radius: 2;");
            progressBar.getChildren().add(seg);
        }

        card.getChildren().addAll(brand, stepLbl, progressBar);
        return card;
    }

    // ===== STEP 1 — Gender & Age =====
    private VBox buildStep1() {
        currentStep = 1;
        VBox card = buildCardShell("Step 1 of 4 — Profile", 4);

        Label genderLabel = new Label("Gender");
        styleFieldLabel(genderLabel);

        Button maleBtn   = choiceBtn("Male");
        Button femaleBtn = choiceBtn("Female");
        Button otherBtn  = choiceBtn("Other");

        setActive(maleBtn);
        setInactive(femaleBtn);
        setInactive(otherBtn);

        maleBtn.setOnAction(e -> {
            selectedGender = "Male";
            setActive(maleBtn); setInactive(femaleBtn); setInactive(otherBtn);
        });
        femaleBtn.setOnAction(e -> {
            selectedGender = "Female";
            setActive(femaleBtn); setInactive(maleBtn); setInactive(otherBtn);
        });
        otherBtn.setOnAction(e -> {
            selectedGender = "Other";
            setActive(otherBtn); setInactive(maleBtn); setInactive(femaleBtn);
        });

        HBox genderRow = new HBox(8, maleBtn, femaleBtn, otherBtn);
        maleBtn.setMaxWidth(Double.MAX_VALUE);
        femaleBtn.setMaxWidth(Double.MAX_VALUE);
        otherBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(maleBtn,   Priority.ALWAYS);
        HBox.setHgrow(femaleBtn, Priority.ALWAYS);
        HBox.setHgrow(otherBtn,  Priority.ALWAYS);

        Label ageLabel = new Label("Age");
        styleFieldLabel(ageLabel);

        TextField ageField = new TextField();
        styleField(ageField, "Enter your age");

        Label msgLabel = new Label("");
        msgLabel.setTextFill(Color.web(ACCENT));
        msgLabel.setFont(Font.font("SansSerif", 11));
        msgLabel.setWrapText(true);
        msgLabel.setPadding(new Insets(4, 0, 0, 0));

        HBox step1NavRow = new HBox(10);
        step1NavRow.setPadding(new Insets(22, 0, 0, 0));

        Button step1BackBtn = new Button("← Back");
        styleBackBtn(step1BackBtn);
        step1BackBtn.setOnAction(e -> Navigator.go(stage, "login"));

        Button nextBtn = actionBtn("Next →");
        nextBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nextBtn, Priority.ALWAYS);

        nextBtn.setOnAction(e -> {
            msgLabel.setText("");
            String ageText = ageField.getText().trim();

            if (ageText.isEmpty()) { msgLabel.setText("Please enter your age."); return; }

            int age;
            try { age = Integer.parseInt(ageText); }
            catch (NumberFormatException ex) {
                msgLabel.setText("Age must be a number."); return;
            }

            if (age < 8)   { msgLabel.setText("You must be at least 8 years old."); return; }
            if (age > 120) { msgLabel.setText("Please enter a valid age."); return; }

            selectedAge = age;
            UserSession.getInstance().setGender(selectedGender);
            UserSession.getInstance().setAge(selectedAge);
            root.setCenter(wrapInStack(buildStep2()));
        });

        step1NavRow.getChildren().addAll(step1BackBtn, nextBtn);

        card.getChildren().addAll(
                genderLabel, genderRow,
                ageLabel, ageField,
                msgLabel, step1NavRow
        );
        return card;
    }

    // ===== STEP 2 — Frequency =====
    private VBox buildStep2() {
        currentStep = 2;
        VBox card = buildCardShell("Step 2 of 4 — Workout Frequency", 4);

        Label question = new Label("How frequently do you work out?");
        question.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        question.setTextFill(Color.web(TEXT_PRIMARY));
        question.setPadding(new Insets(0, 0, 14, 0));
        question.setWrapText(true);

        String[] options = {
                "No Experience",
                "0–1 day per week",
                "2–3 days per week",
                "4–5+ days per week"
        };

        Button[] btns = new Button[4];
        for (int i = 0; i < 4; i++) {
            btns[i] = choiceBtn(options[i]);
            btns[i].setMaxWidth(Double.MAX_VALUE);
            btns[i].setWrapText(true);
        }

        setActive(btns[0]);
        for (int i = 1; i < 4; i++) setInactive(btns[i]);
        selectedFrequency = options[0];

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            btns[i].setOnAction(e -> {
                selectedFrequency = options[idx];
                for (Button b : btns) setInactive(b);
                setActive(btns[idx]);
            });
        }

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        grid.add(btns[0], 0, 0); grid.add(btns[1], 1, 0);
        grid.add(btns[2], 0, 1); grid.add(btns[3], 1, 1);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        HBox navRow = buildNavRow(
                e -> root.setCenter(wrapInStack(buildStep1())),
                e -> {
                    UserSession.getInstance().setFrequency(selectedFrequency);
                    root.setCenter(wrapInStack(buildStep3()));
                }
        );

        card.getChildren().addAll(question, grid, navRow);
        return card;
    }

    // ===== STEP 3 — Fitness Goal =====
    private VBox buildStep3() {
        currentStep = 3;
        VBox card = buildCardShell("Step 3 of 4 — Fitness Goal", 4);

        Label question = new Label("What is your fitness goal?");
        question.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        question.setTextFill(Color.web(TEXT_PRIMARY));
        question.setPadding(new Insets(0, 0, 14, 0));

        String[][] goals = {
                {"Lose Fat",                "⚖"},
                {"Build Muscle & Strength", "💪"}
        };

        Button[] btns = new Button[2];
        for (int i = 0; i < 2; i++) {
            btns[i] = buildGoalBtn(goals[i][0], goals[i][1]);
        }

        selectedGoal = goals[1][0];
        setGoalActive(btns[1]);
        setGoalInactive(btns[0]);

        for (int i = 0; i < 2; i++) {
            final int idx = i;
            btns[i].setOnAction(e -> {
                selectedGoal = goals[idx][0];
                for (int j = 0; j < 2; j++) {
                    if (j == idx) setGoalActive(btns[j]);
                    else          setGoalInactive(btns[j]);
                }
            });
        }

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        grid.add(btns[0], 0, 0);
        grid.add(btns[1], 1, 0);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        HBox navRow = buildNavRow(
                e -> root.setCenter(wrapInStack(buildStep2())),
                e -> {
                    UserSession.getInstance().setGoal(selectedGoal);
                    // Branch: Lose Fat → lbs/week selector; Build Muscle → level selector
                    if (selectedGoal.equals("Lose Fat")) {
                        root.setCenter(wrapInStack(buildLoseFatStep()));
                    } else {
                        root.setCenter(wrapInStack(buildStep4()));
                    }
                }
        );

        card.getChildren().addAll(question, grid, navRow);
        return card;
    }

    // ===== LOSE FAT PATH — Step 4A: lbs per week =====
    private VBox buildLoseFatStep() {
        currentStep = 4;
        VBox card = buildCardShell("Step 4 of 4 — Weight Loss Goal", 4);

        Label question = new Label("How much fat do you want to lose per week?");
        question.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        question.setTextFill(Color.web(TEXT_PRIMARY));
        question.setPadding(new Insets(0, 0, 6, 0));
        question.setWrapText(true);

        Label hint = new Label("Tip: 0.5–1.5 lbs/week is the healthy, sustainable range recommended by fitness experts.");
        hint.setFont(Font.font("SansSerif", 11));
        hint.setTextFill(Color.web(TEXT_SECONDARY));
        hint.setWrapText(true);
        hint.setPadding(new Insets(0, 0, 14, 0));

        // Three options with descriptions
        String[] lbsOptions  = { "0.5 lbs/week", "1.0 lbs/week", "1.5 lbs/week" };
        String[] lbsDescs    = {
                "Light & Steady — Minimal calorie cut, lower workout intensity. Ideal for beginners or those with low body fat.",
                "Moderate — Balanced cardio + diet. The most popular and sustainable approach for most people.",
                "Aggressive — High-intensity cardio & strict diet. Best for those comfortable with intense training."
        };

        Button[] lbsBtns = new Button[3];
        for (int i = 0; i < 3; i++) {
            lbsBtns[i] = buildSplitBtn(lbsOptions[i], lbsDescs[i]);
            lbsBtns[i].setMaxWidth(Double.MAX_VALUE);
            lbsBtns[i].setMinHeight(72);
            lbsBtns[i].setMaxHeight(72);
            setInactive(lbsBtns[i]);
        }

        for (int i = 0; i < 3; i++) {
            final int idx = i;
            lbsBtns[i].setOnAction(e -> {
                selectedLbsPerWeek = lbsOptions[idx];
                for (Button b : lbsBtns) setInactive(b);
                setActive(lbsBtns[idx]);
            });
        }

        VBox optionList = new VBox(8);
        optionList.getChildren().addAll(lbsBtns);

        // Nav row
        HBox navRow = new HBox(10);
        navRow.setPadding(new Insets(22, 0, 0, 0));

        Button backBtn = new Button("← Back");
        styleBackBtn(backBtn);
        backBtn.setOnAction(e -> root.setCenter(wrapInStack(buildStep3())));

        Button finishBtn = actionBtn("Finish & Start →");
        finishBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(finishBtn, Priority.ALWAYS);

        finishBtn.setOnAction(e -> {
            if (selectedLbsPerWeek.isEmpty()) {
                showAlert("Please select a weight loss goal!");
                return;
            }

            // For Lose Fat: level = lbsPerWeek, split = "Lose Fat"
            String level = selectedLbsPerWeek;
            String split = "Lose Fat";

            UserSession.getInstance().setLevel(level);
            UserSession.getInstance().setWorkoutSplit(split);
            UserSession.getInstance().setLbsPerWeek(selectedLbsPerWeek);
            UserSession.getInstance().setUsername(username);

            DatabaseManager.saveUserProfile(
                    UserSession.getInstance().getGender(),
                    UserSession.getInstance().getAge(),
                    UserSession.getInstance().getFrequency(),
                    UserSession.getInstance().getGoal(),
                    level,
                    split
            );

            showSuccess("Profile saved! Welcome to TaraGym, " + username + "!");
        });

        navRow.getChildren().addAll(backBtn, finishBtn);
        card.getChildren().addAll(question, hint, optionList, navRow);
        return card;
    }

    // ===== BUILD MUSCLE PATH — Step 4: Level Selection =====
    private VBox buildStep4() {
        currentStep = 4;
        VBox card = buildCardShell("Step 4 of 4 — Workout Split", 4);

        Label question = new Label("Choose your experience level:");
        question.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        question.setTextFill(Color.web(TEXT_PRIMARY));
        question.setPadding(new Insets(0, 0, 14, 0));

        Button beginnerBtn     = choiceBtn("🌱  Beginner");
        Button intermediateBtn = choiceBtn("💪  Intermediate");
        Button advanceBtn      = choiceBtn("🔥  Advance");

        beginnerBtn.setMaxWidth(Double.MAX_VALUE);
        intermediateBtn.setMaxWidth(Double.MAX_VALUE);
        advanceBtn.setMaxWidth(Double.MAX_VALUE);

        setInactive(beginnerBtn);
        setInactive(intermediateBtn);
        setInactive(advanceBtn);

        beginnerBtn.setOnAction(e -> {
            selectedLevel = "Beginner";
            UserSession.getInstance().setLevel("Beginner");
            setActive(beginnerBtn);
            setInactive(intermediateBtn);
            setInactive(advanceBtn);
            root.setCenter(wrapInStack(buildSplitStep(selectedLevel)));
        });

        intermediateBtn.setOnAction(e -> {
            selectedLevel = "Intermediate";
            UserSession.getInstance().setLevel("Intermediate");
            setActive(intermediateBtn);
            setInactive(beginnerBtn);
            setInactive(advanceBtn);
            root.setCenter(wrapInStack(buildSplitStep(selectedLevel)));
        });

        advanceBtn.setOnAction(e -> {
            selectedLevel = "Advance";
            UserSession.getInstance().setLevel("Advance");
            setActive(advanceBtn);
            setInactive(beginnerBtn);
            setInactive(intermediateBtn);
            root.setCenter(wrapInStack(buildSplitStep(selectedLevel)));
        });

        VBox levelButtons = new VBox(8,
                beginnerBtn, intermediateBtn, advanceBtn);

        Button backBtn = new Button("← Back");
        styleBackBtn(backBtn);
        backBtn.setOnAction(e -> root.setCenter(wrapInStack(buildStep3())));
        backBtn.setPadding(new Insets(22, 0, 0, 0));

        card.getChildren().addAll(question, levelButtons, backBtn);
        return card;
    }

    // ===== BUILD MUSCLE PATH — Split Selection =====
    private VBox buildSplitStep(String level) {
        VBox card = buildCardShell("Choose your workout split — " + level, 4);

        Label question = new Label("Select a split for " + level + ":");
        question.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        question.setTextFill(Color.web(TEXT_PRIMARY));
        question.setPadding(new Insets(0, 0, 14, 0));

        String[] splits = switch (level) {
            case "Beginner"     -> new String[]{"Full Body", "PPL"};
            case "Intermediate" -> new String[]{"Upper Lower", "Powerbuilding", "PPL"};
            case "Advance"      -> new String[]{"Upper Lower", "PPL", "5x5"};
            default             -> new String[]{};
        };

        String[] splitDescs = switch (level) {
            case "Beginner"     -> new String[]{
                    "Train all muscles each session. 3 days/week.",
                    "Push, Pull, Legs. Classic 3-day program."
            };
            case "Intermediate" -> new String[]{
                    "Upper and lower body split. 4 days/week.",
                    "Push, Pull, Legs. 3 day program.",
                    "Chest/Back, Shoulders/Arms, Legs. 3 days/week."
            };
            case "Advance"      -> new String[]{
                    "Upper and lower body split. 6 day program.",
                    "Push, Pull, Legs. 6 day program.",
                    "5 sets of 5 reps on compound lifts. 5 days/week."
            };
            default -> new String[]{};
        };

        Button[] splitBtns = new Button[splits.length];
        for (int i = 0; i < splits.length; i++) {
            splitBtns[i] = buildSplitBtn(splits[i], splitDescs[i]);
            splitBtns[i].setMaxWidth(Double.MAX_VALUE);
            splitBtns[i].setMinHeight(64);
            splitBtns[i].setMaxHeight(64);
            setInactive(splitBtns[i]);
        }

        for (int i = 0; i < splits.length; i++) {
            final int idx = i;
            splitBtns[i].setOnAction(e -> {
                selectedSplit = splits[idx];
                for (Button b : splitBtns) setInactive(b);
                setActive(splitBtns[idx]);
            });
        }

        VBox splitList = new VBox(8);
        splitList.getChildren().addAll(splitBtns);

        // Nav row
        HBox navRow = new HBox(10);
        navRow.setPadding(new Insets(22, 0, 0, 0));

        Button backBtn = new Button("← Back");
        styleBackBtn(backBtn);
        backBtn.setOnAction(e -> root.setCenter(wrapInStack(buildStep4())));

        Button finishBtn = actionBtn("Finish & Start →");
        finishBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(finishBtn, Priority.ALWAYS);

        finishBtn.setOnAction(e -> {
            if (selectedSplit.isEmpty()) {
                showAlert("Please select a workout split!");
                return;
            }

            // Save to UserSession
            UserSession.getInstance().setLevel(level);
            UserSession.getInstance().setWorkoutSplit(selectedSplit);
            UserSession.getInstance().setUsername(username);

            // Save full profile to DB
            DatabaseManager.saveUserProfile(
                    UserSession.getInstance().getGender(),
                    UserSession.getInstance().getAge(),
                    UserSession.getInstance().getFrequency(),
                    UserSession.getInstance().getGoal(),
                    level,
                    selectedSplit
            );

            // Show success then navigate to home
            showSuccess("Profile saved! Welcome to TaraGym, " + username + "!");
        });

        navRow.getChildren().addAll(backBtn, finishBtn);
        card.getChildren().addAll(question, splitList, navRow);
        return card;
    }

    // ===== Show success then navigate =====
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("TaraGym");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        Navigator.go(stage, "home");
    }

    // ===== Style Helpers =====
    private Button buildGoalBtn(String text, String icon) {
        VBox content = new VBox(6);
        content.setAlignment(Pos.CENTER);
        content.setMouseTransparent(true);

        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font(22));
        iconLbl.setMouseTransparent(true);

        Label textLbl = new Label(text);
        textLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 11));
        textLbl.setTextFill(Color.web(TEXT_SECONDARY));
        textLbl.setWrapText(true);
        textLbl.setAlignment(Pos.CENTER);
        textLbl.setMouseTransparent(true);

        content.getChildren().addAll(iconLbl, textLbl);

        Button btn = new Button();
        btn.setGraphic(content);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setMaxHeight(80);
        btn.setMinHeight(80);
        btn.setStyle(
                "-fx-background-color: #1e2428;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #2a2d30;" +
                        "-fx-border-radius: 6;" +
                        "-fx-border-width: 1;" +
                        "-fx-cursor: hand;"
        );
        return btn;
    }

    private Button buildSplitBtn(String title, String desc) {
        VBox content = new VBox(4);
        content.setMouseTransparent(true);

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
        titleLbl.setTextFill(Color.WHITE);
        titleLbl.setMouseTransparent(true);

        Label descLbl = new Label(desc);
        descLbl.setFont(Font.font("SansSerif", 11));
        descLbl.setTextFill(Color.web(TEXT_SECONDARY));
        descLbl.setWrapText(true);
        descLbl.setMouseTransparent(true);

        content.getChildren().addAll(titleLbl, descLbl);

        Button btn = new Button();
        btn.setGraphic(content);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(10, 14, 10, 14));
        return btn;
    }

    private HBox buildNavRow(
            javafx.event.EventHandler<javafx.event.ActionEvent> backAction,
            javafx.event.EventHandler<javafx.event.ActionEvent> nextAction) {

        HBox row = new HBox(10);
        row.setPadding(new Insets(22, 0, 0, 0));

        Button backBtn = new Button("← Back");
        styleBackBtn(backBtn);
        backBtn.setOnAction(backAction);

        Button nextBtn = actionBtn("Next →");
        nextBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nextBtn, Priority.ALWAYS);
        nextBtn.setOnAction(nextAction);

        row.getChildren().addAll(backBtn, nextBtn);
        return row;
    }

    private void styleBackBtn(Button btn) {
        btn.setStyle(
                "-fx-background-color: #2a2d30;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 10 20;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        );
    }

    private Button choiceBtn(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("SansSerif", 13));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setWrapText(true);
        return btn;
    }

    private void setActive(Button btn) {
        btn.setStyle(
                "-fx-background-color: " + ACCENT + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 9 0;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: " + ACCENT + ";" +
                        "-fx-border-radius: 6;" +
                        "-fx-border-width: 1;" +
                        "-fx-cursor: hand;"
        );
    }

    private void setInactive(Button btn) {
        btn.setStyle(
                "-fx-background-color: #1e2428;" +
                        "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                        "-fx-padding: 9 0;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #2a2d30;" +
                        "-fx-border-radius: 6;" +
                        "-fx-border-width: 1;" +
                        "-fx-cursor: hand;"
        );
    }

    private void setGoalActive(Button btn) {
        btn.setStyle(
                "-fx-background-color: " + ACCENT + ";" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: " + ACCENT + ";" +
                        "-fx-border-radius: 6;" +
                        "-fx-border-width: 1;" +
                        "-fx-cursor: hand;"
        );
        if (btn.getGraphic() instanceof VBox vbox) {
            vbox.getChildren().forEach(node -> {
                if (node instanceof Label lbl) lbl.setTextFill(Color.WHITE);
            });
        }
    }

    private void setGoalInactive(Button btn) {
        btn.setStyle(
                "-fx-background-color: #1e2428;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #2a2d30;" +
                        "-fx-border-radius: 6;" +
                        "-fx-border-width: 1;" +
                        "-fx-cursor: hand;"
        );
        if (btn.getGraphic() instanceof VBox vbox) {
            vbox.getChildren().forEach(node -> {
                if (node instanceof Label lbl)
                    lbl.setTextFill(Color.web(TEXT_SECONDARY));
            });
        }
    }

    private void styleFieldLabel(Label lbl) {
        lbl.setFont(Font.font("SansSerif", 12));
        lbl.setTextFill(Color.web(TEXT_SECONDARY));
        lbl.setPadding(new Insets(0, 0, 6, 0));
    }

    private void styleField(TextField field, String prompt) {
        field.setPromptText(prompt);
        field.setStyle(
                "-fx-background-color: #1e2428;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #555f6d;" +
                        "-fx-padding: 10 12;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #2a2d30;" +
                        "-fx-border-radius: 6;" +
                        "-fx-border-width: 1;"
        );
    }

    private Button actionBtn(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
                "-fx-background-color: " + ACCENT + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 10 0;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        );
        return btn;
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("TaraGym");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
