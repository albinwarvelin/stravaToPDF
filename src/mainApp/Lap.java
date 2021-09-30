package mainApp;

import org.json.JSONObject;

public class Lap
{
    private int lap_Index = 0;
    private int moving_Time = 0;
    private int elapsed_Time = 0;
    private double distance = 0;
    private double average_Speed = 0;

    public Lap(JSONObject jsonLap)
    {
        lap_Index = jsonLap.getInt("lap_index") - 1;
        moving_Time = jsonLap.getInt("moving_time");
        elapsed_Time = jsonLap.getInt("elapsed_time");
        distance = jsonLap.getDouble("distance");
        average_Speed = jsonLap.getDouble("average_speed");
    }

    public int getLap_Index()
    {
        return lap_Index;
    }
    public int getMoving_Time()
    {
        return moving_Time;
    }
    public int getElapsed_Time()
    {
        return elapsed_Time;
    }
    public double getDistance()
    {
        return distance;
    }
    public double getAverage_Speed()
    {
        return average_Speed;
    }
}
