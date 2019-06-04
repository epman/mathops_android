package org.epm.math.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

/**
 * https://github.com/epman/mathops_android
 * GNU General Public License v3.0
 */
public class Math {
    private final static String DEFKEY_INT_POINTS = "points";
    public final static String DEFKEY_BOOL_IT_NOTATION = "itn";
    public final static String DEFKEY_BOOL_HIGH_CONTRAST = "pref_bool_highcontrast";

    @Nullable
    private static Math ourInstance = null;

    public static Math getInstance(@NonNull final Context ctx) {
        if (ourInstance==null)
            ourInstance = new Math(ctx);
        return ourInstance;
    }

    private int points;

    private Math(@NonNull final Context ctx) {
        final SharedPreferences defs = PreferenceManager.getDefaultSharedPreferences(ctx);
        points = defs.getInt(DEFKEY_INT_POINTS, 0);
    }

    public final void increasePoints(@NonNull final Context ctx)
    {
        points++;
        final SharedPreferences defs = PreferenceManager.getDefaultSharedPreferences(ctx);
        defs.edit()
                .putInt(DEFKEY_INT_POINTS, points)
                .apply();
    }

    public final int getPoints() {
        return points;
    }


    /**
     * @see <a href="https://developer.android.com/training/system-ui/immersive.html">Using Immersive Full-Screen Mode</a>
     */
    public static void hideSystemUI(@NonNull final View mDecorView) {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public static void showSystemUI(@NonNull final View mDecorView) {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
