package com.berry.blue.reds.game;

import com.berry.blue.reds.fires.Beans;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

class Guess {
    private long startTime;
    private DatabaseReference reference;
    private String word;
    private List<Beans.Guess> guessList;

    Guess(Beans.Word word, DatabaseReference reference) {
        this.word = word.name;
        this.reference = reference;
        this.guessList = new ArrayList<>();
    }

    void start() {
        this.startTime = System.currentTimeMillis();
    }

    void endWithAnswer(boolean value) {
        long endTime = System.currentTimeMillis();
        guessList.add(new Beans.Guess(endTime - startTime, value));
        if (value) this.save();
    }

    private void save() {
        this.reference.setValue(this.guessList);
        this.reference.child("word").setValue(word);
    }
}
