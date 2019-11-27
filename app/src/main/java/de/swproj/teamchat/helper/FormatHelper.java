package de.swproj.teamchat.helper;

/*
 * Created by Manuel Lanzinger on 27. November 2019.
 * For the project: TeamChat.
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import de.swproj.teamchat.datamodell.chat.Event;

public class FormatHelper {

    private static final String[] DAYOFTHEWEEK = new String[]{
            "So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"
    };

    /*
     *Convert the Calendar Date in a String to set the TextView for the Date
     */
    public static String formatDate(GregorianCalendar date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY");
        sdf.setCalendar(date);
        String dateFormatted = sdf.format(date.getTime());

        // Getting and Setting the Dy of the week
        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
        dateFormatted = DAYOFTHEWEEK[dayOfWeek-1] + ": " + dateFormatted;

        return dateFormatted;
    }

    /*
     * Convert the time out of the calendar into a string
     */
    public static String formatTime(GregorianCalendar date){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setCalendar(date);
        return sdf.format(date.getTime());
    }
}
