package sample;

import javafx.beans.property.DoubleProperty;

import java.util.List;

public class ComplimentIMU extends IMU {
    private double angle_gx = 0, angle_gy=0, angle_gz=0;
    private final double FK = 0.1;

    public ComplimentIMU(int time){
        super(time);
    }


    @Override
    public double[] getRawData() {
        return new double[0];
    }

    @Override
    public void update(List<Float> rawData) {
        double angle_ax = 90 - Math.toDegrees(Math.acos(rawData.get(3)));
        double angle_ay = 90 - Math.toDegrees(Math.acos(rawData.get(4)));
        double angle_az = 90 - Math.toDegrees(Math.acos(rawData.get(5)));
        angle_gx = angle_gx + rawData.get(0) * this.time/1000.0;
        angle_gx = angle_gx*(1-FK) + angle_ax*FK;

        angle_gy = angle_gy + rawData.get(1) * this.time/1000.0;
        angle_gy = angle_gy*(1-FK) + angle_ay*FK;

        angle_gz = angle_gz + rawData.get(2) * this.time/1000.0;
        angle_gz = angle_gz*(1-FK) + angle_az*FK;

        //System.out.println(angle_gx+" "+angle_gy+" "+angle_gz);
        pitch.setValue(angle_gy);
        roll.setValue(angle_gx);
        //yaw.setValue(angle_gz);
    }
}
