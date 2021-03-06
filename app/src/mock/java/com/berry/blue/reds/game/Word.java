package com.berry.blue.reds.game;

import android.util.Log;

import com.berry.blue.reds.fires.Beans;

class Word {
    private GameI view;
    private String[][] sample = new String[][]{
            new String[] {"manzana", "a"},
            new String[] {"plátano", "b"},
            new String[] {"durazno", "c"},
            new String[] {"pera", "d"},
            new String[] {"mango", "e"},
            new String[] {"naranja", "f"},
            new String[] {"mandarina", "g"},
            new String[] {"uva", "h"},
            new String[] {"cereza", "i"},
            new String[] {"papaya", "j"},
            new String[] {"fresa", "k"},
            new String[] {"lucuma", "l"},
            new String[] {"sandia", "m"},
            new String[] {"palta", "n"},
            new String[] {"piña", "o"},
            new String[] {"cereza", "p"}
    };
    private int i = 0;

    static Word instance() {
        if (instance == null) instance = new Word();
        return instance;
    }
    private static Word instance;

    void setView(GameI view) {
        this.view = view;
    }

    void getRandomWord() {
        Beans.Word word = new Beans.Word();
        word.name = sample[i][0];
        view.onNewWord(word, sample[i][1]);

        i++;
        if (i >= 16) i = 0;
    }

    void getWord(String key) {
        Beans.Word word = new Beans.Word();
        word.name = sample[i][0];
        view.onNewWord(word, sample[i][1]);
    }
}
