package grupp1.projekt.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

public class SettingsValues {

    private final static String TIME_TO_STUDY_PREF = "study_goal_minutes";

    private final SharedPreferences mPreferences;

    public SettingsValues(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getMinutesToStudy() {
        return Integer.valueOf(mPreferences.getString(TIME_TO_STUDY_PREF, "-1"));
    }

}
