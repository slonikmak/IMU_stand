package com.oceanos.tracker;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import jssc.SerialPortException;
import sample.SerialReader;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends Application {
    static final int DELAY = 50;
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/trackerView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);

        primaryStage.show();

        TrackerController controller = loader.getController();
        Tracker tracker = new Tracker(DELAY);
        controller.setTracker(tracker);

        SerialReader reader = new SerialReader(115200, "COM6");

        Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.millis(DELAY), event -> {
            try {
                reader.serialWriteString("s");
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }));

        reader.setOnGetString(d->{
            Platform.runLater(()->{
                try {
                    List<Float> data = Arrays.stream(d.split(",")).map(Float::parseFloat).collect(Collectors.toList());
                    //float[] angels = mahonyAHRS.updateIMU(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4), data.get(5));
                   tracker.update(data);
                } catch (NumberFormatException ignored) {
                }
            });
        });

        primaryStage.setOnCloseRequest(e->{
            fiveSecondsWonder.stop();
            try {
                reader.close();
            } catch (SerialPortException e1) {
                e1.printStackTrace();
            }
        });

        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();


    }

    public static void main(String[] args) {
        launch(args);
    }
}
