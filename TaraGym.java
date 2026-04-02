import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
// Added Image and ImageView imports
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
public class TaraGym extends Application {

    private final String BG_PRIMARY     = "#1A1D20";
    private final String BG_SECONDARY   = "#0D0F12";
    private final String ACCENT         = "#E63946";
    private final String TEXT_PRIMARY   = "#FFFFFF";
    private final String TEXT_SECONDARY = "#A8B2C1";
    private final String CARD_BG        = "#1e2428";

    // Cardio card uses a distinct accent colour to stand out from the muscle group cards
    private final String CARDIO_ACCENT  = "#1A8FE3";

    private Stage primaryStage;

    private final String[] MUSCLES = {
            "Chest", "Back", "Biceps", "Triceps", "Shoulders",
            "Quads", "Abs", "Calves", "Forearms", "Neck", "Traps"
    };

    private final String[] MUSCLE_ICONS = {
            "images/chest.png", "images/back.png", "images/biceps.png",
            "images/triceps.png", "images/shoulders.png", "images/thighs.png",
            "images/abs.png", "images/calves.png",
            "images/Forearms.png", "images/neck.png", "images/traps.png"
    };

    // Cardio icon path — place a cardio.png in your images/ folder
    private final String CARDIO_ICON = "images/cardio.png";

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        if (UserSession.getInstance().isLoggedIn()) {
            DatabaseManager.loadUserProfile(
                    UserSession.getInstance().getUsername());
        }

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PRIMARY + ";");

        VBox topSection = new VBox(0);
        topSection.getChildren().addAll(buildTopBar(), buildNavBar());
        root.setTop(topSection);

        ScrollPane scroll = new ScrollPane(buildContent());
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_PRIMARY + "; -fx-background-color: " + BG_PRIMARY + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setCenter(scroll);

        stage.setTitle("TaraGym");
        stage.setScene(new Scene(root, 900, 620));
        stage.show();
    }

    // ── TOP BAR ──────────────────────────────────────────────────────────────

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(14, 20, 14, 20));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + BG_SECONDARY + ";");

        Label logo = new Label("TaraGym");
        logo.setFont(Font.font("SansSerif", FontWeight.BOLD, 20));
        logo.setTextFill(Color.web(ACCENT));

        Label sep = new Label("  |  ");
        sep.setTextFill(Color.web(TEXT_SECONDARY));

        Label page = new Label("Home");
        page.setFont(Font.font("SansSerif", FontWeight.NORMAL, 14));
        page.setTextFill(Color.web(TEXT_PRIMARY));

        bar.getChildren().addAll(logo, sep, page);
        return bar;
    }

    // ── NAV BAR ──────────────────────────────────────────────────────────────

    private HBox buildNavBar() {
        HBox nav = new HBox(0);
        nav.setAlignment(Pos.CENTER);
        nav.setStyle("-fx-background-color: " + BG_SECONDARY + ";");
        nav.setPadding(new Insets(8, 20, 8, 20));

        nav.getChildren().addAll(
                navButton("🏠 Home",     "home",     true),
                navButton("📋 History",  "history",  false),
                navButton("💪 Workout",  "workout",  false),
                navButton("📅 Schedule", "schedule", false),
                navButton("📈 Progress", "progress", false),
                navButton("⚙ Settings", "settings", false)
        );
        return nav;
    }

    private Button navButton(String text, String screen, boolean active) {
        Button btn = new Button(text);

        String activeStyle = "-fx-background-color: " + ACCENT + "; -fx-text-fill: white;"
                + "-fx-font-size: 12px; -fx-padding: 8 16;"
                + "-fx-background-radius: 6; -fx-cursor: hand;";
        String normalStyle = "-fx-background-color: transparent; -fx-text-fill: " + TEXT_SECONDARY + ";"
                + "-fx-font-size: 12px; -fx-padding: 8 16;"
                + "-fx-background-radius: 6; -fx-cursor: hand;";
        String hoverStyle  = "-fx-background-color: #2a2d30; -fx-text-fill: white;"
                + "-fx-font-size: 12px; -fx-padding: 8 16;"
                + "-fx-background-radius: 6; -fx-cursor: hand;";

        btn.setStyle(active ? activeStyle : normalStyle);
        if (!active) {
            btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
            btn.setOnMouseExited(e  -> btn.setStyle(normalStyle));
        }

        btn.setOnAction(e -> Navigator.go(primaryStage, screen));

        return btn;
    }

    // ── CONTENT ──────────────────────────────────────────────────────────────

    private VBox buildContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color: " + BG_PRIMARY + ";");

        content.getChildren().addAll(
                buildWelcomeBanner(),
                buildSectionLabel("Browse Workouts by Muscle Group"),
                buildMuscleGrid(),
                buildSectionLabel("Cardio Training"),
                buildCardioGrid()
        );
        return content;
    }

    // ── WELCOME BANNER ────────────────────────────────────────────────────────

    private HBox buildWelcomeBanner() {
        HBox banner = new HBox(16);
        banner.setPadding(new Insets(24, 28, 24, 28));
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setStyle(
                "-fx-background-color: " + BG_SECONDARY + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #2a2d30;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;"
        );

        // Left — welcome text
        VBox textBox = new VBox(6);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        String name = UserSession.getInstance().getUsername();
        Label greet = new Label("Welcome back, " +
                (name == null || name.isEmpty() ? "Athlete" : name));
        greet.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));
        greet.setTextFill(Color.web(TEXT_PRIMARY));

        String split = UserSession.getInstance().getWorkoutSplit();
        String level = UserSession.getInstance().getLevel();
        Label sub = new Label(
                (split == null || split.isEmpty())
                        ? "Ready to crush today's workout? Let's go."
                        : "Level: " + level + "  ·  Split: " + split + "  —  Let's go!"
        );
        sub.setFont(Font.font("SansSerif", 13));
        sub.setTextFill(Color.web(TEXT_SECONDARY));

        textBox.getChildren().addAll(greet, sub);

        // Right — buttons stacked vertically with spacing
        VBox btnBox = new VBox(10);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        Button newHereBtn = new Button("New here? Get Started →");
        newHereBtn.setStyle(
                "-fx-background-color: " + ACCENT + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 10 20;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        );
        newHereBtn.setOnMouseEntered(e -> newHereBtn.setStyle(
                "-fx-background-color: #c0303b;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 10 20;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        ));
        newHereBtn.setOnMouseExited(e -> newHereBtn.setStyle(
                "-fx-background-color: " + ACCENT + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 10 20;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        ));
        newHereBtn.setOnAction(e -> {
            RegisterScreen register = new RegisterScreen();
            primaryStage.setScene(register.buildScene(primaryStage));
            primaryStage.setTitle("TaraGym — Register");
        });

        btnBox.getChildren().addAll(newHereBtn);
        banner.getChildren().addAll(textBox, btnBox);
        return banner;
    }

    // ── SECTION LABEL ────────────────────────────────────────────────────────

    private Label buildSectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));
        lbl.setTextFill(Color.web(TEXT_PRIMARY));
        return lbl;
    }

    // ── MUSCLE GRID ───────────────────────────────────────────────────────────

    private GridPane buildMuscleGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setPercentWidth(25);
            grid.getColumnConstraints().add(cc);
        }

        for (int i = 0; i < MUSCLES.length; i++) {
            grid.add(buildMuscleCard(MUSCLE_ICONS[i], MUSCLES[i]), i % 4, i / 4);
        }
        return grid;
    }

    private VBox buildMuscleCard(String imagePath, String muscle) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(18, 12, 18, 12));
        card.setMinHeight(100);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 10; -fx-cursor: hand;");

        ImageView iconView = new ImageView();
        try {
            Image img = new Image(imagePath);
            iconView.setImage(img);
            iconView.setFitWidth(40);
            iconView.setFitHeight(40);
            iconView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Could not load image: " + imagePath);
        }

        Label nameLbl = new Label(muscle);
        nameLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
        nameLbl.setTextFill(Color.web(TEXT_PRIMARY));

        card.getChildren().addAll(iconView, nameLbl);

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: " + ACCENT  + "; -fx-background-radius: 10; -fx-cursor: hand;"));
        card.setOnMouseExited(e  -> card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 10; -fx-cursor: hand;"));
        card.setOnMouseClicked(e -> {
            MuscleGroupPanel panel = new MuscleGroupPanel(muscle, primaryStage);
            primaryStage.setScene(panel.buildScene());
            primaryStage.setTitle(muscle);
        });

        return card;
    }

    // ── CARDIO GRID ───────────────────────────────────────────────────────────
    // Six cardio category cards laid out in a single-row grid (or wrapping).
    // Clicking any of them opens CardioPanel with the matching type pre-filtered.

    private GridPane buildCardioGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);

        ColumnConstraints cc = new ColumnConstraints();
        cc.setHgrow(Priority.ALWAYS);
        cc.setPercentWidth(100);
        grid.getColumnConstraints().add(cc);

        grid.add(buildCardioCard("Cardio Exercises", "images/cardio.png"), 0, 0);
        return grid;
    }

    private VBox buildCardioCard(String label, String imagePath) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(18, 12, 18, 12));
        card.setMinHeight(100);
        card.setMaxWidth(Double.MAX_VALUE);

        String normalStyle = "-fx-background-color: " + CARD_BG + ";"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: " + CARDIO_ACCENT + ";"
                + "-fx-border-radius: 10;"
                + "-fx-border-width: 1.5;"
                + "-fx-cursor: hand;";
        String hoverStyle  = "-fx-background-color: " + CARDIO_ACCENT + ";"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: " + CARDIO_ACCENT + ";"
                + "-fx-border-radius: 10;"
                + "-fx-border-width: 1.5;"
                + "-fx-cursor: hand;";

        card.setStyle(normalStyle);

        ImageView iconView = new ImageView();
        try {
            Image img = new Image(imagePath);
            iconView.setImage(img);
            iconView.setFitWidth(40);
            iconView.setFitHeight(40);
            iconView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Could not load cardio image: " + imagePath);
        }

        Label nameLbl = new Label(label);
        nameLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
        nameLbl.setTextFill(Color.web(TEXT_PRIMARY));

        Label badgeLbl = new Label("CARDIO");
        badgeLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 9));
        badgeLbl.setTextFill(Color.web(CARDIO_ACCENT));
        badgeLbl.setStyle(
                "-fx-background-color: #0d1e2b;"
                        + "-fx-background-radius: 4;"
                        + "-fx-padding: 2 6;"
                        + "-fx-border-color: " + CARDIO_ACCENT + ";"
                        + "-fx-border-radius: 4;"
                        + "-fx-border-width: 1;"
        );

        card.getChildren().addAll(iconView, nameLbl, badgeLbl);

        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e  -> card.setStyle(normalStyle));
        card.setOnMouseClicked(e -> {
            CardioPanel panel = new CardioPanel(primaryStage);
            primaryStage.setScene(panel.buildScene());
            primaryStage.setTitle("TaraGym — Cardio");
        });

        return card;
    }

    public static void main(String[] args) {
        launch();
    }
}