package com.berry.blue.reds.game;

import com.berry.blue.reds.Constants;
import com.berry.blue.reds.RedDb;
import com.berry.blue.reds.fires.Beans;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

class Word {
    private GameI view;
    private DatabaseReference reference;
    private Beans.Word previousWord;

    private static Word instance;
    private Constants constants;

    private Word() {
        this.reference = RedDb.instance().getReference("words");
    }

    static Word instance() {
        if (instance == null) instance = new Word();
        return instance;
    }

    void setView(GameI view) {
        this.view = view;
    }

    void getRandomWord() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                Beans.Word word;
                DataSnapshot snapshot;
                Constants constants = Constants.getInstance();
                do {
                    long wordNum = ThreadLocalRandom.current().nextLong(snap.getChildrenCount());
                    Iterator<DataSnapshot> itr = snap.getChildren().iterator();
                    for (long i = 0; i < wordNum; i++) itr.next();
                    snapshot = itr.next();
                    word = snapshot.getValue(Beans.Word.class);
                } while (previousWord != null && previousWord.name.equals(word.name) ||
                        !(constants.category.equals("todo") || word.category.equals(constants.category)));
                previousWord = word;
                view.onNewWord(word, snapshot.getKey());

//                long wordNum = ThreadLocalRandom.current().nextLong(snap.getChildrenCount());
//
//                Iterator<DataSnapshot> itr = snap.getChildren().iterator();
//
//                for (long i = 0; i < wordNum; i++) itr.next();
//
//                DataSnapshot snapshot = itr.next();
//                Beans.Word word = snapshot.getValue(Beans.Word.class);
//
//                if (word != null && previousWord != null && previousWord.name.equals(word.name)  && (constants.category.equals("todo") || word.category.equals(constants.category))) {
//                    if (itr.hasNext())
//                        snapshot = itr.next();
//                    else
//                        snapshot = snap.getChildren().iterator().next();
//
//                    word = snapshot.getValue(Beans.Word.class);
//                }
//                previousWord = word;
//                view.onNewWord(word, snapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                view.onError(databaseError);
            }
        };
        this.reference.addListenerForSingleValueEvent(listener);
    }

    void getWord(String key) {
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
