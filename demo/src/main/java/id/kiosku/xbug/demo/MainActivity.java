package id.kiosku.xbug.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import id.kiosku.xbug.XBug;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XBug.getInstance().i(this.getLocalClassName(),"Test");
    }
}
