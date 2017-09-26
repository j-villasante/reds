package com.berry.blue.reds.main;

import com.google.firebase.database.Exclude;

public class Word {
    public String name;

    @Exclude
    public String key;

    public Word () {}
}