package sample;

import javafx.beans.property.SimpleDoubleProperty;

import java.util.List;

public class MachonyIMU extends IMU {

    MahonyAHRS algorithm;

    public MachonyIMU(int time){
        super(time);
        this.algorithm = new MahonyAHRS(0.05f);
    }


    @Override
    public double[] getRawData() {
        return new double[0];
    }

    @Override
    public void update(List<Float> rawData) {
        float[] angels = algorithm.updateIMU(rawData.get(0), rawData.get(1), rawData.get(2), rawData.get(3), rawData.get(4), rawData.get(5));
        yaw.setValue(angels[0]);
        roll.setValue(angels[1]);
        pitch.setValue(angels[2]);
    }
}
