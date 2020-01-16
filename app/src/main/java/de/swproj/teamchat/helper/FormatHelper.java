package de.swproj.teamchat.helper;

/*
 * Created by Manuel Lanzinger on 27. November 2019.
 * For the project: TeamChat.
 */

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FormatHelper {

    private static final String[] DAYOFTHEWEEK = new String[]{
            "Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"
    };

    /*
     *Convert the Calendar Date in a String to set the TextView for the Date
     */
    public static String formatDateTime(GregorianCalendar date) {
        // Format the Date
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
        sdfDate.setCalendar(date);
        String dateFormatted = sdfDate.format(date.getTime());
        // Getting and Setting the Dy of the week
        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);

        // Format the time
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        sdfTime.setCalendar(date);
        String timeFormatted = sdfTime.format(date.getTime()) + " Uhr";

        return dateFormatted + "\n" + DAYOFTHEWEEK[dayOfWeek - 1] + " " + timeFormatted;
    }

    public static GregorianCalendar formatDate(String date) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
        GregorianCalendar greg_date = (GregorianCalendar) GregorianCalendar.getInstance();
        try {
            greg_date.setTime(sdfDate.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return greg_date;
    }

    public static Time formatTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        long ms = 0;
        try {
            ms = sdf.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Time(ms);
    }

    public static String formatTime(Time time) {
        DateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(time.getTime()); // 11:17
    }

    public static String formatTime(GregorianCalendar time) {
        // Format the time
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        sdfTime.setCalendar(time);
        return sdfTime.format(time.getTime());
    }

    public static String formatDate(GregorianCalendar date) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
        sdfDate.setCalendar(date);
        return sdfDate.format(date.getTime());
    }

    public static String formatHoursMinutesFromDate(Date date) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm");
        return sdfDate.format(date);
    }

    public static String getMonthfromDate(GregorianCalendar date) {
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MMMM yyyy");
        return sdfMonth.format(date.getTime());
    }
}
