package com.berry.blue.reds.main;

import android.util.Log;

import com.berry.blue.reds.RedDb;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class WordCon {
    private ViewStartI view;
    private DatabaseReference reference;
    private Word actualWord;

    private static WordCon instance;

    private final String TAG = this.getClass().getSimpleName();

    private WordCon(ViewStartI view) {
        this.view = view;
        this.reference = RedDb.instance().getDatabase().getReference("words");
    }

    public static WordCon instance(ViewStartI view) {
        if (instance == null) {
            instance = new WordCon(view);
        }
        return instance;
    }

    public void getRandomWord() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                long wordNum = ThreadLocalRandom.current().nextLong(snap.getChildrenCount());
                Log.i(TAG, "Random: " + wordNum);
                Iterator<DataSnapshot> itr = snap.getChildren().iterator();

                for (long i = 0; i < wordNum; i++)
                    itr.next();

                DataSnapshot item = itr.next();
                Word word = item.getValue(Word.class);
                if (word != null) {
                    word.key = item.getKey();
                    actualWord = word;
                    view.onWordObtained(word);
                } else {
                    view.showMessage("Error");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                view.onValueError(databaseError.getMessage());
            }
        };
        this.reference.addListenerForSingleValueEvent(listener);
    }

    public void getWord(String key) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                Word word = snap.getValue(Word.class);
                if (word != null){
                    word.key = snap.getKey();
                    view.onWordObtained(word);
                } else {
                    view.showMessage("El tag no esta registrado");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                view.onValueError(databaseError.getMessage());
            }
        };
        this.reference.child(key).addListenerForSingleValueEvent(listener);
    }

    public boolean isActualWord(String key) {
        return this.actualWord.key.equals(key);
    }
}
