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

public class SettingsScreen {

    // Colors
    private final String BG_PRIMARY     = "#1A1D20";
    private final String BG_SECONDARY   = "#0D0F12";
    private final String ACCENT         = "#E63946";
    private final String TEXT_PRIMARY   = "#FFFFFF";
    private final String TEXT_SECONDARY = "#A8B2C1";
    private final String CARD_BG        = "#1e2428";

    // Content stack for swapping views
    private StackPane contentStack;
    private Stage primaryStage;

    // Temp selections while changing split
    private String tempLevel       = "";
    private String tempSplit       = "";
    private String tempLbsPerWeek  = "";

    public Scene buildScene(Stage stage) {
        this.primaryStage = stage;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PRIMARY + ";");

        VBox topSection = new VBox(0);
        topSection.getChildren().addAll(buildTopBar(), buildNavBar(stage));
        root.setTop(topSection);

        contentStack = new StackPane();
        contentStack.getChildren().add(buildMainView(stage));
        root.setCenter(contentStack);

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

        Label screen = new Label("Settings");
        screen.setFont(Font.font("SansSerif", FontWeight.NORMAL, 14));
        screen.setTextFill(Color.web(TEXT_PRIMARY));

        bar.getChildren().addAll(title, sep, screen);
        return bar;
    }

    // ===== Nav Bar =====
    private HBox buildNavBar(Stage stage) {
        HBox nav = new HBox(0);
        nav.setAlignment(Pos.CENTER);
        nav.setStyle("-fx-background-color: " + BG_SECONDARY + ";");
        nav.setPadding(new Insets(8, 20, 8, 20));

        String[] labels  = {"🏠 Home", "📋 History", "💪 Workout",
                "📅 Schedule", "📈 Progress", "⚙ Settings"};
        String[] screens = {"home", "history", "workout",
                "schedule", "progress", "settings"};

        for (int i = 0; i < labels.length; i++) {
            nav.getChildren().add(navButton(labels[i], screens[i], stage));
        }
        return nav;
    }

    private Button navButton(String text, String screen, Stage stage) {
        Button btn = new Button(text);
        boolean isActive = screen.equals("settings");

        String activeStyle  = "-fx-background-color: " + ACCENT + "; -fx-text-fill: white;"
                + "-fx-font-size: 12px; -fx-padding: 8 16;"
                + "-fx-background-radius: 6; -fx-cursor: hand;";
        String normalStyle  = "-fx-background-color: transparent; -fx-text-fill: " + TEXT_SECONDARY + ";"
                + "-fx-font-size: 12px; -fx-padding: 8 16;"
                + "-fx-background-radius: 6; -fx-cursor: hand;";
        String hoverStyle   = "-fx-background-color: #2a2d30; -fx-text-fill: white;"
                + "-fx-font-size: 12px; -fx-padding: 8 16;"
                + "-fx-background-radius: 6; -fx-cursor: hand;";

        btn.setStyle(isActive ? activeStyle : normalStyle);

        if (!isActive) {
            btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
            btn.setOnMouseExited(e  -> btn.setStyle(normalStyle));
        }

        btn.setOnAction(e -> Navigator.go(stage, screen));
        return btn;
    }

    // ===== Main Settings View =====
    private ScrollPane buildMainView(Stage stage) {
        VBox main = new VBox(20);
        main.setPadding(new Insets(24));

        main.getChildren().addAll(
                buildSectionTitle("Account"),
                buildLoginSection(stage),
                buildDivider(),
                buildSectionTitle("Change Workout Split"),
                buildChangeGoalBtn(stage),
                buildDivider(),
                buildSectionTitle("Current Split"),
                buildCurrentSplitCard(),
                buildDivider(),
                buildSectionTitle("Terms and Services"),
                buildTermsCard(),
                buildDivider(),
                buildSectionTitle("Contact"),
                buildContactCard()
        );

        ScrollPane scroll = new ScrollPane(main);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background: " + BG_PRIMARY + ";" +
                        "-fx-background-color: " + BG_PRIMARY + ";"
        );
        return scroll;
    }

    private Label buildSectionTitle(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));
        lbl.setTextFill(Color.web(TEXT_PRIMARY));
        return lbl;
    }

    private Region buildDivider() {
        Region div = new Region();
        div.setPrefHeight(1);
        div.setMaxWidth(Double.MAX_VALUE);
        div.setStyle("-fx-background-color: #2a2d30;");
        return div;
    }

    // ===== Change Goal Button =====
    private HBox buildChangeGoalBtn(Stage stage) {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));
        card.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label titleLbl = new Label("Update Fitness Goal & Split");
        titleLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        titleLbl.setTextFill(Color.web(TEXT_PRIMARY));

        Label descLbl = new Label("Change your goal, level, or workout split anytime.");
        descLbl.setFont(Font.font("SansSerif", 12));
        descLbl.setTextFill(Color.web(TEXT_SECONDARY));

        info.getChildren().addAll(titleLbl, descLbl);

        Label arrow = new Label("→");
        arrow.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));
        arrow.setTextFill(Color.web(ACCENT));

        card.getChildren().addAll(info, arrow);

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #2a2d30;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        ));

        card.setOnMouseClicked(e -> {
            tempLevel      = "";
            tempSplit      = "";
            tempLbsPerWeek = "";
            contentStack.getChildren().clear();
            contentStack.getChildren().add(buildGoalView(stage));
        });

        return card;
    }

    // ===== STEP 1 — Goal Selection (matches ProfileSetupScreen Step 3) =====
    private ScrollPane buildGoalView(Stage stage) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(24));
        view.setMaxWidth(500);
        view.setStyle("-fx-background-color: " + BG_PRIMARY + ";");

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = buildBackBtn();
        backBtn.setOnAction(e -> {
            contentStack.getChildren().clear();
            contentStack.getChildren().add(buildMainView(stage));
        });

        Label titleLbl = new Label("What is your fitness goal?");
        titleLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
        titleLbl.setTextFill(Color.web(TEXT_PRIMARY));

        header.getChildren().addAll(backBtn, titleLbl);

        // Goal buttons — same as ProfileSetupScreen
        String[][] goals = {
                {"Lose Fat",                "⚖"},
                {"Build Muscle & Strength", "💪"}
        };

        Button[] goalBtns = new Button[2];
        for (int i = 0; i < 2; i++) {
            goalBtns[i] = buildGoalBtn(goals[i][0], goals[i][1]);
            goalBtns[i].setMaxWidth(Double.MAX_VALUE);
        }

        // Pre-select current goal
        String currentGoal = UserSession.getInstance().getGoal();
        if (currentGoal.equals("Lose Fat")) {
            setGoalActive(goalBtns[0]);
            setGoalInactive(goalBtns[1]);
        } else {
            setGoalActive(goalBtns[1]);
            setGoalInactive(goalBtns[0]);
        }

        final String[] selectedGoal = {currentGoal.isEmpty()
                ? "Build Muscle & Strength" : currentGoal};

        for (int i = 0; i < 2; i++) {
            final int idx = i;
            goalBtns[i].setOnAction(e -> {
                selectedGoal[0] = goals[idx][0];
                for (int j = 0; j < 2; j++) {
                    if (j == idx) setGoalActive(goalBtns[j]);
                    else          setGoalInactive(goalBtns[j]);
                }
            });
        }

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(goalBtns[0], 0, 0);
        grid.add(goalBtns[1], 1, 0);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        // Next button
        Button nextBtn = actionBtn("Next →");
        nextBtn.setOnAction(e -> {
            UserSession.getInstance().setGoal(selectedGoal[0]);
            contentStack.getChildren().clear();
            if (selectedGoal[0].equals("Lose Fat")) {
                contentStack.getChildren().add(buildLoseFatView(stage));
            } else {
                contentStack.getChildren().add(buildLevelView(stage));
            }
        });

        view.getChildren().addAll(header, grid, nextBtn);

        StackPane wrapper = new StackPane(view);
        wrapper.setStyle("-fx-background-color: " + BG_PRIMARY + ";");
        wrapper.setAlignment(Pos.TOP_CENTER);

        ScrollPane scroll = new ScrollPane(wrapper);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background: " + BG_PRIMARY + ";" +
                        "-fx-background-color: " + BG_PRIMARY + ";"
        );
        return scroll;
    }

    // ===== LOSE FAT PATH — lbs per week (matches ProfileSetupScreen buildLoseFatStep) =====
    private ScrollPane buildLoseFatView(Stage stage) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(24));
        view.setMaxWidth(500);

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = buildBackBtn();
        backBtn.setOnAction(e -> {
            contentStack.getChildren().clear();
            contentStack.getChildren().add(buildGoalView(stage));
        });

        Label titleLbl = new Label("How much fat do you want to lose per week?");
        titleLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));
        titleLbl.setTextFill(Color.web(TEXT_PRIMARY));
        titleLbl.setWrapText(true);

        header.getChildren().addAll(backBtn, titleLbl);

        Label hint = new Label("Tip: 0.5–1.5 lbs/week is the healthy, sustainable range.");
        hint.setFont(Font.font("SansSerif", 11));
        hint.setTextFill(Color.web(TEXT_SECONDARY));
        hint.setWrapText(true);

        String[] lbsOptions = {"0.5 lbs/week", "1.0 lbs/week", "1.5 lbs/week"};
        String[] lbsDescs   = {
                "Light & Steady — Minimal calorie cut, lower workout intensity.",
                "Moderate — Balanced cardio + diet. Most popular and sustainable approach.",
                "Aggressive — High-intensity cardio & strict diet. Best for intense trainers."
        };

        Button[] lbsBtns = new Button[3];
        for (int i = 0; i < 3; i++) {
            lbsBtns[i] = buildSplitBtn(lbsOptions[i], lbsDescs[i]);
            lbsBtns[i].setMaxWidth(Double.MAX_VALUE);
            lbsBtns[i].setMinHeight(72);
            lbsBtns[i].setMaxHeight(72);
            setInactive(lbsBtns[i]);
        }

        // Pre-select if previously set
        String currentLbs = UserSession.getInstance().getLbsPerWeek();
        for (int i = 0; i < lbsOptions.length; i++) {
            if (lbsOptions[i].equals(currentLbs)) {
                setActive(lbsBtns[i]);
                tempLbsPerWeek = currentLbs;
            }
        }

        for (int i = 0; i < 3; i++) {
            final int idx = i;
            lbsBtns[i].setOnAction(e -> {
                tempLbsPerWeek = lbsOptions[idx];
                for (Button b : lbsBtns) setInactive(b);
                setActive(lbsBtns[idx]);
            });
        }

        VBox optionList = new VBox(8);
        optionList.getChildren().addAll(lbsBtns);

        // Save button
        Button saveBtn = actionBtn("Save Changes ✓");
        saveBtn.setOnAction(e -> {
            if (tempLbsPerWeek.isEmpty()) {
                showAlert("Please select a weight loss goal!");
                return;
            }

            String level = tempLbsPerWeek;
            String split = "Lose Weight";

            UserSession.getInstance().setGoal("Lose Fat");
            UserSession.getInstance().setLevel(level);
            UserSession.getInstance().setWorkoutSplit(split);
            UserSession.getInstance().setLbsPerWeek(tempLbsPerWeek);

            DatabaseManager.updateUserSplit("Lose Fat", level, split);

            showSuccess("Goal updated to: Lose Fat — " + tempLbsPerWeek, stage);
        });

        view.getChildren().addAll(header, hint, optionList, saveBtn);

        StackPane wrapper = new StackPane(view);
        wrapper.setStyle("-fx-background-color: " + BG_PRIMARY + ";");
        wrapper.setAlignment(Pos.TOP_CENTER);

        ScrollPane scroll = new ScrollPane(wrapper);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background: " + BG_PRIMARY + ";" +
                        "-fx-background-color: " + BG_PRIMARY + ";"
        );
        return scroll;
    }

    // ===== BUILD MUSCLE PATH — Level Selection (matches ProfileSetupScreen buildStep4) =====
    private ScrollPane buildLevelView(Stage stage) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(24));
        view.setMaxWidth(500);

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = buildBackBtn();
        backBtn.setOnAction(e -> {
            contentStack.getChildren().clear();
            contentStack.getChildren().add(buildGoalView(stage));
        });

        Label titleLbl = new Label("Choose your experience level:");
        titleLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
        titleLbl.setTextFill(Color.web(TEXT_PRIMARY));

        header.getChildren().addAll(backBtn, titleLbl);

        Button beginnerBtn     = choiceBtn("🌱  Beginner");
        Button intermediateBtn = choiceBtn("💪  Intermediate");
        Button advanceBtn      = choiceBtn("🔥  Advance");

        beginnerBtn.setMaxWidth(Double.MAX_VALUE);
        intermediateBtn.setMaxWidth(Double.MAX_VALUE);
        advanceBtn.setMaxWidth(Double.MAX_VALUE);

        // Pre-select current level
        String currentLevel = UserSession.getInstance().getLevel();
        switch (currentLevel) {
            case "Beginner"     -> { setActive(beginnerBtn);     setInactive(intermediateBtn); setInactive(advanceBtn); }
            case "Intermediate" -> { setActive(intermediateBtn); setInactive(beginnerBtn);     setInactive(advanceBtn); }
            case "Advance"      -> { setActive(advanceBtn);      setInactive(beginnerBtn);     setInactive(intermediateBtn); }
            default             -> { setInactive(beginnerBtn);   setInactive(intermediateBtn); setInactive(advanceBtn); }
        }

        beginnerBtn.setOnAction(e -> {
            tempLevel = "Beginner";
            setActive(beginnerBtn);
            setInactive(intermediateBtn);
            setInactive(advanceBtn);
            contentStack.getChildren().clear();
            contentStack.getChildren().add(buildSplitView(tempLevel, stage));
        });

        intermediateBtn.setOnAction(e -> {
            tempLevel = "Intermediate";
            setActive(intermediateBtn);
            setInactive(beginnerBtn);
            setInactive(advanceBtn);
            contentStack.getChildren().clear();
            contentStack.getChildren().add(buildSplitView(tempLevel, stage));
        });

        advanceBtn.setOnAction(e -> {
            tempLevel = "Advance";
            setActive(advanceBtn);
            setInactive(beginnerBtn);
            setInactive(intermediateBtn);
            contentStack.getChildren().clear();
            contentStack.getChildren().add(buildSplitView(tempLevel, stage));
        });

        VBox levelButtons = new VBox(8, beginnerBtn, intermediateBtn, advanceBtn);

        view.getChildren().addAll(header, levelButtons);

        StackPane wrapper = new StackPane(view);
        wrapper.setStyle("-fx-background-color: " + BG_PRIMARY + ";");
        wrapper.setAlignment(Pos.TOP_CENTER);

        ScrollPane scroll = new ScrollPane(wrapper);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background: " + BG_PRIMARY + ";" +
                        "-fx-background-color: " + BG_PRIMARY + ";"
        );
        return scroll;
    }

    // ===== BUILD MUSCLE PATH — Split Selection (matches ProfileSetupScreen buildSplitStep) =====
    private ScrollPane buildSplitView(String level, Stage stage) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(24));
        view.setMaxWidth(500);

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = buildBackBtn();
        backBtn.setOnAction(e -> {
            contentStack.getChildren().clear();
            contentStack.getChildren().add(buildLevelView(stage));
        });

        Label titleLbl = new Label("Select a split — " + level);
        titleLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
        titleLbl.setTextFill(Color.web(TEXT_PRIMARY));

        header.getChildren().addAll(backBtn, titleLbl);

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

        // Pre-select current split if same level
        if (level.equals(UserSession.getInstance().getLevel())) {
            String currentSplit = UserSession.getInstance().getWorkoutSplit();
            for (int i = 0; i < splits.length; i++) {
                if (splits[i].equals(currentSplit)) {
                    setActive(splitBtns[i]);
                    tempSplit = currentSplit;
                }
            }
        }

        for (int i = 0; i < splits.length; i++) {
            final int idx = i;
            splitBtns[i].setOnAction(e -> {
                tempSplit = splits[idx];
                for (Button b : splitBtns) setInactive(b);
                setActive(splitBtns[idx]);
            });
        }

        VBox splitList = new VBox(8);
        splitList.getChildren().addAll(splitBtns);

        // Save button
        Button saveBtn = actionBtn("Save Changes ✓");
        saveBtn.setOnAction(e -> {
            if (tempSplit.isEmpty()) {
                showAlert("Please select a workout split!");
                return;
            }

            UserSession.getInstance().setGoal("Build Muscle & Strength");
            UserSession.getInstance().setLevel(level);
            UserSession.getInstance().setWorkoutSplit(tempSplit);

            DatabaseManager.updateUserSplit("Build Muscle & Strength", level, tempSplit);

            showSuccess("Split updated to: " + level + " — " + tempSplit, stage);
        });

        view.getChildren().addAll(header, splitList, saveBtn);

        StackPane wrapper = new StackPane(view);
        wrapper.setStyle("-fx-background-color: " + BG_PRIMARY + ";");
        wrapper.setAlignment(Pos.TOP_CENTER);

        ScrollPane scroll = new ScrollPane(wrapper);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background: " + BG_PRIMARY + ";" +
                        "-fx-background-color: " + BG_PRIMARY + ";"
        );
        return scroll;
    }

    // ===== Login Section =====
    private VBox buildLoginSection(Stage stage) {
        VBox section = new VBox(12);
        section.setPadding(new Insets(16));
        section.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                        "-fx-background-radius: 10;"
        );

        HBox userRow = new HBox(12);
        userRow.setAlignment(Pos.CENTER_LEFT);

        Label userLbl = new Label("Username:");
        userLbl.setFont(Font.font("SansSerif", 13));
        userLbl.setTextFill(Color.web(TEXT_SECONDARY));
        userLbl.setPrefWidth(100);

        TextField userField = new TextField();
        userField.setStyle(fieldStyle());
        HBox.setHgrow(userField, Priority.ALWAYS);

        if (UserSession.getInstance().isLoggedIn()) {
            userField.setText(UserSession.getInstance().getUsername());
            userField.setEditable(false);
        }

        userRow.getChildren().addAll(userLbl, userField);

        HBox passRow = new HBox(12);
        passRow.setAlignment(Pos.CENTER_LEFT);

        Label passLbl = new Label("Password:");
        passLbl.setFont(Font.font("SansSerif", 13));
        passLbl.setTextFill(Color.web(TEXT_SECONDARY));
        passLbl.setPrefWidth(100);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter password");
        passField.setStyle(fieldStyle());
        HBox.setHgrow(passField, Priority.ALWAYS);

        passRow.getChildren().addAll(passLbl, passField);

        Button loginBtn = new Button(
                UserSession.getInstance().isLoggedIn() ? "Logout" : "Login");
        loginBtn.setStyle(
                "-fx-background-color: " + ACCENT + ";" +
                        "-fx-text-fill: white; -fx-font-weight: bold;" +
                        "-fx-padding: 10 24; -fx-background-radius: 6; -fx-cursor: hand;"
        );

        Label statusLbl = new Label("");
        statusLbl.setFont(Font.font("SansSerif", 11));
        statusLbl.setWrapText(true);

        loginBtn.setOnAction(e -> {
            if (loginBtn.getText().equals("Logout")) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Logout");
                confirm.setHeaderText(null);
                confirm.setContentText("Are you sure you want to logout?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        UserSession.getInstance().clear();
                        userField.setText("");
                        userField.setEditable(true);
                        passField.clear();
                        loginBtn.setText("Login");
                        statusLbl.setText("Logged out successfully.");
                        statusLbl.setTextFill(Color.web(TEXT_SECONDARY));
                        contentStack.getChildren().clear();
                        contentStack.getChildren().add(buildMainView(stage));
                    }
                });
                return;
            }

            String username = userField.getText().trim();
            String password = passField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                statusLbl.setText("Please fill in all fields!");
                statusLbl.setTextFill(Color.web(ACCENT));
                return;
            }

            boolean success = DatabaseManager.loginUser(username, password);

            if (success) {
                DatabaseManager.loadUserProfile(username);
                UserSession.getInstance().setUsername(username);
                userField.setEditable(false);
                passField.clear();
                loginBtn.setText("Logout");
                statusLbl.setText("Welcome back, " + username + "!");
                statusLbl.setTextFill(Color.web("#4caf50"));
                contentStack.getChildren().clear();
                contentStack.getChildren().add(buildMainView(stage));
            } else {
                statusLbl.setText("Incorrect username or password!");
                statusLbl.setTextFill(Color.web(ACCENT));
                passField.clear();
            }
        });

        section.getChildren().addAll(userRow, passRow, loginBtn, statusLbl);
        return section;
    }

    // ===== Current Split Card =====
    private HBox buildCurrentSplitCard() {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 10;");

        VBox info = new VBox(4);

        String goal  = UserSession.getInstance().getGoal();
        String level = UserSession.getInstance().getLevel();
        String split = UserSession.getInstance().getWorkoutSplit();

        Label goalLbl = new Label("Goal: " + (goal.isEmpty()  ? "Not set" : goal));
        goalLbl.setFont(Font.font("SansSerif", 12));
        goalLbl.setTextFill(Color.web(TEXT_SECONDARY));

        Label levelLbl = new Label("Level: " + (level.isEmpty() ? "Not set" : level));
        levelLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        levelLbl.setTextFill(Color.web(TEXT_PRIMARY));

        Label splitLbl = new Label("Split: " + (split.isEmpty() ? "Not set" : split));
        splitLbl.setFont(Font.font("SansSerif", 13));
        splitLbl.setTextFill(Color.web(TEXT_SECONDARY));

        info.getChildren().addAll(goalLbl, levelLbl, splitLbl);

        Label badge = new Label(level.isEmpty() ? "Not set" : "Active");
        badge.setFont(Font.font("SansSerif", FontWeight.BOLD, 11));
        badge.setTextFill(Color.WHITE);
        badge.setPadding(new Insets(4, 12, 4, 12));
        badge.setStyle("-fx-background-color: " +
                (level.isEmpty() ? "#555555" : ACCENT) + "; -fx-background-radius: 20;");

        card.getChildren().addAll(info, badge);
        return card;
    }

    // ===== Terms Card =====
    private VBox buildTermsCard() {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 10;");

        String terms = """
            By using TaraGym, you agree to the following:

            • This app is for fitness tracking purposes only.
            • All workout data is stored locally on your device.
            • We are not responsible for any injuries sustained during workouts.
            • Always consult a physician before starting a new exercise program.
            • Exercise recommendations are general guidelines, not medical advice.
            """;

        Label termsLbl = new Label(terms);
        termsLbl.setFont(Font.font("SansSerif", 12));
        termsLbl.setTextFill(Color.web(TEXT_SECONDARY));
        termsLbl.setWrapText(true);
        termsLbl.setLineSpacing(4);

        card.getChildren().add(termsLbl);
        return card;
    }

    // ===== Contact Card =====
    private VBox buildContactCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 10;");

        String[][] contacts = {
                {"📞 Contact",  "0908766312"},
                {"📧 Email",    "lakasmopar@gmail.com"},
                {"📘 Facebook", "Denmark Olpenda"}
        };

        for (String[] contact : contacts) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);

            Label keyLbl = new Label(contact[0]);
            keyLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
            keyLbl.setTextFill(Color.web(TEXT_PRIMARY));
            keyLbl.setPrefWidth(120);

            Label valLbl = new Label(contact[1]);
            valLbl.setFont(Font.font("SansSerif", 13));
            valLbl.setTextFill(Color.web(TEXT_SECONDARY));

            row.getChildren().addAll(keyLbl, valLbl);
            card.getChildren().add(row);
        }
        return card;
    }

    // ===== Style Helpers =====
    private Button buildBackBtn() {
        Button btn = new Button("← Back");
        btn.setStyle(
                "-fx-background-color: #2a2d30; -fx-text-fill: white;" +
                        "-fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;"
        );
        return btn;
    }

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

    private Button choiceBtn(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("SansSerif", 13));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setWrapText(true);
        return btn;
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

    private String fieldStyle() {
        return "-fx-background-color: #1e2428; -fx-text-fill: white;" +
                "-fx-prompt-text-fill: " + TEXT_SECONDARY + ";" +
                "-fx-padding: 8; -fx-background-radius: 6;" +
                "-fx-border-color: #2a2d30; -fx-border-radius: 6;";
    }

    private void showSuccess(String message, Stage stage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("TaraGym");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        contentStack.getChildren().clear();
        contentStack.getChildren().add(buildMainView(stage));
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("TaraGym");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}