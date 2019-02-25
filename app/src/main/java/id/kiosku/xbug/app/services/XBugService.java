package id.kiosku.xbug.app.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.kiosku.xbug.DataBug;
import id.kiosku.xbug.XBugManager;
import id.kiosku.xbug.app.DataManager;
import id.kiosku.xbug.app.R;
import id.kiosku.xbug.app.activities.DetailDebugActivity;
import id.kiosku.xbug.app.adapters.ListViewAdapter;
import id.kiosku.xbug.app.models.DebugModel;

/**
 * Created by Dodi on 07/05/2017.
 */

public class XBugService extends Service {
    private WindowManager windowManager;
    private View view;

    @BindView(R.id.list)
    ListView listView;
    ListViewAdapter listViewAdapter;
    ArrayList<DebugModel> list;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new XBugManager.Stub() {
            @Override
            public void send(DataBug data) throws RemoteException {
                DataManager.getInstance().save(data);
                if(view!=null){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            initList();
                        }
                    });
                }
            }

            @Override
            public void screenOn() throws RemoteException {
                drawMonitor();
            }

            @Override
            public void screenOff() throws RemoteException {
                undrawMonitor();
            }

            @Override
            public void screenToggle() throws RemoteException {
                toggleMonitor();
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
    }

    private void initList(){
        list = DataManager.with(getBaseContext()).getLogLatest();
        listViewAdapter = new ListViewAdapter(getBaseContext(),list);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(getApplicationContext(), DetailDebugActivity.class);
                in.putExtra("monitor", true);
                in.putExtra("data", list.get(position));
                startActivity(in);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        undrawMonitor();
    }

    public boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return Settings.canDrawOverlays(this);
    }

    private void drawMonitor(){
        if(checkDrawOverlayPermission()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    view = LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_monitor, null, false);
                    ButterKnife.bind(XBugService.this, view);
                    initList();

                    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                            (int) getResources().getDimension(R.dimen.monitor_width),
                            (int) getResources().getDimension(R.dimen.monitor_height),
                            WindowManager.LayoutParams.TYPE_PHONE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                            PixelFormat.TRANSLUCENT);
                    params.gravity = Gravity.TOP | Gravity.END | Gravity.RIGHT;
                    try {
                        windowManager.removeViewImmediate(view);
                    } catch (Exception e) {
                    }
                    windowManager.addView(view, params);
                }
            });
        }else{
            Toast.makeText(this,"XBug App need overlay permission for monitor",Toast.LENGTH_SHORT).show();
        }
    }
    private void undrawMonitor(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    windowManager.removeViewImmediate(view);
                    view = null;
                }catch (Exception e){}
            }
        });
    }

    private void toggleMonitor(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    windowManager.removeViewImmediate(view);
                    view = null;
                }catch (Exception e){
                    drawMonitor();
                }
            }
        });
    }
}
