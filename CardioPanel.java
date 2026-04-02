import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.*;

public class CardioPanel {

    // ── Colour palette — cardio uses blue accent, not red ─────────────────────
    private final String BG_PRIMARY     = "#1A1D20";
    private final String BG_SECONDARY   = "#0D0F12";
    private final String ACCENT         = "#1A8FE3";   // blue — cardio identity
    private final String ACCENT_BG      = "#0d1e2b";   // dark blue tint for badges
    private final String TEXT_PRIMARY   = "#FFFFFF";
    private final String TEXT_SECONDARY = "#A8B2C1";
    private final String CARD_BG        = "#1e2428";
    private final String TABLE_ROW_ODD  = "#16191C";
    private final String TABLE_ROW_EVEN = "#1e2428";
    private final String DIVIDER        = "#2a2d30";
    private final String TAB_INACTIVE   = "#1e2428";

    private final String DIFF_BEGINNER_COLOR     = "#2ECC71";
    private final String DIFF_INTERMEDIATE_COLOR = "#F39C12";
    private final String DIFF_ADVANCED_COLOR     = "#E63946";

    private final Stage stage;
    private VBox exerciseListContainer;

    private String activeCategory   = "All";
    private String activeDifficulty = "All";
    private List<Button> diffBtns   = new ArrayList<>();

    // ── Exercise model ────────────────────────────────────────────────────────
    static class CardioExercise {
        String name, altName, difficulty, category, equipment, intensity, calorieBurn;
        String imagePath;
        List<String> steps;

        CardioExercise(String name, List<String> steps, String altName,
                       String difficulty, String category, String equipment,
                       String intensity, String calorieBurn, String imagePath) {
            this.name = name; this.steps = steps; this.altName = altName;
            this.difficulty = difficulty; this.category = category;
            this.equipment = equipment; this.intensity = intensity;
            this.calorieBurn = calorieBurn; this.imagePath = imagePath;
        }
    }

    // ── Category tab labels ───────────────────────────────────────────────────
    private static final List<String> CATEGORIES = Arrays.asList(
            "All", "Steady-State", "HIIT", "Low-Impact", "Machine", "Outdoor"
    );

    // ── Helpers ───────────────────────────────────────────────────────────────
    private static CardioExercise ex(String name, List<String> steps, String alt,
                                     String diff, String cat, String equip,
                                     String intensity, String calories, String img) {
        return new CardioExercise(name, steps, alt, diff, cat, equip, intensity, calories, img);
    }
    private static List<String> l(String... s) { return Arrays.asList(s); }

    // ── Exercise Database ─────────────────────────────────────────────────────
    private static final List<CardioExercise> CARDIO_DB = new ArrayList<>();

    static {

        // ═══════════════════════ STEADY-STATE ════════════════════════════════

        CARDIO_DB.add(ex("Treadmill Walk",
                l("Set treadmill speed to a brisk walking pace of 3.5–4.5 mph.",
                        "Stand upright with your core lightly engaged, arms swinging naturally.",
                        "Breathe steadily through your nose; maintain this pace for 20–60 minutes.",
                        "Keep your gaze forward, shoulders back — avoid gripping the handrails."),
                "Incline Walk", "Beginner", "Steady-State", "Treadmill",
                "Low", "~200–300 kcal / 30 min",
                "treadmill_walk.jpg"));

        CARDIO_DB.add(ex("Treadmill Jog",
                l("Warm up with a 5-minute walk, then raise speed to a comfortable jog of 5–6 mph.",
                        "Land mid-foot with each stride, arms bent at 90° and relaxed at the hands.",
                        "Breathe rhythmically — inhale for 2–3 steps, exhale for 2–3 steps.",
                        "Cool down with a 5-minute walk; stretch calves and hip flexors after."),
                "Easy Run", "Beginner", "Steady-State", "Treadmill",
                "Moderate", "~300–400 kcal / 30 min",
                "treadmill_jog.jpg"));

        CARDIO_DB.add(ex("Stationary Bike — Steady Pace",
                l("Adjust seat height so your knee has a 5–10° bend at the bottom of the pedal stroke.",
                        "Set resistance so you can hold a conversation but still feel the effort.",
                        "Pedal at 60–80 RPM with a smooth, circular motion for 20–45 minutes.",
                        "Keep your core lightly braced and avoid hunching forward over the handlebars."),
                "Steady Cycling", "Beginner", "Steady-State", "Stationary Bike",
                "Low–Moderate", "~250–350 kcal / 30 min",
                "stationary_bike_moderate.jpg"));

        CARDIO_DB.add(ex("Elliptical — Steady State",
                l("Step onto the elliptical and grip the moving handles lightly — don't lean on them.",
                        "Set a moderate resistance and find a comfortable stride length.",
                        "Push and pull the handles actively to engage your chest and back.",
                        "Maintain a smooth, oval motion at 140–160 strides per minute for 20–40 minutes."),
                "Cross Trainer", "Beginner", "Steady-State", "Elliptical",
                "Low–Moderate", "~270–380 kcal / 30 min",
                "elliptical.jpg"));

        CARDIO_DB.add(ex("Rowing Machine — Steady Pace",
                l("Sit with feet strapped in, knees bent, shins vertical — grab the handle overhand.",
                        "Drive through your legs first (60% of power), then hinge the torso back slightly.",
                        "Finally pull the handle to your lower chest with your arms.",
                        "Row at a steady 22–26 strokes per minute for 20–30 minutes, focusing on form."),
                "Ergometer Row", "Intermediate", "Steady-State", "Rowing Machine",
                "Moderate", "~300–420 kcal / 30 min",
                "rowing_machine_steady.jpg"));

        CARDIO_DB.add(ex("Jump Rope — Continuous",
                l("Hold rope handles at hip height with the rope lying behind your feet.",
                        "Swing the rope overhead with your wrists and jump with both feet as it passes under.",
                        "Land softly on the balls of your feet with a slight knee bend each time.",
                        "Maintain a steady rhythm for 10–20 minutes; rest briefly if needed."),
                "Skipping", "Intermediate", "Steady-State", "Jump Rope",
                "Moderate–High", "~350–450 kcal / 30 min",
                "jump_rope_continuous.jpg"));

        // ═══════════════════════════ HIIT ════════════════════════════════════

        CARDIO_DB.add(ex("Burpee",
                l("Stand with feet shoulder-width apart, core braced.",
                        "Drop your hands to the floor and jump both feet back into a high plank.",
                        "Perform one push-up, then jump feet back to hands.",
                        "Explode upward into a jump, reaching both arms overhead — that's one rep."),
                "Squat Thrust", "Advanced", "HIIT", "Bodyweight",
                "Very High", "~10–15 kcal / min",
                "burpee.jpg"));

        CARDIO_DB.add(ex("Sprint Intervals",
                l("Warm up with 5 minutes of easy jogging.",
                        "Sprint at 90–100% of your maximum effort for 20–30 seconds.",
                        "Walk or jog slowly for 40–60 seconds to recover your breath.",
                        "Repeat 6–10 rounds depending on fitness level; cool down for 5 minutes after."),
                "Treadmill Sprints", "Advanced", "HIIT", "Treadmill / Track",
                "Very High", "~12–16 kcal / min during sprints",
                "sprint_intervals.jpg"));

        CARDIO_DB.add(ex("Jump Squat",
                l("Stand with feet shoulder-width apart, toes turned slightly outward.",
                        "Lower into a squat until thighs are parallel to the floor, weight in heels.",
                        "Drive through the floor explosively and jump as high as possible.",
                        "Land softly with bent knees and flow immediately into the next squat."),
                "Squat Jump", "Intermediate", "HIIT", "Bodyweight",
                "High", "~8–12 kcal / min",
                "jump_squat.jpg"));

        CARDIO_DB.add(ex("Mountain Climbers",
                l("Start in a high plank: hands directly under shoulders, body in a straight line.",
                        "Drive your right knee toward your chest, then quickly switch to the left.",
                        "Alternate as fast as possible while keeping your hips level and core tight.",
                        "Work for 30–45 seconds per interval, rest, and repeat 6–8 rounds."),
                "Running Planks", "Intermediate", "HIIT", "Bodyweight",
                "High", "~9–13 kcal / min",
                "mountain_climbers.jpg"));

        CARDIO_DB.add(ex("Box Jump",
                l("Stand 1–2 feet from a sturdy box or platform, feet hip-width apart.",
                        "Dip into a quarter squat and swing your arms back to load the jump.",
                        "Drive through the floor explosively and jump onto the box, landing softly.",
                        "Stand up fully on the box, then step back down before resetting."),
                "Plyometric Box Jump", "Advanced", "HIIT", "Box / Platform",
                "High", "~10–14 kcal / min",
                "box_jump.jpg"));

        CARDIO_DB.add(ex("High Knees",
                l("Stand with feet hip-width apart, arms bent at 90°.",
                        "Drive your right knee up toward your chest while pumping your left arm forward.",
                        "Quickly switch, alternating legs in a running-in-place motion.",
                        "Maintain a fast, rhythmic pace for 30–45 seconds; rest and repeat."),
                "Running in Place", "Beginner", "HIIT", "Bodyweight",
                "High", "~8–11 kcal / min",
                "high_knees.jpg"));

        CARDIO_DB.add(ex("Battle Ropes",
                l("Anchor a thick rope at one end and hold the other end in each hand.",
                        "Stand with feet shoulder-width apart, knees soft, core braced.",
                        "Alternate slamming each arm up and down to create powerful, continuous waves.",
                        "Work hard for 20–30 seconds, rest 30–40 seconds; repeat for 6–8 rounds."),
                "Wave Ropes", "Intermediate", "HIIT", "Battle Ropes",
                "Very High", "~10–15 kcal / min",
                "battle_ropes.jpeg"));

        CARDIO_DB.add(ex("Kettlebell Swing",
                l("Stand feet shoulder-width apart, kettlebell on the floor between your feet.",
                        "Hinge at the hips, grip the handle, and hike it back between your legs.",
                        "Drive your hips forward explosively to swing the bell to shoulder height.",
                        "Let it arc back through your legs and immediately repeat the hip drive."),
                "KB Swing", "Intermediate", "HIIT", "Kettlebell",
                "High", "~9–13 kcal / min",
                "kettlebell_swing.jpg"));

        // ═══════════════════════ LOW-IMPACT ══════════════════════════════════

        CARDIO_DB.add(ex("Swimming — Freestyle",
                l("Push off the wall and begin alternating arm strokes, rotating your body with each pull.",
                        "Keep your hips near the surface — kick from the hips with relaxed, straight legs.",
                        "Turn your head to the side to breathe every 2–3 strokes, not straight up.",
                        "Aim for 20–40 continuous minutes; rest at the wall between sets if needed."),
                "Freestyle Swim", "Beginner", "Low-Impact", "Pool",
                "Moderate", "~300–450 kcal / 30 min",
                "swimming_freestyle.png"));

        CARDIO_DB.add(ex("Water Aerobics",
                l("Enter a pool where water is between waist and chest depth.",
                        "Perform movements such as leg kicks, arm sweeps, jumping jacks, and marching in place.",
                        "Water provides 12× more resistance than air, intensifying every movement.",
                        "Work for 30–45 minutes — ideal for joint recovery or low-impact conditioning."),
                "Aqua Aerobics", "Beginner", "Low-Impact", "Pool",
                "Low–Moderate", "~200–350 kcal / 30 min",
                "water_aerobics.jpg"));

        CARDIO_DB.add(ex("Outdoor Cycling",
                l("Adjust saddle height so your knee retains a slight bend at the bottom of the stroke.",
                        "Start on flat terrain, pedalling at 70–90 RPM in a gear that feels challenging but smooth.",
                        "Shift to easier gears on climbs to maintain cadence rather than grinding heavy gears.",
                        "Aim for 30–90 minutes; wear a helmet and use hand signals in traffic."),
                "Road Cycling", "Beginner", "Low-Impact", "Bicycle",
                "Low–Moderate", "~250–400 kcal / 30 min",
                "cycling_outdoor.jpg"));

        CARDIO_DB.add(ex("Elliptical — Low Resistance",
                l("Set resistance to 1–3 and step onto the machine.",
                        "Move in a smooth oval motion, maintaining a comfortable stride.",
                        "Keep weight spread evenly across both feet — avoid pushing only with your toes.",
                        "This setting is perfect for active recovery days or post-leg-day cardio."),
                "Recovery Elliptical", "Beginner", "Low-Impact", "Elliptical",
                "Very Low", "~150–250 kcal / 30 min",
                "elliptical.jpg"));

        CARDIO_DB.add(ex("Yoga Flow",
                l("Begin standing in Mountain Pose — feet together, arms at your sides, breathing steady.",
                        "Inhale and sweep arms overhead; exhale and fold forward into Standing Forward Fold.",
                        "Step back to Plank, lower to Chaturanga, then press into Upward Dog.",
                        "Exhale into Downward Dog; hold 5 breaths, then step forward and repeat 5–10 rounds."),
                "Sun Salutation", "Beginner", "Low-Impact", "Bodyweight / Mat",
                "Low", "~120–200 kcal / 30 min",
                "yoga_flow.jpeg"));

        CARDIO_DB.add(ex("Stair Climbing",
                l("Stand at the base of a staircase or step machine — no need to rush.",
                        "Step up one stair at a time, driving through the heel of the elevated foot.",
                        "Use the handrail only for balance — do not offload your weight onto it.",
                        "Climb for 10–30 minutes at a pace where breathing is elevated but controlled."),
                "Step Climbing", "Beginner", "Low-Impact", "Stairs / Step Machine",
                "Moderate", "~250–380 kcal / 30 min",
                "stair_climbing.jpeg"));

        // ═══════════════════════════ MACHINE ════════════════════════════════

        CARDIO_DB.add(ex("Treadmill Incline Walk",
                l("Set treadmill incline to 8–15% and speed to 2.5–3.5 mph.",
                        "Walk upright without holding the handrails — this is key to the calorie burn.",
                        "Keep your core engaged and let your arms swing naturally.",
                        "Walk for 20–45 minutes; the steep angle dramatically increases intensity without running."),
                "Incline Treadmill", "Beginner", "Machine", "Treadmill",
                "Moderate–High", "~350–500 kcal / 30 min",
                "treadmill_incline_walk.jpg"));

        CARDIO_DB.add(ex("Stationary Bike — Intervals",
                l("Warm up at low resistance for 5 minutes, pedalling at 70 RPM.",
                        "Crank up resistance and sprint at 90–110 RPM for 30 seconds.",
                        "Reduce resistance and spin easily at 60–70 RPM for 60 seconds to recover.",
                        "Repeat 8–12 rounds; cool down at low resistance for 5 minutes."),
                "Bike Intervals", "Intermediate", "Machine", "Stationary Bike",
                "High", "~400–550 kcal / 30 min",
                "stationary_bike_moderate.jpg"));

        CARDIO_DB.add(ex("Stair Climber Machine",
                l("Step onto the stair climber and set a moderate pace — avoid clinging to the rails.",
                        "Take full, deliberate steps; don't shuffle on your toes or bounce.",
                        "Keep your torso upright and let your glutes and quads do the work.",
                        "Climb for 15–30 minutes; one of the highest calorie-burning machines in the gym."),
                "StepMill", "Intermediate", "Machine", "Stair Climber",
                "High", "~400–500 kcal / 30 min",
                "stair_climber_machine.jpg"));

        CARDIO_DB.add(ex("Ski Erg",
                l("Stand in front of the Ski Erg and grip both handles overhead with arms extended.",
                        "Drive both handles down and back simultaneously in a powerful arc using lats and core.",
                        "Follow through until your hands reach your hips, hinging slightly at the waist.",
                        "Return handles overhead under control and repeat; work in 30–60 second intervals."),
                "Concept2 SkiErg", "Intermediate", "Machine", "Ski Erg",
                "High", "~8–12 kcal / min",
                "ski_erg.jpg"));

        CARDIO_DB.add(ex("Assault Bike",
                l("Sit on the Assault Bike and grip both handles, feet on the pedals.",
                        "Simultaneously push and pull the handles while pedalling to drive the fan.",
                        "Work at near-maximum effort for 20–30 seconds; the harder you go, the harder it pushes back.",
                        "Rest for 40–60 seconds and repeat for 6–10 rounds."),
                "Air Bike / Fan Bike", "Advanced", "Machine", "Assault Bike",
                "Very High", "~12–20 kcal / min",
                "assault_bike.jpg"));

        CARDIO_DB.add(ex("Rowing Machine — Intervals",
                l("Warm up with 5 minutes of easy rowing at 20 strokes per minute.",
                        "Row hard at 26–30 strokes per minute for 250 metres, driving with full power.",
                        "Rest for 60 seconds by backing off intensity or stopping completely.",
                        "Repeat 6–8 rounds; focus on maintaining powerful leg drives even when fatigued."),
                "Rowing Intervals", "Intermediate", "Machine", "Rowing Machine",
                "High", "~10–14 kcal / min",
                "rowing_machine_steady.jpg"));

        // ═══════════════════════════ OUTDOOR ════════════════════════════════

        CARDIO_DB.add(ex("Running",
                l("Warm up with a 5-minute brisk walk to prepare your joints and muscles.",
                        "Run at a pace where you can speak in short sentences — this is your aerobic zone.",
                        "Land with a slight forward lean, striking mid-foot under your hips.",
                        "Cool down with a 5-minute walk and stretch your calves, hip flexors, and quads."),
                "Road Running", "Intermediate", "Outdoor", "Running Shoes",
                "Moderate–High", "~350–500 kcal / 30 min",
                "running.jpg"));

        CARDIO_DB.add(ex("Trail Running",
                l("Choose a trail appropriate to your current fitness level — start easy.",
                        "Shorten your stride on uneven terrain and look 6–10 feet ahead to read the path.",
                        "Use your arms for balance on steep sections and engage your core on descents.",
                        "Slow to a walk on technical downhills to protect your knees."),
                "Off-Road Running", "Intermediate", "Outdoor", "Trail Shoes",
                "Moderate–High", "~370–520 kcal / 30 min",
                "trail_running.jpg"));

        CARDIO_DB.add(ex("Hill Sprints",
                l("Find a hill with a 6–15% gradient and a length of 40–80 metres.",
                        "Sprint up at near-maximum effort, driving your knees high with each stride.",
                        "Walk slowly back down the hill to fully recover — this is your rest period.",
                        "Repeat 6–10 rounds; builds explosive power, speed, and cardio capacity."),
                "Hill Repeats", "Advanced", "Outdoor", "Hill / Incline",
                "Very High", "~12–16 kcal / min during sprints",
                "hill_sprints.jpg"));

        CARDIO_DB.add(ex("Jump Rope — Outdoors",
                l("Find a flat surface with sufficient overhead clearance and good footing.",
                        "Hold handles loosely at hip height and swing the rope with your wrists.",
                        "Jump with both feet together as the rope passes beneath, landing lightly.",
                        "Do 1 minute on / 30 seconds rest for 10–15 minutes; add tricks as you improve."),
                "Skipping Outdoor", "Beginner", "Outdoor", "Jump Rope",
                "Moderate–High", "~350–450 kcal / 30 min",
                "jump_rope_continuous.jpg"));

        CARDIO_DB.add(ex("Sled Push",
                l("Load the sled with a weight that challenges you but allows full sprint speed.",
                        "Grip the vertical poles and lean forward at 45°, body forming a straight diagonal line.",
                        "Drive through the floor with powerful, alternating short strides.",
                        "Push for 20–30 metres at full effort, rest 60–90 seconds, and repeat 6–8 rounds."),
                "Prowler Push", "Advanced", "Outdoor", "Weighted Sled",
                "Very High", "~12–18 kcal / min",
                "sled_push.jpg"));
    }

    // ── Constructor ───────────────────────────────────────────────────────────
    public CardioPanel(Stage stage) {
        this.stage = stage;
    }

    // ── Build Scene ───────────────────────────────────────────────────────────
    public Scene buildScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PRIMARY + ";");

        VBox topSection = new VBox(0);
        topSection.getChildren().addAll(buildTopBar(), buildNavBar());
        root.setTop(topSection);

        exerciseListContainer = new VBox(0);

        ScrollPane scroll = new ScrollPane(buildContent());
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_PRIMARY + "; -fx-background-color: " + BG_PRIMARY + ";");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setCenter(scroll);

        stage.setTitle("TaraGym — Cardio");
        return new Scene(root, 900, 620);
    }

    // ── TOP BAR ───────────────────────────────────────────────────────────────
    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(14, 20, 14, 20));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + BG_SECONDARY + ";");

        Label logo = new Label("TaraGym");
        logo.setFont(Font.font("SansSerif", FontWeight.BOLD, 20));
        logo.setTextFill(Color.web("#E63946")); // brand logo stays red

        Label sep = new Label("  |  ");
        sep.setTextFill(Color.web(TEXT_SECONDARY));

        Label page = new Label("Cardio");
        page.setFont(Font.font("SansSerif", FontWeight.NORMAL, 14));
        page.setTextFill(Color.web(TEXT_PRIMARY));

        // Small blue pill to signal the cardio section
        Label pill = new Label("🏃 CARDIO");
        pill.setFont(Font.font("SansSerif", FontWeight.BOLD, 10));
        pill.setTextFill(Color.web(ACCENT));
        pill.setStyle("-fx-background-color: " + ACCENT_BG + "; -fx-background-radius: 4;"
                + "-fx-padding: 3 8; -fx-border-color: " + ACCENT + ";"
                + "-fx-border-radius: 4; -fx-border-width: 1;");
        HBox.setMargin(pill, new Insets(0, 0, 0, 10));

        bar.getChildren().addAll(logo, sep, page, pill);
        return bar;
    }

    // ── NAV BAR ───────────────────────────────────────────────────────────────
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
        String norm  = "-fx-background-color: transparent; -fx-text-fill: " + TEXT_SECONDARY + ";"
                + "-fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;";
        String hover = "-fx-background-color: #2a2d30; -fx-text-fill: white;"
                + "-fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;";
        btn.setStyle(norm);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited (e -> btn.setStyle(norm));
        btn.setOnAction(e -> Navigator.go(stage, screen));
        return btn;
    }

    // ── CONTENT ───────────────────────────────────────────────────────────────
    private VBox buildContent() {
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + BG_PRIMARY + ";");
        content.setPadding(new Insets(20, 32, 32, 32));

        // Back button
        Button backBtn = new Button("← Back");
        String backNorm  = "-fx-background-color: " + TAB_INACTIVE + "; -fx-text-fill: " + TEXT_SECONDARY + ";"
                + "-fx-font-size: 12px; -fx-padding: 7 16; -fx-background-radius: 6; -fx-cursor: hand;"
                + "-fx-border-color: " + DIVIDER + "; -fx-border-radius: 6; -fx-border-width: 1;";
        String backHover = "-fx-background-color: #2a2d30; -fx-text-fill: white;"
                + "-fx-font-size: 12px; -fx-padding: 7 16; -fx-background-radius: 6; -fx-cursor: hand;"
                + "-fx-border-color: " + DIVIDER + "; -fx-border-radius: 6; -fx-border-width: 1;";
        backBtn.setStyle(backNorm);
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(backHover));
        backBtn.setOnMouseExited (e -> backBtn.setStyle(backNorm));
        backBtn.setOnAction(e -> Navigator.go(stage, "home"));

        HBox backRow = new HBox(backBtn);
        backRow.setPadding(new Insets(0, 0, 14, 0));
        content.getChildren().add(backRow);

        // Header
        Label header = new Label("Cardio Exercises");
        header.setFont(Font.font("SansSerif", FontWeight.BOLD, 26));
        header.setTextFill(Color.web(TEXT_PRIMARY));
        header.setPadding(new Insets(0, 0, 4, 0));

        Label sub = new Label("Filter by cardio type and difficulty level.");
        sub.setFont(Font.font("SansSerif", 13));
        sub.setTextFill(Color.web(TEXT_SECONDARY));
        sub.setPadding(new Insets(0, 0, 16, 0));

        content.getChildren().addAll(header, sub);

        // Category tabs — blue active state
        List<Button> tabBtns = new ArrayList<>();
        HBox tabBar = new HBox(8);
        tabBar.setPadding(new Insets(0, 0, 10, 0));
        tabBar.setAlignment(Pos.CENTER_LEFT);

        for (String cat : CATEGORIES) {
            Button tab = new Button(cat);
            boolean isFirst = cat.equals("All");
            tab.setStyle(tabStyle(isFirst));
            tabBtns.add(tab);
            tab.setOnAction(e -> {
                tabBtns.forEach(b -> b.setStyle(tabStyle(false)));
                tab.setStyle(tabStyle(true));
                activeCategory = cat;
                refreshExercises();
            });
            tab.setOnMouseEntered(e -> {
                if (!tab.getStyle().contains("1A8FE3")) tab.setStyle(tabStyleHover());
            });
            tab.setOnMouseExited(e -> {
                if (!tab.getStyle().contains("1A8FE3")) tab.setStyle(tabStyle(false));
            });
            tabBar.getChildren().add(tab);
        }
        content.getChildren().add(tabBar);

        // Difficulty filter
        HBox diffRow = buildDifficultyFilterRow();
        diffRow.setPadding(new Insets(0, 0, 18, 0));
        content.getChildren().add(diffRow);

        // Exercise list
        exerciseListContainer = new VBox(0);
        refreshExercises();
        content.getChildren().add(exerciseListContainer);

        return content;
    }

    // ── DIFFICULTY FILTER ROW ─────────────────────────────────────────────────
    private HBox buildDifficultyFilterRow() {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        Label filterLabel = new Label("Difficulty:");
        filterLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        filterLabel.setTextFill(Color.web(TEXT_SECONDARY));
        filterLabel.setPadding(new Insets(0, 4, 0, 0));
        row.getChildren().add(filterLabel);

        String[][] diffOptions = {
                {"All",          "#A8B2C1", "#2a2d30"},
                {"Beginner",     DIFF_BEGINNER_COLOR,     "#0d2b1a"},
                {"Intermediate", DIFF_INTERMEDIATE_COLOR, "#2b1e0a"},
                {"Advanced",     DIFF_ADVANCED_COLOR,     "#2b0a0d"}
        };

        diffBtns.clear();
        for (String[] opt : diffOptions) {
            final String btnLabel    = opt[0];
            final String btnColor    = opt[1];
            final String btnActiveBg = opt[2];

            Button btn = new Button(btnLabel.equals("All") ? "All Levels" : btnLabel);
            boolean init = btnLabel.equals("All");
            btn.setStyle(buildDiffBtnStyle(init, btnColor, btnActiveBg, init));
            diffBtns.add(btn);

            btn.setOnAction(e -> {
                activeDifficulty = btnLabel;
                for (int i = 0; i < diffBtns.size(); i++) {
                    String[] o = diffOptions[i];
                    boolean active = o[0].equals(btnLabel);
                    diffBtns.get(i).setStyle(buildDiffBtnStyle(active, o[1], o[2], active));
                }
                refreshExercises();
            });
            btn.setOnMouseEntered(e -> {
                if (!activeDifficulty.equals(btnLabel))
                    btn.setStyle(buildDiffBtnStyleHover(btnColor));
            });
            btn.setOnMouseExited(e -> {
                boolean active = activeDifficulty.equals(btnLabel);
                btn.setStyle(buildDiffBtnStyle(active, btnColor, btnActiveBg, active));
            });
            row.getChildren().add(btn);
        }
        return row;
    }

    private String buildDiffBtnStyle(boolean active, String color, String activeBg, boolean isActive) {
        if (isActive)
            return "-fx-background-color: " + activeBg + "; -fx-text-fill: " + color + ";"
                    + "-fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 7 14;"
                    + "-fx-background-radius: 6; -fx-cursor: hand;"
                    + "-fx-border-color: " + color + "; -fx-border-radius: 6; -fx-border-width: 1.5;";
        return "-fx-background-color: " + TAB_INACTIVE + "; -fx-text-fill: " + TEXT_SECONDARY + ";"
                + "-fx-font-size: 12px; -fx-padding: 7 14; -fx-background-radius: 6; -fx-cursor: hand;"
                + "-fx-border-color: " + DIVIDER + "; -fx-border-radius: 6; -fx-border-width: 1;";
    }

    private String buildDiffBtnStyleHover(String color) {
        return "-fx-background-color: #2a2d30; -fx-text-fill: " + color + ";"
                + "-fx-font-size: 12px; -fx-padding: 7 14; -fx-background-radius: 6; -fx-cursor: hand;"
                + "-fx-border-color: " + color + "; -fx-border-radius: 6; -fx-border-width: 1;";
    }

    // ── Refresh exercise list ─────────────────────────────────────────────────
    private void refreshExercises() {
        exerciseListContainer.getChildren().clear();

        List<CardioExercise> filtered = new ArrayList<>();
        for (CardioExercise e : CARDIO_DB) {
            boolean catMatch  = activeCategory.equals("All")   || e.category.equals(activeCategory);
            boolean diffMatch = activeDifficulty.equals("All") || e.difficulty.equals(activeDifficulty);
            if (catMatch && diffMatch) filtered.add(e);
        }

        if (filtered.isEmpty()) {
            VBox emptyBox = new VBox(8);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40, 0, 0, 0));
            Label icon = new Label("🔍");
            icon.setFont(Font.font("SansSerif", 32));
            Label msg = new Label("No exercises match your current filters.");
            msg.setFont(Font.font("SansSerif", 14));
            msg.setTextFill(Color.web(TEXT_SECONDARY));
            Label hint = new Label("Try selecting \"All\" in the difficulty or type filter.");
            hint.setFont(Font.font("SansSerif", 12));
            hint.setTextFill(Color.web(DIVIDER));
            emptyBox.getChildren().addAll(icon, msg, hint);
            exerciseListContainer.getChildren().add(emptyBox);
            return;
        }

        String diffText = activeDifficulty.equals("All") ? "" : " · " + activeDifficulty;
        String catText  = activeCategory.equals("All")   ? "" : " · " + activeCategory;
        Label countLbl = new Label(filtered.size() + " exercise" + (filtered.size() == 1 ? "" : "s") + diffText + catText);
        countLbl.setFont(Font.font("SansSerif", 12));
        countLbl.setTextFill(Color.web(TEXT_SECONDARY));
        countLbl.setPadding(new Insets(0, 0, 12, 2));
        exerciseListContainer.getChildren().add(countLbl);

        for (CardioExercise e : filtered)
            exerciseListContainer.getChildren().add(buildExerciseCard(e));
    }

    // ── Tab styles — blue active ──────────────────────────────────────────────
    private String tabStyle(boolean active) {
        if (active) return "-fx-background-color: " + ACCENT + "; -fx-text-fill: white;"
                + "-fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 7 16;"
                + "-fx-background-radius: 6; -fx-cursor: hand;";
        return "-fx-background-color: " + TAB_INACTIVE + "; -fx-text-fill: " + TEXT_SECONDARY + ";"
                + "-fx-font-size: 12px; -fx-padding: 7 16; -fx-background-radius: 6; -fx-cursor: hand;"
                + "-fx-border-color: " + DIVIDER + "; -fx-border-radius: 6; -fx-border-width: 1;";
    }

    private String tabStyleHover() {
        return "-fx-background-color: #2a2d30; -fx-text-fill: white;"
                + "-fx-font-size: 12px; -fx-padding: 7 16; -fx-background-radius: 6; -fx-cursor: hand;"
                + "-fx-border-color: " + DIVIDER + "; -fx-border-radius: 6; -fx-border-width: 1;";
    }

    // ── EXERCISE CARD ─────────────────────────────────────────────────────────
    private VBox buildExerciseCard(CardioExercise ex) {
        VBox card = new VBox(0);
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 10;");
        VBox.setMargin(card, new Insets(0, 0, 18, 0));

        // Header section
        VBox headerBox = new VBox(0);
        headerBox.setStyle("-fx-background-color: " + BG_SECONDARY + "; -fx-background-radius: 10 10 0 0;");

        // Blue stripe — distinguishes cardio cards from muscle group cards at a glance
        Region stripe = new Region();
        stripe.setPrefHeight(4);
        stripe.setStyle("-fx-background-color: " + ACCENT + "; -fx-background-radius: 10 10 0 0;");

        HBox nameRow = new HBox(10);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        nameRow.setPadding(new Insets(12, 20, 12, 20));

        Label nameLbl = new Label(ex.name);
        nameLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));
        nameLbl.setTextFill(Color.web(TEXT_PRIMARY));

        // Category tag — blue
        Label tagLbl = new Label(ex.category);
        tagLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 10));
        tagLbl.setTextFill(Color.web(ACCENT));
        tagLbl.setStyle("-fx-background-color: " + ACCENT_BG + "; -fx-background-radius: 4;"
                + "-fx-padding: 3 8; -fx-border-color: " + ACCENT + ";"
                + "-fx-border-radius: 4; -fx-border-width: 1;");

        // Difficulty badge — colour-coded
        String diffColor   = getDifficultyColor(ex.difficulty);
        String diffBgColor = getDifficultyBgColor(ex.difficulty);
        Label diffBadge = new Label(ex.difficulty);
        diffBadge.setFont(Font.font("SansSerif", FontWeight.BOLD, 10));
        diffBadge.setTextFill(Color.web(diffColor));
        diffBadge.setStyle("-fx-background-color: " + diffBgColor + "; -fx-background-radius: 4;"
                + "-fx-padding: 3 8; -fx-border-color: " + diffColor + ";"
                + "-fx-border-radius: 4; -fx-border-width: 1;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        nameRow.getChildren().addAll(nameLbl, tagLbl, spacer, diffBadge);
        headerBox.getChildren().addAll(stripe, nameRow);
        card.getChildren().add(headerBox);

        // Body
        HBox body = new HBox(24);
        body.setPadding(new Insets(16, 20, 16, 20));
        body.setAlignment(Pos.TOP_LEFT);

        // Image box
        VBox imgBox = new VBox();
        imgBox.setAlignment(Pos.CENTER);
        imgBox.setMinWidth(400);
        imgBox.setMaxWidth(400);
        imgBox.setMinHeight(450);
        imgBox.setStyle("-fx-background-color: #141618; -fx-background-radius: 8;");

        boolean imageLoaded = false;
        if (ex.imagePath != null && !ex.imagePath.isEmpty()) {
            try {
                var imgStream = getClass().getResourceAsStream("images/" + ex.imagePath);
                if (imgStream != null) {
                    Image img = new Image(imgStream);
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(400);
                    iv.setFitHeight(450);
                    iv.setPreserveRatio(true);
                    iv.setSmooth(true);
                    imgBox.getChildren().add(iv);
                    imageLoaded = true;
                }
            } catch (Exception ignored) { }
        }
        if (!imageLoaded) {
            Label ph = new Label("🏃\n" + ex.name);
            ph.setFont(Font.font("SansSerif", 11));
            ph.setTextFill(Color.web(TEXT_SECONDARY));
            ph.setAlignment(Pos.CENTER);
            ph.setWrapText(true);
            ph.setMaxWidth(130);
            imgBox.getChildren().add(ph);
        }

        // Steps — blue step numbers
        VBox stepsBox = new VBox(7);
        stepsBox.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(stepsBox, Priority.ALWAYS);

        Label stepsHeader = new Label("How to perform:");
        stepsHeader.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        stepsHeader.setTextFill(Color.web(ACCENT));
        stepsHeader.setPadding(new Insets(0, 0, 3, 0));
        stepsBox.getChildren().add(stepsHeader);

        for (int i = 0; i < ex.steps.size(); i++) {
            HBox stepRow = new HBox(8);
            stepRow.setAlignment(Pos.TOP_LEFT);

            Label numLbl = new Label((i + 1) + ".");
            numLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
            numLbl.setTextFill(Color.web(ACCENT));
            numLbl.setMinWidth(18);

            Label stepLbl = new Label(ex.steps.get(i));
            stepLbl.setFont(Font.font("SansSerif", 12));
            stepLbl.setTextFill(Color.web(TEXT_SECONDARY));
            stepLbl.setWrapText(true);
            HBox.setHgrow(stepLbl, Priority.ALWAYS);

            stepRow.getChildren().addAll(numLbl, stepLbl);
            stepsBox.getChildren().add(stepRow);
        }

        body.getChildren().addAll(imgBox, stepsBox);
        card.getChildren().add(body);
        card.getChildren().add(buildMetaTable(ex));
        return card;
    }

    // ── Difficulty colour helpers ─────────────────────────────────────────────
    private String getDifficultyColor(String d) {
        return switch (d) {
            case "Beginner"     -> DIFF_BEGINNER_COLOR;
            case "Intermediate" -> DIFF_INTERMEDIATE_COLOR;
            case "Advanced"     -> DIFF_ADVANCED_COLOR;
            default             -> TEXT_SECONDARY;
        };
    }

    private String getDifficultyBgColor(String d) {
        return switch (d) {
            case "Beginner"     -> "#0d2b1a";
            case "Intermediate" -> "#2b1e0a";
            case "Advanced"     -> "#2b0a0d";
            default             -> TAB_INACTIVE;
        };
    }

    // ── META TABLE — cardio-specific fields ───────────────────────────────────
    private GridPane buildMetaTable(CardioExercise ex) {
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: " + BG_SECONDARY + "; -fx-background-radius: 0 0 10 10;");

        Region topLine = new Region();
        topLine.setPrefHeight(1);
        topLine.setStyle("-fx-background-color: " + DIVIDER + ";");
        topLine.setMaxWidth(Double.MAX_VALUE);
        GridPane.setColumnSpan(topLine, 2);
        grid.add(topLine, 0, 0);

        String diffColor = getDifficultyColor(ex.difficulty);

        // Cardio-specific rows: no "Force / Grips / Mechanic / Target" — replaced with
        // Category, Equipment, Intensity, and Est. Calorie Burn
        String[][] rows = {
                {"Alternative Name",   ex.altName.isEmpty() ? "—" : ex.altName},
                {"Difficulty",         ex.difficulty},
                {"Category",           ex.category},
                {"Equipment",          ex.equipment},
                {"Intensity",          ex.intensity},
                {"Est. Calorie Burn",  ex.calorieBurn}
        };

        for (int i = 0; i < rows.length; i++) {
            String bg = (i % 2 == 0) ? TABLE_ROW_EVEN : TABLE_ROW_ODD;

            Label keyLbl = new Label(rows[i][0]);
            keyLbl.setFont(Font.font("SansSerif", 12));
            keyLbl.setTextFill(Color.web(TEXT_SECONDARY));
            keyLbl.setPadding(new Insets(7, 20, 7, 20));
            keyLbl.setMaxWidth(Double.MAX_VALUE);
            keyLbl.setStyle("-fx-background-color: " + bg + ";");

            Label valLbl = new Label(rows[i][1]);
            valLbl.setFont(Font.font("SansSerif", 12));

            if (rows[i][0].equals("Difficulty")) {
                valLbl.setTextFill(Color.web(diffColor));
            } else if (rows[i][0].equals("Est. Calorie Burn")) {
                valLbl.setTextFill(Color.web(ACCENT)); // blue highlight — the payoff stat
            } else {
                valLbl.setTextFill(Color.web(TEXT_PRIMARY));
            }

            valLbl.setPadding(new Insets(7, 20, 7, 20));
            valLbl.setMaxWidth(Double.MAX_VALUE);
            valLbl.setStyle("-fx-background-color: " + bg + ";");

            if (i == rows.length - 1) {
                keyLbl.setStyle(keyLbl.getStyle() + "-fx-background-radius: 0 0 0 10;");
                valLbl.setStyle(valLbl.getStyle() + "-fx-background-radius: 0 0 10 0;");
            }

            GridPane.setHgrow(keyLbl, Priority.ALWAYS);
            GridPane.setHgrow(valLbl, Priority.ALWAYS);
            grid.add(keyLbl, 0, i + 1);
            grid.add(valLbl, 1, i + 1);
        }

        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(40);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(60);
        grid.getColumnConstraints().addAll(c1, c2);
        return grid;
    }
}