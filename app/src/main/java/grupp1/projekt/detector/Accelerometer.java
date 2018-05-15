package grupp1.projekt.detector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

public class Accelerometer implements SensorFence, SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean flipped;
    private List<SensorFenceListener> listeners;
    private boolean lastState;

    public Accelerometer() {
        flipped = false;
        listeners = new ArrayList<>();
    }

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
        listeners.add(listener);
    }

    @Override
    public void unregisterListener(SensorFenceListener listener) {
        listeners.remove(listener);
    }

    @Override
    public SensorEnums getLastState() {
        return getSensorEnum();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        lastState = flipped;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (z < 0 && !flipped) {
            flipped = true;
        }

        if (z > 0 && flipped) {
            flipped = false;
        }

        if (lastState != flipped) {
            for (SensorFenceListener listener : listeners) {
                listener.stateChanged(this, getSensorEnum());
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //idk
    }

    private SensorEnums getSensorEnum() {
        return flipped ? SensorEnums.INSIDE : SensorEnums.OUTSIDE;
    }

    @Override
    public String getName() {
        return "accelerometer";
    }
}