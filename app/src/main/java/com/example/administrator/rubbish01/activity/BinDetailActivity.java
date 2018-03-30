package com.example.administrator.rubbish01.activity;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.rubbish01.R;


public class BinDetailActivity extends AppCompatActivity {
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo4);
        String lat=getIntent().getStringExtra("data");
        Log.d("和客sjdjn款",String.valueOf(lat));
        DatabaseHelper db = new DatabaseHelper(this);
        Cursor cursor = db.getReadableDatabase().rawQuery("select * from rubbish1 where latitude=?",new String[]{lat});
        //调用moveToFirst()将数据指针移动到第一行的位置。
        if (cursor.moveToFirst()) {
            do {
                //然后通过Cursor的getColumnIndex()获取某一列中所对应的位置的索引
                int id = cursor.getInt(0);
                double distance = cursor.getDouble(4);
                float tmp = cursor.getFloat(3);
                String time = cursor.getString(5);
                TextView id1 = findViewById(R.id.RBid);
                TextView distance1 = findViewById(R.id.RBdistance);
                TextView time1= findViewById(R.id.RBtime);
                id1.setText(String.format(String.valueOf(id)));
                distance1.setText(String.format(String.valueOf(distance)+"m"));
                time1.setText(String.format(time+"分钟"));
                setRBstatus(tmp);
            }while(cursor.moveToNext());
        }
    }

    protected void setRBstatus(float tmp) {

        // 初始化垃圾桶状态
        // 垃圾桶状态图分三级：empty.png, mid.png和full.png
        ImageView rubbishBinStatus = (ImageView) findViewById(R.id.rubbishBin);

        // 初始化进度条等级
        /*
        * 进度条共18级，1-6级绿色，7-12级蓝色，13-18级红色
        * 1-6级，7-12级，13-18级分别对应不同的三张垃圾桶图片empty.png, mid.png和full.png
        * */
        int progress[] = new int[]{
                R.drawable.progressbar1,
                R.drawable.progressbar2,
                R.drawable.progressbar3,
                R.drawable.progressbar4,
                R.drawable.progressbar5,
                R.drawable.progressbar6,
                R.drawable.progressbar7,
                R.drawable.progressbar8,
                R.drawable.progressbar9,
                R.drawable.progressbar10,
                R.drawable.progressbar11,
                R.drawable.progressbar12,
                R.drawable.progressbar13,
                R.drawable.progressbar14,
                R.drawable.progressbar15,
                R.drawable.progressbar16,
                R.drawable.progressbar17,
                R.drawable.progressbar18,
        };
        ImageView progressBar = (ImageView) findViewById(R.id.progressbar);

        if(tmp > 0.0 && tmp <= 5.56) {
            rubbishBinStatus.setImageResource(R.drawable.empty);
            progressBar.setImageResource(progress[0]);
        }
        else if(tmp > 5.56 && tmp <= 11.11){
            rubbishBinStatus.setImageResource(R.drawable.empty);
            progressBar.setImageResource(progress[1]);
        }
        else if(tmp > 11.11 && tmp <= 16.67){
            rubbishBinStatus.setImageResource(R.drawable.empty);
            progressBar.setImageResource(progress[2]);
        }
        else if(tmp > 16.67 && tmp <= 22.22){
            rubbishBinStatus.setImageResource(R.drawable.empty);
            progressBar.setImageResource(progress[3]);
        }
        else if(tmp > 22.22 && tmp <= 27.78){
            rubbishBinStatus.setImageResource(R.drawable.empty);
            progressBar.setImageResource(progress[4]);
        }
        else if(tmp > 27.78 && tmp <= 33.33){
            rubbishBinStatus.setImageResource(R.drawable.empty);
            progressBar.setImageResource(progress[5]);
        }
        else if(tmp > 33.33 && tmp <= 38.89){
            rubbishBinStatus.setImageResource(R.drawable.mid);
            progressBar.setImageResource(progress[6]);
        }
        else if(tmp > 38.89 && tmp <= 44.44){
            rubbishBinStatus.setImageResource(R.drawable.mid);
            progressBar.setImageResource(progress[7]);
        }
        else if(tmp > 44.44 && tmp <= 50.00){
            rubbishBinStatus.setImageResource(R.drawable.mid);
            progressBar.setImageResource(progress[8]);
        }
        else if(tmp > 50.00 && tmp <= 55.56){
            rubbishBinStatus.setImageResource(R.drawable.mid);
            progressBar.setImageResource(progress[9]);
        }
        else if(tmp > 55.56 && tmp <= 61.11){
            rubbishBinStatus.setImageResource(R.drawable.mid);
            progressBar.setImageResource(progress[10]);
        }
        else if(tmp > 61.11 && tmp <= 66.67){
            rubbishBinStatus.setImageResource(R.drawable.mid);
            progressBar.setImageResource(progress[11]);
        }
        else if(tmp > 66.67 && tmp <= 72.22){
            rubbishBinStatus.setImageResource(R.drawable.full);
            progressBar.setImageResource(progress[12]);
        }
        else if(tmp > 72.22 && tmp <= 77.78){
            rubbishBinStatus.setImageResource(R.drawable.full);
            progressBar.setImageResource(progress[13]);
        }
        else if(tmp > 77.78 && tmp <= 83.33){
            rubbishBinStatus.setImageResource(R.drawable.full);
            progressBar.setImageResource(progress[14]);
        }
        else if(tmp > 83.33 && tmp <= 88.89){
            rubbishBinStatus.setImageResource(R.drawable.full);
            progressBar.setImageResource(progress[15]);
        }
        else if(tmp > 88.89 && tmp <= 95.55){
            rubbishBinStatus.setImageResource(R.drawable.full);
            progressBar.setImageResource(progress[16]);
        }
        else if(tmp > 95.55 && tmp <= 100){
            rubbishBinStatus.setImageResource(R.drawable.full);
            progressBar.setImageResource(progress[17]);
        }

    }

}
