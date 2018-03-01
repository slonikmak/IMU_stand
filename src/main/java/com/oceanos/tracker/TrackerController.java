package com.oceanos.tracker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

public class TrackerController {
    @FXML
    private AnchorPane trackPane;

    @FXML
    private Label xLabel;

    @FXML
    private Label yLabel;

    @FXML
    private Circle circle;

    Tracker tracker;

    public void setTracker(Tracker tracker) {
        this.tracker = tracker;
        tracker.xProperty().addListener((a,b,c)->updateCircle());
        tracker.yProperty().addListener((a,b,c)->updateCircle());
    }

    public void initialize(){
        //circle.setTranslateX(-circle.getBoundsInParent().getMaxX()+circle.getRadius());
        //circle.setTranslateY(-circle.getBoundsInParent().getMaxY()+circle.getRadius());

    }

    private void updateCircle(){
        circle.setTranslateX(circle.getTranslateX()+tracker.xProperty().get());
        circle.setTranslateY(circle.getTranslateY()+tracker.yProperty().get());
    }


}
