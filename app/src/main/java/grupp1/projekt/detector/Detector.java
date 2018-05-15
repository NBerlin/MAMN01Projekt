package grupp1.projekt.detector;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;

import grupp1.projekt.settings.SettingsValues;

import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class Detector implements SensorFenceListener {

    private final ArrayList<DetectorListener> mListeners;
    private final ArrayList<SensorFence> mFences;
    private final SettingsValues mSettings;
    private final Context mContext;
    private HashMap<String, grupp1.projekt.detector.SensorEnums> states;

    public Detector(Context context) {
        mListeners = new ArrayList<>();
        mFences = new ArrayList<>();
        mContext = context;
        mSettings = new SettingsValues(mContext);
    }

    public void start() {
        mFences.add(new Accelerometer());
        mFences.add(new Proximity());

        for (SensorFence fence : mFences) {
            fence.registerListener(this);
            fence.start(mContext);
        }
    }

    public void stop() {
        for (SensorFence fence : mFences) {
            fence.stop();
            fence.unregisterListener(this);
        }
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

        setSilent(isInside, mContext);
        setBrightness(isInside, mContext);
        for (DetectorListener listener : mListeners) {
            listener.onStateChange(outState);
        }
    }

    private void setBrightness(boolean darken, Context mContext) {
        if (!Settings.System.canWrite(mContext)) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            mContext.startActivity(intent);
        }

        if (Settings.System.canWrite(mContext)) {
            if (darken) {
                Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, 0);
            } else {
                Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, 125);
            }
        }
    }

    private void setSilent(boolean silent, Context context) {
        if (!mSettings.isDoNotDisturbOn()) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            mContext.startActivity(intent);
        }

        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audio != null) {
                if (silent) {
                    audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                } else {
                    audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
            }
        }
    }

    public HashMap<String, SensorEnums> getFenceStates() {
        HashMap<String, SensorEnums> map = new HashMap<>();
        for (SensorFence fence : mFences) {
            map.put(fence.getName(), fence.getLastState());
        }
        return map;
    }
}
