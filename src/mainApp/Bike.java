package mainApp;
import org.json.JSONObject;

public class Bike
{
    /** Variables **/
    private String id;
    private String name;
    private int distance_meters;
    private boolean isPrimary;

    /** Constructor method **/
    public Bike(JSONObject JSONBike)
    {
        setId(JSONBike.getString("id"));
        setName(JSONBike.getString("name"));
        setDistance_meters(JSONBike.getInt("distance"));
        setPrimary(JSONBike.getBoolean("primary"));
    }

    /** Get and set methods **/
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public int getDistance_meters()
    {
        return distance_meters;
    }
    public void setDistance_meters(int distance_meters)
    {
        this.distance_meters = distance_meters;
    }
    public boolean isPrimary()
    {
        return isPrimary;
    }
    public void setPrimary(boolean primary)
    {
        isPrimary = primary;
    }
}
