package grupp1.projekt.detector;

public interface DetectorListener {

    int inside = 102;
    int outside = 103;

    void onStateChange(SensorEnums state);

}
