package mainApp;
import org.json.JSONObject;

public class Shoe
{
    /** Variables **/
    private String id;
    private String name;
    private int distance_meters;
    private boolean isPrimary;

    /** Constructor method **/
    public Shoe(JSONObject JSONShoe)
    {
        setId(JSONShoe.getString("id"));
        setName(JSONShoe.getString("name"));
        setDistance_meters(JSONShoe.getInt("distance"));
        setPrimary(JSONShoe.getBoolean("primary"));
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
