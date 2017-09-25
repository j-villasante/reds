package com.berry.blue.reds;

import com.google.firebase.database.FirebaseDatabase;

public class RedDb {
    private static RedDb db;

    private FirebaseDatabase database;

    private RedDb() {
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        database.getReference("words").keepSynced(true);
    }

    public static RedDb instance() {
        if (db == null) {
            db = new RedDb();
        }
        return db;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }
}
