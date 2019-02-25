package id.kiosku.xbug.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.kiosku.xbug.XBug;
import id.kiosku.xbug.app.R;
import id.kiosku.xbug.app.models.DebugModel;


/**
 * Created by Dina on 12/21/2016.
 */

public class DetailDebugActivity extends Activity {

    @BindView(R.id.packagename)
    TextView packageName;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.tanggal)
    TextView tanggal;
    @BindView(R.id.type)
    TextView type;

    private boolean isMonitor = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_debug);
        ButterKnife.bind(this);

        DebugModel data = (DebugModel) getIntent().getSerializableExtra("data");
        if(getIntent().hasExtra("monitor"))isMonitor = getIntent().getBooleanExtra("monitor",false);

        packageName.setText(data.packageName);
        title.setText(data.title);
        message.setText(Html.fromHtml(data.message));
        tanggal.setText(data.tanggal);

        switch (data.type){
            case XBug.TYPE_VERBOSE:{
                type.setText("V");
            }break;
            case XBug.TYPE_DEBUG:{
                type.setText("D");
            }break;
            case XBug.TYPE_INFO:{
                type.setText("I");
            }break;
            case XBug.TYPE_WARNING:{
                type.setText("W");
            }break;
            case XBug.TYPE_ERROR:{
                type.setText("E");
            }break;
            case XBug.TYPE_ASSERT:{
                type.setText("A");
            }break;
            default: type.setText("X");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isMonitor)XBug.getInstance().screenOff();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isMonitor)XBug.getInstance().screenOn();
    }
}

