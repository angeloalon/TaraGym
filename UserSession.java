import java.util.*;

public class UserSession {

    private static UserSession instance;

    public static UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }

    private UserSession() {}

    //  fields:
    private boolean monUnavailable = false;
    private boolean tueUnavailable = false;
    private boolean wedUnavailable = false;
    private boolean thuUnavailable = false;
    private boolean friUnavailable = false;
    private boolean satUnavailable = false;
    private boolean sunUnavailable = false;
    private List<String> availableDays = new ArrayList<>();

    // User data
    private String username    = "";
    private String gender      = "";
    private int    age         = 0;
    private String frequency   = "";
    private String goal        = "";
    private String level       = "";
    private String workoutSplit = "";
    private String lbsPerWeek = "";


    // Getters

    public String getUsername()     { return username;     }
    public String getGender()       { return gender;       }
    public int    getAge()          { return age;          }
    public String getFrequency()    { return frequency;    }
    public String getGoal()         { return goal;         }
    public String getLevel()        { return level;        }
    public String getWorkoutSplit() { return workoutSplit; }
    public boolean isMonUnavailable() { return monUnavailable; }
    public boolean isTueUnavailable() { return tueUnavailable; }
    public boolean isWedUnavailable() { return wedUnavailable; }
    public boolean isThuUnavailable() { return thuUnavailable; }
    public boolean isFriUnavailable() { return friUnavailable; }
    public boolean isSatUnavailable() { return satUnavailable; }
    public boolean isSunUnavailable() { return sunUnavailable; }
    public String getLbsPerWeek() { return lbsPerWeek; }
    public List<String> getAvailableDays() { return availableDays; }


    // Setters
    public void setLbsPerWeek(String lbsPerWeek) { this.lbsPerWeek = lbsPerWeek; }
    public void setUsername(String username)  { this.username     = username; }
    public void setGender(String gender)      { this.gender       = gender;   }
    public void setAge(int age)               { this.age          = age;      }
    public void setFrequency(String frequency){ this.frequency    = frequency;}
    public void setGoal(String goal)          { this.goal         = goal;     }
    public void setLevel(String level)        { this.level        = level;    }
    public void setWorkoutSplit(String split) { this.workoutSplit = split;    }
    public void setMonUnavailable(boolean v)  { monUnavailable = v; }
    public void setTueUnavailable(boolean v)  { tueUnavailable = v; }
    public void setWedUnavailable(boolean v)  { wedUnavailable = v; }
    public void setThuUnavailable(boolean v)  { thuUnavailable = v; }
    public void setFriUnavailable(boolean v)  { friUnavailable = v; }
    public void setSatUnavailable(boolean v)  { satUnavailable = v; }
    public void setSunUnavailable(boolean v)  { sunUnavailable = v; }
    public void setAvailableDays(List<String> days) { availableDays = days; }

    public void clear() {
        username = ""; gender = ""; age = 0;
        frequency = ""; goal = ""; level = ""; workoutSplit = "";
        monUnavailable = false;
        tueUnavailable = false;
        wedUnavailable = false;
        thuUnavailable = false;
        friUnavailable = false;
        satUnavailable = false;
        sunUnavailable = false;
        availableDays  = new ArrayList<>();
    }



    public boolean isLoggedIn() { return !username.isEmpty(); }
}