package grupp1.projekt.detector;

import android.content.Context;

import java.util.ArrayList;

public class Detector implements SensorFenceListener {

    private final ArrayList<DetectorListener> mListeners;
    private final ArrayList<SensorFence> mFences;

    private final Context mContext;

    public Detector(Context context) {
        mListeners = new ArrayList<>();
        mFences = new ArrayList<>();
        mContext = context;
    }

    public void start() {
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
    public void stateChanged(SensorFence sensor, int state) {
        boolean isInside = true;
        for (SensorFence mFence : mFences) {
            isInside &= mFence.getLastState() == SensorFenceListener.inside;
        }

        int outState = isInside ? DetectorListener.inside : DetectorListener.outside;

        for (DetectorListener listener : mListeners) {
            listener.onStateChange(outState);
        }
    }

}
