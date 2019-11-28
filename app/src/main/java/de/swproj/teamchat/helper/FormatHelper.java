package de.swproj.teamchat.helper;

/*
 * Created by Manuel Lanzinger on 27. November 2019.
 * For the project: TeamChat.
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class FormatHelper {

    private static final String[] DAYOFTHEWEEK = new String[]{
            "So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"
    };

    /*
     *Convert the Calendar Date in a String to set the TextView for the Date
     */
    public static String formatDateTime(GregorianCalendar date){
        // Format the Date
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.YYYY");
        sdfDate.setCalendar(date);
        String dateFormatted = sdfDate.format(date.getTime());

        // Getting and Setting the Dy of the week
        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
        dateFormatted = DAYOFTHEWEEK[dayOfWeek-1] + ": " + dateFormatted;

        // Format the time
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        sdfTime.setCalendar(date);
        String timeFormatted = sdfTime.format(date.getTime()) + "Uhr";

        return dateFormatted + " @" + timeFormatted;
    }

    public static String formatTime(GregorianCalendar time){
        // Format the time
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        sdfTime.setCalendar(time);
        return sdfTime.format(time.getTime());
    }

    public static String formatDate(GregorianCalendar date){
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.YYYY");
        sdfDate.setCalendar(date);
        return sdfDate.format(date.getTime());
    }

}
