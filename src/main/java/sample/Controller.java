package sample;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class Controller {
    @FXML
    HBox container;

    void addImuController(Node node){
        container.getChildren().add(node);
    }


}
