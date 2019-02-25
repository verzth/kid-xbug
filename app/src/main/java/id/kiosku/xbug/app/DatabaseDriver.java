package id.kiosku.xbug.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import id.kiosku.xbug.app.contract.DebugContract;

/**
 * Created by Dina on 12/19/2016.
 */

public class DatabaseDriver extends SQLiteOpenHelper {

    public DatabaseDriver(Context context, String name, int version){
        super (context, name, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getDebugQuery());

    }

    private String getDebugQuery(){
        String sql = "create table "+ DebugContract.TABLE_NAME;
        sql+="("+DebugContract.ID+" INTEGER PRIMARY KEY, ";
        sql+=DebugContract.PACKAGE_NAME+" TEXT, ";
        sql+=DebugContract.TYPE+" TEXT, ";
        sql+=DebugContract.TITLE+" TEXT, ";
        sql+=DebugContract.MESSAGE+" TEXT, ";
        sql+=DebugContract.TANGGAL+" TEXT);";

        return sql;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        String sql = "DROP TABLE "+ DebugContract.TABLE_NAME;
        db.execSQL(sql);
    }
}
