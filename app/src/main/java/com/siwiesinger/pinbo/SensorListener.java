package com.siwiesinger.pinbo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by user on 02.07.2015.
 */
public class SensorListener implements SensorEventListener{
    static SensorListener instance;
    BaseActivity activity;

    public static SensorListener getSharedInstance() {
        if (instance == null)
            instance = new SensorListener();
        return instance;
    }

    public SensorListener() {
        instance = this;
        activity= BaseActivity.getSharedInstance();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    activity.accelerometerSpeedX = event.values[0];
                    activity.accelerometerSpeedY = event.values[1];
                    break;
                default:
                    break;
            }
        }
    }
}
