package grupp1.projekt.detector;

import android.content.Context;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import grupp1.projekt.settings.SettingsValues;
import grupp1.projekt.util.SystemSettings;

public class Detector implements SensorFenceListener {

    private final ArrayList<DetectorListener> mListeners;
    private final ArrayList<SensorFence> mFences;
    private final SettingsValues mSettings;
    private final Context mContext;
    private final SystemSettings mSystemSettings;
    private boolean mIsRunning;

    public Detector(Context context) {
        mListeners = new ArrayList<>();
        mFences = new ArrayList<>();
        mContext = context;
        mSettings = new SettingsValues(mContext);
        mSystemSettings = new SystemSettings(mContext);
    }

    public void start() {
        if (!mIsRunning) {
            mIsRunning = true;
            if (mSettings.isAccelerometerOn()) {
                mFences.add(new Accelerometer());
            }
            if (mSettings.isProximityOn()) {
                mFences.add(new Proximity());
            }
            if (mSettings.isNoiseOn() && mSystemSettings.isRecordingAvailable()) {
                mFences.add(new Noise());
            }

            for (SensorFence fence : mFences) {
                try {
                    fence.start(mContext);
                    fence.registerListener(this);
                } catch (PermissionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        if (mIsRunning) {
            for (SensorFence fence : mFences) {
                fence.stop();
                fence.unregisterListener(this);
            }
            mFences.clear();
        }
        mIsRunning = false;
    }

    public void registerListener(DetectorListener listener) {
        mListeners.add(listener);
    }

    public void unregisterListener(DetectorListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void stateChanged(SensorFence sensor, SensorEnums state) {
        boolean isInside = true;
        Log.d("Detector", "onStateChange " + sensor.getClass().getSimpleName() + " " + state);
        for (SensorFence mFence : mFences) {
            isInside &= mFence.getLastState() == SensorEnums.INSIDE;
        }

        SensorEnums outState = isInside ? SensorEnums.INSIDE : SensorEnums.OUTSIDE;

        for (DetectorListener listener : mListeners) {
            listener.onStateChange(outState);
        }
    }

    public HashMap<String, SensorEnums> getFenceStates() {
        HashMap<String, SensorEnums> map = new HashMap<>();
        for (SensorFence fence : mFences) {
            map.put(fence.getName(), fence.getLastState());
        }
        return map;
    }

    public void restart() {
        stop();
        start();
    }
}
