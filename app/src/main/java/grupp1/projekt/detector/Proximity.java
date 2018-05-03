package grupp1.projekt.detector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;

import java.util.ArrayList;
import java.util.List;

import static grupp1.projekt.detector.SensorEnums.INSIDE;
import static grupp1.projekt.detector.SensorEnums.OUTSIDE;

public class Proximity implements SensorFence, SensorEventListener {
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private List<SensorFenceListener> listeners;
    private PowerManager pm;
    private PowerManager.WakeLock mProximityWakeLock;


    public Proximity(){
        listeners = new ArrayList<SensorFenceListener>();

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        float distance = event.values[0];
        SensorEnums result;
        if(distance < proximitySensor.getMaximumRange()) {
            result=INSIDE;
        } else {
            result=OUTSIDE;
        }
        for (SensorFenceListener listener : listeners) {
            listener.stateChanged(this,result);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void start(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mProximityWakeLock = pm.newWakeLock(32,"test");
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void stop() {

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
        return null;
    }
}
