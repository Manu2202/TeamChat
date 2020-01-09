package de.swproj.teamchat.helper;

/*
 * Created by Manuel Lanzinger on 09. Januar 2020.
 * For the project: TeamChat.
 */

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.ColorUtils;

public class ColorHelper {

    /*
     * Helper Class for changing the text color depending on the color of the cardView
     */
    public static String cardViewColorContrast(int color, TextView[] textViews) {
        double contrast_ratio = ColorUtils.calculateContrast(color, Color.parseColor("#FFFFFF"));

        String colorString;
        if (contrast_ratio > 4)
            colorString = "#FFFFFF";
        else
            colorString = "#000000";

        // Set all Textviews to black or white depending on the contrast ratio
        for (TextView tv : textViews) {
            tv.setTextColor(Color.parseColor(colorString));
        }

        return colorString;
    }

    /*
     * Helper Class for changing the color of the Buttons of a f.e. cardView depending on the color
     */
    public static String buttonColorChanger(int color) {

        return "";
    }
}
