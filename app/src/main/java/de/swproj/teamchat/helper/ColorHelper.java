package de.swproj.teamchat.helper;

/*
 * Created by Manuel Lanzinger on 09. Januar 2020.
 * For the project: TeamChat.
 */

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;

import java.util.Arrays;
import java.util.List;

import de.swproj.teamchat.R;

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

    /**
     * Helper Class for changing the color of the Buttons of a f.e. cardView depending on the color
     */
    public static void cardViewButtonColor(Button[] buttons, int chatColor, Resources resources, Context context) {
        int[] androidColors = resources.getIntArray((R.array.androidcolors));
        int[] buttonColors = resources.getIntArray((R.array.lightcolors)); // contains lighter color versions

        // Default Color, if nothing else is found
        int buttonColor = 0x6EC6FF;

        // Not very nice solution. Searches Chatcolor Array for the color in order to find index for lightColor array
        for (int i = 0; i < androidColors.length; i++) {
            if (chatColor == androidColors[i]){
                buttonColor = buttonColors[i];
            }
        }

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[] {
                buttonColor,
  //              changeHSVofColor(buttonColor, 0.5f, 1),
                buttonColor,
//                changeHSVofColor(buttonColor, 0.5f, 1)
                buttonColor,
                buttonColor
        };

        ColorStateList myList = new ColorStateList(states, colors);

        for (Button btn : buttons) {
           // Resources.Theme theme = context.getTheme();
            btn.setBackgroundTintList(myList);
        }


        // Step 2: Set a good text color for the buttons
        double contrast_ratio = ColorUtils.calculateContrast(chatColor, Color.parseColor("#FFFFFF"));

        int buttonTextColor;
        if (contrast_ratio > 4)
            buttonTextColor = Color.WHITE;
        else
            buttonTextColor = Color.BLACK;

        for (Button btn : buttons) {
            // Resources.Theme theme = context.getTheme();
            btn.setTextColor(buttonTextColor);
        }

    }


    /**
     * Changes a color's hue, saturation or value
     * @param color : The Color value you want changed
     * @param factor : The factor by which it should change
     * @param HueSatOrValue : 0 = Hue ,  1 = Saturation , 2 = Value
     * @return
     */
    private static int changeHSVofColor(int color, float factor, int HueSatOrValue) {

        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        float[] hsv = new float[3];
        Color.RGBToHSV(red, green, blue, hsv);

        // [0] = Hue ,  [1] = Saturation , [2] = Value
        hsv[HueSatOrValue] *= factor;

        return Color.HSVToColor(hsv);
    }



}
