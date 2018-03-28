package com.example.administrator.rubbish01.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Think on 2018/3/27.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_DB_SQL = "CREATE TABLE rubbish (id INTEGER     PRIMARY KEY AUTOINCREMENT, longitude double, latitude double, usage float, distance double,time String);";
    public DatabaseHelper(Context context) {
        super(context, "rubbish", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_DB_SQL);
        db.execSQL("insert into rubbish(id,longitude,latitude,usage) values(1,121.40586,31.2297,95.67,345.5,'2018/3/23')");//图书馆
        db.execSQL("insert into rubbish(id,longitude,latitude,usage) values(2,121.40496,31.22555,77.25,478.2,'2018/3/19')");//物理楼
        db.execSQL("insert into rubbish(id,longitude,latitude,usage) values(3,121.40993,31.22815,25.57,564.1,'2018/3/21')");//正门
        db.execSQL("insert into rubbish(id,longitude,latitude,usage) values(4,121.40309,31.22187,50.80,389,'2018/2/26')");//设计学院
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL("DROP TABLE IF EXISTS rubbish");
        onCreate(db);
    }
}
