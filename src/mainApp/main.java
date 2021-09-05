package mainApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class main extends Application {

    public static Stage window;
    public static Scene scene;
    public static UserPreferences userPreferences;

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        userPreferences = new UserPreferences();

        Parent root = FXMLLoader.load(getClass().getResource("scenes/main.fxml"));
        window.setTitle("Strava To PDF");
        currentScene(root);

        window.setScene(scene);

        window.initStyle(StageStyle.UNDECORATED);
        ResizeHelper.addResizeListener(window);
        window.setOnCloseRequest(e -> close());

        window.show();
    }

    private void currentScene(Parent root )
    {
        scene = new Scene(root, 1280, 720);

        if (userPreferences.wantFullscreen())
        {
            Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();

            main.window.setWidth(screenSize.getWidth());
            main.window.setHeight(screenSize.getHeight());

            main.window.centerOnScreen();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void close()
    {
        userPreferences.saveUserPref();

        window.close();
    }
}
