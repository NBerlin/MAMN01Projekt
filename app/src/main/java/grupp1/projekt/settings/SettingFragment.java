package grupp1.projekt.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import grupp1.projekt.R;
import grupp1.projekt.detector.StudyTimer;
import grupp1.projekt.util.SystemSettings;

public class SettingFragment extends PreferenceFragmentCompat {
    SharedPreferences mSharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_general, rootKey);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        Preference button = findPreference(getString(R.string.factory_reset));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new StudyTimer(getContext()).factoryReset();
                return true;
            }
        });
    }
}
