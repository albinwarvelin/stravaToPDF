package mainApp;

import org.json.JSONObject;

public class Activity
{
    /** Variables, all initialized so there's no requirement for all variables to be filled **/
    private long id = 0;
    private double max_Heartrate = 0;
    private String type = "";
    private int comment_Count = 0;
    private double average_Heartrate = 0;
    private int kudos_Count = 0;
    private int athlete_Count = 0;
    private int max_Speed = 0;  //meters per second
    private String name = "";
    private boolean commute = false;
    private double average_Cadence = 0;
    private double distance = 0;
    private String location_Country = "";
    private boolean manual_Activity = false;
    private String gear_Id = "";
    private double elevation_Low = 0;
    private boolean flagged = false;
    private int elapsed_Time = 0;
    private int moving_Time = 0;
    private double average_Speed = 0;
    private double average_Temp = 0;
    private double total_Elevation_Gain = 0;
    private double elevation_High = 0;

    /** Constructor methods, empty constructor for creating activity manually and constructor using JSONObject **/
    public Activity()
    {

    }

    public Activity(JSONObject jsonObject)
    {

    }
}
