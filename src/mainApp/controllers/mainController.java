package mainApp.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Worker;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.util.Duration;
import mainApp.ResizeHelper;
import mainApp.Toolbox;
import mainApp.main;

import java.net.URL;
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

    /* Status bar */
    public Label statusBarLabel;
    public Pane statusBarPane;
    private Timeline timeline;
    private int timeTicks;

    /* Variables */
    private boolean webViewFinished;

    /* Run on first startup */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        makeScreenDraggable(topHBox);
    }

    private void openAuthorization()
    {
        /* Loads strava oauth, strava oauth redirects to login screen automatically */
        authorizationWV.getEngine().load("https://www.strava.com/oauth/authorize?client_id=67536&response_type=code&redirect_uri=http://localhost&approval_prompt=force&scope=read_all,activity:read_all");

        webViewFinished = false;

        displayStatusBar("Strava authorization loading....", 2000);

        authorizationWV.getEngine().getLoadWorker().stateProperty().addListener((observable, oldState, newState) ->
        {
            System.out.println(newState);
            System.out.println(authorizationWV.getEngine().getLocation());

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
                if (authorizationWV.getEngine().getLocation().equals("https://www.strava.com/oauth/authorize?client_id=67536&response_type=code&redirect_uri=http://localhost&approval_prompt=force&scope=read_all,activity:read_all"))
                {
                    stopAuthorizationWV();

                    displayStatusBar("Error: No internet connection.", 5000);
                }

                /* Checks if domain is localhost, if true program handles url with parameters */
                String urlString = authorizationWV.getEngine().getLocation();
                String urlDomain = urlString.substring(Toolbox.findNIndexOf('/', 2, urlString) + 1, Toolbox.findNIndexOf('/', 3, urlString));

                if (urlDomain.equals("localhost"))
                {
                    handleAuthCode(urlString);
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

    private void stopAuthorizationWV()
    {
        authorizationWV.toBack();
        webViewFinished = true;
        authorizationWV.getEngine().load(null);
    }

    private void handleAuthCode(String url)
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

            if (!scope.equals("read,activity:read_all,read_all"))
            {
                displayStatusBar("Error: Not all access given.", 10000);
            }

            /* If scopes are allowed, program extracts authorization code */
            else if (url.contains("code="))
            {
                String code = url.substring(url.indexOf("code=") + 5, url.indexOf('&', url.indexOf("code=") + 6));


            }
        }

        stopAuthorizationWV();
    }

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

    }
    public void signOutButton_Action()
    {
        System.out.println(authorizationWV.getEngine().getLocation());
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
