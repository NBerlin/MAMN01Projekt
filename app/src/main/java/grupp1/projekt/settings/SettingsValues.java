package grupp1.projekt.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

public class SettingsValues {

    private final static String PREF_TIME_TO_STUDY = "study_goal_minutes";

    private final static String PREF_ACCELEROMETER = "detector_accelerometer";
    private final static String PREF_PROXIMITY = "detector_proximity";
    private final static String PREF_NOISE = "detector_voice";

    private final static String PREF_FEEDBACK_ON = "feedback_on";
    private final static String PREF_DO_NOT_DISTURB = "do_not_disturb";
    private final static String PREF_BRIGHTNESS = "brightness_control";

    private final SharedPreferences mPreferences;

    public SettingsValues(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getMinutesToStudy() {
        int study;
        try {
            study = Integer.valueOf(mPreferences.getString(PREF_TIME_TO_STUDY, "-1"));
        } catch (Exception e) {
            study = 10;
        }
        if (study < 1) {
            study = 10;
        }
        return study;
    }

    public boolean isFeedbackOn() {
        return mPreferences.getBoolean(PREF_FEEDBACK_ON, false);
    }

    public boolean isDoNotDisturbOn() {
        return mPreferences.getBoolean(PREF_DO_NOT_DISTURB, false);
    }

    public boolean isAccelerometerOn() {
        return mPreferences.getBoolean(PREF_ACCELEROMETER, false);
    }

    public boolean isProximityOn() {
        return mPreferences.getBoolean(PREF_PROXIMITY, false);
    }

    public boolean isNoiseOn() {
        return mPreferences.getBoolean(PREF_NOISE, false);
    }


    public boolean isBrightnessOn() {
        return mPreferences.getBoolean(PREF_BRIGHTNESS, false);
    }
}
