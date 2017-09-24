package com.berry.blue.reds;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.berry.blue.reds.main.ViewStartI;
import com.berry.blue.reds.main.Word;
import com.berry.blue.reds.main.WordCon;
import com.berry.blue.reds.nfcUtil.MifareCon;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Start extends Activity implements ViewStartI {
    @BindView(R.id.fullscreen_content) View mContentView;
    @BindView(R.id.rla_play_layout) View mPlayLayout;
    @BindView(R.id.main_word_view) TextView tviWord;

    private final Handler mHideHandler = new Handler();
    private WordCon controller = WordCon.getInstance(this);

    private final Runnable mHideRunnable = this::hide;
    private View.OnClickListener startClickListener = (View view) -> {
        this.mPlayLayout.setVisibility(View.GONE);
        this.controller.getRandomWord();
        this.enableNfcRead();
    };

    // NFC variables
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        mPlayLayout.setOnClickListener(this.startClickListener);

        // NFC initiation
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        }catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] {ndef, };
        mTechLists = new String[][] { new String[] { MifareUltralight.class.getName() } };
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHideHandler.postDelayed(mHideRunnable, 500);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Parcelable[] tagFromIntent = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage[] message = MifareCon.readTag(tagFromIntent);
        for (NdefMessage aMessage : message) {
            Log.i(this.getClass().getSimpleName(), aMessage.toString());
        }
    }

    @Override
    public void onWordObtained(Word word) {
        tviWord.setText(word.name);
    }

    @Override
    public void onValueError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void enableNfcRead() {
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        Toast.makeText(this, "NFC read enabled", Toast.LENGTH_SHORT).show();
    }

    private void disableNfcRead() {
        mNfcAdapter.disableForegroundDispatch(this);
    }

    private void hide() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
