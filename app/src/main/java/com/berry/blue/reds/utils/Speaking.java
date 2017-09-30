package com.berry.blue.reds.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class Speaking {
    private static Speaking instance;
    private TextToSpeech tts;

    private Speaking() {}

    public static Speaking instance() {
        if (instance == null) instance = new Speaking();
        return instance;
    }

    public Speaking init(Context context){
        this.tts = new TextToSpeech(context, (status) -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(new Locale("es", "PE"));
                tts.setSpeechRate(0.5f);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                }

            } else {
                Log.e("TTS", "Initialization Failed!");
            }
        });
        return instance;
    }

    public void speak(String text) {
        if (tts == null)
            Log.e("TTS", "TTS has not me initiated.");
        else
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void stop() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
