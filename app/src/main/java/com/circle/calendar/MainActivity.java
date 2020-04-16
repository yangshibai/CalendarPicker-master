package com.circle.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.circle.calendar.activity.ItActivity;
import com.circle.calendar.activity.LvActivity;
import com.circle.calendar.activity.XiaoMiActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView xiaomi = (TextView) findViewById(R.id.calendar_xiaomi);
        TextView it = (TextView) findViewById(R.id.calendar_it);
        TextView lv = (TextView) findViewById(R.id.calendar_lv);
        xiaomi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent xiaomi = new Intent(MainActivity.this, XiaoMiActivity.class);
                startActivity(xiaomi);
            }
        });
        it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, ItActivity.class);
                startActivity(it);
            }
        });
        lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lv = new Intent(MainActivity.this, LvActivity.class);
                startActivity(lv);
            }
        });
    }
}
