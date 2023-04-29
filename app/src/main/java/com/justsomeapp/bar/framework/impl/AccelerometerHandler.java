package com.justsomeapp.bar.framework.impl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

// If no accelerometer is installed (unlikely), this handler will return zero on all axes.
/*
 * No synchronization is needed here, even though onSensorChanged() might be called in
 * a different thread. The Java memory model guarantees that writes and reads, to and from,
 * primitive types, are atomic. Since we aren't doing anything more complex than assigning
 * new values, we need no syncing here.
 */
public class AccelerometerHandler implements SensorEventListener
{
    float accelX;
    float accelY;
    float accelZ;

    public AccelerometerHandler(Context context)
    {
        SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0)
        {
            Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    public void onSensorChanged(SensorEvent event)
    {
        accelX = event.values[0];
        accelY = event.values[1];
        accelZ = event.values[2];
    }
    public float getAccelX() { return accelX; }
    public float getAccelY() { return accelY; }
    public float getAccelZ() { return accelZ; }
}
