package com.berry.blue.reds;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;

public class MockedStartActivity extends StartActivity{
    private String[] letters = new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p"};
    private int i = 0;
    private int testNum = 0;
    private Handler handler = new Handler();
    private boolean isFirst = true;

    @Override
    public void onWordObtained(String word) {
        super.onWordObtained(word);
        if (isFirst) {
            this.startTest();
            isFirst = false;
        }
        Log.e(getClass().getSimpleName(), "test");
    }



    private void startTest() {
        handler.postDelayed(this::performTest, 4000);
    }

    private void performTest() {
        this.onNewIntent(getNextNfcIntent());
        testNum++;
        if (testNum < 5) this.startTest();
    }

    private Intent getNextNfcIntent() {
        Parcelable message[] = new NdefMessage[]{ new NdefMessage(NdefRecord.createTextRecord("en", letters[i])) };
        Intent intent = new Intent(NfcAdapter.ACTION_NDEF_DISCOVERED);
        intent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, message);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i++;
        if (i >= 16) i = 0;
        return intent;
    }
}
