package com.berry.blue.reds.game;

import android.util.Log;

import com.berry.blue.reds.RedDb;
import com.berry.blue.reds.main.Word;
import com.google.firebase.database.Exclude;

public class Guess {
    private Word word;
    private long elapsedTime;
    private int tries;
    @Exclude
    private long startTime;
    @Exclude
    private long endTime;
    @Exclude
    private boolean finished;
    @Exclude
    private static final String TAG = Guess.class.getSimpleName();

    public Guess(Word word) {
        this.word = word;
        this.tries = 0;
        this.finished = false;
    }

    public void start() {
        if (!finished)
            startTime = System.currentTimeMillis();
        else
            Log.i(TAG, "Cannot start an ended Guess");
    }

    public void end() {
        endTime = System.currentTimeMillis();
        finished = true;
        elapsedTime = endTime - startTime;
    }

    public Guess() {}

    public void save() {
        RedDb.instance()
                .getDatabase()
                .getReference("guesses")
                .push()
                .setValue(this);
    }
}
