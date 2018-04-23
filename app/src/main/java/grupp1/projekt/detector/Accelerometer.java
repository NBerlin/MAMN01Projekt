package grupp1.projekt.detector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Accelerometer implements SensorFence, SensorEventListener {

    SensorManager sensorManager;
    Sensor accelerometer;

    @Override
    public void start(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void registerListener(SensorFenceListener listener) {

    }

    @Override
    public void unregisterListener(SensorFenceListener listener) {

    }

    @Override
    public int getLastState() {
        return 0;
    }


    // x < 0 ? "right" : "left"

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //idk
    }
}