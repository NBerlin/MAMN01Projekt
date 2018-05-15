package grupp1.projekt.detector;

import android.content.Context;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import grupp1.projekt.util.Storage;

public class StudyTimer {
    private DateFormat mDateFormat;

    private Context mContext;

    private Long lastStart;
    private HashMap<String, Integer> mStorage;

    public StudyTimer(Context context) {
        mContext = context;
        lastStart = null;
        mStorage = Storage.loadStudyTime(context);
        mDateFormat = new SimpleDateFormat("YYYY-MM-DD", Locale.getDefault());
    }

    public void start() {
        lastStart = System.currentTimeMillis();
    }

    public void stop() {
        long stop = System.currentTimeMillis();
        if (lastStart != null && lastStart < stop) {
            int totalStudied = getToday();
            totalStudied += (int) TimeUnit.MILLISECONDS.toSeconds(stop - lastStart);
            setToday(totalStudied);
        }
        lastStart = null;
    }

    private void setToday(int totalStudied) {
        String key = mDateFormat.format(new Date());
        mStorage.put(key, totalStudied);
        Storage.saveStudyTime(mContext, mStorage);
    }

    public int getToday() {
        String key = mDateFormat.format(new Date());
        return mStorage.containsKey(key) ? mStorage.get(key) : 0;
    }
}
