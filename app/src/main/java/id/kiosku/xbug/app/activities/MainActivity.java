package id.kiosku.xbug.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.kiosku.xbug.XBug;
import id.kiosku.xbug.app.DataManager;
import id.kiosku.xbug.app.R;
import id.kiosku.xbug.app.adapters.ListViewAdapter;
import id.kiosku.xbug.app.models.DebugModel;


public class MainActivity extends Activity {
    private DataManager dataManager;
    @BindView(R.id.list_bug)
    ListView listBug;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.txtsearch)
    EditText search;
    @BindView(R.id.btn_refresh)
    Button refreshBtn;
    @BindView(R.id.btn_clear)
    Button clearBtn;
    private ListViewAdapter listViewAdapter;
    private ArrayList<DebugModel> list;
    private int type;
    private boolean onLoading, hasNext=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        dataManager = new DataManager(this);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.TYPE_DEBUG));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        initView();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                search();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (spinner.getSelectedItem().toString()){
                    case "verbose":{
                        type = XBug.TYPE_VERBOSE;
                    }break;
                    case "debug":{
                        type = XBug.TYPE_DEBUG;
                    }break;
                    case "info":{
                        type = XBug.TYPE_INFO;
                    }break;
                    case "warning":{
                        type = XBug.TYPE_WARNING;
                    }break;
                    case "error":{
                        type = XBug.TYPE_ERROR;
                    }break;
                    case "assert":{
                        type = XBug.TYPE_ASSERT;
                    }break;
                    case "critical":{
                        type = XBug.TYPE_CRITICAL;
                    }break;
                    default: type = 0;
                }
                search();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        listBug.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent in = new Intent(getApplicationContext(), DetailDebugActivity.class);
                in.putExtra("data", list.get(position));
                startActivity(in);
            }
        });

        listBug.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount){
                if(!onLoading && hasNext) {
                    if(firstVisibleItem>totalItemCount-10){
                        onLoading=true;
                        ArrayList<DebugModel> data = new ArrayList<>();
                        if(!search.getText().toString().trim().isEmpty() && type!=0){
                            data.addAll(dataManager.getLog(totalItemCount,search.getText().toString(),type));
                        }else if(!search.getText().toString().trim().isEmpty()){
                            data.addAll(dataManager.getLog(totalItemCount,search.getText().toString()));
                        }else if(type!=0){
                            data.addAll(dataManager.getLogByType(totalItemCount,type));
                        }else{
                            data.addAll(dataManager.getLog(totalItemCount));
                        }
                        if(data.size()<100)hasNext=false;
                        Log.e("cek", String.valueOf(data.size()));
                        list.addAll(data);
                        listViewAdapter.notifyDataSetChanged();
                        onLoading=false;
                    }
                }
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initView();
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataManager.clear();
                initView();
            }
        });
    }

    private void initView(){
        list = dataManager.getLog();
        listViewAdapter= new ListViewAdapter(this,list);
        listBug.setAdapter(listViewAdapter);
    }

    private void search(){
        list.clear();
        if(!search.getText().toString().trim().isEmpty() && type!=0){
            list.addAll(dataManager.getLog(search.getText().toString(),type));
        }else if(!search.getText().toString().trim().isEmpty()){
            list.addAll(dataManager.getLog(search.getText().toString()));
        }else if(type!=0){
            list.addAll(dataManager.getLogByType(type));
        }else{
            list.addAll(dataManager.getLog());
        }
        listViewAdapter.notifyDataSetChanged();
    }

    public void onResume(){
        super.onResume();
        search();
    }

    @Override
    protected void onDestroy() {
        dataManager.destroy();
        super.onDestroy();
    }
}
