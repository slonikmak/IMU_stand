package sample;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.List;

public abstract class IMU {
    DoubleProperty pitch;
    DoubleProperty roll;
    DoubleProperty yaw;
    float frequency;
    int time;

    public IMU(int time){
        this.time = time;
        this.frequency = 1/time;
        pitch = new SimpleDoubleProperty(0);
        roll = new SimpleDoubleProperty(0);
        yaw = new SimpleDoubleProperty(0);
    }

    DoubleProperty pitchProperty(){
        return pitch;
    }
    DoubleProperty rollProperty(){
        return roll;
    }
    DoubleProperty yawProperty(){
        return yaw;
    }

    abstract double[] getRawData();
    abstract void update(List<Float> rawData);
}
