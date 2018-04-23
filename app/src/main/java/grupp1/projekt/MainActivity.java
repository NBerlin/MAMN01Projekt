package grupp1.projekt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import grupp1.projekt.detector.Detector;
import grupp1.projekt.detector.DetectorListener;

public class MainActivity extends AppCompatActivity implements DetectorListener {

    private Detector mDetector;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.main_text);

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
    public void onStateChange(int state) {
        mTextView.setText("State " + state);
    }
}
