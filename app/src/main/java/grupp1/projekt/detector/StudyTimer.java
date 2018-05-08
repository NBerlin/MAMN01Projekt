package grupp1.projekt.detector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StudyTimer {
    private DateFormat mDateFormat;

    private Long lastStart;
    private HashMap<String, Integer> mStorage;

    public StudyTimer() {
        lastStart = null;
        mStorage = new HashMap<>();
        mDateFormat = new SimpleDateFormat("YYYY-MM-DD", Locale.getDefault());
    }

    public void start() {
        lastStart = System.currentTimeMillis();
    }

    public void stop() {
        long stop = System.currentTimeMillis();
        if (lastStart != null && lastStart < stop) {
            int totalStudied = getTotalStudied();
            totalStudied += (int) TimeUnit.MILLISECONDS.toSeconds(stop - lastStart);
            setTotalStudied(totalStudied);
        }
        lastStart = null;
    }

    private void setTotalStudied(int totalStudied) {
        String key = mDateFormat.format(new Date());
        mStorage.put(key, totalStudied);
    }

    public int getTotalStudied() {
        String key = mDateFormat.format(new Date());
        return mStorage.containsKey(key) ? mStorage.get(key) : 0;
    }
}
