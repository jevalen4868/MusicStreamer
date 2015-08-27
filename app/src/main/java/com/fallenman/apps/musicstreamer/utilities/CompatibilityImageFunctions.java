package com.fallenman.apps.musicstreamer.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;

/**
 * Created by jeremyvalenzuela on 8/26/15.
 */
public class CompatibilityImageFunctions {
    public static Drawable getDrawable(Context context, int id, Resources resources) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return resources.getDrawable(id, context.getTheme());
        } else {
            return resources.getDrawable(id);
        }
    }
}
