package id.kiosku.xbug;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import id.kiosku.xbug.receivers.GeneralApiReceiver;

/**
 * Created by Dodi on 01/26/2018.
 */

public interface XBugAPI {
    @POST("services/bug")
    Call<GeneralApiReceiver> send(@Body DataBug data);
}
