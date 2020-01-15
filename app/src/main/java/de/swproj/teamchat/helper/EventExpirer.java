package de.swproj.teamchat.helper;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.datamodell.chat.Event;

public class EventExpirer {
    private ScheduledExecutorService executorService;
    private long startDelay;
    private long frequency;

    public EventExpirer(long startDelay, long frequency) {
        this.startDelay = startDelay;
        this.frequency = frequency;

        startExpirationChecker();
    }

    private void startExpirationChecker() {
        executorService = Executors.newScheduledThreadPool(1);

     //   Log.d("EventExpiration", "Scheduler started");

        executorService.scheduleWithFixedDelay(new TimerTask() {
            @Override
            public void run() {
                Calendar now = new GregorianCalendar();
                ArrayList<Event> eventlist = DBStatements.getEvents();
               // Log.d("EventExpiration", "Executed.");

                // Event has status "expired" (= 1) if it is later than current time
                if (eventlist.size() > 0) {
                    for (Event e : eventlist) {
                        if (e != null) {
                            if (e.getStatus() == 0) {
                                 // Log.d("EventExpiration", "Event is checked " + e.getId());
                                //  Log.d("EventExpiration", "Event Date: " + e.getDate());
                                 if (GregorianCalendar.getInstance().compareTo(e.getDate()) > 0) {
                                    e.setStatus(1);
                                    DBStatements.updateEvent(e);
                                    //Log.d("EventExpiration", "Event expired: " + e.getId());
                                }
                                //   Log.d("EventExpiration", "(Local) Event " + e.getMessage() + " is " + e.getStatusString());
                            }
                        }
                       //     Log.d("EventExpiration", "Event " + e.getMessage() + " " + DBStatements.getEvent(e.getId()).getStatusString());
                    }
                }

            }
        }, startDelay, frequency, TimeUnit.SECONDS);
    }

    public void shutdownNow() {
        executorService.shutdownNow();
        executorService = null;
    }
}
