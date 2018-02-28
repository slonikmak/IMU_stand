package sample;

import java.util.Arrays;
import java.util.List;

public class MadgwickIMU extends IMU {
    MadgwickAHRS algorithm;

    public MadgwickIMU(int time){
        super(time);
        this.algorithm = new MadgwickAHRS(this.frequency, 1f);
        System.out.println("Madgwick "+ this.frequency+" "+time);
    }

    @Override
    public double[] getRawData() {
        return new double[0];
    }

    @Override
    public void update(List<Float> rawData) {
        System.out.println(rawData);
        algorithm.update(rawData.get(0), rawData.get(1), rawData.get(2), rawData.get(3), rawData.get(4), rawData.get(5));
        float[] angels = algorithm.get360deg();
        //yaw.setValue(angels[0]);
        roll.setValue(angels[1]);
        pitch.setValue(angels[2]);

    }
}
