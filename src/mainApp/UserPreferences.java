package mainApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class UserPreferences
{
    private boolean fullscreen = false;

    public UserPreferences()
    {
        loadUserPref();
    }

    /* Saves in file */
    public void saveUserPref()
    {
        try
        {
            JSONObject userPref =  variablesToJSONObject();

            BufferedWriter dataWriter = new BufferedWriter(new FileWriter("src/mainApp/athleteData/userPreferences.json"));
            dataWriter.write(userPref.toString());

            dataWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /* Loads file */
    private void loadUserPref()
    {
        try
        {
            BufferedReader dataReader = new BufferedReader(new FileReader("src/mainApp/athleteData/userPreferences.json"));
            String jsonString = dataReader.readLine();

            if (!jsonString.equals(""))
            {
                JSONObject loadedJSONObject = new JSONObject(jsonString);

                fullscreen = loadedJSONObject.getBoolean("fullscreen");

                dataReader.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /* Packs all variables in JSON Object */
    private JSONObject variablesToJSONObject()
    {
        JSONObject data = new JSONObject();

        data.put("fullscreen", fullscreen);

        return data;
    }

    /* Get and set methods */
    public boolean wantFullscreen()
    {
        return fullscreen;
    }
    public void setFullscreen(boolean fullscreen)
    {
        this.fullscreen = fullscreen;
    }
}
