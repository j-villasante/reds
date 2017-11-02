package com.berry.blue.reds;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.Parcelable;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class StartActivityTests {
    @Rule
    public ActivityTestRule<StartActivity> startActivityTestRule = new ActivityTestRule<>(StartActivity.class);
    private long waitingTime = 10000;
    private IdlingResource idlingResource;
    private String[] letters = new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p"};
    private int i = 0;

    private class mRunnable implements Runnable {
        @Override
        public void run() {
            Handler handler = new Handler();
            handler.postDelayed(this::perform, 3000);
        }

        void perform() {
            startActivityTestRule.getActivity().onNewIntent(getNextNfcIntent());
            this.run();
        }
    }

    @Before
    public void registerIdlingResource() {
        onView(withId(R.id.rla_play_layout)).perform(click());
        idlingResource = new ElapsedTimeIdlingResource(waitingTime);
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @Test
    public void shouldShowWord() throws Throwable{
        startActivityTestRule.runOnUiThread(new mRunnable());
        onView(withId(R.id.tvi_main_word)).check(matches(withText("plátano")));
    }

    @After
    public void unregisterIdlingResource() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }

    private Intent getNextNfcIntent() {
        Parcelable message[] = new NdefMessage[]{ new NdefMessage(NdefRecord.createTextRecord("en", letters[i])) };
        Intent intent = new Intent(NfcAdapter.ACTION_NDEF_DISCOVERED);
        intent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, message);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i++;
        if (i >= 16) i = 0;
        return intent;
    }
}

