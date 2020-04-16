/*
 * Copyright (C) 2016 Facishare Technology Co., Ltd. All Rights Reserved.
 */
package com.circle.calendar.view.itcalendar.view;

import android.content.Context;
import android.widget.GridView;

import com.circle.calendar.view.itcalendar.utils.DateBean;
import com.circle.calendar.view.itcalendar.utils.SpecialCalendar;


public class CalendarView extends GridView {
    public CalendarView(Context context) {
        super(context);
    }

    public interface OnCalendarClickListener {
        void onCalendarClick(SpecialCalendar.CalendarType type, int position, DateBean dateBean);
    }

    /**
     * 刷新UI
     */
    public void refreshSelf(){}
}
