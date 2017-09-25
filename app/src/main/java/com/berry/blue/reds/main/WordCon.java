package com.berry.blue.reds.main;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class WordCon {
    private ViewStartI view;
    private DatabaseReference reference;
    private Word actualWord;

    private static WordCon instance;

    private final String WORD_CONTR = this.getClass().getSimpleName();

    private WordCon(ViewStartI view) {
        this.view = view;
        this.reference = FirebaseDatabase.getInstance().getReference("words");
    }

    public static WordCon getInstance(ViewStartI view) {
        if (instance == null) {
            instance = new WordCon(view);
        }
        return instance;
    }

    public void getRandomWord() {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                long wordNum = ThreadLocalRandom.current().nextLong(snap.getChildrenCount());
                Iterator<DataSnapshot> itr = snap.getChildren().iterator();

                for (long i = 0; i < wordNum; i++) {
                    itr.next();
                }
                DataSnapshot item = itr.next();
                Word word = item.getValue(Word.class);
                if (word != null) word.key = item.getKey();
                actualWord = word;
                Log.i(WORD_CONTR, word.key);
                view.onWordObtained(word);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                view.onValueError(databaseError.getMessage());
            }
        };
        this.reference.addListenerForSingleValueEvent(postListener);
    }

    public void handleTagScan(String key) {
        if (this.actualWord.key.equals(key)) {
            this.getRandomWord();
        } else {
            this.view.showMessage("Palabra incorrecta");
        }
    }
}
