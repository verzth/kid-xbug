package id.kiosku.xbug.utilities;

import android.content.Context;

/**
 * Created by Dodi on 05/09/2017.
 */

public class ScreenDensityReader {
    public interface OnScreen {
        void LDPI();
        void MDPI();
        void HDPI();
        void XHDPI();
        void XXHDPI();
        void XXXHDPI();
        void unknown();
    }

    public static void scan(Context context, OnScreen listener){
        float density = context.getResources().getDisplayMetrics().density;
        if (density >= 0.75f && density < 1.0f) listener.LDPI();
        else if (density >= 1.0f && density < 1.5f) listener.MDPI();
        else if (density >= 1.5f && density < 2.0f) listener.HDPI();
        else if (density >= 2.0f && density < 3.0f) listener.XHDPI();
        else if (density >= 3.0f && density < 4.0f) listener.XXHDPI();
        else if (density >= 4.0f) listener.XXXHDPI();
        else listener.unknown();
    }
}
