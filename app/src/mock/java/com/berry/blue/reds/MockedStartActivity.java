package com.berry.blue.reds;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;

public class MockedStartActivity extends StartActivity{
    private String[] letters = new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p"};
    private int testNum = 0;
    private Handler handler = new Handler();
    private boolean isFirst = true;

    private int amount = 1000;
    private int attempts = 2;
    private final long delay = 700;
    private int i = 0;
    private int j = 1;

    private AnimatorListenerAdapter animatorListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            if (testNum < amount) startTest();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.successAnimationView.addAnimatorListener(animatorListenerAdapter);
        this.errorAnimationView.addAnimatorListener(animatorListenerAdapter);
    }

    @Override
    public void onWordObtained(String word) {
        super.onWordObtained(word);
        if (isFirst) {
            this.startTest();
            isFirst = false;
        }
    }

    private void startTest() {
        handler.postDelayed(() -> this.onNewIntent(getNextNfcIntent()), delay);
    }

    private Intent getNextNfcIntent() {
        String key;
        if (j < attempts) {
            j++;
            key = "abc";
        } else {
            j = 1;
            key = letters[i];
            i++;
            if (i >= 16) i = 0;
            testNum++;
        }

        Parcelable message[] = new NdefMessage[]{ new NdefMessage(NdefRecord.createTextRecord("en", key)) };
        Intent intent = new Intent(NfcAdapter.ACTION_NDEF_DISCOVERED);
        intent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, message);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }
}
