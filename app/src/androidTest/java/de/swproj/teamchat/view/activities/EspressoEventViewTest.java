package de.swproj.teamchat.view.activities;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.User;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class EspressoEventViewTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, true,
            true);

    private MainActivity mActivity = null;
    private DBStatements db;
    private Date currentTime = Calendar.getInstance().getTime();
    private Time time = new Time(currentTime.getTime());

    private String groupName1 = "Espresso Gruppe 1";


    /**
     * This is a UI test. Espresso automatically performs actions on the UI and then makes assertions
     * (for example, if a certain textview is displaying the correct text after you clicked a button)
     *
     * Log in before you start test - Bot cannot handle Google SignIn
     */


    // Prepares Database with Dummy Data - This is run automatically before every Test
    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();


        DBStatements.dropAll();

        DBStatements.insertUser(new User("Test1", "test1@mail.de", "Test User 1", "Gott", "Herr"));
        DBStatements.insertUser(new User("Admin", "admin@mail.d", "Admin", "Hors", "tidiot"));
        DBStatements.insertUser(new User("abc", "abc@mail.d", "ICH", "ABC", "Derine"));
        DBStatements.insertUser(new User("Test2", "test2@mail.d", "Test User 2", "Gott2", "Herr"));
        DBStatements.insertUser(new User("emusk", "elon@mail.d", "Elon Musk", "Musk", "Elon"));
        DBStatements.insertChat(new Chat("Gugel", (mActivity.getResources().getIntArray(R.array.androidcolors))[0], "123", "Admin"));
        //DBStatements.updateChatMembers(new String[]{"Test1", "Admin", "abc", "Test2", "emusk"}, "123");

        Date currentTime = Calendar.getInstance().getTime();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setGregorianChange(new java.sql.Date(10000));
        Time time = new Time(currentTime.getTime());
        DBStatements.insertMessage(new Event(time, "Mars Tour", "14546s", true, "emusk", new GregorianCalendar(2020, 10, 27, 9, 6), "Colonize mars with me", "123", (byte) 1));
    }


    /**
     * Opens Event Detail View (first Item in List), accepts Event, then checks if Status Display says "Committed"
     */
    @Test
    public void testEventDetailView1() {


        ViewInteraction bottomNavigationItemView = onView(allOf(withId(R.id.nav_events), withContentDescription("Events"),
                        childAtPosition(childAtPosition(withId(R.id.bottom_nav_main),0),1),isDisplayed()));
        bottomNavigationItemView.perform(click());

        DataInteraction constraintLayout = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(withClassName(is("android.widget.FrameLayout")),1))).atPosition(0);
        constraintLayout.perform(click());

        ViewInteraction materialButton = onView(allOf(withId(R.id.viewevent_btncomit), withText("zusagen"),
                        childAtPosition(childAtPosition(withId(R.id.li_message_cv),0),12),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction textView = onView(allOf(withId(R.id.viewevent_tvstatus), withText("committed"),
                childAtPosition(childAtPosition(withId(R.id.li_message_cv),0),10), isDisplayed()));
        textView.check(matches(withText("committed")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    /**
     * Opens first Chat group (which contains an event), opens the details on this event, confirms,
     * then checks if the status says "Committed"
     */

    @Test
    public void testEventDetailViewFromChat() {
        DataInteraction relativeLayout = onData(anything()).inAdapterView(allOf(withId(android.R.id.list),
                childAtPosition(withClassName(is("android.widget.FrameLayout")), 1)))
                .atPosition(0);
        relativeLayout.perform(click());

        ViewInteraction cardView = onView(allOf(withId(R.id.li_message_cv),
                        childAtPosition(withParent(withId(R.id.lvMessages)), 0), isDisplayed()));
        cardView.perform(click());

        ViewInteraction materialButton = onView(allOf(withId(R.id.viewevent_btncomit), withText("zusagen"),
                        childAtPosition(childAtPosition(withId(R.id.li_message_cv),0),12),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction textView = onView(allOf(withId(R.id.viewevent_tvstatus), withText("committed"),
                        childAtPosition(childAtPosition(withId(R.id.li_message_cv),0),10),
                        isDisplayed()));
        textView.check(matches(withText("committed")));
    }




}
