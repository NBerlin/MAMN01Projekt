package grupp1.projekt.detector;


public interface SensorFenceListener {

    int inside = 101;
    int outside = 102;

    void stateChanged(int state);

}
