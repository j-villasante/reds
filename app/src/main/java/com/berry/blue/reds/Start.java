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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.berry.blue.reds.game.Game;
import com.berry.blue.reds.main.ViewStartI;
import com.berry.blue.reds.main.Word;
import com.berry.blue.reds.main.WordCon;
import com.berry.blue.reds.nfcUtil.TagControl;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Start extends Activity implements ViewStartI {
    // View binding
    @BindView(R.id.fullscreen_content) View mContentView;
    @BindView(R.id.rla_play_layout) View mPlayLayout;
    @BindView(R.id.main_word_view) TextView tviWord;
    @BindView(R.id.animation_view) LottieAnimationView starAnimationView;

    // Fullscreen variables
    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = this::hide;

    // Controllers
    private WordCon wordControl = WordCon.instance(this);
    private Game game = Game.instance();

    // Click and touch variables
    private View.OnClickListener startClickListener = (View view) -> {
        this.mPlayLayout.setVisibility(View.GONE);
        this.wordControl.getRandomWord();
        this.game.startFindObject();
        this.enableNfcRead();
    };
    private Handler playTouchHandler = new Handler();
    private Runnable longStartClickHandler = () -> {
        this.mPlayLayout.setVisibility(View.GONE);
        this.game.startLearnWords();
        this.enableNfcRead();
    };
    private View.OnTouchListener startTouchListener = (View v, MotionEvent e) -> {
        if(e.getAction() == MotionEvent.ACTION_DOWN)
            playTouchHandler.postDelayed(longStartClickHandler, 1000);
        if((e.getAction() == MotionEvent.ACTION_MOVE)||(e.getAction() == MotionEvent.ACTION_UP))
            playTouchHandler.removeCallbacks(longStartClickHandler);
        return true;
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

        mPlayLayout.setOnClickListener(this.startClickListener);
        // mPlayLayout.setOnTouchListener(this.startTouchListener);

        this.nfcInit();
        this.game.init();
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
        if (game.hasStarted()) {
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
        if (game.isFindObject()) {
            tviWord.setVisibility(View.INVISIBLE);
            if (this.wordControl.isActualWord(messages.get(0))) {
                this.game.endGuess();
                wordControl.getRandomWord();
                this.startGameAnimation("favourite_app_icon.json", () -> {
                    tviWord.setVisibility(View.VISIBLE);
                });
            } else {
                this.startGameAnimation("x_pop.json", () -> {
                    tviWord.setVisibility(View.VISIBLE);
                    this.game.addFailedGuess();
                });
            }
        }
        else if (game.isLearnWords()) {
            this.wordControl.getWord(messages.get(0));
        }
    }

    @Override
    public void onWordObtained(Word word) {
        tviWord.setText(word.name);
        this.game.startGuess();
    }

    @Override
    public void onValueError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

    private void enableNfcRead() {
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        Log.i(TAG, "NFC read enabled");
    }

    private void disableNfcRead() {
        mNfcAdapter.disableForegroundDispatch(this);
    }

    private void startGameAnimation(String animationFile, Runnable onAnimationFinished) {
        starAnimationView.setAnimation(animationFile);
        starAnimationView.playAnimation();
        starAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
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
}
