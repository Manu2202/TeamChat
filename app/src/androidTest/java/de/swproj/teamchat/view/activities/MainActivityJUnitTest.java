package de.swproj.teamchat.view.activities;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;

import static org.junit.Assert.*;

public class MainActivityJUnitTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;
    private DBStatements db;
    private int testScale = 70;  // Number of Test entries
    private Date currentTime = Calendar.getInstance().getTime();
    private Time time = new Time(currentTime.getTime());
    private Time timeEvent = new Time(currentTime.getTime() + 50000);

    /**
     * Sets up testing Activity and variables
     */
    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
        db = new DBStatements(mActivity.getBaseContext());

        db.insertUser(new User("1", "DUMMY@email.de", "DummyAccountName", "DummyName", "DummyFirstName"));

        for (int i = 0; i < testScale; i++) {
            // For User Tests
            db.insertUser(new User("10"+i, i+"@junkmail.de", "Account "+i, "Horst "+ i, i+". Voll"));

            // For Chat Tests
            db.insertChat(new Chat("TestGruppe " + i, 0xFF004888 + i, "1000" + i, "10" + i));
            db.updateChatMembers(new String[]{"10"+i, "1"}, "1000" + i);
            db.insertMessage(new Message(time, "Message Text " + i, "1000" + i, false,
                    "10"+i, "1000"+i));

        }

    }

    /**
     * Basically a Hello World test
     */
    @Test
    public void testLaunch() {
        View view = mActivity.findViewById(R.id.bottom_nav_main);
        assertNotNull(view);
    }

    /**
     * Database Test: First Name
     */
    @Test
    public void testDBStatementsUserFirstName() {
        for (int i = 0; i < testScale; i++) {
            assertEquals((db.getUser("10"+i).getFirstName()), i + ". Voll");
        }
    }

    @Test
    public void testDBStatementsUserFirstName2() {
        for (int i = 0; i < testScale; i++) {
            assertNotEquals((db.getUser("10"+i).getFirstName()), "Nix");
        }
    }


    /**
     * Database Test: Username
     */
    @Test
    public void testDBStatementsUserName() {
        for (int i = 0; i < testScale; i++) {
            assertEquals((db.getUser("10"+i).getName()), "Horst " + i);
        }
    }

    @Test
    public void testDBStatementsUserName2() {
        for (int i = 0; i < testScale; i++) {
            assertNotEquals((db.getUser("10"+i).getName()), "Nix");
        }
    }


    /**
     * Database Test: User ID
     */
    @Test
    public void testDBStatementsUserGoogleId() {
        for (int i = 0; i < testScale; i++) {
            assertEquals((db.getUser("10"+i).getGoogleId()), "10" + i);
        }
    }

    @Test
    public void testDBStatementsUserGoogleId2() {
        for (int i = 0; i < testScale; i++) {
            assertNotEquals((db.getUser("10"+i).getGoogleId()), "00000");
        }
    }


    /**
     * Database Test: Email
     */
    @Test
    public void testDBStatementsUserGoogleMail() {
        for (int i = 0; i < testScale; i++) {
            assertEquals((db.getUser("10"+i).getGoogleMail()), i+"@junkmail.de");
        }
    }

    @Test
    public void testDBStatementsUserGoogleMail2() {
        for (int i = 0; i < testScale; i++) {
            assertNotEquals((db.getUser("10"+i).getGoogleMail()), "Nix@Nix.de");
        }
    }

    /**
     * Database Test: Account Name
     */
    @Test
    public void testDBStatementsUserAccountName() {
        for (int i = 0; i < testScale; i++) {
            assertEquals((db.getUser("10"+i).getAccountName()), "Account " + i );
        }
    }

    @Test
    public void testDBStatementsUserAccountName2() {
        for (int i = 0; i < testScale; i++) {
            assertNotEquals((db.getUser("10"+i).getAccountName()), "____");
        }
    }


    /**
     * Database Test: Chats
     */
    @Test
    public void testChatFunctionsAdmin() {
        for (int i = 0; i < testScale; i++) {
            assertEquals(db.getChat(("1000" + i)).getAdmin(), "10" + i);
        }
    }

    @Test
    public void testChatFunctionsMembers() {
        for (int i = 0; i < testScale; i++) {
            ArrayList<String> memberIds = new ArrayList<>();
            memberIds.add("10"+i);
            memberIds.add("1");
            assertEquals(db.getChatMembers("1000" + i), memberIds);
        }
    }

    @Test
    public void testChatFunctionsID() {
        for (int i = 0; i < testScale; i++) {
            assertEquals(db.getChat(("1000" + i)).getId(), "1000" + i);
        }
    }

    @Test
    public void testChatFunctionsName() {
        for (int i = 0; i < testScale; i++) {
            assertEquals(db.getChat(("1000" + i)).getName(), "TestGruppe " + i);
        }
    }


    @Test
    public void testDBStatementsMessages() {

        /*  db.insertMessage(new Message(time, "Message Text " + i, "1000" + i, false,
                    "10"+i, "1000"+i));
         */
        for (int i = 0; i < testScale; i++) {
            Message testMessage = db.getMessage("1000" + i);
            assertEquals(testMessage.getMessage(), "Message Text " + i);
            assertEquals(testMessage.getChatid(), "1000" + i);
            assertEquals(testMessage.getCreator(), "10" + i);
            assertFalse(testMessage.isEvent());

            //  Not the same:
            // assertEquals(testMessage.getTimeStamp(), time);
            // Log.d("Actual Time :", time.toString());
            // Log.d("Returned Time :", testMessage.getTimeStamp().toString());
        }
    }


    /**
     * Destructor that is run after tests
     */
    @After
    public void tearDown() throws Exception {
             mActivity = null;

    }

}