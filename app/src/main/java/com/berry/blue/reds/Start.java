package com.berry.blue.reds;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.berry.blue.reds.main.ViewStartI;
import com.berry.blue.reds.main.Word;
import com.berry.blue.reds.main.WordCon;

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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        mPlayLayout.setOnClickListener(this.startClickListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHideHandler.postDelayed(mHideRunnable, 500);
    }

    private void hide() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onWordObtained(Word word) {
        tviWord.setText(word.name);
    }

    @Override
    public void onValueError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
