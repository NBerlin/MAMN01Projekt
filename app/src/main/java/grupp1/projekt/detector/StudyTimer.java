package grupp1.projekt.detector;

import java.util.concurrent.TimeUnit;

public class StudyTimer {
    private int totalStudied;
    private Long lastStart;

    public StudyTimer() {
        totalStudied = 0;
        lastStart = null;
    }

    public void start() {
        lastStart = System.currentTimeMillis();
    }

    public void stop() {
        long stop = System.currentTimeMillis();
        if (lastStart != null && lastStart < stop) {
            totalStudied += (int) TimeUnit.MILLISECONDS.toSeconds(stop - lastStart);
        }
        lastStart = null;
    }

    public int getTotalStudied() {
        return totalStudied;
    }
}
