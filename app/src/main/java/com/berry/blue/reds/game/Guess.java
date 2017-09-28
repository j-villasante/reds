package com.berry.blue.reds.game;

import android.util.Log;

import com.berry.blue.reds.fires.Beans;
import com.google.firebase.database.DatabaseReference;

class Guess {
    private long startTime;
    private boolean finished;
    private DatabaseReference reference;
    private static final String TAG = Guess.class.getSimpleName();
    private Beans.Guess guess;

    Guess(Beans.Word word, DatabaseReference reference) {
        this.guess = new Beans.Guess(word.name);
        this.finished = false;
        this.reference = reference;
    }

    public Guess() {}

    void start() {
        if (!finished)
            startTime = System.currentTimeMillis();
        else
            Log.i(TAG, "Cannot start an ended Guess");
    }

    void addTry() {
        this.guess.tries++;
    }

    void end() {
        long endTime = System.currentTimeMillis();
        finished = true;
        this.guess.elapsedTime = endTime - startTime;
    }

    void save() {
        this.reference.setValue(this.guess);
    }
}
