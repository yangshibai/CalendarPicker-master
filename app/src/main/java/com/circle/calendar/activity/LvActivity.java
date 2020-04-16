package com.circle.calendar.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.circle.calendar.R;
import com.circle.calendar.view.lvcalendar.DatePickerController;
import com.circle.calendar.view.lvcalendar.DayPickerView;
import com.circle.calendar.view.lvcalendar.SimpleMonthAdapter;


public class LvActivity extends AppCompatActivity implements DatePickerController {
    private DayPickerView calendarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lv);

        calendarView = (DayPickerView) findViewById(R.id.calendar_view);
        calendarView.setController(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_today:
                calendarView.scrollToToday();
                break;
        }
        return true;
    }

    @Override
    public int getMaxYear() {
        return 0;
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
        Log.e("data", year + "--" + month + "--" + day);
    }

    @Override
    public void onDateRangeSelected(SimpleMonthAdapter.SelectedDays<SimpleMonthAdapter.CalendarDay> selectedDays) {
        Log.e("data__", selectedDays.getFirst() + "@@@" + selectedDays.getLast());  //月份记得+1
    }
}
