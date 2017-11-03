package com.berry.blue.reds;

import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class StartActivityTests {
    @Rule
    public ActivityTestRule<MockedStartActivity> startActivityTestRule = new ActivityTestRule<>(MockedStartActivity.class);
    private CountingIdlingResource idlingResource;

    @Before
    public void registerIdlingResource() {
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.HOURS);
        onView(withId(R.id.rla_play_layout)).perform(click());
        idlingResource = new CountingIdlingResource("TIME");
        idlingResource.increment();
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @Test
    public void shouldShowWord() {
        MockedStartActivity activity = startActivityTestRule.getActivity();
        activity.setAmount(2);
        activity.setGameEndRunnable(() -> {
            idlingResource.decrement();
            onView(withId(R.id.tvi_main_word)).check(matches(not(isDisplayed())));
        });
    }

    @After
    public void unregisterIdlingResource() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }
}

