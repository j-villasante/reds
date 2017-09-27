package com.berry.blue.reds.game;

import android.util.Log;

import com.berry.blue.reds.main.Word;
import com.google.firebase.database.DatabaseReference;

public class Guess {
    private long startTime;
    private boolean finished;
    private DatabaseReference gameDbReference;
    private static final String TAG = Guess.class.getSimpleName();
    private FireGuess guess;

    Guess(Word word, DatabaseReference ref) {
        this.guess = new FireGuess(word);
        this.finished = false;
        this.gameDbReference = ref;
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
        this.gameDbReference
                .child("guesses")
                .push()
                .setValue(this.guess);
    }
}
