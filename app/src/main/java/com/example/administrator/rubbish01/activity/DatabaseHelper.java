package com.example.administrator.rubbish01.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Think on 2018/3/27.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_DB_SQL = "CREATE TABLE rubbish1 (id INTEGER     PRIMARY KEY AUTOINCREMENT, longitude double, latitude double, usage float, distance double,time String);";
    public DatabaseHelper(Context context) {
        super(context, "rubbish1", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_SQL);
        db.execSQL("insert into rubbish1(id,longitude,latitude,usage,distance,time) values(1,121.40586,31.2297,95.67,345.5,'5.76')");//图书馆
        db.execSQL("insert into rubbish1(id,longitude,latitude,usage,distance,time) values(2,121.405161,31.226158,77.25,478.2,'7.97')");//计算机楼
        db.execSQL("insert into rubbish1(id,longitude,latitude,usage,distance,time) values(3,121.40993,31.22815,25.57,564.1,'9.37')");//正门
        db.execSQL("insert into rubbish1(id,longitude,latitude,usage,distance,time) values(4,121.40309,31.22187,50.80,389,'6.48')");//设计学院
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL("DROP TABLE IF EXISTS rubbish1");
        onCreate(db);
    }
}
