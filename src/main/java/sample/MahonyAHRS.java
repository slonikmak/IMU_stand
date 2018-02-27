/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

import javafx.beans.property.FloatProperty;

/**
 *
 * @author Philipp
 */
public class MahonyAHRS {

    float twoKpDef = (10.5f * 0.5f);        // 2 * proportional gain
    float twoKiDef = (2.0f * 0.5f);

    float twoKp = twoKpDef;                                                                                        // 2 * proportional gain (Kp)
    float twoKi = twoKiDef;

    float q0 = 1.0f, q1 = 0.0f, q2 = 0.0f, q3 = 0.0f;                                        // quaternion of sensor frame relative to auxiliary frame
    float integralFBx = 0.0f, integralFBy = 0.0f, integralFBz = 0.0f;        // integral error terms scaled by Ki

    final float M_PI = 3.1415926535f;
    final float DEGREE = (float) 180.0 / M_PI;
    float invSampleFreq;
    float gx;
    float gy;
    float gz;
//---------------------------------------------------------------------------------------------------
// Function declarations
//====================================================================================================
// Functions
//---------------------------------------------------------------------------------------------------
// AHRS algorithm update

    public MahonyAHRS(float sampleFreqDef) {
        invSampleFreq = 1.0f / sampleFreqDef;
    }

    public float[] update(float gx, float gy, float gz, float ax, float ay, float az, float mx, float my, float mz) {
        this.gx = gx;
        this.gy = gy;
        this.gz = gz;
        float recipNorm;
        float q0q0, q0q1, q0q2, q0q3, q1q1, q1q2, q1q3, q2q2, q2q3, q3q3;
        float hx, hy, bx, bz;
        float halfvx, halfvy, halfvz, halfwx, halfwy, halfwz;
        float halfex, halfey, halfez;
        float qa, qb, qc;

        // Use IMU algorithm if magnetometer measurement invalid (avoids NaN in magnetometer normalisation)
        if ((mx == 0.0f) && (my == 0.0f) && (mz == 0.0f)) {
            return updateIMU(gx, gy, gz, ax, ay, az);

            //  return getAngles();
        }

        // Compute feedback only if accelerometer measurement valid (avoids NaN in accelerometer normalisation)
        if (!((ax == 0.0f) && (ay == 0.0f) && (az == 0.0f))) {

            // Normalise accelerometer measurement
            recipNorm = invSqrt(ax * ax + ay * ay + az * az);
            ax *= recipNorm;
            ay *= recipNorm;
            az *= recipNorm;

            // Normalise magnetometer measurement
            recipNorm = invSqrt(mx * mx + my * my + mz * mz);
            mx *= recipNorm;
            my *= recipNorm;
            mz *= recipNorm;

            // Auxiliary variables to avoid repeated arithmetic
            q0q0 = q0 * q0;
            q0q1 = q0 * q1;
            q0q2 = q0 * q2;
            q0q3 = q0 * q3;
            q1q1 = q1 * q1;
            q1q2 = q1 * q2;
            q1q3 = q1 * q3;
            q2q2 = q2 * q2;
            q2q3 = q2 * q3;
            q3q3 = q3 * q3;

            // Reference direction of Earth's magnetic field
            hx = 2.0f * (mx * (0.5f - q2q2 - q3q3) + my * (q1q2 - q0q3) + mz * (q1q3 + q0q2));
            hy = 2.0f * (mx * (q1q2 + q0q3) + my * (0.5f - q1q1 - q3q3) + mz * (q2q3 - q0q1));
            bx = (float) Math.sqrt(hx * hx + hy * hy);
            bz = 2.0f * (mx * (q1q3 - q0q2) + my * (q2q3 + q0q1) + mz * (0.5f - q1q1 - q2q2));

            // Estimated direction of gravity and magnetic field
            halfvx = q1q3 - q0q2;
            halfvy = q0q1 + q2q3;
            halfvz = q0q0 - 0.5f + q3q3;

            halfwx = bx * (0.5f - q2q2 - q3q3) + bz * (q1q3 - q0q2);
            halfwy = bx * (q1q2 - q0q3) + bz * (q0q1 + q2q3);
            halfwz = bx * (q0q2 + q1q3) + bz * (0.5f - q1q1 - q2q2);

            // Error is sum of cross product between estimated direction and measured direction of field vectors
            halfex = (ay * halfvz - az * halfvy) + (my * halfwz - mz * halfwy);
            halfey = (az * halfvx - ax * halfvz) + (mz * halfwx - mx * halfwz);
            halfez = (ax * halfvy - ay * halfvx) + (mx * halfwy - my * halfwx);

            // Compute and apply integral feedback if enabled
            if (twoKi > 0.0f) {
                integralFBx += twoKi * halfex * invSampleFreq;        // integral error scaled by Ki
                integralFBy += twoKi * halfey * invSampleFreq;
                integralFBz += twoKi * halfez * invSampleFreq;
                gx += integralFBx;        // apply integral feedback
                gy += integralFBy;
                gz += integralFBz;
            } else {
                integralFBx = 0.0f;        // prevent integral windup
                integralFBy = 0.0f;
                integralFBz = 0.0f;
            }

            // Apply proportional feedback
            gx += twoKp * halfex;
            gy += twoKp * halfey;
            gz += twoKp * halfez;
        }

        // Integrate rate of change of quaternion
        gx *= (0.5f * invSampleFreq);                // pre-multiply common factors
        gy *= (0.5f * invSampleFreq);
        gz *= (0.5f * invSampleFreq);
        qa = q0;
        qb = q1;
        qc = q2;
        q0 += (-qb * gx - qc * gy - q3 * gz);
        q1 += (qa * gx + qc * gz - q3 * gy);
        q2 += (qa * gy - qb * gz + q3 * gx);
        q3 += (qa * gz + qb * gy - qc * gx);

        // Normalise quaternion
        recipNorm = invSqrt(q0 * q0 + q1 * q1 + q2 * q2 + q3 * q3);
        q0 *= recipNorm;
        q1 *= recipNorm;
        q2 *= recipNorm;
        q3 *= recipNorm;
      

        return get360deg();
    }

//---------------------------------------------------------------------------------------------------
// IMU algorithm update
    public float[] updateIMU(float gx, float gy, float gz, float ax, float ay, float az) {

        this.gx = gx;
        this.gy = gy;
        this.gz = gz;
        float recipNorm;
        float halfvx, halfvy, halfvz;
        float halfex, halfey, halfez;
        float qa, qb, qc;

        // Compute feedback only if accelerometer measurement valid (avoids NaN in accelerometer normalisation)
        if ((ax != 0.0f) && (ay != 0.0f) && (az != 0.0f)) {

            // Normalise accelerometer measurement
            recipNorm = invSqrt(ax * ax + ay * ay + az * az);
            ax *= recipNorm;
            ay *= recipNorm;
            az *= recipNorm;

            // Estimated direction of gravity and vector perpendicular to magnetic flux
            halfvx = q1 * q3 - q0 * q2;
            halfvy = q0 * q1 + q2 * q3;
            halfvz = q0 * q0 - 0.5f + q3 * q3;

            // Error is sum of cross product between estimated and measured direction of gravity
            halfex = (ay * halfvz - az * halfvy);
            halfey = (az * halfvx - ax * halfvz);
            halfez = (ax * halfvy - ay * halfvx);

            // Compute and apply integral feedback if enabled
            if (twoKi > 0.0f) {
                integralFBx += twoKi * halfex * invSampleFreq;        // integral error scaled by Ki
                integralFBy += twoKi * halfey * invSampleFreq;
                integralFBz += twoKi * halfez * invSampleFreq;
                gx += integralFBx;        // apply integral feedback
                gy += integralFBy;
                gz += integralFBz;
            } else {
                integralFBx = 0.0f;        // prevent integral windup
                integralFBy = 0.0f;
                integralFBz = 0.0f;
            }

            // Apply proportional feedback
            gx += twoKp * halfex;
            gy += twoKp * halfey;
            gz += twoKp * halfez;
        }

        // Integrate rate of change of quaternion
        gx *= (0.5f * invSampleFreq);                // pre-multiply common factors
        gy *= (0.5f * invSampleFreq);
        gz *= (0.5f * invSampleFreq);
        qa = q0;
        qb = q1;
        qc = q2;
        q0 += (-qb * gx - qc * gy - q3 * gz);
        q1 += (qa * gx + qc * gz - q3 * gy);
        q2 += (qa * gy - qb * gz + q3 * gx);
        q3 += (qa * gz + qb * gy - qc * gx);

        // Normalise quaternion
        recipNorm = invSqrt(q0 * q0 + q1 * q1 + q2 * q2 + q3 * q3);
        q0 *= recipNorm;
        q1 *= recipNorm;
        q2 *= recipNorm;
        q3 *= recipNorm;

        return get360deg();

    }

   

    private float[] get360deg() {

        // Данный код был взят из библиотек FreeIMU
        float[] angles = new float[3];

        gx = 2 * (q1 * q3 - q0 * q2);
        gy = 2 * (q0 * q1 + q2 * q3);
        gz = q0 * q0 - q1 * q1 - q2 * q2 + q3 * q3;

        // Курс (180 до -180). 
        angles[0] = (float) Math.toDegrees((float) Math.atan2(2 * q1 * q2 - 2 * q0 * q3, 2 * q0 * q0 + 2 * q1 * q1 - 1));
        // Дифферент
        angles[1] = (float) Math.toDegrees((float) Math.atan(gx / Math.sqrt(gy * gy + gz * gz)));
        // Крен
        angles[2] = (float) Math.toDegrees((float) Math.atan(gy / Math.sqrt(gx * gx + gz * gz)));

        angles[0] = (angles[0] + 360) % 360;
        //  System.out.println(angles[0]);
        return angles;

    }

    float toDegrees(double rad) {
        return (float) rad * DEGREE;
    }

    float invSqrt(float x) {
        /*float halfx = 0.5f * x;
        float y = x;
        long i = (long)y;
        i = 0x5f3759df - (i>>1);
        y = (float)i;
        y = y * (1.5f - (halfx * y * y));
        return y;*/
        float xhalf = 0.5f*x; // x пополам
        int i = Float.floatToIntBits(x); // битовое представление числа.
        i = 0x5f3759d5 - (i >> 1); //  отрицательные x не волнуют, т.к. из отриц. корень брать нельзя
        x = Float.intBitsToFloat(i); // вот оно, первое прибл. значение
        x = x*(1.5f - xhalf*x*x);
        x = x*(1.5f - xhalf*x*x);
        return x;

    }

}
