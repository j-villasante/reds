package com.berry.blue.reds.nfcUtil;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class TagControl {
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
