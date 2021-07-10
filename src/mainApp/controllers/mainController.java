package mainApp.controllers;

import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class mainController implements Initializable
{
    /* Scene */
    public StackPane contentSP;
    public WebView authorizationWV;



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
        authorizationWV.getEngine().load("https://www.strava.com/oauth/authorize?client_id=67536&response_type=code&redirect_uri=http://localhost&approval_prompt=force&scope=read_all,activity:read_all");
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

    }
}
