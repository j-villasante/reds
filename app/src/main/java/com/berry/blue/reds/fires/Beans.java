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
        public String word;
        public long elapsedTime;
        public int tries;

        public Guess(String word) {
            this.word = word;
            this.tries = 0;
        }

        public Guess() {}
    }

    public static class Word {
        public String name;

        public Word () {}
    }
}
