package com.berry.blue.reds;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        updateUI(mAuth.getCurrentUser());
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, StartActivity.class));
        }
    }
}
