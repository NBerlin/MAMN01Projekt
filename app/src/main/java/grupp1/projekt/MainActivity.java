package grupp1.projekt;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;

import grupp1.projekt.detector.Detector;
import grupp1.projekt.detector.DetectorListener;
import grupp1.projekt.detector.SensorEnums;
import grupp1.projekt.detector.StudyTimer;
import grupp1.projekt.util.SystemSettings;
import grupp1.projekt.settings.SettingActivity;
import grupp1.projekt.settings.SettingsValues;

public class MainActivity extends AppCompatActivity implements DetectorListener, View.OnClickListener {

    private Detector mDetector;

    private TextView mTextView, mTextAccelerometer, mTextProximity, mTextNoise;
    private ProgressBar mProgressView;
    private Button mSettingsButton;
    private Button mStartButton;
    private MediaPlayer mediaPlayer;

    private SystemSettings mSystemSettings;
    private SettingsChanger mSettingsChanger;
    private SettingsValues mSettingsValues;

    private SensorEnums lastState;
    private StudyTimer timer;

    private TextToSpeech mTts;
    private boolean hasRung;
    private boolean hasStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hasRung = false;
        hasStarted = false;
        mStartButton = findViewById(R.id.button_start);
        timer = new StudyTimer(this.getApplicationContext());
        mTextView = findViewById(R.id.main_text);
        mProgressView = findViewById(R.id.progress_text);
        mSettingsButton = findViewById(R.id.button_settings);

        mTextAccelerometer = findViewById(R.id.text_accelerometer);
        mTextProximity = findViewById(R.id.text_proximity);
        mTextNoise = findViewById(R.id.text_noise);

        mProgressView.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        mProgressView.setProgress(timer.getToday());

        mDetector = new Detector(this);
        lastState = SensorEnums.OUTSIDE;

        mSettingsValues = new SettingsValues(this.getBaseContext());
        mSettingsChanger = new SettingsChanger(this.getBaseContext());
        mSystemSettings = new SystemSettings(this.getBaseContext());

        mTts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                    int result = mTts.setLanguage(Locale.UK);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        mTts.setLanguage(Locale.ENGLISH);
                    }
            }
        });

        onStateChange(lastState);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tada);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSettingsValues.isBrightnessOn() && !mSystemSettings.isBrightnessAvailable()) {
            mSystemSettings.requestBrightness(this);
        } else if(mSettingsValues.isDoNotDisturbOn() && !mSystemSettings.isDoNotDisturbAvailable()) {
            mSystemSettings.requestDoNotDisturb(this);
        } else if (mSettingsValues.isNoiseOn() && !mSystemSettings.isRecordingAvailable()) {
            mSystemSettings.requestAudioRecording(this);
        }

        onStateChange(lastState);

        mDetector.registerListener(this);
        mDetector.registerListener(mSettingsChanger);
        mDetector.restart();

        mSettingsButton.setOnClickListener(this);
        mStartButton.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDetector.unregisterListener(this);
        mDetector.unregisterListener(mSettingsChanger);
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
        voiceFeedback(fenceStates);
        for (String key : fenceStates.keySet()) {
            TextView view = null;
            if (key.equals("proximity")) {
                view = mTextProximity;
            } else if (key.equals("accelerometer")) {
                view = mTextAccelerometer;
            } else if (key.equals("noise")) {
                view = mTextNoise;
            }

            SensorEnums s = fenceStates.get(key);
            if (s == SensorEnums.INSIDE) {
                view.setBackgroundColor(Color.GREEN);
                view.setVisibility(View.VISIBLE);
            } else if (s == SensorEnums.OUTSIDE) {
                view.setBackgroundColor(Color.RED);
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }

        int timeToStudy = mSettingsValues.getMinutesToStudy();

        mTextView.setText("Currently: " + (state == SensorEnums.INSIDE ? "Studying" : "Not studying, flip to start") + "\nYou have studied for: "
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
            mTextProximity.setVisibility(View.GONE);
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
            case R.id.button_start: {
                if(!hasStarted) {
                    mDetector.registerListener(this);
                    mDetector.start();
                    hasStarted=true;
                }
                else{
                    mDetector.unregisterListener(this);
                    mDetector.stop();
                    hasStarted=false;
                }
            }
        }
    }

    private void voiceFeedback(HashMap<String, SensorEnums> fenceStates) {
        if (!mSettingsValues.isFeedbackOn()) {
            return;
        }
        String text = "";
        for (HashMap.Entry<String, SensorEnums> entry : fenceStates.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == SensorEnums.OUTSIDE) {
                if (key.equals("proximity")) {
                    text += "Please put the phone down. ";
                } else if (key.equals("accelerometer")) {
                    text += "Please turn the phone face down. ";
                } else if (key.equals("noise")) {
                    text += "I consider studying a quiet activity. Shut up! ";
                }
            }
        }
        speak(text);
    }

    private void speak(String text) {
        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}