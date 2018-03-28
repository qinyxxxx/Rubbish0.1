package com.example.administrator.rubbish01.activity;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.administrator.rubbish01.R;

public class Demo1Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1);
        DatabaseHelper db = new DatabaseHelper(this);
        Cursor cursor = db.getReadableDatabase().query("rubbish", null, null, null, null, null, null);
        //调用moveToFirst()将数据指针移动到第一行的位置。
        if (cursor.moveToFirst()) {
            do {
                //然后通过Cursor的getColumnIndex()获取某一列中所对应的位置的索引
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                double distance = cursor.getDouble(cursor.getColumnIndex("distance"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                TextView id1 = findViewById(R.id.RBid);
                TextView distance1 = findViewById(R.id.RBdistance);
                TextView time1= findViewById(R.id.RBtime);
                id1.setText(String.format(String.valueOf(id)));
                distance1.setText(String.format(String.valueOf(distance)+"m"));
                time1.setText(String.format(time));
            }while(cursor.moveToNext());
        }
    }
}
