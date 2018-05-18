package grupp1.projekt;

import android.content.Context;

import grupp1.projekt.detector.DetectorListener;
import grupp1.projekt.detector.FenceState;
import grupp1.projekt.util.SystemSettings;

public class SettingsChanger implements DetectorListener {
    private final SystemSettings mSystemSettings;

    public SettingsChanger(Context context) {
        mSystemSettings = new SystemSettings(context);
    }

    @Override
    public void onStateChange(FenceState state) {
        if (mSystemSettings.isBrightnessAvailable()) {
            mSystemSettings.setBrightness(state == FenceState.INSIDE);
        }
        if (mSystemSettings.isDoNotDisturbAvailable()) {
            mSystemSettings.setSilent(state == FenceState.INSIDE);
        }
    }
}
