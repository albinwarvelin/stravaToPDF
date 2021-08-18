package mainApp;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class Athlete
{
    /** App resources **/
    private String token_Type;
    private long expires_At = 0;
    private int expires_In;
    private String refresh_Token;
    private String access_Token;

    /** Athlete information **/
    private int id;
    private String username;
    private String firstname;
    private String lastname;
    private String bio;
    private String city;
    private String state;
    private String country;
    private String gender;
    private boolean summit_Membership;
    private String created_At;
    private String updated_At;
    private int badge_Type_Id;
    private double weight;
    private String profile_Picture_Medium;
    private String profile_Picture;
    private int following_Count;
    private int follower_Count;
    private int ftp;
    private String measurement_Preference;

    /** Athlete gear **/
    private HashMap<Integer, Club> clubsHashMap = new HashMap<>();
    private HashMap<String, Shoe> shoesHashMap = new HashMap<>();
    private HashMap<String, Bike> bikesHashMap = new HashMap<>();

    /* Updates token and athlete with JSON-Java */
    public void tokenUpdate(String jsonString)
    {
        JSONObject tokenJSONObject = new JSONObject(jsonString);

        access_Token = tokenJSONObject.getString("access_token");
        refresh_Token = tokenJSONObject.getString("refresh_token");
        expires_In = tokenJSONObject.getInt("expires_in");
        expires_At = tokenJSONObject.getLong("expires_at");
        token_Type = tokenJSONObject.getString("token_type");

        JSONObject athleteJSONObject = tokenJSONObject.getJSONObject("athlete");

        id = athleteJSONObject.getInt("id");
        username = athleteJSONObject.getString("username");
        firstname = athleteJSONObject.getString("firstname");
        lastname = athleteJSONObject.getString("lastname");
        bio = athleteJSONObject.getString("bio");
        city = athleteJSONObject.getString("city");
        state = athleteJSONObject.getString("state");
        country = athleteJSONObject.getString("country");
        gender = athleteJSONObject.getString("sex");
        summit_Membership = athleteJSONObject.getBoolean("summit");
        created_At = athleteJSONObject.getString("created_at");
        updated_At = athleteJSONObject.getString("updated_at");
        badge_Type_Id = athleteJSONObject.getInt("badge_type_id");
        profile_Picture_Medium = athleteJSONObject.getString("profile_medium");
        profile_Picture = athleteJSONObject.getString("profile");
    }

    /* Updates athlete with JSON-Java */
    public void athleteUpdate(String jsonString)
    {
        JSONObject athleteJSONObject = new JSONObject(jsonString);

        id = athleteJSONObject.getInt("id");
        username = athleteJSONObject.getString("username");
        firstname = athleteJSONObject.getString("firstname");
        lastname = athleteJSONObject.getString("lastname");
        bio = athleteJSONObject.getString("bio");
        city = athleteJSONObject.getString("city");
        state = athleteJSONObject.getString("state");
        country = athleteJSONObject.getString("country");
        gender = athleteJSONObject.getString("sex");
        summit_Membership = athleteJSONObject.getBoolean("summit");
        weight = athleteJSONObject.getDouble("weight");
        created_At = athleteJSONObject.getString("created_at");
        updated_At = athleteJSONObject.getString("updated_at");
        badge_Type_Id = athleteJSONObject.getInt("badge_type_id");
        profile_Picture_Medium = athleteJSONObject.getString("profile_medium");
        profile_Picture = athleteJSONObject.getString("profile");
        follower_Count = athleteJSONObject.getInt("follower_count");
        following_Count = athleteJSONObject.getInt("friend_count");
        measurement_Preference = athleteJSONObject.getString("measurement_preference");


        JSONArray JSONClubsArray = athleteJSONObject.getJSONArray("clubs");
        JSONArray JSONShoesArray = athleteJSONObject.getJSONArray("shoes");
        JSONArray JSONBikesArray = athleteJSONObject.getJSONArray("bikes");

        for (int i = 0; i < JSONClubsArray.length(); i++)
        {
            Club club = new Club(JSONClubsArray.getJSONObject(i)); //Initiates new club class

            clubsHashMap.put(club.getId(), club);
        }

        for (int i = 0; i < JSONShoesArray.length(); i++)
        {
            Shoe shoe = new Shoe(JSONShoesArray.getJSONObject(i));

            shoesHashMap.put(shoe.getId(), shoe);
        }

        for (int i = 0; i < JSONBikesArray.length(); i++)
        {
            Bike bike = new Bike(JSONBikesArray.getJSONObject(i));

            bikesHashMap.put(bike.getId(), bike);
        }
    }

    /* Updates token with refresh token response, with JSON-Java */
    public void refreshTokenUpdate(String jsonString)
    {
        JSONObject tokenJSONObject = new JSONObject(jsonString);

        access_Token = tokenJSONObject.getString("access_token");
        refresh_Token = tokenJSONObject.getString("refresh_token");
        expires_In = tokenJSONObject.getInt("expires_in");
        expires_At = tokenJSONObject.getLong("expires_at");
        token_Type = tokenJSONObject.getString("token_type");
    }

    public void saveTokenDataToFile()
    {
        try
        {
            JSONObject tokenData = tokenDataToJSONObject();

            BufferedWriter athleteDataWriter = new BufferedWriter(new FileWriter("src/mainApp/athleteData/tokenData.txt"));
            athleteDataWriter.write(tokenData.toString());

            athleteDataWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean loadTokenDataFromFile()
    {
        boolean dataAvailable = false;

        try
        {
            BufferedReader athleteDataReader = new BufferedReader(new FileReader("src/mainApp/athleteData/tokenData.txt"));
            String jsonString = athleteDataReader.readLine();

            if (!jsonString.equals(""))
            {
                JSONObject loadedJSONObject = new JSONObject(jsonString);

                access_Token = loadedJSONObject.getString("access_token");
                refresh_Token = loadedJSONObject.getString("refresh_token");
                expires_In = loadedJSONObject.getInt("expires_in");
                expires_At = loadedJSONObject.getInt("expires_at");
                token_Type = loadedJSONObject.getString("token_type");

                dataAvailable = true;

                athleteDataReader.close();
            }
        }
        catch (FileNotFoundException e)
        {
            dataAvailable = false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return dataAvailable;
    }

    /* Saves JSON-Object to JSON-File */
    public void saveAthleteDataToFile()
    {
        try
        {
            JSONObject athleteData = athleteDataToJSONObject();

            BufferedWriter athleteDataWriter = new BufferedWriter(new FileWriter("src/mainApp/athleteData/athleteInformation.txt"));
            athleteDataWriter.write(athleteData.toString());

            athleteDataWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /* Loads data from file, if no file is found boolean false is returned */
    public boolean loadAthleteDataFromFile()
    {
        boolean dataAvailable = false;

        try
        {
            BufferedReader athleteDataReader = new BufferedReader(new FileReader("src/mainApp/athleteData/athleteInformation.txt"));
            String jsonString = athleteDataReader.readLine();

            if (!jsonString.equals(""))
            {
                JSONObject loadedJSONObject = new JSONObject(jsonString);

                access_Token = loadedJSONObject.getString("access_token");
                refresh_Token = loadedJSONObject.getString("refresh_token");
                expires_In = loadedJSONObject.getInt("expires_in");
                expires_At = loadedJSONObject.getInt("expires_at");
                token_Type = loadedJSONObject.getString("token_type");
                id = loadedJSONObject.getInt("id");
                username = loadedJSONObject.getString("username");
                firstname = loadedJSONObject.getString("firstname");
                lastname = loadedJSONObject.getString("lastname");
                bio = loadedJSONObject.getString("bio");
                city = loadedJSONObject.getString("city");
                state = loadedJSONObject.getString("state");
                country = loadedJSONObject.getString("country");
                gender = loadedJSONObject.getString("gender");
                summit_Membership = loadedJSONObject.getBoolean("summit");
                weight = loadedJSONObject.getDouble("weight");
                created_At = loadedJSONObject.getString("created_at");
                updated_At = loadedJSONObject.getString("updated_at");
                badge_Type_Id = loadedJSONObject.getInt("badge_type_id");
                profile_Picture_Medium = loadedJSONObject.getString("profile_picture_medium");
                profile_Picture = loadedJSONObject.getString("profile_picture");
                follower_Count = loadedJSONObject.getInt("follower_count");
                following_Count = loadedJSONObject.getInt("following_count");
                ftp = loadedJSONObject.getInt("ftp");
                measurement_Preference = loadedJSONObject.getString("measurement_preference");


                JSONArray JSONClubsArray = loadedJSONObject.getJSONArray("clubs");
                JSONArray JSONShoesArray = loadedJSONObject.getJSONArray("shoes");
                JSONArray JSONBikesArray = loadedJSONObject.getJSONArray("bikes");

                for (int i = 0; i < JSONClubsArray.length(); i++)
                {
                    Club club = new Club(JSONClubsArray.getJSONObject(i)); //Initiates new club class

                    clubsHashMap.put(club.getId(), club);
                }

                for (int i = 0; i < JSONShoesArray.length(); i++)
                {
                    Shoe shoe = new Shoe(JSONShoesArray.getJSONObject(i));

                    shoesHashMap.put(shoe.getId(), shoe);
                }

                for (int i = 0; i < JSONBikesArray.length(); i++)
                {
                    Bike bike = new Bike(JSONBikesArray.getJSONObject(i));

                    bikesHashMap.put(bike.getId(), bike);
                }


                dataAvailable = true;

                athleteDataReader.close();
            }
        }
        catch (FileNotFoundException e)
        {
            dataAvailable = false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return dataAvailable;
    }

    /* Puts token data in JSONObject */
    private JSONObject tokenDataToJSONObject()
    {
        JSONObject data = new JSONObject();

        data.put("token_type", token_Type);
        data.put("expires_at", expires_At);
        data.put("expires_in", expires_In);
        data.put("refresh_token", refresh_Token);
        data.put("access_token", access_Token);

        return data;
    }

    /* Puts all data in JSONObject */
    private JSONObject athleteDataToJSONObject()
    {
        JSONObject data = new JSONObject();

        data.put("id", id);
        data.put("username", username);
        data.put("firstname", firstname);
        data.put("lastname", lastname);
        data.put("bio", bio);
        data.put("city", city);
        data.put("state", state);
        data.put("country", country);
        data.put("gender", gender);
        data.put("summit", summit_Membership);
        data.put("created_at", created_At);
        data.put("updated_at", updated_At);
        data.put("badge_type_id", badge_Type_Id);
        data.put("weight", weight);
        data.put("profile_picture_medium", profile_Picture_Medium);
        data.put("profile_picture", profile_Picture);
        data.put("following_count", following_Count);
        data.put("follower_count", follower_Count);
        data.put("ftp", ftp);
        data.put("measurement_preference", measurement_Preference);

        JSONArray clubs = new JSONArray();
        ArrayList<Integer> clubKeys = new ArrayList<>(clubsHashMap.keySet());

        for (Integer clubKey : clubKeys)
        {
            JSONObject clubToPut = new JSONObject();
            Club focusedClub = clubsHashMap.get(clubKey);

            clubToPut.put("id", focusedClub.getId());
            clubToPut.put("name", focusedClub.getName());
            clubToPut.put("city", focusedClub.getCity());
            clubToPut.put("state", focusedClub.getState());
            clubToPut.put("profile", focusedClub.getProfile());
            clubToPut.put("profile_medium", focusedClub.getProfile_Medium());
            clubToPut.put("cover_photo", focusedClub.getCover_Photo());
            clubToPut.put("cover_photo_small", focusedClub.getCover_Photo_Medium());
            clubToPut.put("owner", focusedClub.isOwner());
            clubToPut.put("admin", focusedClub.isAdmin());
            clubToPut.put("private", focusedClub.isPrivate());

            clubs.put(clubToPut);
        }

        data.put("clubs", clubs);

        JSONArray shoes = new JSONArray();
        ArrayList<String> shoeKeys = new ArrayList<>(shoesHashMap.keySet());

        for (String shoeKey : shoeKeys)
        {
            JSONObject shoeToPut = new JSONObject();
            Shoe focusedShoe = shoesHashMap.get(shoeKey);

            shoeToPut.put("id", focusedShoe.getId());
            shoeToPut.put("name", focusedShoe.getName());
            shoeToPut.put("distance", focusedShoe.getDistance_meters());
            shoeToPut.put("primary", focusedShoe.isPrimary());

            shoes.put(shoeToPut);
        }

        data.put("shoes", shoes);

        JSONArray bikes = new JSONArray();
        ArrayList<String> bikeKeys = new ArrayList<>(bikesHashMap.keySet());

        for (String bikeKey : bikeKeys)
        {
            JSONObject bikeToPut = new JSONObject();
            Bike focusedBike = bikesHashMap.get(bikeKey);

            bikeToPut.put("id", focusedBike.getId());
            bikeToPut.put("name", focusedBike.getName());
            bikeToPut.put("distance", focusedBike.getDistance_meters());
            bikeToPut.put("primary", focusedBike.isPrimary());

            bikes.put(bikeToPut);
        }

        data.put("bikes", bikes);

        return data;
    }


    /** App resources get and set methods **/
    public String getToken_Type()
    {
        return token_Type;
    }
    public long getExpires_At()
    {
        return expires_At;
    }
    public int getExpires_In()
    {
        return expires_In;
    }
    public String getRefresh_Token()
    {
        return refresh_Token;
    }
    public String getAccess_Token()
    {
        return access_Token;
    }

    public void setToken_Type(String input)
    {
        token_Type = input;
    }
    public void setExpires_At(int input)
    {
        expires_At = input;
    }
    public void setExpires_In(int input)
    {
        expires_In = input;
    }
    public void setRefresh_Token(String input)
    {
        refresh_Token = input;
    }
    public void setAccess_Token(String input)
    {
        access_Token = input;
    }

    /** Athlete information get and set methods**/
    public int getId()
    {
        return id;
    }
    public String getUsername()
    {
        return username;
    }
    public String getFirstname()
    {
        return firstname;
    }
    public String getLastname()
    {
        return lastname;
    }
    public String getBio()
    {
        return bio;
    }
    public String getCity()
    {
        return city;
    }
    public String getState()
    {
        return state;
    }
    public String getCountry()
    {
        return country;
    }
    public String getGender()
    {
        return gender;
    }
    public boolean getSummit_Membership()
    {
        return summit_Membership;
    }
    public String getCreated_At()
    {
        return created_At;
    }
    public String getUpdated_At()
    {
        return updated_At;
    }
    public int getBadge_Type_Id()
    {
        return badge_Type_Id;
    }
    public double getWeight()
    {
        return weight;
    }
    public String getProfile_Picture_Medium()
    {
        return profile_Picture_Medium;
    }
    public String getProfile_Picture()
    {
        return profile_Picture;
    }
    public int getFollowing_Count()
    {
        return following_Count;
    }
    public int getFollower_Count()
    {
        return follower_Count;
    }
    public int getFTP()
    {
        return ftp;
    }
    public String getMeasurement_Preference()
    {
        return measurement_Preference;
    }
    public HashMap<Integer, Club> getClubsHashMap()
    {
        return clubsHashMap;
    }
    public HashMap<String, Shoe> getShoesHashMap()
    {
        return shoesHashMap;
    }
    public HashMap<String, Bike> getBikesHashMap()
    {
        return bikesHashMap;
    }

    public void setId(int input)
    {
        id = input;
    }
    public void setUsername(String input)
    {
         username = input;
    }
    public void setFirstname(String input)
    {
         firstname = input;
    }
    public void setLastname(String input)
    {
         lastname = input;
    }
    public void setBio(String input)
    {
         bio = input;
    }
    public void setCity(String input)
    {
         city = input;
    }
    public void setState(String input)
    {
         state = input;
    }
    public void setCountry(String input)
    {
         country = input;
    }
    public void setGender(String input)
    {
         gender = input;
    }
    public void setSummit_Membership(boolean input)
    {
         summit_Membership = input;
    }
    public void setCreated_At(String input)
    {
         created_At = input;
    }
    public void setUpdated_At(String input)
    {
         updated_At = input;
    }
    public void setBadge_Type_Id(int input)
    {
         badge_Type_Id = input;
    }
    public void setWeight(double input)
    {
         weight = input;
    }
    public void setProfile_Picture_Medium(String input)
    {
         profile_Picture_Medium = input;
    }
    public void setProfile_Picture(String input)
    {
         profile_Picture = input;
    }
    public void setFollowing_Count(int input)
    {
         following_Count = input;
    }
    public void setFollower_Count(int input)
    {
         follower_Count = input;
    }
    public void setFTP(String input) {
        if (!input.equals("null"))
        {
            ftp = Integer.valueOf(input);
        }
    }
    public void setMeasurement_Preference(String input)
    {
        measurement_Preference = input;
    }
}
