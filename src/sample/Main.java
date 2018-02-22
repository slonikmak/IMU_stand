package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("imuView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 400, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        IMUController controller = loader.getController();
        controller.buildCamera();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
