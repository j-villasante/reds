package com.berry.blue.reds;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
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
    public void shouldShowWord() throws InterruptedException {
        onView(withId(R.id.rla_play_layout)).perform(click());
        Parcelable message = new NdefMessage(NdefRecord.createTextRecord("en", "a"));
        Intent intent = new Intent();
        intent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, message);
        startActivityTestRule.getActivity().onNewIntent(intent);

        Thread.sleep(10000);
        onView(withId(R.id.tvi_main_word)).check(matches(withText("manzana")));
        //onView(withId(R.id.tvi_main_word)).check(matches(not()));
    }

}

