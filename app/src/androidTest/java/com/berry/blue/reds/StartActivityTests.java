package com.berry.blue.reds;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.Parcelable;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class StartActivityTests {
    @Rule
    public ActivityTestRule<StartActivity> startActivityTestRule = new ActivityTestRule<>(StartActivity.class);

//    @Test
//    public void shouldShowPlayButton() {
//        Log.d("testing", "I am testing");
//        onView(withId(R.id.rla_play_layout)).check(matches(isDisplayed()));
//    }

    @Test
    public void shouldShowWord() {
        onView(withId(R.id.rla_play_layout)).perform(click());
        Parcelable message[] = new NdefMessage[]{ new NdefMessage(NdefRecord.createTextRecord("en", "a")) };
        Intent intent = new Intent(NfcAdapter.ACTION_NDEF_DISCOVERED);
        intent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, message);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        timeout(2000, 1, () -> {
            startActivityTestRule.launchActivity(intent);
        });

        //onView(withId(R.id.tvi_main_word)).check(matches(withText("manzana")));
    }

    public void timeout(int millis, int amount, Runnable runnable) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            runnable.run();
            if (amount <= 0) timeout(millis, amount - 1, runnable);
        }, millis);
    }

}

