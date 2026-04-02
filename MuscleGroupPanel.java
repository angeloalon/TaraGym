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

public class MuscleGroupPanel {

    private final String BG_PRIMARY     = "#1A1D20";
    private final String BG_SECONDARY   = "#0D0F12";
    private final String ACCENT         = "#E63946";
    private final String TEXT_PRIMARY   = "#FFFFFF";
    private final String TEXT_SECONDARY = "#A8B2C1";
    private final String CARD_BG        = "#1e2428";
    private final String TABLE_ROW_ODD  = "#16191C";
    private final String TABLE_ROW_EVEN = "#1e2428";
    private final String DIVIDER        = "#2a2d30";
    private final String TAB_INACTIVE   = "#1e2428";
    private final String TAB_ACTIVE     = "#E63946";

    // Difficulty filter colors
    private final String DIFF_BEGINNER_COLOR     = "#2ECC71";
    private final String DIFF_INTERMEDIATE_COLOR = "#F39C12";
    private final String DIFF_ADVANCED_COLOR     = "#E63946";

    private final String muscle;
    private final Stage  stage;
    private VBox exerciseListContainer;

    // ── Active filters ────────────────────────────────────────────────────────
    private String activeRegion     = "All";
    private String activeDifficulty = "All";

    // ── Exercise model ────────────────────────────────────────────────────────

    static class Exercise {
        String name, altName, difficulty, force, grips, mechanic, target;
        String imagePath;
        List<String> steps;

        Exercise(String name, List<String> steps, String altName,
                 String difficulty, String force, String grips,
                 String mechanic, String target, String imagePath) {
            this.name = name; this.steps = steps; this.altName = altName;
            this.difficulty = difficulty; this.force = force; this.grips = grips;
            this.mechanic = mechanic; this.target = target;
            this.imagePath = imagePath;
        }
    }

    // ── Sub-region tab labels ─────────────────────────────────────────────────

    private static final Map<String, List<String>> REGIONS = new LinkedHashMap<>();
    static {
        REGIONS.put("Chest",     Arrays.asList("Chest", "Middle Chest", "Upper Chest", "Lower Chest"));
        REGIONS.put("Back",      Arrays.asList("Upper Back","Mid Back","Lower Back","Lats"));
        REGIONS.put("Biceps",    Arrays.asList("Long Head","Short Head","Brachialis"));
        REGIONS.put("Triceps",   Arrays.asList("Long Head","Lateral Head","Medial Head"));
        REGIONS.put("Shoulders", Arrays.asList("Front Delt","Side Delt","Rear Delt"));
        REGIONS.put("Quads",     Arrays.asList("Quads","Hamstrings","Inner Quads","Outer Quads"));
        REGIONS.put("Abs",       Arrays.asList("Upper Abs","Lower Abs","Obliques"));
        REGIONS.put("Butt",      Arrays.asList("Glute Max","Glute Med","Glute Min"));
        REGIONS.put("Calves",    Arrays.asList("Gastrocnemius","Soleus"));
        REGIONS.put("Forearms",  Arrays.asList("Wrist Flexors","Wrist Extensors","Brachioradialis"));
        REGIONS.put("Neck",      Arrays.asList("Neck Flexors","Neck Extensors","Neck Lateral"));
        REGIONS.put("Traps",     Arrays.asList("Upper Traps","Mid Traps","Lower Traps"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static Exercise ex(String name, List<String> steps, String alt,
                               String diff, String force, String grips,
                               String mech, String target, String img) {
        return new Exercise(name, steps, alt, diff, force, grips, mech, target, img);
    }

    private static List<String> l(String... s) { return Arrays.asList(s); }

    // ── Exercise database ─────────────────────────────────────────────────────

    private static final Map<String, List<Exercise>> DB = new LinkedHashMap<>();

    static {

        // ════════════════════════════ CHEST ════════════════════════════════════
        DB.put("Chest", Arrays.asList(
                // Upper Chest
                ex("Incline Barbell Press",
                        l("Set bench to 30–45° incline and lie back with bar above you.",
                                "Lower bar slowly to upper chest, elbows at ~75°.",
                                "Press back up explosively to full arm extension.",
                                "Keep shoulder blades pinched together throughout."),
                        "Incline Bench Press","Intermediate","Push","Overhand","Compound","Upper Chest",
                        "incline_barbell_press.jpg"),
                ex("Incline Dumbbell Press",
                        l("Set incline bench to 30–45° and hold dumbbells at chest level.",
                                "Press dumbbells up and in until arms are fully extended.",
                                "Lower slowly, feeling the stretch across upper chest.",
                                "Avoid letting elbows flare past 75°."),
                        "Incline DB Press","Intermediate","Push","Neutral/Overhand","Compound","Upper Chest",
                        "incline_dumbbell_press.jpg"),
                ex("Low-to-High Cable Fly",
                        l("Set both cable pulleys to the lowest position, grab a handle each hand.",
                                "Step forward and bring both hands up and together in an arc toward chin height.",
                                "Hold the contracted position at the top for one second.",
                                "Slowly return, keeping a slight elbow bend throughout."),
                        "Low Cable Fly","Intermediate","Push","Neutral","Isolation","Upper Chest",
                        "low_to_high_cable_fly.jpg"),
                ex("Landmine Press",
                        l("Wedge one end of a barbell into a corner and hold the other end at shoulder height.",
                                "Press the bar forward and upward in an arc until arm is fully extended.",
                                "Lower back to shoulder height with control.",
                                "The angled path naturally targets the upper chest."),
                        "","Intermediate","Push","Neutral","Compound","Upper Chest",
                        "landmine_press.jpg"),

                // Lower Chest
                ex("Decline Barbell Press",
                        l("Lie on a decline bench with feet secured and unrack the barbell.",
                                "Lower the bar to your lower chest in a controlled manner.",
                                "Press back up until arms are fully extended.",
                                "Keep your core tight to stay stable on the decline angle."),
                        "Decline Bench Press","Intermediate","Push","Overhand","Compound","Lower Chest",
                        "decline_barbell_press.jpg"),
                ex("Chest Dips",
                        l("Grip parallel bars, start with elbows extended, body leaning ~30° forward.",
                                "Lower yourself until elbows reach roughly 90°.",
                                "Push back up without fully locking elbows at the top.",
                                "Forward lean shifts emphasis to the lower pec."),
                        "Chest Dips","Advanced","Push","Neutral","Compound","Lower Chest",
                        "chest_dips.jpg"),
                ex("Decline Dumbbell Fly",
                        l("Lie on a decline bench holding dumbbells above your lower chest.",
                                "Lower both arms out in a wide arc until you feel a stretch.",
                                "Bring dumbbells back together over lower chest.",
                                "Keep a slight bend in the elbows throughout."),
                        "","Intermediate","Push","Neutral","Isolation","Lower Chest",
                        "decline_dumbbell_fly.jpg"),
                ex("High-to-Low Cable Fly",
                        l("Set both cable pulleys to the highest position and grab a handle each hand.",
                                "Step forward and bring both hands down and together in an arc toward lower chest.",
                                "Squeeze the lower pec at the bottom for one second.",
                                "Slowly return to the starting position."),
                        "High Cable Fly","Beginner","Push","Neutral","Isolation","Lower Chest",
                        "high_to_low_cable_fly.jpg"),

                // Inner Chest
                ex("Barbell Bench Press",
                        l("Lie flat on the bench, feet on the floor, unrack the barbell.",
                                "Lower the bar to mid-chest while keeping elbows at ~75°.",
                                "Press the bar back up, thinking about 'pushing your hands together'.",
                                "Fully extend your arms and repeat."),
                        "Bench Press","Intermediate","Push","Overhand","Compound","Middle Chest",
                        "barbell_bench_press.jpg"),
                ex("Dumbbell Squeeze Press",
                        l("Lie flat on a bench holding two dumbbells pressed together above your chest.",
                                "Squeeze the dumbbells together as hard as possible throughout.",
                                "Lower slowly to your chest while maintaining the squeeze.",
                                "Press back up — the constant squeeze activates Chest fibers."),
                        "Hex Press","Intermediate","Push","Neutral","Isolation","Middle Chest",
                        "dumbbell_squeeze_press.jpg"),
                ex("Dumbbell Fly",
                        l("Lie flat on a bench, dumbbells above chest, elbows slightly bent.",
                                "Lower both arms out in a wide arc until a deep chest stretch is felt.",
                                "Bring arms back together, squeezing the Middle chest hard at the top.",
                                "Do not let the dumbbells touch — keep tension throughout."),
                        "DB Fly","Intermediate","Push","Neutral","Isolation","Chest",
                        "dumbbell_fly.jpg"),
                ex("Diamond Push-Up",
                        l("Get into push-up position, hands together forming a diamond under chest.",
                                "Lower your chest to your hands, elbows close to sides.",
                                "Push back up to the starting position.",
                                "Narrow hand placement maximises chest activation."),
                        "Triangle Push-Up","Intermediate","Push","Overhand","Compound","Chest",
                        "diamond_push_up.jpg"),

                // Outer Chest
                ex("Wide-Grip Bench Press",
                        l("Lie flat and grip the barbell wider than shoulder-width.",
                                "Lower the bar to outer chest with elbows flared wide.",
                                "Press back up to the starting position.",
                                "Wider grip shifts load to the outer pec fibres."),
                        "","Intermediate","Push","Overhand","Compound","Chest",
                        "wide_grip_bench_press.jpg"),
                ex("Cable Fly",
                        l("Stand in the middle of a cable machine, pulleys at mid height.",
                                "Bring both handles together in a hugging arc in front of your chest.",
                                "Slowly open your arms back out for a full outer-chest stretch.",
                                "Keep a soft bend in the elbows throughout."),
                        "Pec Deck Alternative","Beginner","Push","Neutral","Isolation","Chest",
                        "cable_fly.jpg"),
                ex("Wide Push-Up",
                        l("Place hands wider than shoulder-width on the floor.",
                                "Lower your chest to the floor, body in a straight line.",
                                "Push back up to the starting position.",
                                "Wide base targets the outer chest and anterior deltoid."),
                        "","Beginner","Push","Overhand","Compound","Chest",
                        "wide_push_up.jpg"),
                ex("Pec Deck Machine",
                        l("Sit in the pec deck machine and place forearms against the pads.",
                                "Bring both pads together in front of chest in a controlled arc.",
                                "Hold the squeeze briefly, then slowly return to start.",
                                "Adjust the seat so elbows are at shoulder height."),
                        "Chest Fly Machine","Beginner","Push","Neutral","Isolation","Chest",
                        "pec_deck_machine.jpg")
        ));

        // ════════════════════════════ BACK ═════════════════════════════════════
        DB.put("Back", Arrays.asList(
                ex("Face Pull",
                        l("Attach a rope to a high pulley and grasp both ends overhand.",
                                "Pull the rope toward your face, flaring elbows out wide.",
                                "Hold at end range briefly to squeeze rear delts and upper traps.",
                                "Slowly return to starting position."),
                        "","Beginner","Pull","Overhand","Isolation","Upper Back",
                        "face_pull.jpg"),
                ex("Inverted Row",
                        l("Lay underneath a fixed bar, grab it with a wide overhand grip.",
                                "Keep body straight from head to heels.",
                                "Pull your chest up to the bar squeezing shoulder blades.",
                                "Lower back to full arm extension."),
                        "","Beginner","Pull","Overhand","Compound","Upper Back",
                        "inverted_row.jpg"),
                ex("Seated Cable Row (Wide Grip)",
                        l("Sit at a cable row station with a wide bar attachment.",
                                "Pull the bar toward your upper chest, elbows flared wide.",
                                "Squeeze shoulder blades hard at the end of the movement.",
                                "Slowly extend arms back to start."),
                        "Wide Cable Row","Beginner","Pull","Overhand","Compound","Upper Back",
                        "seated_cable_row_wide_grip.jpg"),
                ex("Barbell Shrug",
                        l("Stand holding a barbell in front with an overhand grip.",
                                "Shrug shoulders straight up as high as possible.",
                                "Hold at the top for one second to peak the trapezius.",
                                "Lower slowly and repeat."),
                        "","Beginner","Pull","Overhand","Isolation","Upper Back",
                        "barbell_shrug.jpg"),
                ex("Barbell Row",
                        l("Grab a barbell with a shoulder-width pronated grip.",
                                "Hinge at hips until torso is roughly 45° to the floor.",
                                "Pull bar toward navel, squeezing the mid-back hard.",
                                "Lower with control and repeat."),
                        "Bent Over Row","Intermediate","Pull","Pronated/Supinated","Compound","Mid Back",
                        "barbell_row.jpg"),
                ex("Dumbbell Row",
                        l("Place one knee and same-side hand on a bench, hold dumbbell in other hand.",
                                "Pull dumbbell up toward your hip, elbow close to body.",
                                "Squeeze the mid-back at the top, then lower to full extension.",
                                "Complete all reps on one side before switching."),
                        "Single Arm Row","Beginner","Pull","Neutral","Compound","Mid Back",
                        "dumbbell_row.jpg"),
                ex("T-Bar Row",
                        l("Load a T-bar machine or wedge a barbell and straddle it.",
                                "With a flat back, pull the weight up toward your chest.",
                                "Hold briefly at the top, then lower with control.",
                                "Do not round your lower back."),
                        "","Intermediate","Pull","Neutral","Compound","Mid Back",
                        "t_bar_row.jpg"),
                ex("Chest-Supported Row",
                        l("Lie chest-down on an incline bench holding dumbbells.",
                                "Row both dumbbells up toward your hips.",
                                "Squeeze the mid-back at the top.",
                                "Lower slowly to full extension."),
                        "Prone Row","Intermediate","Pull","Neutral","Compound","Mid Back",
                        "chest_supported_row.jpg"),
                ex("Deadlift",
                        l("Stand with feet hip-width apart, bar over mid-foot.",
                                "Hinge and grip bar just outside knees, back flat.",
                                "Drive through heels and extend hips to lift to standing.",
                                "Lower bar back to the floor with control every rep."),
                        "Conventional Deadlift","Advanced","Pull","Overhand/Mixed","Compound","Lower Back",
                        "deadlift.jpg"),
                ex("Back Extension",
                        l("Position yourself face-down on a hyperextension bench, feet secured.",
                                "Lower your torso toward the floor with a flat back.",
                                "Raise torso back up until body forms a straight line.",
                                "Do not hyperextend — stop at neutral spine."),
                        "Hyperextension","Beginner","Pull","N/A","Isolation","Lower Back",
                        "back_extension.jpg"),
                ex("Good Morning",
                        l("Place a barbell on your upper traps, feet shoulder-width apart.",
                                "With flat back and soft knees, hinge forward at the hips.",
                                "Lower until you feel a hamstring stretch.",
                                "Drive hips forward to return upright."),
                        "","Intermediate","Pull","Overhand","Compound","Lower Back",
                        "good_morning.jpg"),
                ex("Superman Hold",
                        l("Lie face-down on the floor with arms extended overhead.",
                                "Simultaneously lift arms, chest and legs off the floor.",
                                "Hold for 2–3 seconds squeezing lower back and glutes.",
                                "Lower and repeat."),
                        "","Beginner","Static","N/A","Isolation","Lower Back",
                        "superman_hold.jpg"),
                ex("Wide-Grip Pull-Up",
                        l("Grip the pull-up bar wider than shoulder-width, palms away.",
                                "Hang with arms fully extended then pull yourself up.",
                                "Aim to bring chin above bar or chest to bar.",
                                "Lower slowly to full arm extension."),
                        "Wide Pull-Up","Advanced","Pull","Overhand","Compound","Lats",
                        "wide_grip_pull_up.jpg"),
                ex("Lat Pulldown",
                        l("Sit at a lat pulldown machine and grip bar wider than shoulder-width.",
                                "Pull bar down to upper chest, leaning back slightly.",
                                "Squeeze lats hard at the bottom of the pull.",
                                "Slowly return to full arm extension."),
                        "Cable Pulldown","Beginner","Pull","Overhand","Compound","Lats",
                        "lat_pulldown.jpg"),
                ex("Straight-Arm Pulldown",
                        l("Stand at a cable machine with bar set high, grip with both hands.",
                                "Keeping arms straight, pull bar down in an arc toward your thighs.",
                                "Squeeze lats hard when bar reaches your hips.",
                                "Slowly return to starting position."),
                        "Lat Prayer","Intermediate","Pull","Overhand","Isolation","Lats",
                        "straight_arm_pulldown.jpg"),
                ex("Single-Arm Lat Pulldown",
                        l("Sit at a cable machine and grip one handle overhead with one hand.",
                                "Pull handle down and toward your outer hip.",
                                "Hold at the bottom to maximise lat contraction.",
                                "Slowly return and repeat, then switch sides."),
                        "","Beginner","Pull","Neutral","Isolation","Lats",
                        "single_arm_lat_pulldown.jpg")
        ));

        // ════════════════════════════ BICEPS ═══════════════════════════════════
        DB.put("Biceps", Arrays.asList(
                ex("Incline Dumbbell Curl",
                        l("Sit on a 45–60° incline bench, arms hanging freely.",
                                "Curl both dumbbells up keeping upper arms stationary.",
                                "Supinate wrists fully at the top.",
                                "Lower slowly to full arm extension for maximum stretch."),
                        "Incline Curl","Intermediate","Pull","Supinated","Isolation","Long Head",
                        "incline_dumbbell_curl.jpg"),
                ex("Barbell Curl",
                        l("Stand with shoulder-width underhand grip on a barbell.",
                                "Keep upper arms pinned to sides and curl bar to shoulder height.",
                                "Squeeze hard at the top, then lower slowly.",
                                "Avoid swinging — control through full range."),
                        "Standing Barbell Curl","Intermediate","Pull","Supinated","Isolation","Long Head",
                        "barbell_curl.jpg"),
                ex("Hammer Curl",
                        l("Stand holding dumbbells with a neutral (palms-in) grip.",
                                "Curl both dumbbells up maintaining the neutral grip throughout.",
                                "Squeeze at the top, then lower with control.",
                                "Neutral grip shifts emphasis toward the long head and brachialis."),
                        "Neutral Grip Curl","Beginner","Pull","Neutral","Isolation","Long Head",
                        "hammer_curl.jpg"),
                ex("Cable Curl",
                        l("Attach a straight bar to the low pulley.",
                                "Grip bar shoulder-width with an underhand grip.",
                                "Curl to shoulder height, keeping elbows at sides.",
                                "Lower slowly — cable keeps constant tension throughout."),
                        "Cable Barbell Curl","Beginner","Pull","Supinated","Isolation","Long Head",
                        "cable_curl.jpg"),
                ex("Preacher Curl",
                        l("Sit at preacher bench, rest back of upper arms on the pad.",
                                "Curl the barbell or dumbbell up toward your shoulder.",
                                "Squeeze at the top, then lower slowly to near full extension.",
                                "The pad prevents swinging, isolating the short head."),
                        "Scott Curl","Intermediate","Pull","Supinated","Isolation","Short Head",
                        "preacher_curl.jpg"),
                ex("Concentration Curl",
                        l("Sit on a bench, lean forward and brace elbow against inner thigh.",
                                "Curl dumbbell toward shoulder keeping upper arm fixed.",
                                "Squeeze hard at the top for one second.",
                                "Lower slowly and repeat, then switch arms."),
                        "","Beginner","Pull","Supinated","Isolation","Short Head",
                        "concentration_curl.jpg"),
                ex("Cable Bayesian Curl",
                        l("Attach a handle to the low pulley and face away from the machine.",
                                "Stagger your stance for balance, supinated grip.",
                                "Curl your hand toward shoulder keeping upper arm back.",
                                "Lower to full extension — cable angle keeps tension on short head."),
                        "","Beginner","Pull","Underhand","Isolation","Short Head",
                        "cable_bayesian_curl.jpg"),
                ex("Wide-Grip Barbell Curl",
                        l("Grip a barbell wider than shoulder-width with an underhand grip.",
                                "Curl the bar up keeping elbows stationary at your sides.",
                                "Wide grip rotates emphasis toward the short (inner) head.",
                                "Lower slowly back to full arm extension."),
                        "","Intermediate","Pull","Supinated","Isolation","Short Head",
                        "wide_grip_barbell_curl.jpg"),
                ex("Reverse Barbell Curl",
                        l("Hold a barbell with a pronated (overhand) grip, shoulder-width.",
                                "Curl the bar up toward shoulders keeping elbows at your sides.",
                                "Overhand grip deactivates the biceps, making brachialis the prime mover.",
                                "Lower with full control."),
                        "Reverse Curl","Intermediate","Pull","Pronated","Isolation","Brachialis",
                        "reverse_barbell_curl.jpg"),
                ex("Spider Curl",
                        l("Lie chest-down on an incline bench holding dumbbells.",
                                "Let arms hang straight down, then curl weight up.",
                                "Prone position pre-stretches the brachialis and long head.",
                                "Lower slowly to full extension."),
                        "Prone Incline Curl","Intermediate","Pull","Supinated","Isolation","Brachialis",
                        "spider_curl.jpg"),
                ex("Cross-Body Hammer Curl",
                        l("Stand holding dumbbells with a neutral grip.",
                                "Curl one dumbbell up and across your body toward the opposite shoulder.",
                                "Hold at the top, then lower slowly.",
                                "The cross-body path isolates the brachialis effectively."),
                        "","Beginner","Pull","Neutral","Isolation","Brachialis",
                        "cross_body_hammer_curl.jpg"),
                ex("Zottman Curl",
                        l("Start with dumbbells in a supinated grip and curl them up normally.",
                                "At the top, rotate wrists so palms face downward (pronated).",
                                "Lower slowly in the pronated position.",
                                "Rotate back to supinated at the bottom — hits biceps up, brachialis down."),
                        "","Intermediate","Pull","Supinated/Pronated","Isolation","Brachialis",
                        "zottman_curl.jpg")
        ));

        // ════════════════════════════ TRICEPS ══════════════════════════════════
        DB.put("Triceps", Arrays.asList(
                ex("Overhead Dumbbell Extension",
                        l("Hold one dumbbell overhead with both hands, arms extended.",
                                "Lower dumbbell behind head by bending at the elbows.",
                                "Extend arms back up, squeezing triceps at the top.",
                                "Overhead position fully stretches the long head."),
                        "French Press","Intermediate","Push","Overhand","Isolation","Long Head",
                        "overhead_dumbbell_extension.jpg"),
                ex("Cable Overhead Extension",
                        l("Attach a rope to the low pulley and face away from machine.",
                                "Hold rope overhead with both hands.",
                                "Extend arms forward until fully straight.",
                                "Slowly bend elbows back — keep upper arms stationary."),
                        "Rope Overhead Extension","Beginner","Push","Neutral","Isolation","Long Head",
                        "cable_overhead_extension.jpg"),
                ex("Incline Skull Crusher",
                        l("Lie on a slight incline bench holding an EZ-bar above chest.",
                                "Lower bar toward top of forehead by bending elbows.",
                                "Extend back to start, squeezing long head at full extension.",
                                "Incline adds more stretch to the long head vs flat."),
                        "","Intermediate","Push","Overhand","Isolation","Long Head",
                        "incline_skull_crusher.jpg"),
                ex("Tricep Dips",
                        l("Grip parallel bars with arms extended, body upright.",
                                "Lower by bending elbows until upper arms are parallel to floor.",
                                "Push back up to starting position.",
                                "Upright torso maximises tricep vs chest involvement."),
                        "Parallel Bar Dips","Advanced","Push","Neutral","Compound","Long Head",
                        "tricep_dips.jpg"),
                ex("Rope Pushdown",
                        l("Attach a rope to the top pulley of a cable machine.",
                                "Keep elbows pinned to sides and push rope down until arms are extended.",
                                "At the bottom, spread rope ends apart to maximise lateral head contraction.",
                                "Slowly return to starting position."),
                        "Cable Rope Pushdown","Beginner","Push","Neutral","Isolation","Lateral Head",
                        "rope_pushdown.jpg"),
                ex("Straight-Bar Pushdown",
                        l("Attach a straight bar to the top pulley.",
                                "Keep elbows close to sides and push bar down to full extension.",
                                "Hold at the bottom briefly, then slowly return.",
                                "Overhand grip targets the lateral head more than underhand."),
                        "Tricep Pushdown","Beginner","Push","Overhand","Isolation","Lateral Head",
                        "straight_bar_pushdown.jpg"),
                ex("Close-Grip Bench Press",
                        l("Lie on flat bench and grip barbell shoulder-width apart.",
                                "Lower bar to lower chest, keeping elbows close to body.",
                                "Press back up until arms are fully extended.",
                                "Narrower grip shifts emphasis to the triceps over the chest."),
                        "Narrow Grip Bench","Intermediate","Push","Overhand","Compound","Lateral Head",
                        "close_grip_bench_press.jpg"),
                ex("Diamond Push-Up",
                        l("Push-up position, hands together forming a diamond under chest.",
                                "Lower chest toward hands, elbows staying close to sides.",
                                "Push back up to starting position.",
                                "Narrow base emphasises the lateral head."),
                        "Triangle Push-Up","Intermediate","Push","Overhand","Compound","Lateral Head",
                        "diamond_push_up.jpg"),
                ex("Skull Crusher",
                        l("Lie on flat bench holding EZ-bar with a narrow overhand grip.",
                                "Lower bar toward forehead by bending at the elbows.",
                                "Extend arms back to starting position.",
                                "Medial head is most active in the lower portion of this movement."),
                        "Lying Tricep Extension","Intermediate","Push","Overhand","Isolation","Medial Head",
                        "skull_crusher.jpg"),
                ex("Reverse-Grip Pushdown",
                        l("Attach a straight bar to top pulley, grip with underhand (supinated) grip.",
                                "Push bar down until arms are fully extended, elbows at sides.",
                                "Slowly return to starting position.",
                                "Underhand grip activates the medial head more than overhand."),
                        "Underhand Pushdown","Beginner","Push","Underhand","Isolation","Medial Head",
                        "reverse_grip_pushdown.jpg"),
                ex("Tate Press",
                        l("Lie on bench holding dumbbells above chest, elbows flared outward.",
                                "Lower dumbbells by bending elbows, bringing them toward centre of chest.",
                                "Extend arms back up by squeezing the triceps.",
                                "Flared-elbow angle targets the medial head."),
                        "","Intermediate","Push","Overhand","Isolation","Medial Head",
                        "tate_press.jpg"),
                ex("Bench Dip",
                        l("Sit on edge of bench, hands beside hips, feet on floor.",
                                "Slide off bench and lower body by bending elbows to ~90°.",
                                "Push back up to starting position.",
                                "Keep your back close to the bench throughout."),
                        "Chair Dip","Beginner","Push","Neutral","Compound","Medial Head",
                        "bench_dip.jpg")
        ));

        // ════════════════════════════ SHOULDERS ════════════════════════════════
        DB.put("Shoulders", Arrays.asList(
                ex("Barbell Overhead Press",
                        l("Stand feet shoulder-width apart, barbell at shoulder height.",
                                "Press bar straight overhead until arms fully extended.",
                                "Lower back to shoulder height with control.",
                                "Keep core braced, avoid arching lower back."),
                        "Military Press","Intermediate","Push","Overhand","Compound","Front Delt",
                        "barbell_overhead_press.jpg"),
                ex("Dumbbell Front Raise",
                        l("Stand holding dumbbells in front of thighs, palms facing down.",
                                "Raise both arms straight out in front to shoulder height.",
                                "Hold briefly at the top, then lower slowly.",
                                "Avoid swinging — use a controlled tempo."),
                        "","Beginner","Push","Overhand","Isolation","Front Delt",
                        "dumbbell_front_raise.jpg"),
                ex("Arnold Press",
                        l("Sit holding dumbbells at shoulder height with palms facing you.",
                                "As you press overhead, rotate palms outward so they face forward at top.",
                                "Lower back while rotating palms back to starting position.",
                                "Rotation increases front delt activation through the full range."),
                        "","Intermediate","Push","Rotating","Compound","Front Delt",
                        "arnold_press.jpg"),
                ex("Cable Front Raise",
                        l("Attach handle to low pulley and stand facing away from machine.",
                                "Hold handle with one hand and raise arm straight out to shoulder height.",
                                "Lower slowly and repeat, then switch arms.",
                                "Cable provides constant tension unlike a dumbbell."),
                        "","Beginner","Push","Overhand","Isolation","Front Delt",
                        "cable_front_raise.jpg"),
                ex("Dumbbell Lateral Raise",
                        l("Stand holding dumbbells at sides, palms facing inward.",
                                "Raise both arms out to the sides to shoulder height.",
                                "Lead with your elbows, keep a slight bend in them throughout.",
                                "Lower slowly — eccentric is key for side delt growth."),
                        "Side Lateral Raise","Beginner","Push","Neutral","Isolation","Side Delt",
                        "dumbbell_lateral_raise.jpg"),
                ex("Cable Lateral Raise",
                        l("Stand beside a cable machine, pulley set to lowest position.",
                                "Grab handle with far hand and raise arm out to shoulder height.",
                                "Cable keeps constant tension unlike a dumbbell at the bottom.",
                                "Lower slowly and repeat, then switch sides."),
                        "","Beginner","Push","Neutral","Isolation","Side Delt",
                        "cable_lateral_raise.jpg"),
                ex("Seated Dumbbell Lateral Raise",
                        l("Sit on the edge of a bench holding dumbbells at your sides.",
                                "Raise both arms out to shoulder height, leading with elbows.",
                                "Sitting removes any chance of body swinging for stricter reps.",
                                "Lower with full control."),
                        "Seated Lateral Raise","Beginner","Push","Neutral","Isolation","Side Delt",
                        "seated_dumbbell_lateral_raise.jpg"),
                ex("Upright Row",
                        l("Stand holding barbell with narrow overhand grip in front of thighs.",
                                "Pull weight straight up along your body, leading with elbows.",
                                "Raise until bar reaches chin height, elbows above the bar.",
                                "Lower slowly back to starting position."),
                        "","Intermediate","Pull","Overhand","Compound","Side Delt",
                        "upright_row.jpg"),
                ex("Face Pull",
                        l("Attach rope to high pulley and grasp both ends overhand.",
                                "Pull rope toward face, flaring elbows out wide.",
                                "Hold peak contraction briefly to maximise rear delt activation.",
                                "Slowly return — this exercise also helps shoulder health."),
                        "","Beginner","Pull","Overhand","Isolation","Rear Delt",
                        "face_pull.jpg"),
                ex("Reverse Dumbbell Fly",
                        l("Sit on edge of bench, lean forward, hold dumbbells below chest.",
                                "Raise both arms out to the sides to shoulder height.",
                                "Squeeze rear delts at the top of the movement.",
                                "Lower with control — do not swing."),
                        "Bent Over Lateral Raise","Beginner","Pull","Neutral","Isolation","Rear Delt",
                        "reverse_dumbbell_fly.jpg"),
                ex("Reverse Pec Deck",
                        l("Sit facing the pec deck machine and grip handles with arms extended.",
                                "Pull both handles back in an arc as far as possible.",
                                "Hold briefly at end range to activate rear delts.",
                                "Slowly return to starting position."),
                        "Reverse Fly Machine","Beginner","Pull","Neutral","Isolation","Rear Delt",
                        "reverse_pec_deck.jpg"),
                ex("Band Pull-Apart",
                        l("Hold a resistance band in front at shoulder height, hands shoulder-width.",
                                "Pull band apart by moving both hands out to the sides until fully extended.",
                                "Squeeze rear delts and upper back at the end of the movement.",
                                "Slowly return to starting position."),
                        "","Beginner","Pull","Overhand","Isolation","Rear Delt",
                        "band_pull_apart.jpg")
        ));

        // ════════════════════════════ THIGHS ═══════════════════════════════════
        DB.put("Quads", Arrays.asList(
                ex("Barbell Back Squat",
                        l("Bar on upper traps, feet shoulder-width apart.",
                                "Break at hips and knees simultaneously, lower until thighs are parallel.",
                                "Drive through whole foot to return to standing.",
                                "Chest up, knees tracking over toes throughout."),
                        "Back Squat","Intermediate","Push","Overhand","Compound","Quads",
                        "barbell_back_squat.jpg"),
                ex("Leg Press",
                        l("Sit in leg press machine, feet shoulder-width on platform.",
                                "Lower platform by bending knees to ~90°.",
                                "Press back up without locking out knees.",
                                "Foot position lower on platform = more quad emphasis."),
                        "","Beginner","Push","N/A","Compound","Quads",
                        "leg_press.jpg"),
                ex("Leg Extension",
                        l("Sit in machine, pad just above ankles.",
                                "Extend legs until fully straight.",
                                "Hold briefly at top to squeeze quads.",
                                "Lower slowly for full eccentric contraction."),
                        "","Beginner","Push","N/A","Isolation","Quads",
                        "leg_extension.jpg"),
                ex("Hack Squat",
                        l("Shoulders under pads of hack squat machine, feet forward on platform.",
                                "Lower until thighs at or below parallel.",
                                "Push through whole foot to return to start.",
                                "Machine angle places strong emphasis on quads."),
                        "","Intermediate","Push","N/A","Compound","Quads",
                        "hack_squat.jpg"),
                ex("Romanian Deadlift",
                        l("Stand holding barbell, feet hip-width apart.",
                                "Hinge at hips with flat back, lower bar along legs.",
                                "Feel a deep hamstring stretch at the bottom.",
                                "Drive hips forward to return to standing."),
                        "RDL","Intermediate","Pull","Overhand","Compound","Hamstrings",
                        "romanian_deadlift.jpg"),
                ex("Lying Leg Curl",
                        l("Lie face-down on machine, pad just above heels.",
                                "Curl legs up toward glutes as far as possible.",
                                "Squeeze hamstrings hard at the top.",
                                "Lower slowly back to starting position."),
                        "","Beginner","Pull","N/A","Isolation","Hamstrings",
                        "lying_leg_curl.jpg"),
                ex("Nordic Hamstring Curl",
                        l("Kneel on a mat and anchor feet under a fixed surface.",
                                "Lower body toward floor as slowly as possible using hamstrings.",
                                "Use hands to push off the floor at the bottom if needed.",
                                "Pull yourself back up with your hamstrings."),
                        "","Advanced","Pull","N/A","Isolation","Hamstrings",
                        "nordic_hamstring_curl.jpg"),
                ex("Seated Leg Curl",
                        l("Sit in machine, pad across lower shins.",
                                "Curl legs down under the seat as far as possible.",
                                "Hold briefly at end range.",
                                "Slowly return to starting position."),
                        "","Beginner","Pull","N/A","Isolation","Hamstrings",
                        "seated_leg_curl.jpg"),
                ex("Sumo Squat",
                        l("Feet much wider than shoulder-width, toes angled out ~45°.",
                                "Lower until thighs are parallel to the floor.",
                                "Push through heels to return to standing.",
                                "Wide stance engages the adductors throughout."),
                        "Wide Stance Squat","Beginner","Push","N/A","Compound","Inner Quads",
                        "sumo_squat.jpg"),
                ex("Cable Hip Adduction",
                        l("Attach cuff to ankle and stand sideways to cable machine.",
                                "Pull leg across body toward and past the standing leg.",
                                "Hold briefly at peak contraction.",
                                "Slowly return to starting position."),
                        "Hip Adduction","Intermediate","Pull","N/A","Isolation","Inner Quads",
                        "cable_hip_adduction.jpg"),
                ex("Adductor Machine",
                        l("Sit in machine with pads against inner sides of knees.",
                                "Squeeze both legs together against the resistance.",
                                "Hold briefly at end range.",
                                "Slowly return to starting position."),
                        "Inner Thigh Machine","Beginner","Pull","N/A","Isolation","Inner Quads",
                        "adductor_machine.jpg"),
                ex("Dumbbell Sumo Squat",
                        l("Stand with feet wide, hold one dumbbell vertically between legs.",
                                "Lower into sumo squat until thighs are parallel or below.",
                                "Drive up through heels.",
                                "Dumbbell adds load to the adductor-dominant squat pattern."),
                        "Goblet Sumo Squat","Beginner","Push","Neutral","Compound","Inner Quads",
                        "dumbbell_sumo_squat.jpg"),
                ex("Walking Lunges",
                        l("Stand upright holding dumbbells at sides.",
                                "Step forward with one leg, lower back knee toward the floor.",
                                "Push through front heel to bring back leg forward.",
                                "Continue alternating legs across the floor."),
                        "Dumbbell Lunge","Beginner","Push","Neutral","Compound","Outer Quads",
                        "walking_lunges.jpg"),
                ex("Cable Hip Abduction",
                        l("Attach cuff to ankle and stand beside cable machine.",
                                "Lift leg out to the side away from machine as far as comfortable.",
                                "Hold briefly at the peak, then lower slowly.",
                                "Keep torso upright throughout."),
                        "Hip Abduction","Beginner","Push","N/A","Isolation","Outer Quads",
                        "cable_hip_abduction.jpg"),
                ex("Abductor Machine",
                        l("Sit in machine with pads against outer knees.",
                                "Push both legs apart against the resistance.",
                                "Hold briefly at the widest point.",
                                "Slowly return to starting position."),
                        "Outer Thigh Machine","Beginner","Push","N/A","Isolation","Outer Quads",
                        "abductor_machine.jpg"),
                ex("Side-Lying Leg Raise",
                        l("Lie on your side, body in a straight line.",
                                "Raise the top leg as high as comfortable.",
                                "Hold briefly at the top.",
                                "Lower slowly and repeat, then switch sides."),
                        "","Beginner","Push","N/A","Isolation","Outer Quads",
                        "side_lying_leg_raise.jpg")
        ));

        // ════════════════════════════ ABS ══════════════════════════════════════
        DB.put("Abs", Arrays.asList(
                ex("Crunch",
                        l("Lie on your back, knees bent, hands behind head.",
                                "Curl shoulders off the floor toward your knees.",
                                "Hold briefly at the top, squeezing upper abs.",
                                "Lower slowly back to starting position."),
                        "","Beginner","Pull","N/A","Isolation","Upper Abs",
                        "crunch.jpg"),
                ex("Cable Crunch",
                        l("Kneel in front of a cable machine with rope attached to high pulley.",
                                "Hold rope at sides of head and crunch down toward the floor.",
                                "Squeeze abs hard at the bottom.",
                                "Slowly return to starting position."),
                        "","Intermediate","Pull","N/A","Isolation","Upper Abs",
                        "cable_crunch.jpg"),
                ex("Ab Wheel Rollout",
                        l("Kneel on the floor holding the ab wheel in front of you.",
                                "Roll the wheel forward as far as you can while keeping your back flat.",
                                "Use your abs to pull yourself back to the starting position.",
                                "Do not let your hips sag."),
                        "","Advanced","Pull","N/A","Compound","Upper Abs",
                        "ab_wheel_rollout.jpg"),
                ex("Sit-Up",
                        l("Lie on your back, knees bent, feet flat on floor.",
                                "Curl your entire torso up until you are sitting upright.",
                                "Lower back to the floor with control.",
                                "You can anchor feet under something for stability."),
                        "","Beginner","Pull","N/A","Compound","Upper Abs",
                        "sit_up.jpg"),
                ex("Hanging Leg Raise",
                        l("Hang from a pull-up bar with arms fully extended.",
                                "Raise legs up until they are parallel to the floor (or higher).",
                                "Squeeze lower abs at the top.",
                                "Lower legs slowly without swinging."),
                        "","Intermediate","Pull","N/A","Isolation","Lower Abs",
                        "hanging_leg_raise.jpg"),
                ex("Reverse Crunch",
                        l("Lie on your back, legs raised to 90°.",
                                "Curl hips off the floor by pulling knees toward chest.",
                                "Hold briefly at the top, squeezing lower abs.",
                                "Lower hips slowly back to the floor."),
                        "","Beginner","Pull","N/A","Isolation","Lower Abs",
                        "reverse_crunch.jpg"),
                ex("Leg Raise",
                        l("Lie flat on your back, arms at sides.",
                                "Raise both legs to 90° keeping them straight.",
                                "Lower slowly back down — do not let feet touch the floor.",
                                "The lower the legs, the harder the lower abs work."),
                        "","Beginner","Pull","N/A","Isolation","Lower Abs",
                        "leg_raise.jpg"),
                ex("Dragon Flag",
                        l("Lie on a bench, grab the bench behind your head.",
                                "Raise your entire body off the bench into a straight line, supported only at shoulders.",
                                "Lower the body slowly, keeping it rigid throughout.",
                                "This is an advanced movement — build up to it progressively."),
                        "","Advanced","Pull","N/A","Compound","Lower Abs",
                        "dragon_flag.jpg"),
                ex("Russian Twist",
                        l("Sit on floor, lean back slightly, feet off floor, knees bent.",
                                "Hold a weight plate or dumbbell with both hands.",
                                "Rotate your torso left and right, touching the weight to the floor each side.",
                                "Keep your feet off the floor throughout."),
                        "","Beginner","Pull","N/A","Isolation","Obliques",
                        "russian_twist.jpg"),
                ex("Side Plank",
                        l("Lie on your side and prop yourself up on one forearm.",
                                "Lift your hips off the floor so your body forms a straight line.",
                                "Hold the position without letting hips drop.",
                                "Repeat on the other side."),
                        "","Beginner","Static","N/A","Isolation","Obliques",
                        "side_plank.jpg"),
                ex("Bicycle Crunch",
                        l("Lie on your back, hands behind head, legs raised off floor.",
                                "Bring one knee toward chest while rotating opposite elbow toward it.",
                                "Alternate sides in a pedalling motion.",
                                "Keep lower back pressed to the floor throughout."),
                        "","Beginner","Pull","N/A","Isolation","Obliques",
                        "bicycle_crunch.jpg"),
                ex("Cable Woodchop",
                        l("Set cable pulley to high position and stand sideways to the machine.",
                                "Pull the handle diagonally downward and across your body.",
                                "Rotate through your core and hips.",
                                "Slowly return to start and repeat, then switch sides."),
                        "","Intermediate","Pull","Neutral","Isolation","Obliques",
                        "cable_woodchop.jpg")
        ));

        // ════════════════════════════ BUTT ═════════════════════════════════════
        DB.put("Butt", Arrays.asList(
                ex("Barbell Hip Thrust",
                        l("Sit on floor, upper back against a bench, barbell across hips.",
                                "Drive hips up until body forms a straight line from knees to shoulders.",
                                "Squeeze glutes hard at the top.",
                                "Lower hips back toward floor and repeat."),
                        "","Intermediate","Push","Overhand","Compound","Glute Max",
                        "barbell_hip_thrust.jpg"),
                ex("Glute Bridge",
                        l("Lie on your back, knees bent, feet flat on floor.",
                                "Drive hips up by squeezing glutes.",
                                "Hold at the top for one second.",
                                "Lower hips back to the floor and repeat."),
                        "","Beginner","Push","N/A","Isolation","Glute Max",
                        "glute_bridge.jpg"),
                ex("Bulgarian Split Squat",
                        l("Stand in front of a bench and place back foot on it.",
                                "Lower your body by bending the front knee toward the floor.",
                                "Drive through the front heel to return to standing.",
                                "Complete all reps before switching legs."),
                        "Rear Foot Elevated Split Squat","Intermediate","Push","N/A","Compound","Glute Max",
                        "bulgarian_split_squat.jpg"),
                ex("Cable Kickback",
                        l("Attach ankle cuff to low cable and stand facing the machine.",
                                "Kick the leg straight back as far as possible, squeezing the glute.",
                                "Hold briefly at the top.",
                                "Lower slowly and repeat, then switch legs."),
                        "","Beginner","Push","N/A","Isolation","Glute Max",
                        "cable_kickback.jpg"),
                ex("Lateral Band Walk",
                        l("Place a resistance band just above your knees.",
                                "Stand in a slight squat, feet shoulder-width apart.",
                                "Step sideways, maintaining tension on the band.",
                                "Continue for desired reps then switch direction."),
                        "Monster Walk","Beginner","Push","N/A","Isolation","Glute Med",
                        "lateral_band_walk.jpg"),
                ex("Single-Leg Hip Thrust",
                        l("Upper back against bench, extend one leg straight out.",
                                "Drive hips up using only the planted leg.",
                                "Squeeze glute hard at the top.",
                                "Complete all reps before switching legs."),
                        "","Intermediate","Push","N/A","Isolation","Glute Med",
                        "single_leg_hip_thrust.jpg"),
                ex("Hip Abduction Machine",
                        l("Sit in machine, pads against outer knees.",
                                "Push both legs apart against the resistance.",
                                "Hold briefly at the widest point.",
                                "Slowly return to starting position."),
                        "","Beginner","Push","N/A","Isolation","Glute Med",
                        "hip_abduction_machine.jpg"),
                ex("Clamshell",
                        l("Lie on side, hips and knees at 45°, band just above knees.",
                                "Keeping feet together, rotate top knee upward as far as possible.",
                                "Hold briefly to squeeze the deep glute.",
                                "Lower slowly and repeat, then switch sides."),
                        "","Beginner","Push","N/A","Isolation","Glute Min",
                        "clamshell.jpg"),
                ex("Fire Hydrant",
                        l("Start on all fours, hands under shoulders, knees under hips.",
                                "Lift one knee out to the side to hip height, knee bent at 90°.",
                                "Hold briefly at top, then lower slowly.",
                                "Repeat on both sides."),
                        "","Beginner","Push","N/A","Isolation","Glute Min",
                        "fire_hydrant.jpg"),
                ex("Side-Lying Hip Abduction",
                        l("Lie on side, legs straight, one on top of the other.",
                                "Lift top leg upward as high as comfortable.",
                                "Hold briefly at top, then lower slowly.",
                                "Add a resistance band for extra difficulty."),
                        "","Beginner","Push","N/A","Isolation","Glute Min",
                        "side_lying_hip_abduction.jpg"),
                ex("Standing Hip Circle",
                        l("Stand holding a wall for balance, band just above ankles.",
                                "Lift one leg and draw large circles outward leading with the heel.",
                                "Perform 10 circles forward then 10 backward.",
                                "Switch legs and repeat."),
                        "","Beginner","Push","N/A","Isolation","Glute Min",
                        "standing_hip_circle.jpg")
        ));

        // ════════════════════════════ CALVES ═══════════════════════════════════
        DB.put("Calves", Arrays.asList(
                ex("Standing Calf Raise",
                        l("Balls of feet on a raised platform, heels hanging off.",
                                "Rise up onto toes as high as possible.",
                                "Hold at top for one second.",
                                "Lower heels below the platform for a full stretch."),
                        "","Beginner","Push","N/A","Isolation","Gastrocnemius",
                        "standing_calf_raise.jpg"),
                ex("Donkey Calf Raise",
                        l("Bend forward at hips on a platform, balls of feet on edge.",
                                "Have a partner sit on lower back or use machine for resistance.",
                                "Rise onto toes as high as possible.",
                                "Lower heels below platform for full range."),
                        "","Advanced","Push","N/A","Isolation","Gastrocnemius",
                        "donkey_calf_raise.jpg"),
                ex("Barbell Standing Calf Raise",
                        l("Barbell on upper traps, balls of feet on a platform.",
                                "Rise onto toes as high as possible.",
                                "Hold peak contraction briefly.",
                                "Lower heels below platform for full stretch."),
                        "","Intermediate","Push","Overhand","Isolation","Gastrocnemius",
                        "barbell_standing_calf_raise.jpg"),
                ex("Smith Machine Calf Raise",
                        l("Bar on traps at shoulder height, balls of feet on a plate.",
                                "Unrack and rise onto toes.",
                                "Hold at top, then lower heels below the plate.",
                                "Smith machine provides stability for heavy loading."),
                        "","Intermediate","Push","Overhand","Isolation","Gastrocnemius",
                        "smith_machine_calf_raise.jpg"),
                ex("Seated Calf Raise",
                        l("Sit in seated calf raise machine, pad on lower thighs.",
                                "Lift heels as high as possible.",
                                "Hold at top briefly.",
                                "Lower slowly — the seated position isolates the soleus."),
                        "","Beginner","Push","N/A","Isolation","Soleus",
                        "seated_calf_raise.jpg"),
                ex("Leg Press Calf Raise",
                        l("Sit in leg press machine, balls of feet on lower platform edge.",
                                "Push through balls of feet to fully extend ankles.",
                                "Hold at top briefly.",
                                "Lower slowly for a full soleus stretch."),
                        "","Beginner","Push","N/A","Isolation","Soleus",
                        "leg_press_calf_raise.jpg"),
                ex("Seated Dumbbell Calf Raise",
                        l("Sit on bench, dumbbells on lower thighs for resistance.",
                                "Rise onto balls of feet as high as possible.",
                                "Hold briefly at top.",
                                "Lower heels to a slight stretch and repeat."),
                        "","Beginner","Push","N/A","Isolation","Soleus",
                        "seated_dumbbell_calf_raise.jpg"),
                ex("Banded Seated Calf Raise",
                        l("Sit on bench with resistance band looped over knees and under a fixed point.",
                                "Press balls of feet down against the band resistance.",
                                "Hold briefly at end range.",
                                "Return slowly to starting position."),
                        "","Beginner","Push","N/A","Isolation","Soleus",
                        "banded_seated_calf_raise.jpg")
        ));

        // ════════════════════════════ FOREARMS ═════════════════════════════════
        DB.put("Forearms", Arrays.asList(
                ex("Barbell Wrist Curl",
                        l("Sit with forearms on thighs, palms up, holding a barbell.",
                                "Lower bar by extending wrists toward the floor.",
                                "Curl bar back up by fully flexing wrists.",
                                "Use full range of motion for maximum flexor development."),
                        "","Beginner","Pull","Supinated","Isolation","Wrist Flexors",
                        "barbell_wrist_curl.jpg"),
                ex("Dumbbell Wrist Curl",
                        l("Sit with one forearm on thigh, palm up, holding a dumbbell.",
                                "Let dumbbell roll down to fingertips.",
                                "Curl fingers and wrist up as high as possible.",
                                "Lower slowly and repeat, then switch arms."),
                        "","Beginner","Pull","Supinated","Isolation","Wrist Flexors",
                        "dumbbell_wrist_curl.jpg"),
                ex("Cable Wrist Curl",
                        l("Kneel in front of low cable machine, hold handle with palm up.",
                                "Rest forearm on thigh for stability.",
                                "Curl your wrist upward as far as possible.",
                                "Lower slowly — cable provides constant tension."),
                        "","Beginner","Pull","Supinated","Isolation","Wrist Flexors",
                        "cable_wrist_curl.jpg"),
                ex("Behind-the-Back Wrist Curl",
                        l("Stand holding barbell behind you, palms facing back.",
                                "Curl wrists upward, rolling bar up your fingers.",
                                "Lower slowly back to starting position.",
                                "This angle provides a unique flexor stretch."),
                        "","Intermediate","Pull","Supinated","Isolation","Wrist Flexors",
                        "behind_the_back_wrist_curl.jpg"),
                ex("Reverse Wrist Curl",
                        l("Sit with forearms on thighs, palms facing down, holding barbell.",
                                "Raise bar by extending wrists upward.",
                                "Hold briefly at the top.",
                                "Lower slowly to starting position."),
                        "","Beginner","Pull","Pronated","Isolation","Wrist Extensors",
                        "reverse_wrist_curl.jpg"),
                ex("Dumbbell Reverse Wrist Curl",
                        l("Sit with forearm on thigh, palm facing down, holding dumbbell.",
                                "Raise dumbbell by extending wrist as high as possible.",
                                "Hold briefly at the top.",
                                "Lower slowly and repeat, then switch arms."),
                        "","Beginner","Pull","Pronated","Isolation","Wrist Extensors",
                        "dumbbell_reverse_wrist_curl.jpg"),
                ex("Plate Pinch",
                        l("Hold two weight plates together between fingers and thumb, smooth sides out.",
                                "Hold for as long as possible without dropping.",
                                "Trains wrist extensors and finger extensors.",
                                "Rest and repeat for desired sets."),
                        "","Intermediate","Static","N/A","Isolation","Wrist Extensors",
                        "plate_pinch.jpg"),
                ex("Towel Wrist Extension",
                        l("Hold a rolled-up towel at arm's length, palms down.",
                                "Wring the towel by alternating wrist extension on each hand.",
                                "Continue for 30–60 seconds per set.",
                                "Great for developing extensor endurance."),
                        "","Beginner","Static","N/A","Isolation","Wrist Extensors",
                        "towel_wrist_extension.jpg"),
                ex("Hammer Curl",
                        l("Stand holding dumbbells with a neutral (palms-in) grip.",
                                "Curl both dumbbells up maintaining the neutral grip.",
                                "Neutral grip maximally activates the brachioradialis.",
                                "Lower with full control."),
                        "Neutral Curl","Beginner","Pull","Neutral","Isolation","Brachioradialis",
                        "hammer_curl.jpg"),
                ex("Reverse Barbell Curl",
                        l("Hold barbell with a pronated (overhand) grip, shoulder-width.",
                                "Curl bar up toward shoulders, elbows at sides.",
                                "Overhand grip shifts load to the brachioradialis.",
                                "Lower slowly back to full arm extension."),
                        "Reverse Curl","Intermediate","Pull","Pronated","Isolation","Brachioradialis",
                        "reverse_barbell_curl.jpg"),
                ex("Zottman Curl",
                        l("Start with dumbbells in a supinated grip and curl them up.",
                                "At top, rotate wrists so palms face downward.",
                                "Lower slowly in the pronated position.",
                                "Rotate back to supinated at bottom — hits brachioradialis on the way down."),
                        "","Intermediate","Pull","Supinated/Pronated","Isolation","Brachioradialis",
                        "zottman_curl.jpg"),
                ex("Cross-Body Hammer Curl",
                        l("Stand holding dumbbells with neutral grip.",
                                "Curl one dumbbell up and across body toward opposite shoulder.",
                                "Hold at top, then lower slowly.",
                                "Cross-body path effectively isolates the brachioradialis."),
                        "","Beginner","Pull","Neutral","Isolation","Brachioradialis",
                        "cross_body_hammer_curl.jpg")
        ));

        // ════════════════════════════ NECK ═════════════════════════════════════
        DB.put("Neck", Arrays.asList(
                ex("Neck Flexion — Manual Resistance",
                        l("Sit upright and place one hand on your forehead.",
                                "Push head forward while hand provides gentle resistance.",
                                "Hold briefly at end range.",
                                "Return to neutral slowly and repeat."),
                        "","Beginner","Pull","N/A","Isolation","Neck Flexors",
                        "neck_flexion_manual.jpg"),
                ex("Plate-Loaded Neck Flexion",
                        l("Lie face-up on bench, head hanging off the edge.",
                                "Place a small weight plate on forehead, hold with both hands.",
                                "Curl chin toward chest against plate's resistance.",
                                "Lower slowly back to starting position."),
                        "","Intermediate","Pull","N/A","Isolation","Neck Flexors",
                        "plate_loaded_neck_flexion.jpg"),
                ex("Neck Harness Flexion",
                        l("Attach neck harness and connect a light cable or weight.",
                                "Sit upright and nod head forward against the resistance.",
                                "Control the return to neutral.",
                                "Keep resistance light — neck muscles are small."),
                        "","Intermediate","Pull","N/A","Isolation","Neck Flexors",
                        "neck_harness_flexion.jpg"),
                ex("Neck Extension — Manual Resistance",
                        l("Sit upright and place both hands on back of head.",
                                "Push head backward while hands provide gentle resistance.",
                                "Hold briefly at end range.",
                                "Return to neutral slowly."),
                        "","Beginner","Push","N/A","Isolation","Neck Extensors",
                        "neck_extension_manual.jpg"),
                ex("Plate-Loaded Neck Extension",
                        l("Lie face-down on bench, head hanging off the edge.",
                                "Place weight plate on back of head, hold with both hands.",
                                "Raise head up against plate's resistance.",
                                "Lower slowly back to neutral."),
                        "","Intermediate","Push","N/A","Isolation","Neck Extensors",
                        "plate_loaded_neck_extension.jpg"),
                ex("Neck Harness Extension",
                        l("Attach neck harness and connect a light weight.",
                                "Face down or sit bent over and raise head against resistance.",
                                "Hold briefly at top.",
                                "Lower with control."),
                        "","Intermediate","Push","N/A","Isolation","Neck Extensors",
                        "neck_harness_extension.jpg"),
                ex("Neck Lateral Flexion — Manual Resistance",
                        l("Sit upright and place one hand on side of head.",
                                "Tilt head toward shoulder against hand's resistance.",
                                "Hold briefly at end range.",
                                "Return to neutral and repeat on other side."),
                        "","Beginner","Pull","N/A","Isolation","Neck Lateral",
                        "neck_lateral_flexion_manual.jpg"),
                ex("Side Neck Stretch with Band",
                        l("Anchor a light resistance band at head height and hold beside head.",
                                "Tilt head away from anchor point against the band's pull.",
                                "Hold briefly, then return to neutral.",
                                "Repeat on the other side."),
                        "","Beginner","Pull","N/A","Isolation","Neck Lateral",
                        "side_neck_stretch_band.jpg"),
                ex("Neck Rotation",
                        l("Sit upright and place one hand on side of jaw.",
                                "Rotate head to one side against gentle resistance from hand.",
                                "Return to neutral and repeat to the other side.",
                                "Slow and controlled — never force range of motion."),
                        "","Beginner","Pull","N/A","Isolation","Neck Lateral",
                        "neck_rotation.jpg")
        ));

        // ════════════════════════════ TRAPS ════════════════════════════════════
        DB.put("Traps", Arrays.asList(
            // Upper Traps
            ex("Barbell Shrug",
               l("Stand holding barbell in front with overhand grip, arms straight.",
                 "Shrug shoulders straight up as high as possible.",
                 "Hold at the top for one second to peak the upper traps.",
                 "Lower slowly and repeat."),
               "","Beginner","Pull","Overhand","Isolation","Upper Traps","barbell_shrug.jpg"),
            ex("Dumbbell Shrug",
               l("Stand holding dumbbells at your sides, palms facing inward.",
                 "Shrug both shoulders upward as high as possible.",
                 "Hold briefly at the top, then lower with control.",
                 "Keep arms straight — do not bend the elbows."),
               "","Beginner","Pull","Neutral","Isolation","Upper Traps","dumbbell_shrug.jpg"),
            ex("Smith Machine Shrug",
               l("Set bar at mid-thigh height on Smith machine.",
                 "Grip bar shoulder-width, unrack and shrug straight up.",
                 "Hold peak contraction briefly.",
                 "Machine path keeps the bar stable for heavier loading."),
               "","Intermediate","Pull","Overhand","Isolation","Upper Traps","smith_machine_shrug.jpg"),

            // Mid Traps
            ex("Face Pull",
               l("Attach rope to high pulley and grasp both ends overhand.",
                 "Pull rope toward face, flaring elbows out wide.",
                 "Hold peak contraction briefly to squeeze mid traps and rear delts.",
                 "Slowly return to start."),
               "","Beginner","Pull","Overhand","Isolation","Mid Traps","face_pull.jpg"),
            ex("Seated Cable Row (Wide Grip)",
               l("Sit at cable row station with wide bar attachment.",
                 "Pull the bar toward your upper chest, elbows flared wide.",
                 "Squeeze shoulder blades hard at end of movement.",
                 "Slowly extend arms back to start."),
               "Wide Cable Row","Beginner","Pull","Overhand","Compound","Mid Traps","seated_cable_row_wide_grip.jpg"),
            ex("Band Pull-Apart",
               l("Hold a resistance band in front at shoulder height.",
                 "Pull band apart by moving both hands out to the sides.",
                 "Squeeze mid traps and rear delts at full extension.",
                 "Slowly return to starting position."),
               "","Beginner","Pull","Overhand","Isolation","Mid Traps","band_pull_apart.jpg"),

            // Lower Traps
            ex("Prone Y Raise",
               l("Lie face-down on an incline bench or flat on the floor.",
                 "Hold light dumbbells with thumbs pointing up.",
                 "Raise both arms in a Y shape overhead, squeezing lower traps.",
                 "Lower slowly — this is a small movement, use very light weight."),
               "","Beginner","Pull","Neutral","Isolation","Lower Traps","Prone-Y-Raise.gif"),
            ex("Straight-Arm Pulldown",
               l("Stand at cable machine with bar set high, grip with both hands.",
                 "Keeping arms straight, pull bar down toward your thighs.",
                 "Squeeze lower traps and lats at the bottom.",
                 "Slowly return to starting position."),
               "","Intermediate","Pull","Overhand","Isolation","Lower Traps","straight_arm_pulldown.jpg"),
            ex("Scapular Wall Slide",
               l("Stand with back against a wall, arms at 90° goalpost position.",
                 "Slide arms upward along the wall, keeping contact throughout.",
                 "Lower back to start, focusing on pulling shoulder blades down.",
                 "Activates and strengthens the lower trapezius directly."),
               "","Beginner","Pull","N/A","Isolation","Lower Traps","Wall-Slides.jpg")
        ));
    }

    // ── Constructor ───────────────────────────────────────────────────────────

    public MuscleGroupPanel(String muscle, Stage stage) {
        this.muscle = muscle;
        this.stage  = stage;
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

        stage.setTitle("TaraGym — " + muscle);
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
        logo.setTextFill(Color.web(ACCENT));

        Label sep = new Label("  |  ");
        sep.setTextFill(Color.web(TEXT_SECONDARY));

        Label page = new Label(muscle);
        page.setFont(Font.font("SansSerif", FontWeight.NORMAL, 14));
        page.setTextFill(Color.web(TEXT_PRIMARY));

        bar.getChildren().addAll(logo, sep, page);
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

        // ── Back button ───────────────────────────────────────────────────────
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

        // ── Header ────────────────────────────────────────────────────────────
        Label header = new Label(muscle + " Exercises");
        header.setFont(Font.font("SansSerif", FontWeight.BOLD, 26));
        header.setTextFill(Color.web(TEXT_PRIMARY));
        header.setPadding(new Insets(0, 0, 4, 0));

        Label sub = new Label("Filter by region and difficulty level.");
        sub.setFont(Font.font("SansSerif", 13));
        sub.setTextFill(Color.web(TEXT_SECONDARY));
        sub.setPadding(new Insets(0, 0, 16, 0));

        content.getChildren().addAll(header, sub);

        // ── Region tabs ───────────────────────────────────────────────────────
        List<String> regions = new ArrayList<>();
        regions.add("All");
        if (REGIONS.containsKey(muscle)) regions.addAll(REGIONS.get(muscle));

        List<Button> tabBtns = new ArrayList<>();
        HBox tabBar = new HBox(8);
        tabBar.setPadding(new Insets(0, 0, 10, 0));
        tabBar.setAlignment(Pos.CENTER_LEFT);

        for (String region : regions) {
            boolean isFirst = region.equals("All");
            Button tab = new Button(region);
            tab.setStyle(tabStyle(isFirst));
            tabBtns.add(tab);
            tab.setOnAction(e -> {
                tabBtns.forEach(b -> b.setStyle(tabStyle(false)));
                tab.setStyle(tabStyle(true));
                activeRegion = region;
                refreshExercises();
            });
            tab.setOnMouseEntered(e -> {
                if (!tab.getStyle().contains("E63946")) tab.setStyle(tabStyleHover());
            });
            tab.setOnMouseExited(e -> {
                if (!tab.getStyle().contains("E63946")) tab.setStyle(tabStyle(false));
            });
            tabBar.getChildren().add(tab);
        }

        content.getChildren().add(tabBar);

        // ── Difficulty filter buttons ─────────────────────────────────────────
        HBox diffRow = buildDifficultyFilterRow();
        diffRow.setPadding(new Insets(0, 0, 18, 0));
        content.getChildren().add(diffRow);

        // ── Exercise list ─────────────────────────────────────────────────────
        exerciseListContainer = new VBox(0);
        refreshExercises();
        content.getChildren().add(exerciseListContainer);

        return content;
    }

    // ── DIFFICULTY FILTER ROW ─────────────────────────────────────────────────

    private List<Button> diffBtns = new ArrayList<>();

    private HBox buildDifficultyFilterRow() {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        Label filterLabel = new Label("Difficulty:");
        filterLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        filterLabel.setTextFill(Color.web(TEXT_SECONDARY));
        filterLabel.setPadding(new Insets(0, 4, 0, 0));
        row.getChildren().add(filterLabel);

        // ── Check if this muscle group has any Advanced exercises ─────────────
        List<Exercise> allExercises = DB.getOrDefault(muscle, Collections.emptyList());
        boolean hasAdvanced = allExercises.stream().anyMatch(e -> "Advanced".equals(e.difficulty));

        // Build option list dynamically — omit Advanced if none exist
        String[][] diffOptions = hasAdvanced ? new String[][] {
                {"All",          "#A8B2C1", "#2a2d30"},
                {"Beginner",     DIFF_BEGINNER_COLOR,     "#0d2b1a"},
                {"Intermediate", DIFF_INTERMEDIATE_COLOR, "#2b1e0a"},
                {"Advanced",     DIFF_ADVANCED_COLOR,     "#2b0a0d"}
        } : new String[][] {
                {"All",          "#A8B2C1", "#2a2d30"},
                {"Beginner",     DIFF_BEGINNER_COLOR,     "#0d2b1a"},
                {"Intermediate", DIFF_INTERMEDIATE_COLOR, "#2b1e0a"}
        };

        diffBtns.clear();
        for (String[] opt : diffOptions) {
            // Capture as final locals so lambdas close over the correct per-iteration values
            final String btnLabel    = opt[0];
            final String btnColor    = opt[1];
            final String btnActiveBg = opt[2];

            // Plain text labels — no circles or emojis
            Button btn = new Button(btnLabel.equals("All") ? "All Levels" : btnLabel);

            boolean isInitiallyActive = btnLabel.equals("All");
            btn.setStyle(buildDiffBtnStyle(isInitiallyActive, btnColor, btnActiveBg, isInitiallyActive));

            diffBtns.add(btn);

            btn.setOnAction(e -> {
                activeDifficulty = btnLabel;
                for (int i = 0; i < diffBtns.size(); i++) {
                    String[] o     = diffOptions[i];
                    boolean active = o[0].equals(btnLabel);
                    diffBtns.get(i).setStyle(buildDiffBtnStyle(active, o[1], o[2], active));
                }
                refreshExercises();
            });

            btn.setOnMouseEntered(e -> {
                if (!activeDifficulty.equals(btnLabel)) {
                    btn.setStyle(buildDiffBtnStyleHover(btnColor));
                }
            });
            btn.setOnMouseExited(e -> {
                boolean active = activeDifficulty.equals(btnLabel);
                btn.setStyle(buildDiffBtnStyle(active, btnColor, btnActiveBg, active));
            });

            row.getChildren().add(btn);
        }

        return row;
    }

    private String getDiffEmoji(String diff) {
        return switch (diff) {
            case "Beginner"     -> "🟢";
            case "Intermediate" -> "🟡";
            case "Advanced"     -> "🔴";
            default             -> "⬤";
        };
    }

    private String buildDiffBtnStyle(boolean active, String color, String activeBg, boolean isActive) {
        if (isActive) {
            return "-fx-background-color: " + activeBg + "; -fx-text-fill: " + color + ";"
                    + "-fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 7 14;"
                    + "-fx-background-radius: 6; -fx-cursor: hand;"
                    + "-fx-border-color: " + color + "; -fx-border-radius: 6; -fx-border-width: 1.5;";
        }
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

        List<Exercise> all      = DB.getOrDefault(muscle, Collections.emptyList());
        List<Exercise> filtered = new ArrayList<>();

        for (Exercise ex : all) {
            boolean regionMatch = activeRegion.equals("All") || ex.target.equals(activeRegion);
            boolean diffMatch   = activeDifficulty.equals("All") || ex.difficulty.equals(activeDifficulty);
            if (regionMatch && diffMatch) filtered.add(ex);
        }

        if (filtered.isEmpty()) {
            VBox emptyBox = new VBox(8);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40, 0, 0, 0));

            Label emptyIcon = new Label("🔍");
            emptyIcon.setFont(Font.font("SansSerif", 32));

            Label emptyLbl = new Label("No exercises match your current filters.");
            emptyLbl.setFont(Font.font("SansSerif", 14));
            emptyLbl.setTextFill(Color.web(TEXT_SECONDARY));

            Label hintLbl = new Label("Try selecting \"All\" in the difficulty or region filter.");
            hintLbl.setFont(Font.font("SansSerif", 12));
            hintLbl.setTextFill(Color.web(DIVIDER));

            emptyBox.getChildren().addAll(emptyIcon, emptyLbl, hintLbl);
            exerciseListContainer.getChildren().add(emptyBox);
            return;
        }

        // ── Count badge ───────────────────────────────────────────────────────
        String diffText   = activeDifficulty.equals("All") ? "" : " · " + activeDifficulty;
        String regionText = activeRegion.equals("All") ? "" : " · " + activeRegion;
        Label countLbl = new Label(filtered.size() + " exercise" + (filtered.size() == 1 ? "" : "s") + diffText + regionText);
        countLbl.setFont(Font.font("SansSerif", 12));
        countLbl.setTextFill(Color.web(TEXT_SECONDARY));
        countLbl.setPadding(new Insets(0, 0, 12, 2));
        exerciseListContainer.getChildren().add(countLbl);

        for (Exercise ex : filtered) {
            exerciseListContainer.getChildren().add(buildExerciseCard(ex));
        }
    }

    // ── Tab styles ────────────────────────────────────────────────────────────

    private String tabStyle(boolean active) {
        if (active) return "-fx-background-color: " + TAB_ACTIVE + "; -fx-text-fill: white;"
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

    private VBox buildExerciseCard(Exercise ex) {
        VBox card = new VBox(0);
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 10;");
        VBox.setMargin(card, new Insets(0, 0, 18, 0));

        // ── Card header ───────────────────────────────────────────────────────
        VBox headerBox = new VBox(0);
        headerBox.setStyle("-fx-background-color: " + BG_SECONDARY + "; -fx-background-radius: 10 10 0 0;");

        Region stripe = new Region();
        stripe.setPrefHeight(4);
        stripe.setStyle("-fx-background-color: " + getDifficultyColor(ex.difficulty)
                + "; -fx-background-radius: 10 10 0 0;");

        HBox nameRow = new HBox(10);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        nameRow.setPadding(new Insets(12, 20, 12, 20));

        Label nameLbl = new Label(ex.name);
        nameLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));
        nameLbl.setTextFill(Color.web(TEXT_PRIMARY));

        Label tagLbl = new Label(ex.target);
        tagLbl.setFont(Font.font("SansSerif", FontWeight.BOLD, 10));
        tagLbl.setTextFill(Color.web(ACCENT));
        tagLbl.setStyle("-fx-background-color: #2a1215; -fx-background-radius: 4;"
                + "-fx-padding: 3 8; -fx-border-color: " + ACCENT + ";"
                + "-fx-border-radius: 4; -fx-border-width: 1;");

        // ── Difficulty badge (plain text, no emoji) ───────────────────────────
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

        // ── Body ──────────────────────────────────────────────────────────────
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
            Label ph = new Label("🏋️\n" + ex.name);
            ph.setFont(Font.font("SansSerif", 11));
            ph.setTextFill(Color.web(TEXT_SECONDARY));
            ph.setAlignment(Pos.CENTER);
            ph.setWrapText(true);
            ph.setMaxWidth(130);
            imgBox.getChildren().add(ph);
        }

        // Steps
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

        // Meta table
        card.getChildren().add(buildMetaTable(ex));
        return card;
    }

    // ── Difficulty colour helpers ─────────────────────────────────────────────

    private String getDifficultyColor(String difficulty) {
        return switch (difficulty) {
            case "Beginner"     -> DIFF_BEGINNER_COLOR;
            case "Intermediate" -> DIFF_INTERMEDIATE_COLOR;
            case "Advanced"     -> DIFF_ADVANCED_COLOR;
            default             -> TEXT_SECONDARY;
        };
    }

    private String getDifficultyBgColor(String difficulty) {
        return switch (difficulty) {
            case "Beginner"     -> "#0d2b1a";
            case "Intermediate" -> "#2b1e0a";
            case "Advanced"     -> "#2b0a0d";
            default             -> TAB_INACTIVE;
        };
    }

    // ── META TABLE ────────────────────────────────────────────────────────────

    private GridPane buildMetaTable(Exercise ex) {
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: " + BG_SECONDARY + "; -fx-background-radius: 0 0 10 10;");

        Region topLine = new Region();
        topLine.setPrefHeight(1);
        topLine.setStyle("-fx-background-color: " + DIVIDER + ";");
        topLine.setMaxWidth(Double.MAX_VALUE);
        GridPane.setColumnSpan(topLine, 2);
        grid.add(topLine, 0, 0);

        String diffColor = getDifficultyColor(ex.difficulty);

        String[][] rows = {
                {"Alternative Name", ex.altName.isEmpty() ? "—" : ex.altName},
                {"Difficulty",       ex.difficulty},
                {"Force",            ex.force},
                {"Grips",            ex.grips},
                {"Mechanic",         ex.mechanic},
                {"Target Region",    ex.target}
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

            // Colour-code the difficulty row value
            if (rows[i][0].equals("Difficulty")) {
                valLbl.setTextFill(Color.web(diffColor));
            } else if (i == rows.length - 1) {
                valLbl.setTextFill(Color.web(ACCENT));
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