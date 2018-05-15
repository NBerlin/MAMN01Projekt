package grupp1.projekt.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import grupp1.projekt.R;

public class SettingFragment extends PreferenceFragmentCompat {

    ListPreference mListPreference;

    SharedPreferences mSharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_general, rootKey);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

}
