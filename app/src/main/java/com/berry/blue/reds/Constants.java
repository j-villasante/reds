package com.berry.blue.reds;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class Constants {
    private static Constants constants;

    public int tries;
    public String category;

    private static final String TAG = "Constants";

    private Constants() {}

    public static void setup(Runnable onFinished) {
        RedDb.instance().getReference("constants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                constants = dataSnapshot.getValue(Constants.class);
                onFinished.run();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "An error occurred when setting constants");
            }
        });
    }

    public static Constants getInstance() {
        return constants;
    }
}
