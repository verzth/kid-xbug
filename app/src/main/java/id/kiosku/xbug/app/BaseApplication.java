package id.kiosku.xbug.app;

import android.app.Application;
import android.util.Log;

import id.kiosku.xbug.XBug;

/**
 * Created by Dodi on 12/06/2017.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XBug.init(this,XBug.MODE_API_ONLY);
        DataManager.init(this);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.v("UncaughtException:"+t.getName(),e.getMessage(),e);
                String errorTrace="";
                for(StackTraceElement stackTraceElement:e.getCause().getStackTrace()){
                    errorTrace+=stackTraceElement.getClassName()+":Method "+stackTraceElement.getMethodName()+":Line "+stackTraceElement.getLineNumber()+"\n";
                }
                Log.v("StackTrace:",errorTrace);
            }
        });
    }
}
