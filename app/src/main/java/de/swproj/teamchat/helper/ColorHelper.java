package de.swproj.teamchat.helper;

/*
 * Created by Manuel Lanzinger on 09. Januar 2020.
 * For the project: TeamChat.
 */

import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

public class ColorHelper {

    /*
     * Helper Class for changing the text color depending on the color of the cardView
     */
    public static String cardViewColorContrast(int color) {
        double contrast_ratio = ColorUtils.calculateContrast(color, Color.parseColor("#FFFFFF"));

        if (contrast_ratio > 4)
            return "#FFFFFF";
        else
            return "#000000";
    }

    /*
     * Helper Class for changing the color of the Buttons of a f.e. cardView depending on the color
     */
    public static String buttonColorChanger(int color) {

        return "";
    }
}
