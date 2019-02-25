package id.kiosku.xbug.receivers;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Dodi on 01/27/2017.
 */

public class GeneralApiReceiver {
    public int status;
    @SerializedName("status_number")
    public int statusNumber;
    @SerializedName("status_code")
    public String statusCode;
    @SerializedName("status_message")
    public String statusMessage;
    @SerializedName("message")
    public String message;
}
