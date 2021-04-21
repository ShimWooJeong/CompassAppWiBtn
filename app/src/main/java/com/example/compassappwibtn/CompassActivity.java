package com.example.compassappwibtn;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.Map;

public class CompassActivity extends Activity {
    CompassView compassView;

    SensorManager sensorManager;
    SensorEventListener listener;
    float [] accelValues, magneticValues;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        compassView = findViewById(R.id.compassView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                switch (event.sensor.getType()){
                    case Sensor.TYPE_ACCELEROMETER: accelValues = event.values.clone(); break;
                    case Sensor.TYPE_MAGNETIC_FIELD: magneticValues = event.values.clone(); break;
                    default: break;
                }
                if (magneticValues != null && accelValues != null) {
                    float[] R = new float[16];
                    float[] I = new float[16];
                    SensorManager.getRotationMatrix(R, I, accelValues, magneticValues);
                    float[] values = new float[3]; // Z, X, Y
                    SensorManager.getOrientation(R, values); // We'll use valuse[0] only

                    compassView.azimuth = (int) radian2Degree(values[0]);
                    compassView.invalidate();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                sensorManager.SENSOR_DELAY_UI);

        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                sensorManager.SENSOR_DELAY_UI);
    }
    float radian2Degree(float radian) { return radian * 180 / (float)Math.PI;
    }
}
