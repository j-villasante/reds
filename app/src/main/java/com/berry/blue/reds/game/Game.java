package com.berry.blue.reds.game;

import com.berry.blue.reds.RedDb;
import com.berry.blue.reds.fires.Beans;
import com.berry.blue.reds.utils.Timy;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class Game implements GameI {
    /**
     * Constant used to indicate the game has not started yet. The interfaced should be a play button.
     */
    private int NOT_PLAYING = 0;

    /**
     * Constant used to indicate the game has started in Find Object mode. In this mode the kid has to find the object according to the word shown on the device.
     */
    private int FIND_OBJECT = 1;

    /**
     * Constant used to indicate the game has started in Find Word mode. In this mode the kid can see the word corresponding to the object the device is held to.
     */
    private int LEARN_WORDS = 2;

    private int status;
    private DatabaseReference reference;
    private Beans.Game game;
    private Guess currentGuess;

    private static Game instance;

    private Game() {
        this.reference = RedDb.instance().getReference("games").push();
    }

    public static Game instance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public boolean isFindObject() {
        return this.status == this.FIND_OBJECT;
    }

    public boolean isLearnWords() {
        return this.status == this.LEARN_WORDS;
    }

    public boolean hasStarted() {
        return this.status != this.NOT_PLAYING;
    }

    public void init() {
        this.status = this.NOT_PLAYING;
    }

    public void startFindObject() {
        this.status = this.FIND_OBJECT;
        this.saveNewGame();
    }

    public void startGuess() {
        this.currentGuess = new Guess(Word.instance().getActualWordKey(), this.reference);
        this.currentGuess.start();
    }

    public void addFailedGuess() {
        this.currentGuess.addTry();
    }

    public void endGuess() {
        this.currentGuess.end();
        this.currentGuess.save();
    }

    public void startLearnWords() {
        this.status = this.LEARN_WORDS;
    }

    private void saveNewGame() {
        Beans.Game game = new Beans.Game(this.status, Timy.nowToString());
        this.reference.setValue(game);
    }

    @Override
    public void onNewWord(Beans.Word word) {

    }

    @Override
    public void onError(DatabaseError error) {

    }
}
