package sample;

import java.util.List;

public class SimpleIMU extends IMU {

    public SimpleIMU(int time){
        super(time);
    }


    @Override
    public double[] getRawData() {
        return new double[0];
    }

    @Override
    public void update(List<Float> rawData) {

        //System.out.println(90 - Math.toDegrees(Math.acos(rawData.get(3))));
        roll.setValue(90 - Math.toDegrees(Math.acos(rawData.get(3))));
        pitch.setValue(90 - Math.toDegrees(Math.acos(rawData.get(4))));
    }
}
