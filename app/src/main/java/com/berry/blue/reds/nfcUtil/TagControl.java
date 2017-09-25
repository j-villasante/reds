package com.berry.blue.reds.nfcUtil;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class TagControl {
    private static final String TAG = TagControl.class.getSimpleName();

    public static void writeTag(Tag tag, String tagText) {
        MifareUltralight ultralight = MifareUltralight.get(tag);
        try {
            ultralight.connect();
            ultralight.writePage(4, "abcd".getBytes(Charset.forName("US-ASCII")));
            ultralight.writePage(5, "efgh".getBytes(Charset.forName("US-ASCII")));
            ultralight.writePage(6, "ijkl".getBytes(Charset.forName("US-ASCII")));
            ultralight.writePage(7, "mnop".getBytes(Charset.forName("US-ASCII")));
        } catch (IOException e) {
            Log.e(TAG, "IOException while closing MifareUltralight...", e);
        } finally {
            try {
                ultralight.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException while closing MifareUltralight...", e);
            }
        }
    }

    public static ArrayList<String> readTag(Intent nfcDataIntent) {
        Parcelable[] messages = nfcDataIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        ArrayList<String> resultMap;
        if (messages != null) {
            resultMap = new ArrayList<>(messages.length);
        } else {
            return new ArrayList<>();
        }

        for (Parcelable message : messages) {
            for (NdefRecord record : ((NdefMessage) message).getRecords()) {
                byte[] payload = record.getPayload();
                resultMap.add(new String(payload, 3, payload.length - 3, Charset.forName("UTF-8")).trim());
            }
        }

        return resultMap;
    }
}
