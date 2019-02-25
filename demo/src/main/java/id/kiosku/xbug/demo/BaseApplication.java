package id.kiosku.xbug.demo;

import android.app.Application;

import id.kiosku.xbug.XBug;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        XBug.init(this, XBug.MODE_NO_SOCKET);
    }
}
