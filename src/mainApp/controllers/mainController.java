package mainApp.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Worker;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.util.Duration;
import mainApp.*;
import org.json.JSONArray;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ResourceBundle;

public class mainController implements Initializable
{
    /* Window */
    private boolean isMaximized = false;
    private boolean resizingActive = false;

    private double xOffset = 0;
    private double yOffset = 0;

    private double maximizedSizeX;
    private double maximizedSizeY;

    private double previousWindowWidth = 1280;
    private double previousWindowHeight = 720;

    private double previousWindowX;
    private double previousWindowY;

    private boolean firstResize = true;

    /* Scene */
    public StackPane contentSP;
    public WebView authorizationWV;
    public HBox topHBox;
    public StackPane authorizationSP;
    public VBox navBarBG;
    public BorderPane mainBG;
    public Pane authorizationButton;
    public AnchorPane stravaConnectedAP;
    public Label activitiesLoadedLabel;
    public Label activitiesDateLabel;

    /* Status bar */
    public Label statusBarLabel;
    public Pane statusBarPane;
    public Pane statusIconPane;
    private Timeline timeline;
    private int timeTicks;

    /* NavBar background animation */
    private long elapsedHundredths; //100 fps

    private double speed = 0.050;
    private final double maxSpeed = 0.100;
    private final double minSpeed = 0.010;
    private String rotation = "cw";

    private int hue = 223;
    private final int maxHue = 300;
    private final int minHue = 130;
    private boolean addHue = true;

    private final int saturation = 100;

    private int luminosity = 23;
    private final int maxLuminosity =  37;
    private final int minLuminosity = 18;

    private double transparency = 0.20;
    private boolean addTransparency = true;
    private final double maxTransparency = 1.00;
    private final double minTransparency = 0.20;

    private double AXPercentage = 50.00;
    private double AYPercentage = 0.00;
    private double BXPercentage = 50.00;
    private double BYPercentage = 100.00;

    /* Variables */
    private boolean webViewFinished;
    public Athlete currentAthlete;

    /* Run on first startup */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        makeScreenDraggable(topHBox);
        currentAthlete = new Athlete();
        isMaximized = main.userPreferences.wantFullscreen();

        /* Loads token data from file, if refresh is necessary HTTP request is sent and access token is updated */
        if (currentAthlete.loadTokenDataFromFile())
        {
            boolean successfulTokenRefresh = false;

            displayStatusBar("Token data loaded from file.", 5000, "alert");

            if (checkRefreshNecessary())
            {
                if (tokenRefreshHTTPRequest(currentAthlete.getRefresh_Token()))
                {
                    successfulTokenRefresh = true;
                    currentAthlete.saveTokenDataToFile();
                }
            }
            if (athleteInfoHTTPRequest(currentAthlete.getAccess_Token()))
            {
                if (successfulTokenRefresh)
                {
                    displayStatusBar("Token refresh and athlete refresh successful.", 3000, "success");
                }
                else
                {
                    displayStatusBar("Athlete information refresh successful. Token refresh not needed.", 3000, "success");
                }

                currentAthlete.saveAthleteDataToFile();
            }
            else
            {
                currentAthlete.loadAthleteDataFromFile();
            }

            updateLoggedInIcon(true);
        }
        else
        {
            updateLoggedInIcon(false);
            displayStatusBar("No athlete connected, log in.", 2000, "alert");
        }


        animatedNavBarBG();
    }

    /** Authorization flow, only called upon first authorization **/
    /* Opens authorization */
    private void openAuthorization()
    {
        /* Loads strava oauth, strava oauth redirects to login screen automatically */
        authorizationWV.getEngine().load("https://www.strava.com/oauth/authorize?client_id=67536&response_type=code&redirect_uri=http://localhost&approval_prompt=force&scope=read_all,activity:read_all,profile:read_all");

        webViewFinished = false;

        displayStatusBar("Strava authorization loading....", 2000, "alert");

        authorizationWV.getEngine().getLoadWorker().stateProperty().addListener((observable, oldState, newState) ->
        {
            /* Checked before website is reached */
            if (newState == Worker.State.SCHEDULED)
            {
                /* Stops webview if user exits strava login (to prevent user from navigating outside strava oauth) */
                if (!checkValidAuthorizationWV())
                {
                    stopAuthorizationWV();

                    displayStatusBar("Error: App cannot leave authorization.", 2000, "error");
                }
            }

            /* Checks if there is no internet connection and stops*/
            if (newState == Worker.State.FAILED)
            {
                if (authorizationWV.getEngine().getLocation().equals("https://www.strava.com/oauth/authorize?client_id=67536&response_type=code&redirect_uri=http://localhost&approval_prompt=force&scope=read_all,activity:read_all,profile:read_all"))
                {
                    stopAuthorizationWV();

                    displayStatusBar("Error: No internet connection.", 5000, "error");
                }

                /* Checks if domain is localhost, if true program handles url with parameters */
                String urlString = authorizationWV.getEngine().getLocation();
                String urlDomain = urlString.substring(Toolbox.findNIndexOf('/', 2, urlString) + 1, Toolbox.findNIndexOf('/', 3, urlString));

                if (urlDomain.equals("localhost"))
                {
                    stopAuthorizationWV();
                    handleAuthQueryURL(urlString);
                }
            }

            /* If webview is loaded, it's sent to front and viewable */
            if (newState == Worker.State.SUCCEEDED)
            {
                /* Brings webview to front if not in front */
                if (contentSP.getChildren().get(contentSP.getChildren().size() - 1) != authorizationWV && !webViewFinished)
                {
                    authorizationWV.toFront();
                }
            }
        });
    }

    /* Checks url so user doesn't leave intended path (goes to strava settings, leaves to strava homepage etc) */
    private boolean checkValidAuthorizationWV()
    {
        boolean validDomain;

        String urlString = authorizationWV.getEngine().getLocation();

        if (urlString.equals("https://www.strava.com/") || urlString.equals("https://www.strava.com/settings/apps") || urlString.equals("https://www.strava.com/terms"))
        {
            validDomain = false;
        }
        else
        {
            validDomain = true;
        }

        return validDomain;
    }

    /* Stops authorization webview and switches back to main pane */
    private void stopAuthorizationWV()
    {
        authorizationWV.toBack();
        webViewFinished = true;
        authorizationWV.getEngine().load(null);
    }

    /* Handles response query url from strava, checks if scopes are correct */
    private void handleAuthQueryURL(String url)
    {
        /* Checks if error is returned */
        if (url.contains("error="))
        {
            String error = url.substring(url.indexOf("error=") + 6);

            if (error.equals("access_denied"))
            {
                displayStatusBar("Error: No access given.", 7000, "error");
            }
            else
            {
                displayStatusBar("Error: Unknown error: " + error, 10000, "error");
            }
        }

        /* Checks if all scopes are allowed */
        else if (url.contains("scope="))
        {
            String scope = url.substring(url.indexOf("scope=") + 6);

            if (!scope.equals("read,activity:read_all,profile:read_all,read_all"))
            {
                displayStatusBar("Error: Not all access given.", 10000, "error");
            }

            /* If scopes are allowed, program extracts authorization code */
            else if (url.contains("code="))
            {
                String code = url.substring(url.indexOf("code=") + 5, url.indexOf('&', url.indexOf("code=") + 5));


                authTokenHTTPRequest(code);
                if(athleteInfoHTTPRequest(currentAthlete.getAccess_Token()))
                {
                    displayStatusBar("Athlete information retrieval successful.", 5000, "success");

                    updateLoggedInIcon(true);

                    currentAthlete.saveTokenDataToFile();
                    currentAthlete.saveAthleteDataToFile();
                }
            }
        }
    }

    /* Exchanges authorization token for refresh token and access token */
    private void authTokenHTTPRequest(String code)
    {
        try
        {
            URL url = new URL("https://www.strava.com/api/v3/oauth/token");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String params = "client_id=67536&client_secret=ca0ffca492eed64219d40971807f716cfa3d50a4&code=" + code + "&grant_type=authorization_code";

            byte[] out = params.getBytes(StandardCharsets.UTF_8);

            OutputStream stream = http.getOutputStream();
            stream.write(out);

            int responseCode = http.getResponseCode();

            switch (responseCode)
            {
                case 200 -> {
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                    String response;

                    do
                    {
                        response = responseReader.readLine();
                    }
                    while ((responseReader.readLine()) != null);

                    currentAthlete.tokenUpdate(response);

                    displayStatusBar("Athlete information retrieval successful.", 5000, "success");
                }
                case 401 -> displayStatusBar("Token error: 401, Unauthorized.", 5000, "error");
                case 403 -> displayStatusBar("Token error: 403, Forbidden, you cannot access.", 5000, "error");
                case 404 -> displayStatusBar("Token error: 404, Not found.", 5000, "error");
                case 429 -> displayStatusBar("Token error: 429, Too many requests. Try again later.", 5000, "error");
                case 500 -> displayStatusBar("Token error: 500, Strava is having issues.", 5000, "error");
            }
            http.disconnect();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /** Data retrieval, from strava or file if strava is not working **/
    /* Checks if token refresh is needed */
    private boolean checkRefreshNecessary()
    {
        boolean refreshNecessary = false;

        long currentTimestamp = Instant.now().getEpochSecond();
        long expires_At = currentAthlete.getExpires_At();

        if ((expires_At - 3600) < currentTimestamp)
        {
            refreshNecessary = true;
        }

        return refreshNecessary;
    }

    /* Token refresh, does not display anything on successful request */
    private boolean tokenRefreshHTTPRequest(String refresh_Token)
    {
        boolean successfulRequest = false;

        if (refresh_Token != null)
        {
            try
            {
                URL url = new URL("https://www.strava.com/api/v3/oauth/token");
                HttpURLConnection http = (HttpURLConnection)url.openConnection();
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String params = "client_id=67536&client_secret=ca0ffca492eed64219d40971807f716cfa3d50a4&grant_type=refresh_token&refresh_token=" + refresh_Token;

                byte[] out = params.getBytes(StandardCharsets.UTF_8);

                OutputStream stream = http.getOutputStream();
                stream.write(out);

                int responseCode = http.getResponseCode();

                switch (responseCode)
                {
                    case 200 -> {
                        BufferedReader responseReader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                        String response;

                        do
                        {
                            response = responseReader.readLine();
                        }
                        while ((responseReader.readLine()) != null);

                        currentAthlete.refreshTokenUpdate(response);
                        successfulRequest = true;
                    }
                    case 401 -> displayStatusBar("Token error: 401, Unauthorized.", 5000, "error");
                    case 403 -> displayStatusBar("Token error: 403, Forbidden, you cannot access.", 5000, "error");
                    case 404 -> displayStatusBar("Token error: 404, Not found.", 5000, "error");
                    case 429 -> displayStatusBar("Token error: 429, Too many requests. Try again later.", 5000, "error");
                    case 500 -> displayStatusBar("Token error: 500, Strava is having issues.", 5000, "error");
                }
                http.disconnect();
            }
            catch (UnknownHostException e)
            {
                displayStatusBar("Internet connection unknown or failed.", 7000, "error");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            displayStatusBar("Refresh error: Faulty refresh Token", 5000, "error");
        }

        return successfulRequest;
    }

    /* Retrieves athlete data with HTTP request to strava api, sends response to currentAthlete, does not display anything on successful request */
    private boolean athleteInfoHTTPRequest(String access_Token)
    {
        boolean successfulRequest = false;

        if (access_Token != null)
        {
            try
            {
                URL url = new URL("https://www.strava.com/api/v3/athlete");

                HttpURLConnection http = (HttpURLConnection)url.openConnection();
                http.setRequestProperty("Authorization", "Bearer " + access_Token);

                int responseCode = http.getResponseCode();

                switch (responseCode)
                {
                    case 200 -> {
                        BufferedReader responseReader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                        String response;

                        do
                        {
                            response = responseReader.readLine();
                        }
                        while ((responseReader.readLine()) != null);

                        currentAthlete.athleteUpdate(response);
                        successfulRequest = true;
                    }
                    case 401 -> displayStatusBar("Token error: 401, Unauthorized.", 5000, "error");
                    case 403 -> displayStatusBar("Token error: 403, Forbidden, you cannot access.", 5000, "error");
                    case 404 -> displayStatusBar("Token error: 404, Not found.", 5000, "error");
                    case 429 -> displayStatusBar("Token error: 429, Too many requests. Try again later.", 5000, "error");
                    case 500 -> displayStatusBar("Token error: 500, Strava is having issues.", 5000, "error");
                }
                http.disconnect();
            }
            catch (UnknownHostException e)
            {
                displayStatusBar("Internet connection unknown or failed.", 7000, "error");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            displayStatusBar("Error: No access token found.", 5000, "error");
        }

        return successfulRequest;
    }

    /* Retrieves activity data */
    private boolean activitiesHTTPRequest(String access_Token)
    {
        boolean successfulRequest = false;

        if (access_Token != null)
        {
            try
            {
                //URL url = new URL("https://www.strava.com/api/v3/athlete/activities?before=1629494417&after=1230768000&per_page=200");
                URL url = new URL("https://www.strava.com/api/v3/athlete/activities?page=2&per_page=200");

                HttpURLConnection http = (HttpURLConnection)url.openConnection();
                http.setRequestProperty("Authorization", "Bearer " + access_Token);

                int responseCode = http.getResponseCode();

                switch (responseCode)
                {
                    case 200 -> {
                        BufferedReader responseReader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                        String response;

                        do
                        {
                            response = responseReader.readLine();
                        }
                        while ((responseReader.readLine()) != null);

                        //response = "{\"activities\":" + response + "}";

                        JSONArray activities = new JSONArray(response);

                        successfulRequest = true;
                    }
                    case 401 -> displayStatusBar("Token error: 401, Unauthorized.", 5000, "error");
                    case 403 -> displayStatusBar("Token error: 403, Forbidden, you cannot access.", 5000, "error");
                    case 404 -> displayStatusBar("Token error: 404, Not found.", 5000, "error");
                    case 429 -> displayStatusBar("Token error: 429, Too many requests. Try again later.", 5000, "error");
                    case 500 -> displayStatusBar("Token error: 500, Strava is having issues.", 5000, "error");
                }
                http.disconnect();
            }
            catch (UnknownHostException e)
            {
                displayStatusBar("Internet connection unknown or failed.", 7000, "error");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            displayStatusBar("Error: No access token found.", 5000, "error");
        }

        return successfulRequest;
    }

    /** UI methods **/
    /* Displays a statusbar with message for duration (milliseconds) */
    private void displayStatusBar(String text, int durationMillis, String iconType)
    {
        /* Throws error if shorter than 500 milliseconds */
        if (durationMillis < 500)
        {
            throw new Error("Duration cannot be shorter than 0.5 seconds.");
        }

        /* Checks and stops running timer */
        if (timeline != null)
        {
            timeline.stop();
        }

        /* Enables status bar*/
        statusBarLabel.setText(text);

        switch (iconType)
        {
            case "error" :
                statusIconPane.setStyle("-fx-background-color: #ab4642; -fx-shape: \"M439.15 453.06L297.17 384l141.99-69.06c7.9-3.95 11.11-13.56 7.15-21.46L432 264.85c-3.95-7.9-13.56-11.11-21.47-7.16L224 348.41 37.47 257.69c-7.9-3.95-17.51-.75-21.47 7.16L1.69 293.48c-3.95 7.9-.75 17.51 7.15 21.46L150.83 384 8.85 453.06c-7.9 3.95-11.11 13.56-7.15 21.47l14.31 28.63c3.95 7.9 13.56 11.11 21.47 7.15L224 419.59l186.53 90.72c7.9 3.95 17.51.75 21.47-7.15l14.31-28.63c3.95-7.91.74-17.52-7.16-21.47zM150 237.28l-5.48 25.87c-2.67 12.62 5.42 24.85 16.45 24.85h126.08c11.03 0 19.12-12.23 16.45-24.85l-5.5-25.87c41.78-22.41 70-62.75 70-109.28C368 57.31 303.53 0 224 0S80 57.31 80 128c0 46.53 28.22 86.87 70 109.28zM280 112c17.65 0 32 14.35 32 32s-14.35 32-32 32-32-14.35-32-32 14.35-32 32-32zm-112 0c17.65 0 32 14.35 32 32s-14.35 32-32 32-32-14.35-32-32 14.35-32 32-32z\";");
                statusIconPane.setPrefHeight(24);
                statusIconPane.setPrefWidth(21);
                statusIconPane.setLayoutY(10);
                break;
            case "alert" :
                statusIconPane.setStyle("-fx-background-color: #7cafc2; -fx-shape: \"M504 256c0 136.997-111.043 248-248 248S8 392.997 8 256C8 119.083 119.043 8 256 8s248 111.083 248 248zm-248 50c-25.405 0-46 20.595-46 46s20.595 46 46 46 46-20.595 46-46-20.595-46-46-46zm-43.673-165.346l7.418 136c.347 6.364 5.609 11.346 11.982 11.346h48.546c6.373 0 11.635-4.982 11.982-11.346l7.418-136c.375-6.874-5.098-12.654-11.982-12.654h-63.383c-6.884 0-12.356 5.78-11.981 12.654z\"");
                statusIconPane.setPrefHeight(24);
                statusIconPane.setPrefWidth(24);
                statusIconPane.setLayoutY(10);
                break;
            case "success" :
                statusIconPane.setStyle("-fx-background-color: #a1b56c; -fx-shape: \"M248 8C111 8 0 119 0 256s111 248 248 248 248-111 248-248S385 8 248 8zm141.4 389.4c-37.8 37.8-88 58.6-141.4 58.6s-103.6-20.8-141.4-58.6S48 309.4 48 256s20.8-103.6 58.6-141.4S194.6 56 248 56s103.6 20.8 141.4 58.6S448 202.6 448 256s-20.8 103.6-58.6 141.4zM328 152c-23.8 0-52.7 29.3-56 71.4-.7 8.6 10.8 11.9 14.9 4.5l9.5-17c7.7-13.7 19.2-21.6 31.5-21.6s23.8 7.9 31.5 21.6l9.5 17c4.1 7.4 15.6 4 14.9-4.5-3.1-42.1-32-71.4-55.8-71.4zm-201 75.9l9.5-17c7.7-13.7 19.2-21.6 31.5-21.6s23.8 7.9 31.5 21.6l9.5 17c4.1 7.4 15.6 4 14.9-4.5-3.3-42.1-32.2-71.4-56-71.4s-52.7 29.3-56 71.4c-.6 8.5 10.9 11.9 15.1 4.5zM362.4 288H133.6c-8.2 0-14.5 7-13.5 15 7.5 59.2 58.9 105 121.1 105h13.6c62.2 0 113.6-45.8 121.1-105 1-8-5.3-15-13.5-15z\";");
                statusIconPane.setPrefHeight(24);
                statusIconPane.setPrefWidth(23);
                statusIconPane.setLayoutY(10);
                break;
        }

        statusBarPane.setOpacity(1.0);
        statusBarLabel.setOpacity(1.0);

        /* Animation */
        timeTicks = durationMillis;

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(10), actionEvent ->
        {
            timeTicks -= 10;

            if (timeTicks < 500)
            {
                double opacity = (double) timeTicks / 500;

                statusBarLabel.setOpacity(opacity);
                statusBarPane.setOpacity(opacity);
            }

            if (timeTicks <= 0)
            {
                timeline.stop();
            }

        }));

        timeline.playFromStart();
    }

    /* Animates navbar background */
    private void animatedNavBarBG()
    {
        Animation animation = new Timeline(new KeyFrame(Duration.millis(10), e ->
        {
            elapsedHundredths++;

            /* Cw randomizer*/
            if (elapsedHundredths % 1000 == 0)
            {
                int decider = (int) (Math.random() * 2);

                if (decider == 0)
                {
                    rotation = "cw";
                }
                else
                {
                    rotation = "ccw";
                }
            }

            /* Speed randomizer */
            if (elapsedHundredths % 500 == 0)
            {
                double adjustment = (Math.random() * 0.02);
                int decider = (int) (Math.random() * 2);

                if (speed + adjustment > maxSpeed)
                {
                    speed -= adjustment;
                }
                else if (speed - adjustment < minSpeed)
                {
                    speed += adjustment;
                }
                else
                {
                    if (decider == 0)
                    {
                        speed += adjustment;
                    }
                    else
                    {
                        speed -= adjustment;
                    }
                }

                speed = (double) Math.round(speed * 10000) / 10000;
            }

            /* Rotation */
            switch (rotation)
            {
                case "ccw":
                    if (AYPercentage == 0.00 && AXPercentage != 0.00)
                    {
                        AXPercentage -= speed;
                        BXPercentage += speed;

                        /* Fail-safes so percentage doesn't end up outside accepted range for linear-gradient */
                        if (AXPercentage < 0.001)
                        {
                            AXPercentage = 0.00;
                        }
                        if (BXPercentage > 99.999)
                        {
                            BXPercentage = 100.00;
                        }
                    }
                    else if (AXPercentage == 0.00 && AYPercentage != 100.00)
                    {
                        AYPercentage += speed;
                        BYPercentage -= speed;

                        /* Fail-safes so percentage doesn't end up outside accepted range for linear-gradient */
                        if (AYPercentage > 99.999)
                        {
                            AYPercentage = 100.00;
                        }
                        if (BYPercentage < 0.001)
                        {
                            BYPercentage = 0.00;
                        }
                    }
                    else if (AYPercentage == 100.00 && AXPercentage != 100.00)
                    {
                        AXPercentage += speed;
                        BXPercentage -= speed;

                        /* Fail-safes so percentage doesn't end up outside accepted range for linear-gradient */
                        if (AXPercentage > 99.999)
                        {
                            AXPercentage = 100.00;
                        }
                        if (BXPercentage < 0.001)
                        {
                            BXPercentage = 0.00;
                        }
                    }
                    else if (AXPercentage == 100.00 && AYPercentage != 0.00)
                    {
                        AYPercentage -= speed;
                        BYPercentage += speed;

                        /* Fail-safes so percentage doesn't end up outside accepted range for linear-gradient */
                        if (AYPercentage < 0.001)
                        {
                            AYPercentage = 0.00;
                        }
                        if (BYPercentage > 99.999)
                        {
                            BYPercentage = 100.00;
                        }
                    }
                    break;

                case "cw":
                    if (AYPercentage == 0.00 && AXPercentage != 100.00)
                    {
                        AXPercentage += speed;
                        BXPercentage -= speed;

                        /* Fail-safes so percentage doesn't end up outside accepted range for linear-gradient */
                        if (AXPercentage > 99.999)
                        {
                            AXPercentage = 100.00;
                        }
                        if (BXPercentage < 0.001)
                        {
                            BXPercentage = 0.00;
                        }
                    }
                    else if (AXPercentage == 100.00 && AYPercentage != 100.00)
                    {
                        AYPercentage += speed;
                        BYPercentage -= speed;

                        /* Fail-safes so percentage doesn't end up outside accepted range for linear-gradient */
                        if (AYPercentage > 99.999)
                        {
                            AYPercentage = 100.00;
                        }
                        if (BYPercentage < 0.001)
                        {
                            BYPercentage = 0.00;
                        }
                    }
                    else if (AYPercentage == 100.00 && AXPercentage != 0.00)
                    {
                        AXPercentage -= speed;
                        BXPercentage += speed;

                        /* Fail-safes so percentage doesn't end up outside accepted range for linear-gradient */
                        if (AXPercentage < 0.001)
                        {
                            AXPercentage = 0.00;
                        }
                        if (BXPercentage > 99.999)
                        {
                            BXPercentage = 100.00;
                        }
                    }
                    else if (AXPercentage == 0.00 && AYPercentage != 0.00)
                    {
                        AYPercentage -= speed;
                        BYPercentage += speed;

                        /* Fail-safes so percentage doesn't end up outside accepted range for linear-gradient */
                        if (AYPercentage < 0.001)
                        {
                            AYPercentage = 0.00;
                        }
                        if (BYPercentage > 99.999)
                        {
                            BYPercentage = 100.00;
                        }
                    }
            }

            /* Hue */
            if (elapsedHundredths % 15 == 0)
            {
                /* Checks so hue 1 isn't outside range */
                if (hue >= maxHue)
                {
                    addHue = false;
                }
                if (hue <= minHue)
                {
                    addHue = true;
                }
                if (addHue)
                {
                    hue++;
                }
                else
                {
                    hue--;
                }
            }

            /* Luminosity */
            if (elapsedHundredths % 1100 == 0)
            {
                int decider = (int) (Math.random() * 2);

                if (luminosity >= maxLuminosity)
                {
                    luminosity--;
                }
                else if (luminosity <= minLuminosity)
                {
                    luminosity++;
                }
                else if (decider == 0)
                {
                    luminosity++;
                }
                else
                {
                    luminosity--;
                }
            }

            /* Creates color1 from all variables above */
            Color color1 = Color.web("hsl(" + hue + "," + saturation + "%," + luminosity + "%)");
            String color1String = "rgb(" + (int) (color1.getRed() * 255) + "," + (int) (color1.getGreen() * 255) + "," + (int) (color1.getBlue() * 255) + ")";

            /* Transparency */
            if (elapsedHundredths % 40 == 0)
            {
                if (transparency >= maxTransparency)
                {
                    addTransparency = false;
                    transparency = maxTransparency;
                }
                if (transparency <= minTransparency)
                {
                    addTransparency = true;
                    transparency = minTransparency;
                }
                if (addTransparency)
                {
                    transparency += 0.01;
                }
                else
                {
                    transparency -= 0.01;
                }

                transparency = (double) Math.round(transparency * 100) / 100;
            }

            /* Creates color 2 from hue of color2 and transparancy*/
            Color color2 = Color.web("hsl(" + (hue + 50) + ",100%,50%)");
            String color2String = "rgba(" + (int) (color2.getRed() * 255) + "," + (int) (color2.getGreen() * 255) + "," + (int) (color2.getBlue() * 255) + "," + transparency + ")";

            /* Style setter */
            String gradientString = "#000000, linear-gradient(from " + AXPercentage + "% " + AYPercentage + "% to " + BXPercentage + "% " + BYPercentage + "%, " + color1String + ", " + color2String + ")";
            navBarBG.setStyle("-fx-background-color:" + gradientString);
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    /* Changes logged in state on connect with strava icon */
    private void updateLoggedInIcon(boolean value)
    {
        currentAthlete.setLoggedIn(value);

        if (currentAthlete.getLoggedIn())
        {
            stravaConnectedAP.toFront();
            stravaConnectedAP.setOpacity(1.0);

            authorizationButton.setOpacity(0.0);
            authorizationButton.setDisable(true);
            authorizationButton.toBack();
        }
        else
        {
            authorizationButton.setOpacity(1.0);
            authorizationButton.setDisable(false);
            authorizationButton.toFront();

            stravaConnectedAP.toBack();
            stravaConnectedAP.setOpacity(0.0);
        }
    }

    /** Handles task buttons **/
    public void authorizationButton_Action()
    {
        openAuthorization();
    }
    public void attributesButton_Action()
    {

    }
    public void graphsButton_Action()
    {

    }
    public void logoButton_Action()
    {
        activitiesHTTPRequest(currentAthlete.getAccess_Token());
    }
    public void settingsButton_Action()
    {

    }
    public void refreshButton_Action()
    {
        if(tokenRefreshHTTPRequest(currentAthlete.getRefresh_Token()) && athleteInfoHTTPRequest(currentAthlete.getAccess_Token()))
        {
            displayStatusBar("Athlete information refresh successful.", 3000, "success");
        }
        /* Refresh activities */
    }
    public void signOutButton_Action()
    {
        File athleteInformation = new File("src/mainApp/athleteData/athleteInformation.json");
        File tokenData = new File("src/mainApp/athleteData/tokenData.json");
        if (athleteInformation.delete() && tokenData.delete())
        {
            currentAthlete = new Athlete();
            displayStatusBar("Signed out athlete." , 3000, "alert");
        }
        else
        {
            displayStatusBar("Sign out failed.", 3000, "error");
        }

        updateLoggedInIcon(false);
    }
    public void backButton_Action()
    {

    }

    /** Handles control bar buttons**/
    public void minimizeButton_Action()
    {
        main.window.setIconified(!main.window.isIconified());
    }

    public void maxRestoreButton_Action()
    {
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();

        if (!isMaximized)
        {
            previousWindowWidth = main.window.getWidth();
            previousWindowHeight = main.window.getHeight();

            previousWindowX = main.window.getX();
            previousWindowY = main.window.getY();

            main.window.setWidth(screenSize.getWidth());
            main.window.setHeight(screenSize.getHeight());

            main.window.centerOnScreen();

            isMaximized = true;
        }
        else
        {
            main.window.setWidth(previousWindowWidth);
            main.window.setHeight(previousWindowHeight);

            if (previousWindowX == 0 && firstResize && previousWindowY == 0)
            {
                main.window.centerOnScreen();
            }
            else
            {
                main.window.setX(previousWindowX);
                main.window.setY(previousWindowY);
            }

            isMaximized = false;
        }

        main.userPreferences.setFullscreen(isMaximized);
    }

    public void closeButton_Action()
    {
        main.close();
    }

    /* Run on initialization */
    public void makeScreenDraggable(HBox topNode)
    {
        topNode.setOnMousePressed((event) ->
        {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();

            if (yOffset < ResizeHelper.getBorder())
            {
                resizingActive = true;
            }
            else
            {
                maximizedSizeX = main.window.getWidth();
                maximizedSizeY = main.window.getWidth();

                main.window.setOpacity(0.8);
            }
        });
        topNode.setOnMouseDragged((event) ->
        {
            if (!resizingActive)
            {
                if (isMaximized)
                {
                    main.window.setWidth(previousWindowWidth);
                    main.window.setHeight(previousWindowHeight);

                    main.window.setX(event.getScreenX() - (xOffset * previousWindowWidth / maximizedSizeX));
                    main.window.setY(event.getScreenY() - (yOffset * previousWindowHeight / maximizedSizeY));
                }
                else
                {
                    main.window.setX(event.getScreenX() - xOffset);
                    main.window.setY(event.getScreenY() - yOffset);
                }

                main.window.setOpacity(0.8);
            }
        });
        topNode.setOnDragDone((event) ->
        {
            if (!resizingActive)
            {
                isMaximized = false;
                main.window.setOpacity(1.0);
            }
        });
        topNode.setOnMouseReleased((event) ->
        {
            if (!resizingActive)
            {
                isMaximized = false;
                main.window.setOpacity(1.0);
            }

            resizingActive = false;
        });
    }
}
