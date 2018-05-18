package grupp1.projekt;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import grupp1.projekt.detector.Detector;
import grupp1.projekt.detector.DetectorListener;
import grupp1.projekt.detector.FenceState;
import grupp1.projekt.settings.SettingsValues;
import grupp1.projekt.settings.SettingActivity;
import grupp1.projekt.detector.StudyTimer;
import grupp1.projekt.util.SystemSettings;
import grupp1.projekt.util.Speaker;

public class MainActivity extends AppCompatActivity implements DetectorListener, View.OnClickListener {

    private Detector mDetector;

    private TextView mTextView, mInstructions, mTextAccelerometer, mTextNoise;
    private ProgressBar mProgressView;
    private Button mSettingsButton;
    private MediaPlayer mediaPlayer;

    private SystemSettings mSystemSettings;
    private SettingsChanger mSettingsChanger;
    private SettingsValues mSettingsValues;

    private Speaker mSpeaker;
    private Handler mHandler;

    private AtomicBoolean mHandlerIsRunning = new AtomicBoolean(false);
    private FenceState lastState;
    private StudyTimer timer;

    private Runnable mSpeakerRun = new Runnable() {
        @Override
        public void run() {
            if (mSettingsValues.isFeedbackOn() && mDetector.getLastState() == FenceState.OUTSIDE) {
                HashMap<String, FenceState> fenceStates = mDetector.getFenceStates();
                mSpeaker.voiceFeedback(fenceStates);
            }
            mHandlerIsRunning.set(false);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer = new StudyTimer(this.getApplicationContext());
        mTextView = findViewById(R.id.main_text);
        mInstructions = findViewById(R.id.instructions);
        mProgressView = findViewById(R.id.progress_text);
        mSettingsButton = findViewById(R.id.button_settings);

        mTextAccelerometer = findViewById(R.id.text_accelerometer);
        mTextNoise = findViewById(R.id.text_noise);

        mProgressView.setProgressTintList(ColorStateList.valueOf(Color.DKGRAY));
        mProgressView.setProgress(timer.getToday());

        mDetector = new Detector(this);
        lastState = FenceState.OUTSIDE;

        mSettingsValues = new SettingsValues(this.getBaseContext());
        mSettingsChanger = new SettingsChanger(this.getBaseContext());
        mSystemSettings = new SystemSettings(this.getBaseContext());

        mSpeaker = new Speaker(this.getBaseContext());

        mHandler = new Handler(Looper.getMainLooper());

        onStateChange(lastState);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tada);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSettingsValues.isBrightnessOn() && !mSystemSettings.isBrightnessAvailable()) {
            mSystemSettings.requestBrightness(this);
        } else if (mSettingsValues.isDoNotDisturbOn() && !mSystemSettings.isDoNotDisturbAvailable()) {
            mSystemSettings.requestDoNotDisturb(this);
        } else if (mSettingsValues.isNoiseOn() && !mSystemSettings.isRecordingAvailable()) {
            mSystemSettings.requestAudioRecording(this);
        }

        onStateChange(lastState);

        mDetector.registerListener(this);
        mDetector.registerListener(mSettingsChanger);
        mDetector.restart();

        mSettingsButton.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDetector.unregisterListener(this);
        mDetector.unregisterListener(mSettingsChanger);
        mDetector.stop();
    }

    @Override
    public void onStateChange(FenceState state) {
        lastState = state;

        if (state == FenceState.INSIDE) {
            timer.start();
        } else if (state == FenceState.OUTSIDE) {
            timer.stop();
            if (timer.getToday() >= mSettingsValues.getMinutesToStudy() && !timer.hasRungToday()) {
                timer.ring();
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
                mProgressView.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
            }
        } else {
            throw new RuntimeException("How did you end up here?");
        }

        HashMap<String, FenceState> fenceStates = mDetector.getFenceStates();
        if (state == FenceState.OUTSIDE && !mHandlerIsRunning.get()) {
            mHandler.postDelayed(mSpeakerRun, 5000);
            mHandlerIsRunning.set(true);
        }
        for (String key : fenceStates.keySet()) {
            TextView view = null;
            if (key.equals("accelerometer")) {
                view = mTextAccelerometer;
            } else if (key.equals("noise")) {
                view = mTextNoise;
            }
            if (view != null) {
                FenceState s = fenceStates.get(key);
                if (s == FenceState.INSIDE) {
                    view.setBackgroundColor(Color.GREEN);
                    view.setVisibility(View.GONE);
                } else if (s == FenceState.OUTSIDE) {
                    view.setBackgroundColor(Color.RED);
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.GONE);
                }
            }
        }

        int timeToStudy = mSettingsValues.getMinutesToStudy();

        mInstructions.setText(state == FenceState.INSIDE ? "Studying" : "To start studying, put your phone face down on a table.");
        mTextView.setText("You have studied for: "
                + timer.getToday() + " minutes\nYour goal is to study for " + timeToStudy + " minutes");
        mProgressView.setProgress(timer.getToday() * 100 / timeToStudy);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SystemSettings.REQUEST_CODE_AUDIO) {
            mDetector.restart();
        } else if (requestCode == SystemSettings.REQUEST_CODE_SETTINGS) {
            mTextAccelerometer.setVisibility(View.GONE);
            mTextNoise.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_settings: {

                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
            }
        }
    }

}
