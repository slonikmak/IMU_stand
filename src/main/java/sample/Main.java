package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jssc.SerialPortException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main extends Application {

    static final int DELAY = 50;
    static boolean run = true;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 400, 600);
        primaryStage.setScene(scene);

        primaryStage.show();

        Controller controller = loader.getController();


        System.out.println(Arrays.toString(SerialReader.getPortList()));

        SerialReader serialReader = new SerialReader(115200, "COM6");

        List<IMU> imus = new ArrayList<>();
        imus.add(new MachonyIMU(DELAY));
        imus.add(new MadgwickIMU(DELAY));
        imus.add(new ComplimentIMU(DELAY));
        //imus.add(new SimpleIMU(DELAY));

        imus.forEach((imu)->{
            FXMLLoader imuLoader = new FXMLLoader();
            imuLoader.setLocation(getClass().getResource("/imuView.fxml"));
            try {
                Parent imuRoot = imuLoader.load();
                IMUController controller1 = imuLoader.getController();
                controller1.setIMU(imu);
                controller1.buildCamera();
                controller1.setName(imu.getClass().getSimpleName());

                controller.addImuController(imuRoot);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //MahonyAHRS mahonyAHRS = new MahonyAHRS(0.22f);
        //MadgwickAHRS madgwickAHRS = new MadgwickAHRS(0.1f);

        //MadgwickIMU imu = new MadgwickIMU();

        //IMU imu = new SimpleIMU(50);

        //MachonyIMU imu = new MachonyIMU();

        //IMU imu = new ComplimentIMU(DELAY);

        //controller.setIMU(imu);

        serialReader.setOnGetString(s -> {

                Platform.runLater(()->{
                    try {
                        //System.out.println(s);
                        List<Float> data = Arrays.stream(s.split(",")).map(Float::parseFloat).collect(Collectors.toList());
                        //float[] angels = mahonyAHRS.updateIMU(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4), data.get(5));
                        imus.forEach((imu)->imu.update(data));
                        //System.out.println(Arrays.toString(madgwickAHRS.get360deg()));
                        //System.out.println("Result: "+angels[0]+" "+angels[1]+" "+angels[2]);
                    } catch (NumberFormatException ignored) {
                    }
                });


        });

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("close");
            run = false;
        });


        new Thread(() -> {
            try {
                while (run) {
                    Thread.sleep(DELAY);
                    serialReader.serialWriteString("s");
                }
                serialReader.close();
            } catch (SerialPortException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
