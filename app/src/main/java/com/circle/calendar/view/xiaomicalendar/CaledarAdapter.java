package com.circle.calendar.view.xiaomicalendar;

import android.view.View;
import android.view.ViewGroup;

/**
 *Created by LiFei on 2017/2/7.
 */

public interface CaledarAdapter {
     View getView(View convertView, ViewGroup parentView, CalendarBean bean);
}
