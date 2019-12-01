package de.swproj.teamchat.view.activities;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.User;

import static org.junit.Assert.*;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;
    private DBStatements db;
    private int testScale = 25;  // Number of Test entries

    /**
     * Sets up testing Activity and variables
     */
    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
        db = new DBStatements(mActivity.getBaseContext());

        for (int i = 0; i < testScale; i++) {
            // For User Tests
            db.insertUser(new User("10"+i, i+"@junkmail.de", "Account "+i, "Horst "+ i, i+". Voll"));

            // For Chat Tests
            db.insertChat(new Chat("Saufgruppe " + i, 0xFF004888 + i, "1000" + i, "Banhammer_L0rd " + i));
            if (i < testScale/2) {
                //db.getChat(("1000" + i)).addChatMember("10" + i);
            }
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




    @Test
    public void testDBStatementsChat() {
        for (int i = 0; i < testScale; i++) {
            assertEquals(db.getChat(("1000" + i)).getAdmin(), "Banhammer_L0rd " + i);
        }
    }


    /**
     * Basically a destructor that is run after tests
     */
    @After
    public void tearDown() throws Exception {
             mActivity = null;

    }

}