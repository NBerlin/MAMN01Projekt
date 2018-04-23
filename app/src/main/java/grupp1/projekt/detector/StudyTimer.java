package grupp1.projekt.detector;

import java.util.concurrent.TimeUnit;

public class StudyTimer {
    private int minutesToStudy;
    private int totalStudied;
    private long lastStart;

    public StudyTimer(int minutesToStudy) {
        this.minutesToStudy = minutesToStudy;
        totalStudied = 0;
    }

    public void start() {
        lastStart = System.currentTimeMillis();
    }

    public void stop() {
        long stop = System.currentTimeMillis();
        if (lastStart < stop) {
            totalStudied = (int) TimeUnit.MILLISECONDS.toMinutes(stop - lastStart);
        }
    }

    public int timeLeft() {
        return minutesToStudy - totalStudied;
    }
}
