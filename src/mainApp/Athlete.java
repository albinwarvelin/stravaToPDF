package mainApp;

import java.util.ArrayList;
import java.util.HashMap;

public class Athlete
{
    /** App resources **/
    private String token_Type;
    private int expires_At;
    private int expires_In;
    private String refresh_Token;
    private String access_Token;

    /** Athlete information **/
    private int id;
    private String username;
    private int resource_State;
    private String firstname;
    private String lastname;
    private String bio;
    private String city;
    private String state;
    private String country;
    private String gender;
    private boolean premium_Membership;
    private boolean summit_Membership;
    private String created_At;
    private String updated_At;
    private int badge_Type_Id;
    private double weight;
    private String profile_Picture_Medium;
    private String profile_Picture;
    private String friend;
    private String follower;

    public void updateAthlete(String jsonString, String requestType)
    {
        switch (requestType)
        {
            case "token_Request":
                tokenRequest(jsonString);
            case "athlete_Request":
                athleteRequest(jsonString);
        }
    }

    private void tokenRequest(String jsonString)
    {
        int indexOfAthlete = jsonString.indexOf("athlete");
        int indexOfAthleteStartBracket = jsonString.indexOf("{", indexOfAthlete);
        int indexOfAthleteEndBracket = jsonString.indexOf("}", indexOfAthleteStartBracket);

        String tokenData = jsonString.substring(1, indexOfAthlete - 2);
        String athleteData = jsonString.substring(indexOfAthleteStartBracket + 1, indexOfAthleteEndBracket);

        HashMap<String, String> tokenDataHM = splitToHashMap(tokenData);
        HashMap<String, String> athleteDataHM = splitToHashMap(athleteData);

        saveToVariables(tokenDataHM);
        saveToVariables(athleteDataHM);
    }

    private void athleteRequest(String jsonString)
    {

    }

    private void saveToVariables(HashMap<String, String > inputHashMap)
    {
        ArrayList<String> keyList = new ArrayList<>(inputHashMap.keySet());

        for (int i = 0; i < keyList.size(); i++)
        {
            switch (keyList.get(i))
            {
                case "token_type":
                    setToken_Type(inputHashMap.get(keyList.get(i)));
                    break;
                case "expires_at":
                    setExpires_At(Integer.valueOf(inputHashMap.get(keyList.get(i))));
                    break;
                case "expires_in":
                    setExpires_In(Integer.valueOf(inputHashMap.get(keyList.get(i))));
                    break;
                case "refresh_token":
                    setRefresh_Token(inputHashMap.get(keyList.get(i)));
                    break;
                case "access_token":
                    setAccess_Token(inputHashMap.get(keyList.get(i)));
                    break;
                case "id":
                    setId(Integer.valueOf(inputHashMap.get(keyList.get(i))));
                    break;
                case "username":
                    setUsername(inputHashMap.get(keyList.get(i)));
                    break;
                case "resource_state":
                    setResource_State(Integer.valueOf(inputHashMap.get(keyList.get(i))));
                    break;
                case "firstname":
                    setFirstname(inputHashMap.get(keyList.get(i)));
                    break;
                case "lastname":
                    setLastname(inputHashMap.get(keyList.get(i)));
                    break;
                case "bio":
                    setBio(inputHashMap.get(keyList.get(i)));
                    break;
                case "city":
                    setCity(inputHashMap.get(keyList.get(i)));
                    break;
                case "state":
                    setState(inputHashMap.get(keyList.get(i)));
                    break;
                case "country":
                    setCountry(inputHashMap.get(keyList.get(i)));
                    break;
                case "sex":
                    setGender(inputHashMap.get(keyList.get(i)));
                    break;
                case "premium_membership":
                    setPremium_Membership(Boolean.valueOf(inputHashMap.get(keyList.get(i))));
                    break;
                case "summit_membership":
                    setSummit_Membership(Boolean.valueOf(inputHashMap.get(keyList.get(i))));
                    break;
                case "created_at":
                    setCreated_At(inputHashMap.get(keyList.get(i)));
                    break;
                case "updated_at":
                    setUpdated_At(inputHashMap.get(keyList.get(i)));
                    break;
                case "badge_type_id":
                    setBadge_Type_Id(Integer.valueOf(inputHashMap.get(keyList.get(i))));
                    break;
                case "weight":
                    setWeight(Double.valueOf(inputHashMap.get(keyList.get(i))));
                    break;
                case "profile_medium":
                    setProfile_Picture_Medium(inputHashMap.get(keyList.get(i)));
                    break;
                case "profile":
                    setProfile_Picture(inputHashMap.get(keyList.get(i)));
                    break;
                case "friend":
                    setFriend(inputHashMap.get(keyList.get(i)));
                    break;
                case "follower":
                    setFollower(inputHashMap.get(keyList.get(i)));
                    break;

            }
        }
    }

    /* Splits json variable string to hashmap */
    private HashMap<String, String> splitToHashMap(String data)
    {
        HashMap<String, String> outHashMap = new HashMap<>();

        String[] dataSplit = data.split(",");

        for (int i = 0; i < dataSplit.length; i++)
        {
            dataSplit[i] = removeAllOfChar("\"" ,dataSplit[i]);

            int colonIndex = dataSplit[i].indexOf(":");

            String[] temp = new String[2];

            temp[0] = dataSplit[i].substring(0, colonIndex);
            temp[1] = dataSplit[i].substring(colonIndex + 1);

            outHashMap.put(temp[0], temp[1]);
        }

        return outHashMap;
    }

    /* Removes all characters of specified type */
    private String removeAllOfChar(String character, String string)
    {
        while (string.contains(character))
        {
            string = string.substring(0, string.indexOf(character)) + string.substring(string.indexOf(character) + 1);
        }

        return string;
    }

    /** App resources get and set methods **/
    public String getToken_Type()
    {
        return token_Type;
    }
    public int getExpires_At()
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
    public int getResource_State()
    {
        return resource_State;
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
    public boolean getPremium_Membership()
    {
        return premium_Membership;
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
    public String getFriend()
    {
        return friend;
    }
    public String getFollower()
    {
        return follower;
    }

    public void setId(int input)
    {
        id = input;
    }
    public void setUsername(String input)
    {
         username = input;
    }
    public void setResource_State(int input)
    {
         resource_State = input;
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
    public void setPremium_Membership(boolean input)
    {
         premium_Membership = input;
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
    public void setFriend(String input)
    {
         friend = input;
    }
    public void setFollower(String input)
    {
         follower = input;
    }
}
