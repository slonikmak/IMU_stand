package com.oceanos.tracker;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Tracker {
    FloatProperty x;
    FloatProperty y;
    int delay;
    float vX, vY, oldAX, oldAY;
    final float alpha = 0.5f;
    int calibrationCount = 20;
    float dX, dY;
    final float G = 9.81f;
    float time;


    public Tracker(int delay){
        this.delay  =delay;
        x = new SimpleFloatProperty(0);
        y = new SimpleFloatProperty(0);
        vX = 0;
        vY = 0;
        oldAX = 0;
        oldAY = 0;
        time = delay/1000f;
        System.out.println(time);
    }

    public void update(List<Float> rawData){

        if (calibrationCount>0){
            calibrationCount--;
            calibrate(rawData);
        } else {

            float aX = (rawData.get(3)-dX)*G;
            float aY = (rawData.get(4)-dY)*G;
            //new BigDecimal(aY*alpha+(oldAY*(1f-alpha))).setScale(1, RoundingMode.HALF_UP).floatValue()

            oldAX = new BigDecimal(aX*alpha+(oldAX*(1f-alpha))).setScale(1, RoundingMode.HALF_UP).floatValue();
            oldAY = new BigDecimal(aY*alpha+(oldAY*(1f-alpha))).setScale(1, RoundingMode.HALF_UP).floatValue();

            System.out.println(oldAX+" "+oldAY);
            //System.out.println(aX+" "+aY);

            vX = vX+oldAX*time;
            vY = vY+oldAY*time;

            //System.out.println(vX+" "+vY);

            x.setValue((x.getValue()+vX*(time)));
            y.setValue((y.getValue()+vY*(time)));

            //System.out.println(x.get()+" "+y.get());
        }

    }

    public FloatProperty xProperty() {
        return x;
    }

    public FloatProperty yProperty() {
        return y;
    }


    public void calibrate(List<Float> rawData){
        if (calibrationCount>0){
            dX+=rawData.get(3);
            dY+=rawData.get(4);
        } else {
            dX = dX/20;
            dY = dY/20;
        }
    }
}
