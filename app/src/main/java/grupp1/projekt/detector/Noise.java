package grupp1.projekt.detector;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.ArrayList;

public class Noise implements SensorFence {

    private Handler mHandler;
    private ArrayList<SensorFenceListener> mListeners;
    private MediaRecorder mRecorder;
    private SensorEnums mLastState;

    private static final long SAMPLE_RATE = 3000;
    private static final double THRESHOLD = 400;

    private final Runnable mSampler = new Runnable() {
        @Override
        public void run() {
            double amplitude = getAmplitude();
            // Log.d("Noise", "amplitude " + amplitude);
            SensorEnums state = amplitude > THRESHOLD ? SensorEnums.OUTSIDE : SensorEnums.INSIDE;
            if (mLastState != state) {
                mLastState = state;
                informListeners(state);
            }
            mHandler.postDelayed(mSampler, SAMPLE_RATE);
        }
    };

    public Noise() {
        mHandler = new Handler(Looper.getMainLooper());
        mListeners = new ArrayList<>();
    }

    @Override
    public void start(Context context) throws PermissionException {
        try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");

            mRecorder.prepare();
            mRecorder.start();
            startSampling();
            mLastState = SensorEnums.OUTSIDE;
        } catch (IOException e) {
            // e.printStackTrace();
            throw new PermissionException();
        }
    }

    private void startSampling() {
        mHandler.post(mSampler);
    }

    private double getAmplitude() {
        if (mRecorder != null)
            return mRecorder.getMaxAmplitude();
        else
            return 0;
    }

    @Override
    public void stop() {
        mHandler.removeCallbacksAndMessages(null);
        mRecorder.stop();
        mRecorder.release();
    }

    @Override
    public void registerListener(SensorFenceListener listener) {
        mListeners.add(listener);
    }

    @Override
    public void unregisterListener(SensorFenceListener listener) {
        mListeners.remove(listener);
    }

    private void informListeners(SensorEnums state) {
        for (SensorFenceListener listener : mListeners) {
            listener.stateChanged(this, state);
        }
    }

    @Override
    public SensorEnums getLastState() {
        return mLastState;
    }

    @Override
    public String getName() {
        return "noise";
    }
}
