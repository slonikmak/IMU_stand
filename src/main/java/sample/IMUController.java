package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.StringConverter;

public class IMUController {
    private IMU imu;

    private AnchorPane cubeView;

    @FXML
    private AnchorPane cubePane;

    @FXML
    private Slider xSlider;

    @FXML
    private CheckBox xInvert;

    @FXML
    private Slider ySlider;

    @FXML
    private Slider zSlider;

    @FXML
    private CheckBox yInvert;

    @FXML
    private CheckBox zInvert;
    @FXML
    private Label xRawLabel;
    @FXML
    private Label yRawLabel;
    @FXML
    private Label zRawLabel;
    @FXML
    private Label rollLabel;
    @FXML
    private Label pitchLabel;
    @FXML
    private Label yawLabel;

    @FXML
    private Label nameLabel;


    PerspectiveCamera camera;
    SubScene subScene;

    private Rotate rotateX;
    private Rotate rotateY;
    private Rotate rotateZ;

    private static final double CAMERA_INITIAL_DISTANCE = -600;
    private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;

    private final Group group = new Group();
    private final Group axisGroup = new Group();

    private final int WIDTH = 400;
    private final int HEIGHT = 400;

    private static final double AXIS_LENGTH = 50.0;


    public void initialize() {
        cubeView = new AnchorPane();
        cubeView.setPrefHeight(WIDTH);
        cubeView.setPrefWidth(HEIGHT);

        subScene = new SubScene(cubeView, 400, 400);
        cubePane.getChildren().addAll(subScene);

        cubeView.getChildren().add(group);

        setSliders();

        buildCube();

        buildAxes();


        System.out.println("init");
    }

    private void setSliders() {
        xSlider.setMin(-180);
        xSlider.setMax(180);
        ySlider.setMin(-180);
        ySlider.setMax(180);
        zSlider.setMin(-180);
        zSlider.setMax(180);

        xSlider.setValue(0);
        ySlider.setValue(0);
        zSlider.setValue(0);

        rotateX = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
        rotateY = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        rotateZ = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);

        rotateX.angleProperty().bindBidirectional(xSlider.valueProperty());
        rotateY.angleProperty().bindBidirectional(ySlider.valueProperty());
        rotateZ.angleProperty().bindBidirectional(zSlider.valueProperty());

        group.getTransforms().add(rotateX);
        group.getTransforms().add(rotateY);
        group.getTransforms().add(rotateZ);
    }

    private void buildCube() {
        final PhongMaterial cubeMaterial = new PhongMaterial();
        cubeMaterial.setDiffuseColor(Color.YELLOW);
        cubeMaterial.setSpecularColor(Color.YELLOWGREEN);

        Box cube = new Box(WIDTH / 4, HEIGHT / 8, WIDTH / 2);
        //cube.getTransforms().addAll(new Translate(WIDTH/2, HEIGHT/2, 200));

        cube.setMaterial(cubeMaterial);
        group.getChildren().add(cube);
    }


    private void buildAxes() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Cylinder xAxis = new Cylinder(1, AXIS_LENGTH);
        final Cylinder yAxis = new Cylinder(1, AXIS_LENGTH);
        final Cylinder zAxis = new Cylinder(1, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        xAxis.getTransforms().addAll(new Rotate(90, Rotate.Z_AXIS), new Translate(0, -AXIS_LENGTH / 2));
        yAxis.getTransforms().addAll(new Translate(0, -AXIS_LENGTH / 2));
        zAxis.getTransforms().addAll(new Rotate(90, Rotate.X_AXIS), new Translate(0, AXIS_LENGTH / 2));

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        //axisGroup.getTransforms().addAll(new Translate(WIDTH/2, HEIGHT/2));
        group.getChildren().add(axisGroup);
        group.setVisible(true);
    }

    public void buildCamera() {
        camera = new PerspectiveCamera(true);
        //camera.getTransforms().addAll(new Translate(200,200,0));
        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        subScene.setCamera(camera);

    }

    public void setIMU(IMU imu) {

        System.out.println("set");
        this.imu = imu;
        rotateX.angleProperty().bind(imu.pitchProperty());
        rotateY.angleProperty().bind(imu.yawProperty());
        rotateZ.angleProperty().bind(imu.rollProperty());
        //rollLabel.textProperty().bind(imu.rollProperty().asString());
        //pitchLabel.textProperty().bindBidirectional(imu.pitchProperty(), new MyStringConverternew());
        //yawLabel.textProperty().bindBidirectional(imu.yawProperty(), new MyStringConverternew());

        imu.rollProperty().addListener((a,b,c)->{
            Platform.runLater(()->{
                rollLabel.setText(String.valueOf(c.intValue()));
            });
        });
        imu.pitchProperty().addListener((a,b,c)->{
            Platform.runLater(()->{
                pitchLabel.setText(String.valueOf(c.intValue()));
            });
        });
        imu.yawProperty().addListener((a,b,c)->{
            Platform.runLater(()->{
                yawLabel.setText(String.valueOf(c.intValue()));
            });
        });

    }

    public void setName(String name){
        nameLabel.setText(name);
    }
}
