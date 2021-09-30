package mainApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Activity
{
    /** Enumerations **/
    public enum ActivityType {ALPINESKI, BACKCOUNTRYSKI, CANOEING, CROSSFIT, EBIKERIDE, ELLIPTICAL, GOLF, HANDCYCLE, ICESKATE, HIKE, INLINESKATE, KAYAKING, KITESURF, NORDICSKI, RIDE, ROCKCLIMBING, ROLLERSKI, ROWING, RUN, SAIL, SKATEBOARD, SNOWBOARD, SNOWSHOE, SOCCER, STAIRSTEPPER, STANDUPPADDLING, SURFING, SWIM, VELOMOBILE, VIRTUALRIDE, VIRTUALRUN, WALK, WEIGHTTRAINING, WHEELCHAIR, WINDSURF, WORKOUT, YOGA};
    public enum WorkoutType {NONE, LONGRUN, WORKOUT, RACE}

    /** Variables, all initialized so there's no requirement for all variables to be filled **/
    private long id = 0;
    private String name = "";
    private String location_Country = "";
    private String gear_Id = "";
    private String description = "";
    private String device_Name = "";
    private int moving_Time = 0;    //Seconds
    private int elapsed_Time = 0;   //Seconds
    private int achievement_Count = 0;
    private int kudos_Count = 0;
    private int comment_Count = 0;
    private int group_Count = 0;    //Athletes in group
    private double max_Speed = 0;  //meters per second
    private double distance = 0;    //Meters
    private double total_Elevation_Gain = 0;    //Meters
    private double elevation_Low = 0;   //Meters
    private double elevation_High = 0;  //Meters
    private double average_Temp = 0;
    private double average_Speed = 0;   //meters per second
    private double max_Heartrate = 0;
    private double average_Heartrate = 0;
    private double average_Cadence = 0;
    private boolean commute = false;
    private boolean manual_Activity = false;
    private boolean private_Activity = false;
    private LocalDateTime startDateTime = null;
    private ActivityType activityType = ActivityType.RUN;
    private WorkoutType workoutType = WorkoutType.NONE;
    private ArrayList<Lap> activity_Laps = new ArrayList<>();


    /** Constructor methods, empty constructor for creating activity manually and constructor using JSONObject.
     * Constructor using JSONObject uses try-catch blocks to assign values **/
    public Activity()
    {

    }

    public Activity(JSONObject jsonObject)
    {
        String[] keyList = {"id", "name", "location_country", "gear_id", "description", "device_name", "moving_time", "elapsed_time", "achievement_count",
                "kudos_count", "comment_count", "athlete_count", "max_speed", "distance", "total_elevation_gain", "elev_low", "elev_high", "average_temp",
                "average_speed", "max_heartrate", "average_heartrate", "average_cadence", "commute", "manual", "private", "start_date_local", "workout_type",
                "type", "laps"};
        for (String s : keyList)
        {
            if (jsonObject.has(s))
            {
                if (!jsonObject.isNull(s))
                {
                    switch (s)
                    {
                        case "id" -> id = jsonObject.getLong("id");
                        case "name" -> name = jsonObject.getString("name");
                        case "location_country" -> location_Country = jsonObject.getString("location_country");
                        case "gear_id" -> gear_Id = jsonObject.getString("gear_id");
                        case "description" -> description = jsonObject.getString("description");
                        case "device_name" -> device_Name = jsonObject.getString("device_name");
                        case "moving_time" -> moving_Time = jsonObject.getInt("moving_time");
                        case "elapsed_time" -> elapsed_Time = jsonObject.getInt("elapsed_time");
                        case "achievement_count" -> achievement_Count = jsonObject.getInt("achievement_count");
                        case "kudos_count" -> kudos_Count = jsonObject.getInt("kudos_count");
                        case "comment_count" -> comment_Count = jsonObject.getInt("comment_count");
                        case "athlete_count" -> group_Count = jsonObject.getInt("athlete_count");
                        case "max_speed" -> max_Speed = jsonObject.getDouble("max_speed");
                        case "distance" -> distance = jsonObject.getDouble("distance");
                        case "total_elevation_gain" -> total_Elevation_Gain = jsonObject.getDouble("total_elevation_gain");
                        case "elev_low" -> elevation_Low = jsonObject.getDouble("elev_low");
                        case "elev_high" -> elevation_High = jsonObject.getDouble("elev_high");
                        case "average_temp" -> average_Temp = jsonObject.getDouble("average_temp");
                        case "average_speed" -> average_Speed = jsonObject.getDouble("average_speed");
                        case "max_heartrate" -> max_Heartrate = jsonObject.getDouble("max_heartrate");
                        case "average_heartrate" -> average_Heartrate = jsonObject.getDouble("average_heartrate");
                        case "average_cadence" -> average_Cadence = jsonObject.getDouble("average_cadence");
                        case "commute" -> commute = jsonObject.getBoolean("commute");
                        case "manual" -> manual_Activity = jsonObject.getBoolean("manual");
                        case "private" -> private_Activity = jsonObject.getBoolean("private");
                        case "start_date_local" -> startDateTime = LocalDateTime.parse(jsonObject.getString("start_date_local").substring(0, jsonObject.getString("start_date_local").length() - 1));
                        case "workout_type" -> workoutType = parseWorkoutType(jsonObject.getInt("workout_type"));
                        case "type" -> activityType = parseActivityType(jsonObject.getString("type"));
                        case "laps" -> parseLaps(jsonObject.getJSONArray("laps"));
                    }
                }
            }
        }


    }

    private WorkoutType parseWorkoutType(int integer)
    {
        return switch (integer)
        {
            case 1 -> WorkoutType.RACE;
            case 2 -> WorkoutType.LONGRUN;
            case 3 -> WorkoutType.WORKOUT;
            default -> WorkoutType.NONE;
        };
    }

    private ActivityType parseActivityType(String string)
    {
        return switch (string)
        {
            case "AlpineSki" -> ActivityType.ALPINESKI;
            case "BackcountrySki" -> ActivityType.BACKCOUNTRYSKI;
            case "Canoeing" -> ActivityType.CANOEING;
            case "Crossfit" -> ActivityType.CROSSFIT;
            case "EBikeRide" -> ActivityType.EBIKERIDE;
            case "Elliptical" -> ActivityType.ELLIPTICAL;
            case "Golf" -> ActivityType.GOLF;
            case "Handcycle" -> ActivityType.HANDCYCLE;
            case "Hike" -> ActivityType.HIKE;
            case "IceSkate" -> ActivityType.ICESKATE;
            case "InlineSkate" -> ActivityType.INLINESKATE;
            case "Kayaking" -> ActivityType.KAYAKING;
            case "Kitesurf" -> ActivityType.KITESURF;
            case "NordicSki" -> ActivityType.NORDICSKI;
            case "Ride" -> ActivityType.RIDE;
            case "RockClimbing" -> ActivityType.ROCKCLIMBING;
            case "RollerSki" -> ActivityType.ROLLERSKI;
            case "Rowing" -> ActivityType.ROWING;
            case "Sail" -> ActivityType.SAIL;
            case "Skateboard" -> ActivityType.SKATEBOARD;
            case "Snowboard" -> ActivityType.SNOWBOARD;
            case "Snowshoe" -> ActivityType.SNOWSHOE;
            case "Soccer" -> ActivityType.SOCCER;
            case "StairStepper" -> ActivityType.STAIRSTEPPER;
            case "StandUpPaddling" -> ActivityType.STANDUPPADDLING;
            case "Surfing" -> ActivityType.SURFING;
            case "Swim" -> ActivityType.SWIM;
            case "Velomobile" -> ActivityType.VELOMOBILE;
            case "VirtualRide" -> ActivityType.VIRTUALRIDE;
            case "VirtualRun" -> ActivityType.VIRTUALRUN;
            case "Walk" -> ActivityType.WALK;
            case "WeightTraining" -> ActivityType.WEIGHTTRAINING;
            case "Wheelchair" -> ActivityType.WHEELCHAIR;
            case "Windsurf" -> ActivityType.WINDSURF;
            case "Workout" -> ActivityType.WORKOUT;
            case "Yoga" -> ActivityType.YOGA;

            default -> ActivityType.RUN;
        };
    }

    private void parseLaps(JSONArray laps)
    {
        for (int i = 0; i < laps.length(); i++)
        {
            activity_Laps.add(new Lap(laps.getJSONObject(i)));
        }
    }

    public String toListString()
    {
        String dateString = startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return dateString + " " + name;
    }

    /** Get methods **/
    public long getId()
    {
        return id;
    }
    public String getName()
    {
        return name;
    }
    public LocalDateTime getStartDateTime()
    {
        return startDateTime;
    }
    public String getLocation_Country()
    {
        return location_Country;
    }
    public String getGear_Id()
    {
        return gear_Id;
    }
    public String getDescription()
    {
        return description;
    }
    public String getDevice_Name()
    {
        return device_Name;
    }
    public int getMoving_Time()
    {
        return moving_Time;
    }
    public int getElapsed_Time()
    {
        return elapsed_Time;
    }
    public int getAchievement_Count()
    {
        return achievement_Count;
    }
    public int getKudos_Count()
    {
        return kudos_Count;
    }
    public int getComment_Count()
    {
        return comment_Count;
    }
    public int getGroup_Count()
    {
        return group_Count;
    }
    public double getMax_Speed()
    {
        return max_Speed;
    }
    public double getDistance()
    {
        return distance;
    }
    public double getTotal_Elevation_Gain()
    {
        return total_Elevation_Gain;
    }
    public double getElevation_Low()
    {
        return elevation_Low;
    }
    public double getElevation_High()
    {
        return elevation_High;
    }
    public double getAverage_Temp()
    {
        return average_Temp;
    }
    public double getAverage_Speed()
    {
        return average_Speed;
    }
    public double getMax_Heartrate()
    {
        return max_Heartrate;
    }
    public double getAverage_Heartrate()
    {
        return average_Heartrate;
    }
    public double getAverage_Cadence()
    {
        return average_Cadence;
    }
    public boolean getCommute()
    {
        return commute;
    }
    public boolean getManual_Activity()
    {
        return manual_Activity;
    }
    public boolean getPrivate_Activity()
    {
        return private_Activity;
    }
    public ActivityType getActivityType()
    {
        return activityType;
    }
    public WorkoutType getWorkoutType()
    {
        return workoutType;
    }


    /** Set methods */
    public void setId(long id)
    {
        this.id = id;
    }
    public void setName(String name)
    {
        this.name = name;
    }


}
