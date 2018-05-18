package grupp1.projekt.util;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import grupp1.projekt.settings.SettingsValues;

public class SystemSettings {

    public static final int REQUEST_CODE_AUDIO = 1203;
    public static final int REQUEST_CODE_SETTINGS = 12231;
    private final Context mContext;
    private final SettingsValues mSettingsValues;

    public SystemSettings(Context context) {
        mContext = context;
        mSettingsValues = new SettingsValues(mContext);
    }

    public boolean isRecordingAvailable() {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isBrightnessAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.System.canWrite(mContext);
        } else {
            return true;
        }
    }

    public boolean isDoNotDisturbAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            return notificationManager.isNotificationPolicyAccessGranted();
        } else {
            return true;
        }
    }

    public void requestAudioRecording(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_CODE_AUDIO);
    }

    public void requestBrightness(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        activity.startActivity(intent);
    }

    public void requestDoNotDisturb(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
        activity.startActivity(intent);
    }

    public void setBrightness(boolean darken) {
        if (darken) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, 0);
        } else {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, 125);
        }
    }

    public void setSilent(boolean silent) {
        if (!mSettingsValues.isDoNotDisturbOn()) {
            return;
        }
        setRingerMode(silent);
    }

    private void setRingerMode(boolean silent) {
        AudioManager audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (audio != null) {
            if (silent) {
                audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else {
                audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
        }
    }
}
