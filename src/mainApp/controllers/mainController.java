package mainApp.controllers;

import javafx.concurrent.Worker;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class mainController implements Initializable
{
    /* Scene */
    public StackPane contentSP;
    public WebView authorizationWV;

    private boolean webViewFinished;
    private Worker.State stateOutside;

    /* Run on first startup */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

    }

    private void handleAuthorization()
    {
        openAuthorization();
    }

    private void openAuthorization()
    {
        /* Loads strava oauth, strava oauth redirects to login screen automatically */
        authorizationWV.getEngine().load("https://www.strava.com/oauth/authorize?client_id=67536&response_type=code&redirect_uri=http://localhost&approval_prompt=force&scope=read_all,activity:read_all");

        webViewFinished = false;

        /* +++ Add loading message in status bar */
        authorizationWV.getEngine().getLoadWorker().stateProperty().addListener((observable, oldState, newState) ->
        {
            System.out.println(newState);
            System.out.println(authorizationWV.getEngine().getLocation());

            /* Checked before website is reached */
            if (newState == Worker.State.SCHEDULED)
            {
                /* Stops webview if user exits strava (to prevent user from navigating outside strava authorization */
                if (!checkValidAuthorizationWV())
                {
                    stopAuthorizationWV();

                    /* +++ Add error message in status bar */
                }
            }

            if (newState == Worker.State.SUCCEEDED || newState == Worker.State.FAILED)
            {
                /* Checks if user is heading to strava dashboard, and cuts off in that case */
                if (authorizationWV.getEngine().getLocation().equals("https://www.strava.com/dashboard"))
                {
                    stopAuthorizationWV();

                    /* +++ Add error message in status bar */
                }

                /* Brings webview to front if not in front */
                if (contentSP.getChildren().get(contentSP.getChildren().size() - 1) != authorizationWV && !webViewFinished)
                {
                    authorizationWV.toFront();
                }
            }
        });
    }
    private boolean checkValidAuthorizationWV()
    {
        String urlString = authorizationWV.getEngine().getLocation();

        boolean validDomain;

        /* If url is about:blank, program skips check. Only happens when null is loaded */
        if (!urlString.equals("about:blank"))
        {
            String domain = urlString.substring(findNIndexOf('/', 2, urlString) + 1, findNIndexOf('/', 3, urlString));
            validDomain = domain.contains("strava") || domain.contains("localhost");
        }
        else
        {
            validDomain = true;
        }

        return validDomain;
    }

    private int findNIndexOf(char character, int n, String string)
    {
        int index = -1;

        for (int i = 0; i < n; i++)
        {
            index = string.indexOf(character, index + 1);
        }

        return index;
    }

    private void stopAuthorizationWV()
    {
        authorizationWV.toBack();
        webViewFinished = true;
        authorizationWV.getEngine().load(null);
    }

    /* Handles buttons */
    public void authorizationButton_Action()
    {
        handleAuthorization();
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
        System.out.println("------");
        System.out.println("State: " + stateOutside);
        System.out.println("Url: " + authorizationWV.getEngine().getLocation());
        System.out.println("------");
    }
}
