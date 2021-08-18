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
import mainApp.Athlete;
import mainApp.ResizeHelper;
import mainApp.Toolbox;
import mainApp.main;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private double previousWindowWidth;
    private double previousWindowHeight;

    private double previousWindowX;
    private double previousWindowY;

    /* Scene */
    public StackPane contentSP;
    public WebView authorizationWV;
    public HBox topHBox;
    public StackPane authorization_SP;
    public VBox navBarBG;
    public BorderPane mainBG;


    /* Status bar */
    public Label statusBarLabel;
    public Pane statusBarPane;
    private Timeline timeline;
    private int timeTicks;

    /* NavBar background animation */
    private long elapsedHundredths; //100 fps

    private double speed = 0.050;
    private final double maxSpeed = 0.100;
    private final double minSpeed = 0.010;
    private String rotation = "cw";

    private int hue1 = 223;
    private final int maxHue = 300;
    private final int minHue = 130;
    private boolean addHue = true;

    private int hue2;
    private int hueMod = 50;
    private boolean addHueMod = false;
    private final int maxHueMod = 50;
    private final int minHueMod = -50;

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

        /* Loads token data from file, if refresh is necessary HTTP request is sent and access token is updated */
        if (currentAthlete.loadTokenDataFromFile())
        {
            displayStatusBar("Token data loaded from file.", 5000);

            if (checkRefreshNecessary())
            {
                tokenRefreshHTTPRequest(currentAthlete.getRefresh_Token());
            }
            if (!athleteInfoHTTPRequest(currentAthlete.getAccess_Token()))
            {
                currentAthlete.loadAthleteDataFromFile();
            }
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

        displayStatusBar("Strava authorization loading....", 2000);

        authorizationWV.getEngine().getLoadWorker().stateProperty().addListener((observable, oldState, newState) ->
        {
            /* Checked before website is reached */
            if (newState == Worker.State.SCHEDULED)
            {
                /* Stops webview if user exits strava login (to prevent user from navigating outside strava oauth) */
                if (!checkValidAuthorizationWV_test())
                {
                    stopAuthorizationWV();

                    displayStatusBar("Error: App cannot leave authorization.", 2000);
                }
            }

            /* Checks if there is no internet connection and stops*/
            if (newState == Worker.State.FAILED)
            {
                if (authorizationWV.getEngine().getLocation().equals("https://www.strava.com/oauth/authorize?client_id=67536&response_type=code&redirect_uri=http://localhost&approval_prompt=force&scope=read_all,activity:read_all,profile:read_all"))
                {
                    stopAuthorizationWV();

                    displayStatusBar("Error: No internet connection.", 5000);
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
    private boolean checkValidAuthorizationWV_test()
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
                displayStatusBar("Error: No access given.", 7000);
            }
            else
            {
                displayStatusBar("Error: Unknown error: " + error, 10000);
            }
        }

        /* Checks if all scopes are allowed */
        else if (url.contains("scope="))
        {
            String scope = url.substring(url.indexOf("scope=") + 6);

            if (!scope.equals("read,activity:read_all,profile:read_all,read_all"))
            {
                displayStatusBar("Error: Not all access given.", 10000);
            }

            /* If scopes are allowed, program extracts authorization code */
            else if (url.contains("code="))
            {
                String code = url.substring(url.indexOf("code=") + 5, url.indexOf('&', url.indexOf("code=") + 5));


                authTokenHTTPRequest(code);
                athleteInfoHTTPRequest(currentAthlete.getAccess_Token());

                currentAthlete.saveTokenDataToFile();
                currentAthlete.saveAthleteDataToFile();
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

                    displayStatusBar("Athlete information retrieval successful.", 5000);
                }
                case 401 -> displayStatusBar("Token error: 401, Unauthorized.", 5000);
                case 403 -> displayStatusBar("Token error: 403, Forbidden, you cannot access.", 5000);
                case 404 -> displayStatusBar("Token error: 404, Not found.", 5000);
                case 429 -> displayStatusBar("Token error: 429, Too many requests. Try again later.", 5000);
                case 500 -> displayStatusBar("Token error: 500, Strava is having issues.", 5000);
            }
            http.disconnect();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /** Data retrieval, from strava or file if strava is not working **/
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

    /* Token refresh */
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
                        displayStatusBar("Token refresh successful.", 5000);
                    }
                    case 401 -> displayStatusBar("Token error: 401, Unauthorized.", 5000);
                    case 403 -> displayStatusBar("Token error: 403, Forbidden, you cannot access.", 5000);
                    case 404 -> displayStatusBar("Token error: 404, Not found.", 5000);
                    case 429 -> displayStatusBar("Token error: 429, Too many requests. Try again later.", 5000);
                    case 500 -> displayStatusBar("Token error: 500, Strava is having issues.", 5000);
                }
                http.disconnect();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            displayStatusBar("Refresh error: Faulty refresh Token", 5000);
        }

        return successfulRequest;
    }

    /* Retrieves athlete data with HTTP request to strava api, sends response to currentAthlete */
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
                        displayStatusBar("Athlete information retrieval successful.", 5000);
                        successfulRequest = true;
                    }
                    case 401 -> displayStatusBar("Token error: 401, Unauthorized.", 5000);
                    case 403 -> displayStatusBar("Token error: 403, Forbidden, you cannot access.", 5000);
                    case 404 -> displayStatusBar("Token error: 404, Not found.", 5000);
                    case 429 -> displayStatusBar("Token error: 429, Too many requests. Try again later.", 5000);
                    case 500 -> displayStatusBar("Token error: 500, Strava is having issues.", 5000);
                }
                http.disconnect();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            displayStatusBar("Error: No access token found.", 5000);
        }

        return successfulRequest;
    }

    /** UI methods **/
    private void displayStatusBar(String text, int durationMillis)
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
                if (hue1 >= maxHue)
                {
                    addHue = false;
                }
                if (hue1 <= minHue)
                {
                    addHue = true;
                }
                if (addHue)
                {
                    hue1++;
                }
                else
                {
                    hue1--;
                }

                /* Hue 2 Modifier */
                if (hueMod >= maxHueMod)
                {
                    addHueMod = false;
                }
                if (hueMod <= minHueMod)
                {
                    addHueMod = true;
                }
                if (addHueMod)
                {
                    hueMod++;
                }
                else
                {
                    hueMod--;
                }
                hue2 = hue1 + hueMod;
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
            Color color1 = Color.web("hsl(" + hue1 + "," + saturation + "%," + luminosity + "%)");
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
            Color color2 = Color.web("hsl(" + (hue1 + 50) + ",100%,50%)");
            String color2String = "rgba(" + (int) (color2.getRed() * 255) + "," + (int) (color2.getGreen() * 255) + "," + (int) (color2.getBlue() * 255) + "," + transparency + ")";

            /* Style setter */
            String gradientString = "#000000, linear-gradient(from " + AXPercentage + "% " + AYPercentage + "% to " + BXPercentage + "% " + BYPercentage + "%, " + color1String + ", " + color2String + ")";
            navBarBG.setStyle("-fx-background-color:" + gradientString);
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
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
    }
    public void settingsButton_Action()
    {

    }
    public void refreshButton_Action()
    {
        /* Refresh access token */
        /* Refresh athlete */
        /* Refresh activities */
    }
    public void signOutButton_Action()
    {
        File athleteInformation = new File("src/mainApp/athleteData/athleteInformation.txt");
        File tokenData = new File("src/mainApp/athleteData/tokenData.txt");
        if (athleteInformation.delete() && tokenData.delete())
        {
            currentAthlete = new Athlete();
            displayStatusBar("Signed out athlete." , 3000);
        }
        else
        {
            displayStatusBar("Sign out failed.", 3000);
        }
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
        //main.window.setMaximized(!main.window.isMaximized());
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

            main.window.setX(previousWindowX);
            main.window.setY(previousWindowY);

            isMaximized = false;
        }
    }

    public void closeButton_Action()
    {
        main.window.close();
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
