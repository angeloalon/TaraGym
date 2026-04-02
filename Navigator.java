import javafx.stage.Stage;

public class Navigator {

    public static void go(Stage stage, String screenId) {
        switch (screenId) {

            case "home" -> {
                TaraGym home = new TaraGym();
                try {
                    home.start(stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            case "history" -> {
                History history = new History();
                stage.setScene(history.buildScene(stage));
                stage.setTitle("TaraGym — History");
            }

            case "workout" -> {
                WorkoutScreen workout = new WorkoutScreen();
                stage.setScene(workout.buildScene(stage));
                stage.setTitle("TaraGym — Workout");
            }

            case "schedule" -> {
                Schedule schedule = new Schedule();
                try {
                    schedule.start(stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            case "progress" -> {
                ProgressScreen progress = new ProgressScreen();
                stage.setScene(progress.buildScene(stage));
                stage.setTitle("TaraGym — Progress");
            }

            case "settings" -> {
                SettingsScreen settings = new SettingsScreen();
                stage.setScene(settings.buildScene(stage));
                stage.setTitle("TaraGym — Settings");
            }




            default -> System.out.println("Unknown screen: " + screenId);
        }
    }
}