package grupp1.projekt.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;

import grupp1.projekt.detector.FenceState;

public class Speaker {

    private TextToSpeech mTts;

    public Speaker(Context context) {
        mTts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                int result = mTts.setLanguage(Locale.UK);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    mTts.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }


    public void voiceFeedback(HashMap<String, FenceState> fenceStates) {
        String text = "";
        for (HashMap.Entry<String, FenceState> entry : fenceStates.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == FenceState.OUTSIDE) {
                if (key.equals("proximity")) {
                    text += "Please put the phone down. ";
                } else if (key.equals("accelerometer")) {
                    text += "Please turn the phone face down. ";
                } else if (key.equals("noise")) {
                    text += "Please be quiet!  ";
                }
            }
        }
        speak(text);
    }

    private void speak(String text) {
        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

}
