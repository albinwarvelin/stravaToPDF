package mainApp;

import javafx.util.converter.LocalDateTimeStringConverter;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class Activity
{
    /** Variables, all initialized so there's no requirement for all variables to be filled **/
    private long id = 0;
    private String name = "";
    private LocalDateTime startDate = null;

    private double max_Heartrate = 0;
    private String type = "";
    private int comment_Count = 0;
    private double average_Heartrate = 0;
    private int kudos_Count = 0;
    private int athlete_Count = 0;
    private int max_Speed = 0;  //meters per second
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
        id = jsonObject.getLong("id");
        name = jsonObject.getString("name");
        String startDateString = jsonObject.getString("start_date_local");
        startDate = LocalDateTime.parse(startDateString.substring(0, startDateString.length() - 1));
        System.out.println(startDate);
    }

    public String toListString()
    {
        return id + " " + name;
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
