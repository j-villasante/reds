package com.berry.blue.reds;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.berry.blue.reds.main.ViewStartI;
import com.berry.blue.reds.main.Word;
import com.berry.blue.reds.main.WordCon;
import com.berry.blue.reds.nfcUtil.TagControl;

import java.util.ArrayList;

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
        this.isPlaying = true;
        this.enableNfcRead();
    };
    private boolean isPlaying = false;

    // NFC variables
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    private static final String TAG = TagControl.class.getSimpleName();

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
        mTechLists = new String[][] { new String[] { NfcA.class.getName() } };
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHideHandler.postDelayed(mHideRunnable, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPlaying) {
            this.enableNfcRead();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.disableNfcRead();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        ArrayList<String> messages = TagControl.readTag(intent);
        this.controller.handleTagScan(messages.get(0));
    }

    @Override
    public void onWordObtained(Word word) {
        tviWord.setText(word.name);
    }

    @Override
    public void onValueError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void enableNfcRead() {
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        Log.i(TAG, "NFC read enabled");
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
