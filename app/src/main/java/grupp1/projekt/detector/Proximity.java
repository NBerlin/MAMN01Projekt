package grupp1.projekt.detector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static grupp1.projekt.detector.SensorEnums.INSIDE;
import static grupp1.projekt.detector.SensorEnums.OUTSIDE;

public class Proximity implements SensorFence, SensorEventListener {
    SensorEnums mLastState;
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private List<SensorFenceListener> mListeners;
    private PowerManager pm;


    public Proximity() {
        mListeners = new ArrayList<>();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float distance = event.values[0];
        SensorEnums state;
        if (distance < 5.0) {
            state = INSIDE;
        } else {
            state = OUTSIDE;
        }
        if (mLastState != state) {
            mLastState = state;
            informListeners(state);
        }
        // Log.d("Proximity", String.valueOf(event.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void start(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void stop() {

    }

    @Override
    public void registerListener(SensorFenceListener listener) {
        mListeners.add(listener);
    }

    @Override
    public void unregisterListener(SensorFenceListener listener) {
        mListeners.remove(listener);
    }

    private void informListeners(SensorEnums state) {
        for (SensorFenceListener listener : mListeners) {
            listener.stateChanged(this, state);
        }
    }

    @Override
    public SensorEnums getLastState() {
        return mLastState;
    }

    @Override
    public String getName() {
        return "proximity";
    }
}
