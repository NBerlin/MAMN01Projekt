package grupp1.projekt.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import grupp1.projekt.R;

public class SettingFragment extends PreferenceFragmentCompat {

    ListPreference mListPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_general, rootKey);
    }




}
