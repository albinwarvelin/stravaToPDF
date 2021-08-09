package mainApp;
import org.json.JSONObject;

public class Club
{
    /** Variables **/
    private int id;
    private boolean isOwner;
    private boolean isAdmin;
    private boolean isPrivate;
    private String name;
    private String city;
    private String state;
    private String profile_Medium;
    private String profile;
    private String cover_Photo_Medium;
    private String cover_Photo;

    /** Constructor method **/
    public Club(JSONObject JSONClub)
    {
        setId(JSONClub.getInt("id"));
        setOwner(JSONClub.getBoolean("owner"));
        setAdmin(JSONClub.getBoolean("admin"));
        setPrivate(JSONClub.getBoolean("private"));
        setName(JSONClub.getString("name"));
        setCity(JSONClub.getString("city"));
        setState(JSONClub.getString("state"));
        setProfile_Medium(JSONClub.getString("profile_medium"));
        setProfile(JSONClub.getString("profile"));
        setCover_Photo_Medium(JSONClub.getString("cover_photo_small"));
        setCover_Photo(JSONClub.getString("cover_photo"));
    }

    /** Get and set methods **/
    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
    public boolean isOwner()
    {
        return isOwner;
    }
    public void setOwner(boolean owner)
    {
        isOwner = owner;
    }
    public boolean isAdmin()
    {
        return isAdmin;
    }
    public void setAdmin(boolean admin)
    {
        isAdmin = admin;
    }
    public boolean isPrivate()
    {
        return isPrivate;
    }
    public void setPrivate(boolean aPrivate)
    {
        isPrivate = aPrivate;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getCity()
    {
        return city;
    }
    public void setCity(String city)
    {
        this.city = city;
    }
    public String getState()
    {
        return state;
    }
    public void setState(String state)
    {
        this.state = state;
    }
    public String getProfile_Medium()
    {
        return profile_Medium;
    }
    public void setProfile_Medium(String profile_Medium)
    {
        this.profile_Medium = profile_Medium;
    }
    public String getProfile()
    {
        return profile;
    }
    public void setProfile(String profile)
    {
        this.profile = profile;
    }
    public String getCover_Photo_Medium()
    {
        return cover_Photo_Medium;
    }
    public void setCover_Photo_Medium(String cover_Photo_Medium)
    {
        this.cover_Photo_Medium = cover_Photo_Medium;
    }
    public String getCover_Photo()
    {
        return cover_Photo;
    }
    public void setCover_Photo(String cover_Photo)
    {
        this.cover_Photo = cover_Photo;
    }
}
