package mainApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class main extends Application {

    public static Stage window;
    public static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("scenes/main.fxml"));
        window.setTitle("Strava To PDF");
        scene = new Scene(root, 1280, 720);
        window.setScene(scene);

        window.initStyle(StageStyle.UNDECORATED);
        ResizeHelper.addResizeListener(window);
        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
