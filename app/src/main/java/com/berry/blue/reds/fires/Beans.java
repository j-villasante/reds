package com.berry.blue.reds.fires;

public class Beans {
    public static class Game {
        public int type;
        public String date;

        public Game(int type, String date) {
            this.type = type;
            this.date = date;
        }

        public Game() {}
    }

    public static class Guess {
        public long elapsedTime;
        public boolean isCorrect;

        public Guess(long elapsedTime, boolean isCorrect) {
            this.elapsedTime = elapsedTime;
            this.isCorrect = isCorrect;
        }

        public Guess() {}
    }

    public static class Word {
        public String name;

        public Word () {}
    }
}
