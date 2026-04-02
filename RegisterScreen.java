import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class RegisterScreen {

    private final String BG_PRIMARY     = "#1A1D20";
    private final String BG_SECONDARY   = "#0D0F12";
    private final String ACCENT         = "#E63946";
    private final String TEXT_PRIMARY   = "#FFFFFF";
    private final String TEXT_SECONDARY = "#A8B2C1";

    public Scene buildScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PRIMARY + ";");

        VBox card = new VBox(16);
        card.setMaxWidth(420);
        card.setPadding(new Insets(40, 36, 36, 36));
        card.setStyle(
                "-fx-background-color: " + BG_SECONDARY + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #2a2d30;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;"
        );

        // Brand
        Label brand = new Label("TaraGym");
        brand.setFont(Font.font("SansSerif", FontWeight.BOLD, 26));
        brand.setTextFill(Color.web(ACCENT));

        Label subtitle = new Label("Create your account to get started");
        subtitle.setFont(Font.font("SansSerif", 13));
        subtitle.setTextFill(Color.web(TEXT_SECONDARY));
        subtitle.setPadding(new Insets(0, 0, 10, 0));

        // Username
        Label userLbl = new Label("Username");
        styleLabel(userLbl);
        TextField userField = new TextField();
        styleField(userField, "At least 3 characters");

        // Password
        Label passLbl = new Label("Password");
        styleLabel(passLbl);
        PasswordField passField = new PasswordField();
        styleField(passField, "At least 6 characters");

        // Confirm Password
        Label confirmLbl = new Label("Confirm Password");
        styleLabel(confirmLbl);
        PasswordField confirmField = new PasswordField();
        styleField(confirmField, "Re-enter your password");

        // Message label
        Label msgLabel = new Label("");
        msgLabel.setFont(Font.font("SansSerif", 11));
        msgLabel.setWrapText(true);

        // Register button
        Button registerBtn = new Button("Create Account");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setStyle(
                "-fx-background-color: " + ACCENT + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 12 0;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        );

        registerBtn.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();
            String confirm  = confirmField.getText().trim();

            // Validate
            if (username.length() < 3) {
                showMsg(msgLabel, "Username must be at least 3 characters.", false);
                return;
            }
            if (password.length() < 6) {
                showMsg(msgLabel, "Password must be at least 6 characters.", false);
                return;
            }
            if (!password.equals(confirm)) {
                showMsg(msgLabel, "Passwords do not match!", false);
                return;
            }

            // Try to register in DB
            boolean success = DatabaseManager.registerUser(username, password);

            if (success) {
                // Save username to UserSession
                UserSession.getInstance().setUsername(username);

                showMsg(msgLabel, "Account created! Setting up your profile...", true);

                // Small delay feel then navigate to profile setup
                javafx.animation.PauseTransition pause =
                        new javafx.animation.PauseTransition(
                                javafx.util.Duration.seconds(0.8));
                pause.setOnFinished(ev -> {
                    ProfileSetupScreen setup = new ProfileSetupScreen(username);
                    stage.setScene(setup.buildScene(stage));
                    stage.setTitle("TaraGym — Setup");
                });
                pause.play();

            } else {
                showMsg(msgLabel,
                        "Username \"" + username + "\" is already taken. Try another.",
                        false);
                userField.clear();
                userField.requestFocus();
            }
        });

        // Already have account hint
        Label loginHint = new Label("Already have an account? Login in Settings.");
        loginHint.setFont(Font.font("SansSerif", 11));
        loginHint.setTextFill(Color.web(TEXT_SECONDARY));
        loginHint.setAlignment(Pos.CENTER);
        loginHint.setMaxWidth(Double.MAX_VALUE);

        // Back button
        Button backBtn = new Button("← Back to Home");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 8 0;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: #2a2d30;" +
                        "-fx-border-radius: 6;" +
                        "-fx-border-width: 1;"
        );
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(
                "-fx-background-color: #2a2d30;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 8 0;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: #2a2d30;" +
                        "-fx-border-radius: 6;" +
                        "-fx-border-width: 1;"
        ));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 8 0;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: #2a2d30;" +
                        "-fx-border-radius: 6;" +
                        "-fx-border-width: 1;"
        ));
        backBtn.setOnAction(e -> Navigator.go(stage, "home"));

        card.getChildren().addAll(
                brand, subtitle,
                userLbl, userField,
                passLbl, passField,
                confirmLbl, confirmField,
                msgLabel,
                registerBtn,
                loginHint,
                backBtn
        );

        StackPane wrapper = new StackPane(card);
        wrapper.setStyle("-fx-background-color: " + BG_PRIMARY + ";");
        root.setCenter(wrapper);

        return new Scene(root, 900, 620);
    }

    // ===== Helpers =====
    private void showMsg(Label lbl, String message, boolean success) {
        lbl.setText(message);
        lbl.setTextFill(success ? Color.web("#4caf50") : Color.web(ACCENT));
    }

    private void styleLabel(Label lbl) {
        lbl.setFont(Font.font("SansSerif", 12));
        lbl.setTextFill(Color.web(TEXT_SECONDARY));
    }

    private void styleField(Control field, String prompt) {
        String style =
                "-fx-background-color: #1e2428;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #555f6d;" +
                        "-fx-padding: 10 12;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #2a2d30;" +
                        "-fx-border-radius: 6;" +
                        "-fx-border-width: 1;";

        if (field instanceof TextField tf) tf.setPromptText(prompt);
        field.setStyle(style);
    }
}