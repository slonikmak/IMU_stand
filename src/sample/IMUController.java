package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class IMUController {
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

    PerspectiveCamera camera;
    SubScene subScene;

    private Rotate rotateX;
    private Rotate rotateY;
    private Rotate rotateZ;

    private static final double CAMERA_INITIAL_DISTANCE = -450;
    private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;

    private final Group group = new Group();

    private static final double AXIS_LENGTH = 100.0;


    public void initialize(){
        cubeView = new AnchorPane();
        cubeView.setPrefHeight(400);
        cubeView.setPrefWidth(400);

        subScene = new SubScene(cubeView, 400,400);
        cubePane.getChildren().addAll(subScene);

        cubeView.getChildren().add(group);

        setSliders();

        buildAxes();

        System.out.println("init");
    }

    private void setSliders(){
        xSlider.setMin(0);
        xSlider.setMax(360);
        ySlider.setMin(0);
        ySlider.setMax(360);
        zSlider.setMin(0);
        zSlider.setMax(360);

        rotateX = new Rotate(0, 200, 200,0, Rotate.X_AXIS);
        rotateY = new Rotate(0, 200,200,0, Rotate.Y_AXIS);
        rotateZ = new Rotate(0, 200,200,0, Rotate.Z_AXIS);

        rotateX.angleProperty().bind(xSlider.valueProperty());
        rotateY.angleProperty().bind(ySlider.valueProperty());
        rotateZ.angleProperty().bind(zSlider.valueProperty());

        group.getTransforms().add(rotateX);
        group.getTransforms().add(rotateY);
        group.getTransforms().add(rotateZ);
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

        final Cylinder xAxis = new Cylinder(2, AXIS_LENGTH);
        final Cylinder yAxis = new Cylinder(2, AXIS_LENGTH);
        final Cylinder zAxis = new Cylinder(2, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        xAxis.getTransforms().addAll(new Rotate(90, Rotate.Z_AXIS),new Translate(200,-250));
        yAxis.getTransforms().addAll(new Translate(200,150));

        group.getChildren().addAll(xAxis, yAxis, zAxis);
        group.setVisible(true);
    }

    public void buildCamera(){
        camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(new Translate(200,200,-200), new Rotate(100));
        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        subScene.setCamera(camera);

    }

    public PerspectiveCamera getCamera() {
        return camera;
    }
}
