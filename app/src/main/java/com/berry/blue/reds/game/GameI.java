package com.berry.blue.reds.game;

import com.berry.blue.reds.fires.Beans;
import com.google.firebase.database.DatabaseError;

public interface GameI {
    void onNewWord(Beans.Word word);
    void onError(DatabaseError error);
}
