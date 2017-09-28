package com.berry.blue.reds.game;

import com.berry.blue.reds.RedDb;
import com.berry.blue.reds.fires.Beans;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class Word {
    private GameI view;
    private DatabaseReference reference;

    private static Word instance;

    private final String TAG = this.getClass().getSimpleName();

    private Word() {
        this.reference = RedDb.instance().getReference("words");
    }

    public static Word instance() {
        if (instance == null)
            instance = new Word();

        return instance;
    }

    public void setView(GameI view) {
        this.view = view;
    }

    public void getRandomWord() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                long wordNum = ThreadLocalRandom.current().nextLong(snap.getChildrenCount());
                Iterator<DataSnapshot> itr = snap.getChildren().iterator();

                for (long i = 0; i < wordNum; i++)
                    itr.next();

                DataSnapshot item = itr.next();
                view.onNewWord(item.getValue(Beans.Word.class), item.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                view.onError(databaseError);
            }
        };
        this.reference.addListenerForSingleValueEvent(listener);
    }

    public void getWord(String key) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                view.onNewWord(snap.getValue(Beans.Word.class), snap.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                view.onError(databaseError);
            }
        };
        this.reference.child(key).addListenerForSingleValueEvent(listener);
    }
}
