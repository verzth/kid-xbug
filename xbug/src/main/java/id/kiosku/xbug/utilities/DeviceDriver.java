package id.kiosku.xbug.utilities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Created by Dodi on 09/22/2016.
 */
@SuppressWarnings("MissingPermission")
public class DeviceDriver {
    private static DeviceDriver anInstance;
    private Context context;
    private TelephonyManager manager;
    private AccountManager accountManager;
    public DeviceDriver(Context context){
        this.context=context;
        manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        accountManager = AccountManager.get(context);
    }
    public static DeviceDriver with(Context context){
        return new DeviceDriver(context);
    }
    public static void init(Context context){
        DeviceDriver.anInstance = DeviceDriver.with(context);
    }

    public static DeviceDriver getInstance() {
        return anInstance;
    }

    /**
     * @return Random string Identity, Priority Device - Android - SIM
     */
    public String getID(){
        String ID = manager.getDeviceId();
        if(ID==null){
            ID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if(ID==null){
                ID = manager.getSimSerialNumber();
            }
        }
        return ID;
    }
    public String getDeviceID(){
        return manager.getDeviceId();
    }
    public String getAndroidID(){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    public String getSimID(){
        return manager.getSimSerialNumber();
    }
    public String getPhoneNumber(){
        return manager.getLine1Number();
    }
    public String getSubscriberID(){
        return manager.getSubscriberId();
    }
    public String getOperator(){
        return manager.getNetworkOperator();
    }
    public String getOperatorName(){
        return manager.getNetworkOperatorName();
    }
    public String getSimOperator(){
        return manager.getSimOperator();
    }
    public String getSimOperatorName(){
        return manager.getSimOperatorName();
    }

    /**
     * NEED Manifest.permission.GET_ACCOUNT PERMISSION
     * @return EMAIL ADDRESS
     * @throws SecurityException
     */
    public String getPrimaryEmail()throws SecurityException {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = accountManager.getAccounts();
        for(Account account : accounts){
            if(emailPattern.matcher(account.name).matches())return account.name;
        }
        return null;
    }
}