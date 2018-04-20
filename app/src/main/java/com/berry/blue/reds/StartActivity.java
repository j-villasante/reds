package com.berry.blue.reds;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

import com.airbnb.lottie.LottieAnimationView;
import com.berry.blue.reds.game.Game;
import com.berry.blue.reds.interfaces.ViewStartI;
import com.berry.blue.reds.nfcUtil.TagControl;
import com.berry.blue.reds.utils.Speaking;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartActivity extends Activity implements ViewStartI {
    // View binding
    @BindView(R.id.fullscreen_content) View mContentView;
    @BindView(R.id.rla_play_one_layout) View mPlaySearchLayout;
    @BindView(R.id.rla_play_two_layout) View mPlayLearnLayout;
    @BindView(R.id.main_word_view) TextView tviWord;
    @BindView(R.id.animation_view) LottieAnimationView starAnimationView;

    // Fullscreen variables
    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = this::hide;

    // Controllers
    private Game game;
    private boolean isWordLoading = false;
    private boolean isAnimationRunning = false;
    private Speaking speaking;

    // Click and touch variables
    private View.OnClickListener startSearchClickListener = (View view) -> {
        this.game = new Game(this);
        this.mPlaySearchLayout.setVisibility(View.GONE);
        this.mPlayLearnLayout.setVisibility(View.GONE);
        this.game.startFindObject();
        this.enableNfcRead();
    };
    private View.OnClickListener startLearnClickListener = (View view) -> {
        this.game = new Game(this);
        this.mPlaySearchLayout.setVisibility(View.GONE);
        this.mPlayLearnLayout.setVisibility(View.GONE);
        this.game.startLearnWords();
        this.enableNfcRead();
    };

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

        mPlaySearchLayout.setOnClickListener(this.startSearchClickListener);
        mPlayLearnLayout.setOnClickListener(this.startLearnClickListener);
        tviWord.setOnClickListener(v -> game.speakWord());

        this.nfcInit();
        this.speaking = Speaking.instance().init(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHideHandler.postDelayed(mHideRunnable, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHideHandler.postDelayed(mHideRunnable, 500);
        if (game != null && game.hasStarted()) {
            this.enableNfcRead();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.disableNfcRead();
    }

    @Override
    protected void onDestroy() {
        this.speaking.stop();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        ArrayList<String> messages = TagControl.readTag(intent);
        this.game.handleGuess(messages.get(0));
    }

    @Override
    public void onWordObtained(String word) {
        Log.i(TAG, word);
        tviWord.setText(word);
        if (!this.isAnimationRunning){
            tviWord.setVisibility(View.VISIBLE);
            this.game.startGuess();
        }
    }

    @Override
    public void startSuccessAnimation() {
        this.startGameAnimation("favourite_app_icon.json", () -> {
            if (!isWordLoading) {
                if (game.isFinished()) {
                    this.mPlaySearchLayout.setVisibility(View.VISIBLE);
                    this.tviWord.setVisibility(View.GONE);
                    this.disableNfcRead();
                } else {
                    tviWord.setVisibility(View.VISIBLE);
                    this.game.startGuess();
                }
            }
        });
        this.tviWord.setVisibility(View.INVISIBLE);
    }

    @Override
    public void startErrorAnimation() {
        this.startGameAnimation("x_pop.json", () -> tviWord.setVisibility(View.VISIBLE));
        this.tviWord.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setIsWordLoading(boolean value) {
        this.isWordLoading = value;
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

    private void startGameAnimation(String animationFile, Runnable onAnimationFinished) {
        this.isAnimationRunning = true;
        starAnimationView.setAnimation(animationFile);
        starAnimationView.playAnimation();
        starAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimationRunning = false;
                starAnimationView.setVisibility(View.INVISIBLE);
                starAnimationView.setProgress(0);
                starAnimationView.clearAnimation();
                onAnimationFinished.run();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                starAnimationView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hide() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void nfcInit() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] {ndef, };
        mTechLists = new String[][] { new String[] { NfcA.class.getName() } };
    }
}
