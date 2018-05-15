package grupp1.projekt;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Vibrator;


import java.util.HashMap;

import grupp1.projekt.detector.Detector;
import grupp1.projekt.detector.DetectorListener;
import grupp1.projekt.detector.SensorEnums;
import grupp1.projekt.settings.SettingsValues;
import grupp1.projekt.settings.SettingActivity;
import grupp1.projekt.detector.StudyTimer;

public class MainActivity extends AppCompatActivity implements DetectorListener, View.OnClickListener {

    private Detector mDetector;

    private TextView mTextView, mTextAccelerometer, mTextProximity;
    private ProgressBar mProgressView;
    private Button mSettingsButton;
    private MediaPlayer mediaPlayer;

    private SettingsValues mSettingsValues;

    private SensorEnums lastState;
    private StudyTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer = new StudyTimer(this.getApplicationContext());
        mTextView = findViewById(R.id.main_text);
        mProgressView = findViewById(R.id.progress_text);
        mSettingsButton = findViewById(R.id.button_settings);
        mTextAccelerometer = findViewById(R.id.text_accelerometer);
        mTextProximity = findViewById(R.id.text_proximity);
        mProgressView.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        mProgressView.setProgress(timer.getToday());


        mDetector = new Detector(this);
        lastState = SensorEnums.OUTSIDE;
        mSettingsValues = new SettingsValues(this.getBaseContext());
        onStateChange(lastState);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tada);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onStateChange(lastState);

        mDetector.registerListener(this);
        mDetector.start();

        mSettingsButton.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDetector.unregisterListener(this);
        mDetector.stop();
    }

    @Override
    public void onStateChange(SensorEnums state) {
        lastState = state;

        if (state == SensorEnums.INSIDE) {
            timer.start();
        } else if (state == SensorEnums.OUTSIDE) {
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

        HashMap<String, SensorEnums> fenceStates = mDetector.getFenceStates();
        for (String key : fenceStates.keySet()) {
            TextView view = null;
            if (key.equals("proximity")) {
                view = mTextProximity;
            } else if (key.equals("accelerometer")) {
                view = mTextAccelerometer;
            }

            SensorEnums s = fenceStates.get(key);
            if (s == SensorEnums.INSIDE) {
                view.setBackgroundColor(Color.GREEN);
            } else {
                view.setBackgroundColor(Color.RED);
            }
        }

        int timeToStudy = mSettingsValues.getMinutesToStudy();

        mTextView.setText("Currently: " + (state == SensorEnums.INSIDE ? "Studying" : "Not studying, flip to start") + "\nYou have studied for: "
                + timer.getToday() + " minutes\nYour goal is to study for " + timeToStudy + " minutes");
        mProgressView.setProgress(timer.getToday() * 100 / timeToStudy);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }
}
