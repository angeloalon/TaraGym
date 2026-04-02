import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:taragym.db";

    // ===== Connection =====
    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            createTables(conn);
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ===== Create Tables =====
    private static void createTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "username TEXT UNIQUE," +
                            "password TEXT)"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS user (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "gender TEXT," +
                            "age INTEGER," +
                            "frequency_days TEXT," +
                            "fitness_goal TEXT," +
                            "level TEXT," +
                            "workout_split TEXT," +
                            "user_id INTEGER)"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS session (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "date TEXT," +
                            "duration_mins INTEGER," +
                            "split TEXT," +
                            "user_id INTEGER)"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS workout_log (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "session_id INTEGER," +
                            "exercise_name TEXT," +
                            "muscle_group TEXT," +
                            "set_number INTEGER," +
                            "reps INTEGER," +
                            "weight_lbs REAL," +
                            "FOREIGN KEY(session_id) REFERENCES session(id))"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== Register User =====
    public static boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            // UNIQUE constraint = username already taken
            return false;
        }
    }

    // ===== Login User =====
    public static boolean loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== Get Current User ID =====
    public static int getCurrentUserId() {
        String username = UserSession.getInstance().getUsername();
        if (username == null || username.isEmpty()) return -1;

        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ===== Save User Profile (after onboarding) =====
    public static void saveUserProfile(String gender, int age, String frequency,
                                       String goal, String level, String split) {
        int userId = getCurrentUserId();
        if (userId == -1) return;

        String sql = "INSERT INTO user (gender, age, frequency_days, fitness_goal, " +
                "level, workout_split, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, gender);
            pstmt.setInt(2, age);
            pstmt.setString(3, frequency);
            pstmt.setString(4, goal);
            pstmt.setString(5, level);
            pstmt.setString(6, split);
            pstmt.setInt(7, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== Load User Profile (after login) =====
    public static void loadUserProfile(String username) {
        String sql = "SELECT u.gender, u.age, u.frequency_days, u.fitness_goal, " +
                "u.level, u.workout_split FROM user u " +
                "JOIN users us ON u.user_id = us.id " +
                "WHERE us.username = ? ORDER BY u.id DESC LIMIT 1";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                UserSession.getInstance().setGender(rs.getString("gender"));
                UserSession.getInstance().setAge(rs.getInt("age"));
                UserSession.getInstance().setFrequency(rs.getString("frequency_days"));
                UserSession.getInstance().setGoal(rs.getString("fitness_goal"));
                UserSession.getInstance().setLevel(rs.getString("level"));
                UserSession.getInstance().setWorkoutSplit(rs.getString("workout_split"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== Save Session =====
    public static int saveSession(String date, int durationMins, String split) {
        int userId = getCurrentUserId();
        if (userId == -1) return -1;

        String sql = "INSERT INTO session (date, duration_mins, split, user_id) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, date);
            pstmt.setInt(2, durationMins);
            pstmt.setString(3, split);
            pstmt.setInt(4, userId);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ===== Save Workout Log =====
    public static void saveWorkoutLog(int sessionId, String exercise,
                                      String muscleGroup, int setNumber,
                                      int reps, double weightLbs) {
        String sql = "INSERT INTO workout_log (session_id, exercise_name, muscle_group, " +
                "set_number, reps, weight_lbs) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sessionId);
            pstmt.setString(2, exercise);
            pstmt.setString(3, muscleGroup);
            pstmt.setInt(4, setNumber);
            pstmt.setInt(5, reps);
            pstmt.setDouble(6, weightLbs);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== Get Session Counts =====
    public static int[] getSessionCounts(int sessionId) {
        String sql = "SELECT COUNT(DISTINCT exercise_name) as exercises, " +
                "COUNT(*) as sets FROM workout_log WHERE session_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sessionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new int[]{rs.getInt("exercises"), rs.getInt("sets")};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new int[]{0, 0};
    }

    // ===== Progress Stats =====
    public static int getWorkoutsThisMonth() {
        int userId = getCurrentUserId();
        if (userId == -1) return 0;

        String sql = "SELECT COUNT(*) as count FROM session WHERE user_id = " + userId +
                " AND strftime('%Y-%m', date) = strftime('%Y-%m', 'now')";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("count");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getAvgDuration() {
        int userId = getCurrentUserId();
        if (userId == -1) return 0;

        String sql = "SELECT AVG(duration_mins) as avg FROM session WHERE user_id = " + userId;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return (int) rs.getDouble("avg");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getMostTrainedMuscle() {
        int userId = getCurrentUserId();
        if (userId == -1) return "N/A";

        String sql = "SELECT muscle_group, COUNT(*) as count FROM workout_log " +
                "WHERE session_id IN (SELECT id FROM session WHERE user_id = " + userId + ") " +
                "GROUP BY muscle_group ORDER BY count DESC LIMIT 1";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getString("muscle_group");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    public static String getLastWorkout() {
        int userId = getCurrentUserId();
        if (userId == -1) return null;

        String sql = "SELECT date FROM session WHERE user_id = " + userId +
                " ORDER BY date DESC LIMIT 1";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getString("date");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateUserSplit(String goal, String level, String split) {
        int userId = getCurrentUserId();
        if (userId == -1) return;

        // Update the most recent user profile entry — including fitness_goal
        String sql = "UPDATE user SET fitness_goal = ?, level = ?, workout_split = ? " +
                "WHERE id = (SELECT MAX(id) FROM user WHERE user_id = ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, goal);
            pstmt.setString(2, level);
            pstmt.setString(3, split);
            pstmt.setInt(4, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getPersonalRecords() {
        int userId = getCurrentUserId();
        if (userId == -1) return null;

        String sql = "SELECT exercise_name, MAX(weight_lbs) as best_weight, date " +
                "FROM workout_log wl " +
                "JOIN session s ON wl.session_id = s.id " +
                "WHERE s.user_id = " + userId +
                " GROUP BY exercise_name ORDER BY exercise_name ASC";
        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}