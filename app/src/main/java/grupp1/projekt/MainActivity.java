package grupp1.projekt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import grupp1.projekt.detector.Detector;
import grupp1.projekt.detector.DetectorListener;
import grupp1.projekt.detector.SensorEnums;

public class MainActivity extends AppCompatActivity implements DetectorListener {

    private Detector mDetector;

    private TextView mTextView;
    private ProgressBar mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.main_text);
        mProgressView = findViewById(R.id.progress_text);

        mDetector = new Detector(this);
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
        mTextView.setText("State " + state);
    }
}
