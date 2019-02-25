package id.kiosku.xbug.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import id.kiosku.xbug.DataBug;
import id.kiosku.xbug.XBug;
import id.kiosku.xbug.app.contract.DebugContract;
import id.kiosku.xbug.app.models.DebugModel;


/**
 * Created by Dina on 12/20/2016.
 */

public class DataManager {
    private static DataManager anInstance;
    private Context context;
    private static final int DB_VERSION = 3;
    private static final String DB_NAME = "bugreader.db";
    private SQLiteDatabase database;

    public static void init(Context context){
        anInstance = new DataManager(context);
    }

    public static DataManager getInstance() {
        return anInstance;
    }

    public DataManager (Context context) {
        this.context = context;
        database = new DatabaseDriver(context, DB_NAME, DB_VERSION).getWritableDatabase();
    }
    public static DataManager with (Context context){
        return new DataManager(context);
    }

    public void clear(){
        database.delete(DebugContract.TABLE_NAME,null,null);
    }

    public void save(@NonNull DataBug data){
        try {
            ContentValues values = new ContentValues();
            values.put(DebugContract.PACKAGE_NAME, data.packageName);
            values.put(DebugContract.TITLE, data.subject);
            values.put(DebugContract.TYPE, data.type);
            values.put(DebugContract.TANGGAL,data.tanggal);
            values.put(DebugContract.MESSAGE, data.message);

            database.insertWithOnConflict(DebugContract.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }catch (Exception e){
            XBug.getInstance().e("DataManager@save",""+e.getMessage());
        }
    }

    public ArrayList<DebugModel> getLogLatest(){
        ArrayList<DebugModel> list = new ArrayList<>();
        try {
            Cursor cursor = database.rawQuery("select * from " + DebugContract.TABLE_NAME + " order by " + DebugContract.ID + " desc LIMIT 5 ", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    DebugModel temp = new DebugModel();
                    temp.id = cursor.getInt(cursor.getColumnIndex(DebugContract.ID));
                    temp.packageName = cursor.getString(cursor.getColumnIndex(DebugContract.PACKAGE_NAME));
                    temp.title = cursor.getString(cursor.getColumnIndex(DebugContract.TITLE));
                    temp.message = cursor.getString(cursor.getColumnIndex(DebugContract.MESSAGE));
                    temp.tanggal = cursor.getString(cursor.getColumnIndex(DebugContract.TANGGAL));
                    temp.type = cursor.getInt(cursor.getColumnIndex(DebugContract.TYPE));
                    list.add(temp);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            XBug.getInstance().e("DataManager@save",""+e.getMessage());
        }
        return list;
    }
    public ArrayList<DebugModel> getLog(){
        ArrayList<DebugModel> list = new ArrayList<>();
        try {
            Cursor cursor = database.rawQuery("select * from " + DebugContract.TABLE_NAME + " order by " + DebugContract.ID + " desc LIMIT 100 ", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    DebugModel temp = new DebugModel();
                    temp.id = cursor.getInt(cursor.getColumnIndex(DebugContract.ID));
                    temp.packageName = cursor.getString(cursor.getColumnIndex(DebugContract.PACKAGE_NAME));
                    temp.title = cursor.getString(cursor.getColumnIndex(DebugContract.TITLE));
                    temp.message = cursor.getString(cursor.getColumnIndex(DebugContract.MESSAGE));
                    temp.tanggal = cursor.getString(cursor.getColumnIndex(DebugContract.TANGGAL));
                    temp.type = cursor.getInt(cursor.getColumnIndex(DebugContract.TYPE));
                    list.add(temp);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            XBug.getInstance().e("DataManager@save",""+e.getMessage());
        }
        return list;
    }
    public ArrayList<DebugModel> getLog(int offset){
        ArrayList<DebugModel> list = new ArrayList<>();
        try {
            Cursor cursor = database.rawQuery("select * from " + DebugContract.TABLE_NAME + " order by " + DebugContract.ID + " desc LIMIT 100 OFFSET " + offset, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    DebugModel temp = new DebugModel();
                    temp.id = cursor.getInt(cursor.getColumnIndex(DebugContract.ID));
                    temp.packageName = cursor.getString(cursor.getColumnIndex(DebugContract.PACKAGE_NAME));
                    temp.title = cursor.getString(cursor.getColumnIndex(DebugContract.TITLE));
                    temp.message = cursor.getString(cursor.getColumnIndex(DebugContract.MESSAGE));
                    temp.tanggal = cursor.getString(cursor.getColumnIndex(DebugContract.TANGGAL));
                    temp.type = cursor.getInt(cursor.getColumnIndex(DebugContract.TYPE));
                    list.add(temp);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            XBug.getInstance().e("DataManager@save",""+e.getMessage());
        }
        return list;
    }

    public ArrayList<DebugModel> getLog(String text){
        ArrayList<DebugModel> list = new ArrayList<>();
        try {
            Cursor cursor = database.rawQuery("select * from " + DebugContract.TABLE_NAME + " where " +
                    DebugContract.TITLE + " LIKE '%" + text + "%' order by " + DebugContract.ID + " desc LIMIT 100 ", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    DebugModel temp = new DebugModel();
                    temp.id = cursor.getInt(cursor.getColumnIndex(DebugContract.ID));
                    temp.title = cursor.getString(cursor.getColumnIndex(DebugContract.TITLE));
                    temp.packageName = cursor.getString(cursor.getColumnIndex(DebugContract.PACKAGE_NAME));
                    temp.message = cursor.getString(cursor.getColumnIndex(DebugContract.MESSAGE));
                    temp.tanggal = cursor.getString(cursor.getColumnIndex(DebugContract.TANGGAL));
                    temp.type = cursor.getInt(cursor.getColumnIndex(DebugContract.TYPE));
                    list.add(temp);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            XBug.getInstance().e("DataManager@save",""+e.getMessage());
        }
        return list;
    }
    public ArrayList<DebugModel> getLog(int offset, String text){
        ArrayList<DebugModel> list = new ArrayList<>();
        try {
            Cursor cursor = database.rawQuery("select * from " + DebugContract.TABLE_NAME + " where " +
                            DebugContract.TITLE + " LIKE '%" + text + "%' order by " + DebugContract.ID + " desc LIMIT 100 OFFSET " + offset,
                    null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    DebugModel temp = new DebugModel();
                    temp.id = cursor.getInt(cursor.getColumnIndex(DebugContract.ID));
                    temp.title = cursor.getString(cursor.getColumnIndex(DebugContract.TITLE));
                    temp.packageName = cursor.getString(cursor.getColumnIndex(DebugContract.PACKAGE_NAME));
                    temp.message = cursor.getString(cursor.getColumnIndex(DebugContract.MESSAGE));
                    temp.tanggal = cursor.getString(cursor.getColumnIndex(DebugContract.TANGGAL));
                    temp.type = cursor.getInt(cursor.getColumnIndex(DebugContract.TYPE));
                    list.add(temp);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            XBug.getInstance().e("DataManager@save",""+e.getMessage());
        }
        return list;
    }
    public ArrayList<DebugModel> getLogByType(int type){
        ArrayList<DebugModel> list = new ArrayList<>();
        try {
            Cursor cursor = database.rawQuery("select * from " + DebugContract.TABLE_NAME + " where " +
                            DebugContract.TYPE + "=? order by " + DebugContract.ID + " desc LIMIT 100",
                    new String[]{String.valueOf(type)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    DebugModel temp = new DebugModel();
                    temp.id = cursor.getInt(cursor.getColumnIndex(DebugContract.ID));
                    temp.packageName = cursor.getString(cursor.getColumnIndex(DebugContract.PACKAGE_NAME));
                    temp.title = cursor.getString(cursor.getColumnIndex(DebugContract.TITLE));
                    temp.message = cursor.getString(cursor.getColumnIndex(DebugContract.MESSAGE));
                    temp.tanggal = cursor.getString(cursor.getColumnIndex(DebugContract.TANGGAL));
                    temp.type = cursor.getInt(cursor.getColumnIndex(DebugContract.TYPE));
                    list.add(temp);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            XBug.getInstance().e("DataManager@save",""+e.getMessage());
        }
        return list;
    }
    public ArrayList<DebugModel> getLogByType(int offset, int type){
        ArrayList<DebugModel> list = new ArrayList<>();
        try {
            Cursor cursor = database.rawQuery("select * from " + DebugContract.TABLE_NAME + " where " +
                            DebugContract.TYPE + "=? order by " + DebugContract.ID + " desc LIMIT 100 OFFSET " + offset,
                    new String[]{String.valueOf(type)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    DebugModel temp = new DebugModel();
                    temp.id = cursor.getInt(cursor.getColumnIndex(DebugContract.ID));
                    temp.packageName = cursor.getString(cursor.getColumnIndex(DebugContract.PACKAGE_NAME));
                    temp.title = cursor.getString(cursor.getColumnIndex(DebugContract.TITLE));
                    temp.message = cursor.getString(cursor.getColumnIndex(DebugContract.MESSAGE));
                    temp.tanggal = cursor.getString(cursor.getColumnIndex(DebugContract.TANGGAL));
                    temp.type = cursor.getInt(cursor.getColumnIndex(DebugContract.TYPE));
                    list.add(temp);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            XBug.getInstance().e("DataManager@save",""+e.getMessage());
        }
        return list;
    }
    public ArrayList<DebugModel> getLog(String text, int type){
        ArrayList<DebugModel> list = new ArrayList<>();
        try {
            Cursor cursor = database.rawQuery("select * from " + DebugContract.TABLE_NAME + " where " +
                            DebugContract.TITLE + " LIKE '%" + text + "%' AND " + DebugContract.TYPE + "=? order by " + DebugContract.ID + " desc LIMIT 100",
                    new String[]{String.valueOf(type)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    DebugModel temp = new DebugModel();
                    temp.id = cursor.getInt(cursor.getColumnIndex(DebugContract.ID));
                    temp.packageName = cursor.getString(cursor.getColumnIndex(DebugContract.PACKAGE_NAME));
                    temp.title = cursor.getString(cursor.getColumnIndex(DebugContract.TITLE));
                    temp.message = cursor.getString(cursor.getColumnIndex(DebugContract.MESSAGE));
                    temp.tanggal = cursor.getString(cursor.getColumnIndex(DebugContract.TANGGAL));
                    temp.type = cursor.getInt(cursor.getColumnIndex(DebugContract.TYPE));
                    list.add(temp);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            XBug.getInstance().e("DataManager@save",""+e.getMessage());
        }
        return list;
    }
    public ArrayList<DebugModel> getLog(int offset, String text, int type){
        ArrayList<DebugModel> list = new ArrayList<>();
        try {
            Cursor cursor = database.rawQuery("select * from " + DebugContract.TABLE_NAME + " where " +
                            DebugContract.TITLE + " LIKE '%" + text + "%' AND " + DebugContract.TYPE + "=? order by " + DebugContract.ID + " desc LIMIT 100 OFFSET " + offset,
                    new String[]{String.valueOf(type)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    DebugModel temp = new DebugModel();
                    temp.id = cursor.getInt(cursor.getColumnIndex(DebugContract.ID));
                    temp.packageName = cursor.getString(cursor.getColumnIndex(DebugContract.PACKAGE_NAME));
                    temp.title = cursor.getString(cursor.getColumnIndex(DebugContract.TITLE));
                    temp.message = cursor.getString(cursor.getColumnIndex(DebugContract.MESSAGE));
                    temp.tanggal = cursor.getString(cursor.getColumnIndex(DebugContract.TANGGAL));
                    temp.type = cursor.getInt(cursor.getColumnIndex(DebugContract.TYPE));
                    list.add(temp);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            XBug.getInstance().e("DataManager@save",""+e.getMessage());
        }
        return list;
    }

    public void destroy(){
        database.close();
    }
}
