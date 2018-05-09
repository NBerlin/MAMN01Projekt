package grupp1.projekt.util;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;

public class Storage {
    private final static String FILENAME_STUDYTIME = "studytime.json";

    public static HashMap<String, Integer> loadStudyTime(Context context) {
        File filesDir = context.getFilesDir();
        String absolutePath = filesDir.getAbsolutePath() + FILENAME_STUDYTIME;
        try {
            Reader reader = new FileReader(absolutePath);
            Gson gson = new GsonBuilder().create();
            Type type = new TypeToken<HashMap<String, Integer>>() {}.getType();
            HashMap<String, Integer> map = gson.fromJson(reader, type);
            return map;
        } catch (FileNotFoundException e) {
            Log.w("Storage", "No file present");
            //e.printStackTrace();
        }
        return new HashMap<>();
    }



    public static void saveStudyTime(Context context, HashMap<String, Integer> storage) {
        File filesDir = context.getFilesDir();
        String absolutePath = filesDir.getAbsolutePath() + FILENAME_STUDYTIME;
        try (Writer writer = new FileWriter(absolutePath)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(storage, writer);
        } catch (IOException e) {
            Log.e("Storage", "Couldn't write to disk.");
            //e.printStackTrace();
        }
    }
}
