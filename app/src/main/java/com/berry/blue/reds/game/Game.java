package com.berry.blue.reds.game;

import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.berry.blue.reds.RedDb;
import com.berry.blue.reds.fires.Beans;
import com.berry.blue.reds.interfaces.ViewStartI;
import com.berry.blue.reds.utils.Speaking;
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

    private final String TAG = getClass().getSimpleName();

    private int status;
    private DatabaseReference reference;
    private ViewStartI view;
    private Guess currentGuess;
    private String currentWordKey;
    private Word word;

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

    public void setView(ViewStartI view) { this. view = view; }

    public boolean hasStarted() {
        return this.status != this.NOT_PLAYING;
    }

    public void init() {
        this.status = this.NOT_PLAYING;
        this.word = Word.instance();
        this.word.setView(this);
    }

    public void startFindObject() {
        if (status == NOT_PLAYING){
            this.status = this.FIND_OBJECT;
            this.saveNewGame();

            this.word.getRandomWord(); // Gets word on the method onNewWord
            this.view.setIsWordLoading(true);
        } else {
            Log.e(TAG, "A started game cannot be restarted.");
        }
    }

    public void handleGuess(String wordKey) {
        if (this.status == this.FIND_OBJECT) {
            if (this.currentWordKey.equals(wordKey)) {
                this.currentGuess.endWithAnswer(true);
                this.view.startSuccessAnimation();
                word.getRandomWord();
                this.view.setIsWordLoading(true);
            } else {
                this.currentGuess.endWithAnswer(false);
                this.view.startErrorAnimation();
            }
        }
        else if (this.status == this.LEARN_WORDS) {
            this.word.getWord(wordKey);
        }
    }

    public void startLearnWords() {
        if (status == NOT_PLAYING){
            this.status = this.LEARN_WORDS;
            this.saveNewGame();
        } else {
            Log.e(TAG, "A started game cannot be restarted.");
        }
    }

    public void startGuess() {
        this.currentGuess.start();
        Speaking.instance().speak("Hola. ¿Hablas algún idioma extranjero? Un solo idioma no es suficiente.");
    }

    private void saveNewGame() {
        Beans.Game game = new Beans.Game(this.status, Timy.nowToString());
        this.reference.setValue(game);
    }

    @Override
    public void onNewWord(Beans.Word word, String key) {
        this.view.setIsWordLoading(false);
        if (word != null) {
            this.currentWordKey = key;
            this.currentGuess = new Guess(word, this.reference.child("guesses").push());
            view.onWordObtained(word.name);
        } else {
            view.showMessage("Error");
        }
    }

    @Override
    public void onError(DatabaseError error) {
        view.onValueError(error.getMessage());
    }
}
