package grupp1.projekt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;

import grupp1.projekt.detector.Detector;
import grupp1.projekt.detector.DetectorListener;
import grupp1.projekt.detector.SensorEnums;
import grupp1.projekt.detector.SettingsValues;
import grupp1.projekt.detector.StudyTimer;

public class MainActivity extends AppCompatActivity implements DetectorListener {

    private Detector mDetector;

    private TextView mTextView;
    private ProgressBar mProgressView;
    private SensorEnums lastState;
    private StudyTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer = new StudyTimer();
        mTextView = findViewById(R.id.main_text);
        mProgressView = findViewById(R.id.progress_text);
        mDetector = new Detector(this);
        lastState = SensorEnums.OUTSIDE;
        onStateChange(lastState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDetector.registerListener(this);
        mDetector.start();
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
        }

        if (state == SensorEnums.OUTSIDE) {
            timer.stop();
        }

        mTextView.setText("State " + state + "\nYou have studied for: "
                + timer.getTotalStudied() + " seconds\nYour goal is to study for " + SettingsValues.MINUTES_TO_STUDY);
        mProgressView.setProgress(timer.getTotalStudied() * 100 / SettingsValues.MINUTES_TO_STUDY);
    }
}