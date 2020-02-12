package id.kiosku.xbug;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.kiosku.utils.DeviceDriver;
import id.kiosku.utils.ScreenDensityReader;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import id.kiosku.xbug.receivers.GeneralApiReceiver;

import static android.content.Context.BIND_AUTO_CREATE;


/**
 * Created by Dodi on 09/09/2016.
 */
public class XBug {
    public static final int DEFAULT=0;
    public static final int MODE_NO_SOCKET=201;
    public static final int MODE_NO_APP=202;
    public static final int MODE_API_ONLY=203;
    public static final int TYPE_CRITICAL=800;
    public static final int TYPE_VERBOSE=801;
    public static final int TYPE_DEBUG=802;
    public static final int TYPE_INFO=803;
    public static final int TYPE_WARNING=804;
    public static final int TYPE_ERROR=805;
    public static final int TYPE_ASSERT=806;

    private static XBug instance;
    private Context context;
    private String url, socketUrl;
    private boolean mode,withSocket,withApp;
    private boolean toast,queue;

    private Toast toaster;

    private XBugManager manager;
    private ServiceConnection serviceConnection;

    private Retrofit retrofit;
    private XBugAPI xBugAPI;

    private Socket socket;

    public XBug(Context context){
        this(context,DEFAULT);
    }

    public XBug(Context context,int flag) {
        this.context = context;
        try {
            url = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData.getString("id.kiosku.xbug.server", "http://127.0.0.1");
            socketUrl = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData.getString("id.kiosku.xbug.server.socket", "http://127.0.0.1");
            mode = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData.getBoolean("id.kiosku.xbug.debug", false);
            toast = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData.getBoolean("id.kiosku.xbug.debug.toast", false);
            queue = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData.getBoolean("id.kiosku.xbug.debug.toast.queue", true);
        } catch (Exception e) {}

        switch (flag){
            case MODE_NO_SOCKET:{
                withSocket = false;
                withApp = true;
            }break;
            case MODE_NO_APP :{
                withSocket = true;
                withApp = false;
            }break;
            case MODE_API_ONLY:{
                withSocket = false;
                withApp = false;
            }break;
            default:{
                withSocket = true;
                withApp = true;
            }
        }
        if (mode){
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            xBugAPI = retrofit.create(XBugAPI.class);

            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    manager = XBugManager.Stub.asInterface(service);
                    if (manager == null) i("XBug", "No XBug Application found");
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    if (manager == null) i("XBug", "XBug Disconnected");
                }
            };

            if(withApp)initService();
            if(withSocket)connect();
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    String errorTrace = "";
                    for (StackTraceElement stackTraceElement : e.getCause().getStackTrace()) {
                        errorTrace += stackTraceElement.getClassName() + ":Method " + stackTraceElement.getMethodName() + ":Line " + stackTraceElement.getLineNumber() + "\n";
                    }
                    c("XBug:UncaughtException", errorTrace);
                }
            });
        }
    }

    private void initService(){
        Intent intent = new Intent("id.kiosku.xbug.app.XBugService");
        intent.setComponent(new ComponentName("id.kiosku.xbug.app","id.kiosku.xbug.app.services.XBugService"));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        context.bindService(intent,serviceConnection,BIND_AUTO_CREATE);
    }

    private void connect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(socket==null) {
                        IO.Options opts = new IO.Options();
                        opts.reconnectionAttempts = 3;
                        opts.reconnectionDelay = 15;
                        opts.reconnectionDelayMax = 60;
                        opts.forceNew = true;
                        socket = IO.socket(socketUrl, opts);
                        socket.on(Socket.EVENT_CONNECT, onConnect)
                                .on(Socket.EVENT_CONNECT_ERROR, onError)
                                .on(Socket.EVENT_RECONNECT_ATTEMPT, onReconnectAttempt)
                                .on(Socket.EVENT_RECONNECT, onReconnect)
                                .on(Socket.EVENT_DISCONNECT, onDisconnect)
                                .on(Socket.EVENT_RECONNECT_ERROR,onReconnectError)
                                .on(Socket.EVENT_RECONNECT_FAILED,onReconnectFailed);
                        socket.connect();
                    }else{
                        socket.open();
                    }
                    socket.emit("join",context.getPackageName());
                }catch (Exception e){
                    if(mode) Log.e("XBug@connect:socket",""+e.getMessage());
                }
            }
        }).start();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(mode) Log.i("XBug$socket@onConnect","Connected");
        }
    };
    private Emitter.Listener onReconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(mode) Log.i("XBug$socket@onConnect","Reconnected");
        }
    };
    private Emitter.Listener onReconnectAttempt = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(mode) Log.i("XBug$socket@onConnect","Reconnect Attempt");
        }
    };
    private Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(mode) Log.i("XBug$socket@onConnect","Error");
        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(mode) Log.i("XBug$socket@onConnect","Disconnected");
        }
    };
    private Emitter.Listener onReconnectError = new Emitter.Listener() {
        @Override
        public void call(Object... objects) {
            if(mode) Log.i("XBug$socket@onConnect","Reconnect Error");
        }
    };
    private Emitter.Listener onReconnectFailed = new Emitter.Listener() {
        @Override
        public void call(Object... objects) {
            if(mode) Log.i("XBug$socket@onConnect", "Reconnect Failed");
        }
    };

    public static XBug with(Context context){
        return new XBug(context);
    }
    public static XBug with(Context context,int flag){
        return new XBug(context,flag);
    }

    public static void init(Context context){
        XBug.instance = new XBug(context);
    }
    public static void init(Context context,int flag){
        XBug.instance = new XBug(context,flag);
    }

    public static XBug getInstance() {
        return instance;
    }

    /**
     * Critical bug report
     * @param name
     * @param value
     */
    public void c(String name, String value){
        if(mode) {
            Log.e(name, ""+value);
            showToast(name,value);
        }
        send(TYPE_CRITICAL, name, value);
    }
    public void c(String name, String value, Throwable throwable){
        if(mode) {
            Log.e(name, ""+value, throwable);
            showToast(name,value);
        }
        send(TYPE_CRITICAL, name, value);
    }

    public void e(String name, String value){
        if(mode) {
            Log.e(name, ""+value);
            showToast(name,value);
            send(TYPE_ERROR, name, value);
        }
    }
    public void e(String name, String value, Throwable throwable){
        if(mode) {
            Log.e(name, ""+value, throwable);
            showToast(name,value);
            send(TYPE_ERROR, name, value);
        }
    }
    public void d(String name, String value){
        if(mode) {
            Log.d(name, ""+value);
            showToast(name,value);
            send(TYPE_DEBUG, name, value);
        }
    }
    public void d(String name, String value, Throwable throwable){
        if(mode) {
            Log.d(name, ""+value, throwable);
            showToast(name,value);
            send(TYPE_DEBUG, name, value);
        }
    }
    public void v(String name, String value){
        if(mode) {
            Log.v(name, ""+value);
            showToast(name,value);
            send(TYPE_VERBOSE, name, value);
        }
    }
    public void v(String name, String value, Throwable throwable){
        if(mode) {
            Log.v(name, ""+value, throwable);
            showToast(name,value);
            send(TYPE_VERBOSE, name, value);
        }
    }
    public void i(String name, String value){
        if(mode) {
            Log.i(name, ""+value);
            showToast(name,value);
            send(TYPE_INFO, name, value);
        }
    }
    public void i(String name, String value, Throwable throwable){
        if(mode) {
            Log.i(name, ""+value, throwable);
            showToast(name,value);
            send(TYPE_INFO, name, value);
        }
    }
    public void w(String name, String value){
        if(mode) {
            Log.w(name, ""+value);
            showToast(name,value);
            send(TYPE_WARNING, name, value);
        }
    }
    public void w(String name, String value, Throwable throwable){
        if(mode) {
            Log.w(name, ""+value, throwable);
            showToast(name,value);
            send(TYPE_WARNING, name, value);
        }
    }
    public void wtf(String name, String value){
        if(mode) {
            Log.wtf(name, ""+value);
            showToast(name,value);
            send(TYPE_ASSERT, name, value);
        }
    }
    public void wtf(String name, String value, Throwable throwable){
        if(mode) {
            Log.wtf(name, ""+value, throwable);
            showToast(name,value);
            send(TYPE_ASSERT, name, value);
        }
    }

    private void showToast(String name, String value){
        if(toaster!=null && !queue)toaster.cancel();
        if(toast){
            toaster = Toast.makeText(context,name+": "+value, Toast.LENGTH_LONG);
            toaster.show();
        }
    }

    public void screenOn(){
        try {
            if(manager!=null)manager.screenOn();
        }catch (RemoteException e){
            if(mode) Log.e("XBug@send:RE",""+e.getMessage());
        }catch (Exception e){
            if(mode) Log.e("XBug@send:E",""+e.getMessage());
        }
    }
    public void screenOff(){
        try {
            if(manager!=null)manager.screenOff();
        }catch (RemoteException e){
            if(mode) Log.e("XBug@send:RE",""+e.getMessage());
        }catch (Exception e){
            if(mode) Log.e("XBug@send:E",""+e.getMessage());
        }
    }
    public void screenToggle(){
        try {
            if(manager!=null)manager.screenToggle();
        }catch (RemoteException e){
            if(mode) Log.e("XBug@send:RE",""+e.getMessage());
        }catch (Exception e){
            if(mode) Log.e("XBug@send:E",""+e.getMessage());
        }
    }

    private void send(int type, String subject, String message){
        if(withApp)instance.initService();

        final DataBug data = new DataBug();
        data.packageName = context.getPackageName();
        data.tanggal = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault()).format(new Date());
        if(context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
            data.deviceId = DeviceDriver.with(context).getID();
        else data.deviceId = DeviceDriver.with(context).getAndroidID();
        data.deviceBrand = Build.BRAND;
        data.deviceType = Build.MODEL;
        data.deviceOs = Build.VERSION.RELEASE;
        data.deviceVersion = String.valueOf(Build.VERSION.SDK_INT);
        ScreenDensityReader.scan(context, new ScreenDensityReader.OnScreen() {
            @Override
            public void LDPI() {
                data.deviceRes = "LDPI";
            }

            @Override
            public void MDPI() {
                data.deviceRes = "MDPI";
            }

            @Override
            public void HDPI() {
                data.deviceRes = "HDPI";
            }

            @Override
            public void XHDPI() {
                data.deviceRes = "XHDPI";
            }

            @Override
            public void XXHDPI() {
                data.deviceRes = "XXHDPI";
            }

            @Override
            public void XXXHDPI() {
                data.deviceRes = "XXXHDPI";
            }

            @Override
            public void unknown() {
                data.deviceRes = "unknown";
            }
        });
        data.type = type;
        data.subject = subject;
        data.message = message;

        try {
            Call<GeneralApiReceiver> call = xBugAPI.send(data);
            call.enqueue(new Callback<GeneralApiReceiver>() {
                @Override
                public void onResponse(Call<GeneralApiReceiver> call, Response<GeneralApiReceiver> response) {
                    if(response.code()==200){
                        if(response.body()!=null && response.body().status!=1){
                            try{
                                if(mode) Log.e("XBug@send:API",""+response.raw().body().string());
                            }catch (Exception e){}
                        }
                    }else{
                        try{
                            if(mode) Log.e("XBug@send:API",""+response.errorBody().string());
                        }catch (Exception e){}
                    }
                }

                @Override
                public void onFailure(Call<GeneralApiReceiver> call, Throwable t) {
                    if(mode) Log.e("XBug@send:API-onFailure",""+t.getMessage(),t);
                }
            });
            if(manager!=null)manager.send(data);
            else if(mode) Log.i("XBug@send","Manager AIDL Null");
            if(socket!=null && socket.connected())socket.emit("send",new Gson().toJson(data));
        }catch (RemoteException e){
            if(mode) Log.e("XBug@send:RE",""+e.getMessage());
        }catch (Exception e){
            if(mode) Log.e("XBug@send:E",""+e.getMessage());
        }
    }
}
