package grupp1.projekt.detector;

import android.content.Context;

public interface SensorFence {

    void start(Context context);
    void stop();

    void registerListener(SensorFenceListener listener);
    void unregisterListener(SensorFenceListener listener);

    int getLastState();

}
