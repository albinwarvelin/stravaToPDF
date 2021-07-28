package mainApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class main extends Application {

    public static Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("scenes/main.fxml"));
        window.setTitle("Strava To PDF");
        window.setScene(new Scene(root, 1280, 720));
        window.initStyle(StageStyle.UNDECORATED);
        ResizeHelper.addResizeListener(window);
        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
