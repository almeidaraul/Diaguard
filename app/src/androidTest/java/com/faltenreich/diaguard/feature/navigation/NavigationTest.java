package com.faltenreich.diaguard.feature.navigation;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.DrawerMatchers;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.faltenreich.diaguard.R;
import com.faltenreich.diaguard.feature.entry.edit.EntryEditActivity;
import com.faltenreich.diaguard.feature.entry.search.EntrySearchActivity;
import com.faltenreich.diaguard.test.junit.rule.CleanUpData;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NavigationTest {

    private ActivityScenario<MainActivity> scenario;

    @Rule public final TestRule dataCleanUp = new CleanUpData();

    @Before
    public void setup() {
        scenario = ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void clickingFab_shouldOpenEntryEdit() {
        scenario.onActivity(activity -> {
            Espresso.onView(ViewMatchers.withContentDescription(R.string.entry_new))
                .perform(ViewActions.click());
            Intents.intended(IntentMatchers.hasComponent(EntryEditActivity.class.getName()));
        });
    }

    @Test
    public void clickingSearch_shouldOpenEntrySearch() {
        scenario.onActivity(activity -> {
            Espresso.onView(ViewMatchers.withContentDescription(R.string.search))
                .perform(ViewActions.click());
            Intents.intended(IntentMatchers.hasComponent(EntrySearchActivity.class.getName()));
        });
    }

    // FIXME
    @Test
    @Ignore("DrawerMatchers do not work as expected")
    public void openingNavigationDrawer_shouldOpenNavigationDrawer() throws InterruptedException {
        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
            .check(ViewAssertions.matches(DrawerMatchers.isOpen()));
    }

    // FIXME
    @Test
    @Ignore("DrawerMatchers do not work as expected")
    public void clickingOverview_shouldOpenOverview() {
        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
            .perform(DrawerActions.open());

        Espresso.onView(ViewMatchers.withId(R.id.navigation_drawer))
            .perform(NavigationViewActions.navigateTo(R.id.nav_home));

        Espresso.onView(ViewMatchers.withId(R.id.layout_latest))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
