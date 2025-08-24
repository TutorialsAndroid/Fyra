package com.app.fyra.utility;

import android.content.Context;

public class Utils {
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().smallestScreenWidthDp >= 600);
    }
}
