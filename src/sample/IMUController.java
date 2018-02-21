package sample;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;

public class IMUController {
    @FXML
    private AnchorPane cubeView;

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

    private final Group group = new Group();

    private static final double AXIS_LENGTH = 250.0;


    public void initialize(){

        cubeView.getChildren().addAll(group);

        buildAxes();
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

        final Cylinder xAxis = new Cylinder(10, AXIS_LENGTH, 1);
        final Cylinder yAxis = new Cylinder(10, AXIS_LENGTH, 1);
        final Cylinder zAxis = new Cylinder(10, 1, 1);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        group.getChildren().addAll(xAxis, yAxis, zAxis);
        group.setVisible(true);


    }

}
