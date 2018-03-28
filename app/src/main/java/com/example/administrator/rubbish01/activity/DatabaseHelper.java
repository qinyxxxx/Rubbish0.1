package com.example.administrator.rubbish01.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Think on 2018/3/27.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_DB_SQL = "CREATE TABLE rubbish (id INTEGER     PRIMARY KEY AUTOINCREMENT, longitude float, latitude float, usage String);";
    public DatabaseHelper(Context context) {
        super(context, "rubbish", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_DB_SQL);
        db.execSQL("insert into rubbish(id,longitude,latitude,usage) values(1,121.4058700000,31.2297000000,'70%')");//图书馆
        db.execSQL("insert into rubbish(id,longitude,latitude,usage) values(2,121.4049600000,31.2255500000,'70%')");//物理楼
        db.execSQL("insert into rubbish(id,longitude,latitude,usage) values(3,121.4099300000,31.2281570000,'70%')");//正门
        db.execSQL("insert into rubbish(id,longitude,latitude,usage) values(4,121.4030900000,31.2218700000,'70%')");//设计学院
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL("DROP TABLE IF EXISTS rubbish");
        onCreate(db);
    }
}
