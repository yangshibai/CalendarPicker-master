# CalendarPicker
Calendar日历控件（;CalendarDateView;StickyCalendar;渐变效果；带有开始时间和结束时间，且最晚到当月，当月当日以后的时间置灰不可点击）


此控件也是基于别人的项目上修改，因为之前的不符合我的需求，所以在此基础上修改，同时也修改原有项目存在的问题，在此先附上原项目链接，并对作者表示感谢：
https://github.com/CristianoLi/FSCalendar

日历一： 
这个日历是用ListView写的，是上下滑动，按需求要求还需要有选择开始时间和结束时间，所以在GitHub上找并在此基础上修改，原Github地址：https://github.com/NLMartian/SilkCal。 先看看效果： 

  ![含有开始和结束时间](https://github.com/yangshibai/CalendarPicker-master/Screenrecorder-2020-04-16.gif)

代码修改：
```
    <com.lf.li.fscalendar.view.lvcalendar.DayPickerView 
        xmlns:calendar="http://schemas.android.com/apk/res-auto"
        android:id="@+id/calendar_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/white"
        calendar:isFold="true" />
```
这里附上使用方式

```
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
```



