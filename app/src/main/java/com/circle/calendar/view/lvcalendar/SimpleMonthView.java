package com.circle.calendar.view.lvcalendar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.circle.calendar.R;

import java.security.InvalidParameterException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

class SimpleMonthView extends View {

    public static final String VIEW_PARAMS_HEIGHT = "height";
    public static final String VIEW_PARAMS_MONTH = "month";
    public static final String VIEW_PARAMS_YEAR = "year";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_DAY = "selected_begin_day";
    public static final String VIEW_PARAMS_SELECTED_LAST_DAY = "selected_last_day";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_MONTH = "selected_begin_month";
    public static final String VIEW_PARAMS_SELECTED_LAST_MONTH = "selected_last_month";
    public static final String VIEW_PARAMS_SELECTED_BEGIN_YEAR = "selected_begin_year";
    public static final String VIEW_PARAMS_SELECTED_LAST_YEAR = "selected_last_year";
    public static final String VIEW_PARAMS_WEEK_START = "week_start";

    private static final int DRAGING_ALPHA = 82;
    private static final int NORMAL_ALPHA = 255;
    protected static int DEFAULT_HEIGHT = 40;
    protected static final int DEFAULT_NUM_ROWS = 6;
    protected static int DAY_SELECTED_CIRCLE_SIZE;
    protected static int EVENT_DOTS_CIRCLE_SIZE;
    protected static int DAY_SEPARATOR_WIDTH = 1;
    protected static int MINI_DAY_NUMBER_TEXT_SIZE;
    protected static int MIN_HEIGHT = 10;

    //周字体大小
    protected static int MONTH_DAY_LABEL_TEXT_SIZE;

    protected static int MONTH_HEADER_SIZE;
    protected static int MONTH_LABEL_TEXT_SIZE;
    protected static int MONTH_INFO_TEXT_SIZE;
    protected static int PADDING_BOTTOM;
    protected static int START_END_TEXT_SIZE;
    private final int mPixelWidth;

    protected int mPadding = 0;

    private String mDayOfWeekTypeface;
    private String mMonthTitleTypeface;

    protected Paint mMonthDayLabelPaint;
    protected Paint mMonthNumPaint;
    protected Paint mMonthInfoPaint;
    protected Paint mMonthTitlePaint;
    protected Paint mSelectedCirclePaint;
    protected Paint mCurrentCirclePaint;
    protected Paint mStartEndPaint;
    protected int mCurrentDayTextColor;
    protected int mMonthTextColor;
    protected int mDayTextColor;
    protected int mMonthDayLabelTextColor;
    protected int mDayNumColor;
    protected int mMonthTitleBGColor;
    protected int mPreviousDayColor;
    protected int mAfterDayColor;
    protected int mSelectedDaysColor;
    protected int mWeekendsColor;
    protected int mCurrentDayColor;

    private final StringBuilder mStringBuilder;

    protected boolean mHasToday = false;
    protected boolean mIsPrev = false;

    protected int mSelectedBeginDay = -1;
    protected int mSelectedLastDay = -1;
    protected int mSelectedBeginMonth = -1;
    protected int mSelectedLastMonth = -1;
    protected int mSelectedBeginYear = -1;
    protected int mSelectedLastYear = -1;

    protected int mToday = -1;
    protected int mWeekStart = 1;
    protected int mNumDays = 7;
    protected int mNumCells = mNumDays;
    private int mDayOfWeekStart = 0;
    protected int mMonth;
    protected int mRowHeight = DEFAULT_HEIGHT;
    protected int mWidth;
    protected int mYear;
    final Time today;
    private final Calendar mCalendar;

    private final Calendar mDayLabelCalendar;
    private final Boolean isPrevDayEnabled;

    private boolean shouldShowMonthInfo = false;
    private long alphaStartTime = -1;
    private final int FRAMES_PER_SECOND = 60;
    private final long ALPHA_DURATION = 100;
    private int currentDraggingAlpha;
    private int currentNormalAlpha;

    private int mNumRows = DEFAULT_NUM_ROWS;

    private Map<Integer, Integer> eventSymbols = new HashMap<>();

    private DateFormatSymbols mDateFormatSymbols = new DateFormatSymbols();

    private OnDayClickListener mOnDayClickListener;


    public SimpleMonthView(Context context, TypedArray typedArray) {
        super(context);
        Resources resources = context.getResources();
        mDayLabelCalendar = Calendar.getInstance();
        mCalendar = Calendar.getInstance();
        today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        mDayOfWeekTypeface = resources.getString(R.string.sans_serif);
        mMonthTitleTypeface = resources.getString(R.string.sans_serif);
        mCurrentDayTextColor = typedArray.getColor(R.styleable.DayPickerView_colorCurrentDay, resources.getColor(R.color.normal_day));
        mMonthTextColor = typedArray.getColor(R.styleable.DayPickerView_colorMonthName, resources.getColor(R.color.normal_month));
        mMonthDayLabelTextColor = resources.getColor(R.color.month_day_label_text);
        mDayTextColor = typedArray.getColor(R.styleable.DayPickerView_colorDayName, resources.getColor(R.color.normal_day));
        //中间颜色值
        mDayNumColor = typedArray.getColor(R.styleable.DayPickerView_colorNormalDay, resources.getColor(R.color.normal_day));
        mPreviousDayColor = typedArray.getColor(R.styleable.DayPickerView_colorPreviousDay, resources.getColor(R.color.normal_day));
        mAfterDayColor = typedArray.getColor(R.styleable.DayPickerView_colorAfterDay, resources.getColor(R.color.color_999999));
        mSelectedDaysColor = typedArray.getColor(R.styleable.DayPickerView_colorSelectedDayBackground, resources.getColor(R.color.selected_day_background));
        //点击选中背景颜色
        mMonthTitleBGColor = typedArray.getColor(R.styleable.DayPickerView_colorSelectedDayText, resources.getColor(R.color.selected_day_text));
        //两边颜色值
        mWeekendsColor = resources.getColor(R.color.normal_day);
        mCurrentDayColor = resources.getColor(R.color.current_day_background);
        mStringBuilder = new StringBuilder(50);

        MINI_DAY_NUMBER_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeDay, resources.getDimensionPixelSize(R.dimen.text_size_day));
        MONTH_LABEL_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeMonth, resources.getDimensionPixelSize(R.dimen.text_size_day));
        MONTH_DAY_LABEL_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeDayName, resources.getDimensionPixelSize(R.dimen.text_size_day_name));
        MONTH_HEADER_SIZE = typedArray.getDimensionPixelOffset(R.styleable.DayPickerView_headerMonthHeight, resources.getDimensionPixelOffset(R.dimen.header_month_height));
        DAY_SELECTED_CIRCLE_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_selectedDayRadius, resources.getDimensionPixelOffset(R.dimen.selected_day_radius));
        EVENT_DOTS_CIRCLE_SIZE = resources.getDimensionPixelSize(R.dimen.event_dots_radius);
        MONTH_INFO_TEXT_SIZE = resources.getDimensionPixelSize(R.dimen.month_info_text_size);
        PADDING_BOTTOM = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, resources.getDisplayMetrics());
        START_END_TEXT_SIZE = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_textSizeStartEnd, resources.getDimensionPixelOffset(R.dimen.text_size_start_end));

        mRowHeight = ((typedArray.getDimensionPixelSize(R.styleable.DayPickerView_calendarHeight, resources.getDimensionPixelOffset(R.dimen.calendar_height)) - MONTH_HEADER_SIZE) / 6);

        isPrevDayEnabled = typedArray.getBoolean(R.styleable.DayPickerView_enablePreviousDay, true);

        mPixelWidth = getDisplayPixelWidth(context);

        initView();

    }

    public static Point getDisplayPixelSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static int getDisplayPixelWidth(Context context) {
        Point size = getDisplayPixelSize(context);
        return (size.x);
    }

    private int calculateNumRows() {
        int offset = findDayOffset();
        int dividend = (offset + mNumCells) / mNumDays;
        int remainder = (offset + mNumCells) % mNumDays;
        return (dividend + (remainder > 0 ? 1 : 0));
    }

    private void drawMonthDayLabels(Canvas canvas) {
        int y = MONTH_HEADER_SIZE - (MONTH_DAY_LABEL_TEXT_SIZE / 2);
        int dayWidthHalf = (mWidth - mPadding * 2) / (mNumDays * 2);

        for (int i = 0; i < mNumDays; i++) {
            if (i == 0 || i == mNumDays - 1) {
                mMonthDayLabelPaint.setColor(mWeekendsColor);
            } else {
                mMonthDayLabelPaint.setColor(mMonthDayLabelTextColor);
            }
            int calendarDay = (i + mWeekStart) % mNumDays;
            int x = (2 * i + 1) * dayWidthHalf + mPadding;
            mDayLabelCalendar.set(Calendar.DAY_OF_WEEK, calendarDay);
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEEE");
            String dayLabelText = dateFormat.format(mDayLabelCalendar.getTime());
            canvas.drawText(dayLabelText, x, y, mMonthDayLabelPaint);
        }
    }

    /**
     * 当前月份标题
     *
     * @param canvas
     */
    private void drawMonthTitle(Canvas canvas) {
        int x = (mWidth + 2 * mPadding) / 2;
        int y = (MONTH_HEADER_SIZE - MONTH_DAY_LABEL_TEXT_SIZE) / 2 + (MONTH_LABEL_TEXT_SIZE / 3);
        StringBuilder stringBuilder = new StringBuilder(getMonthAndYearString().toLowerCase());
        stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
        canvas.drawText(stringBuilder.toString(), x, y, mMonthTitlePaint);
    }

    private int findDayOffset() {
        return (mDayOfWeekStart < mWeekStart ? (mDayOfWeekStart + mNumDays) : mDayOfWeekStart)
                - mWeekStart;
    }

    private String getMonthAndYearString() {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NO_MONTH_DAY;
        mStringBuilder.setLength(0);
        long millis = mCalendar.getTimeInMillis();
        return DateUtils.formatDateRange(getContext(), millis, millis, flags);
    }

    private String getMonthString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM");
        String monthTitleText = dateFormat.format(mCalendar.getTime());
        return monthTitleText;
    }

    private void onDayClick(SimpleMonthAdapter.CalendarDay calendarDay) {
        if (mOnDayClickListener != null && !afterDay(calendarDay.day, today) && (isPrevDayEnabled || !((calendarDay.month == today.month) && (calendarDay.year == today.year) && calendarDay.day < today.monthDay))) {
            mOnDayClickListener.onDayClick(this, calendarDay);
        }
    }

    private boolean sameDay(int monthDay, Time time) {
        return (mYear == time.year) && (mMonth == time.month) && (monthDay == time.monthDay);
    }

    private boolean prevDay(int monthDay, Time time) {
        return ((mYear < time.year)) || (mYear == time.year && mMonth < time.month) || (mMonth == time.month && monthDay < time.monthDay);
    }

    /**
     * 以后的日子
     *
     * @param monthDay
     * @param time
     * @return
     */
    private boolean afterDay(int monthDay, Time time) {
        return (mYear == time.year && mMonth >= time.month && monthDay > time.monthDay);
    }

    /**
     * draw dots to show events
     *
     * @param x      x pos of date number
     * @param y      y pos of date number
     * @param count
     * @param canvas
     */
    protected void drawDots(int x, int y, int count, Canvas canvas) {
        switch (count) {
            case 1:
                canvas.drawCircle(x, y + EVENT_DOTS_CIRCLE_SIZE * 16 / 5, EVENT_DOTS_CIRCLE_SIZE, mCurrentCirclePaint);
                break;
            case 2:
                canvas.drawCircle(x - EVENT_DOTS_CIRCLE_SIZE * 8 / 5, y + EVENT_DOTS_CIRCLE_SIZE * 16 / 5, EVENT_DOTS_CIRCLE_SIZE, mCurrentCirclePaint);
                canvas.drawCircle(x + EVENT_DOTS_CIRCLE_SIZE * 8 / 5, y + EVENT_DOTS_CIRCLE_SIZE * 16 / 5, EVENT_DOTS_CIRCLE_SIZE, mCurrentCirclePaint);
                break;
            case 3:
            default:
                canvas.drawCircle(x - EVENT_DOTS_CIRCLE_SIZE * 16 / 5, y + EVENT_DOTS_CIRCLE_SIZE * 16 / 5, EVENT_DOTS_CIRCLE_SIZE, mCurrentCirclePaint);
                canvas.drawCircle(x + EVENT_DOTS_CIRCLE_SIZE * 16 / 5, y + EVENT_DOTS_CIRCLE_SIZE * 16 / 5, EVENT_DOTS_CIRCLE_SIZE, mCurrentCirclePaint);
                canvas.drawCircle(x, y + EVENT_DOTS_CIRCLE_SIZE * 16 / 5, EVENT_DOTS_CIRCLE_SIZE, mCurrentCirclePaint);
                break;
        }
    }

    //有开始结束功能
    protected void drawMonthNums_(Canvas canvas) {

        int y = (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2 - DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE;
        int paddingDay = (mWidth - 2 * mPadding) / (2 * mNumDays);
        int dayOffset = findDayOffset();
        int day = 1;


        while (day <= mNumCells) {

            int x = paddingDay * (1 + dayOffset * 2) + mPadding;

            if ((mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear) ||
                    (mMonth == mSelectedLastMonth && mSelectedLastDay == day && mSelectedLastYear == mYear)) {


                if (mSelectedLastDay == -1) {
                    drawRoundRect(false, (mSelectedBeginYear == mYear && mMonth == mSelectedBeginMonth && mSelectedBeginDay == day),
                            x, y, canvas,
                            (mSelectedLastYear == mYear && mMonth == mSelectedLastMonth && mSelectedLastDay == day));
                } else {
                    drawRoundRect(true, (mSelectedBeginYear == mYear && mMonth == mSelectedBeginMonth && mSelectedBeginDay == day),
                            x, y, canvas,
                            (mSelectedLastYear == mYear && mMonth == mSelectedLastMonth && mSelectedLastDay == day));
                }


            } else if (mHasToday && (mToday == day)) {
                canvas.drawCircle(x, y - MINI_DAY_NUMBER_TEXT_SIZE / 3, DAY_SELECTED_CIRCLE_SIZE, mCurrentCirclePaint);
            }

            if (mHasToday && (mToday == day)) {
                mMonthNumPaint.setColor(mCurrentDayTextColor);
                mMonthNumPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            } else {
                mMonthNumPaint.setColor(mDayNumColor);
                mMonthNumPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }

            if ((mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear) ||
                    (mMonth == mSelectedLastMonth && mSelectedLastDay == day && mSelectedLastYear == mYear)) {
                mMonthNumPaint.setColor(mMonthTitleBGColor);
            }

            //开始与结束 一样
            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 &&
                    mSelectedBeginYear == mSelectedLastYear &&
                    mSelectedBeginMonth == mSelectedLastMonth &&
                    mSelectedBeginDay == mSelectedLastDay &&
                    day == mSelectedBeginDay &&
                    mMonth == mSelectedBeginMonth &&
                    mYear == mSelectedBeginYear)) {

                mMonthNumPaint.setColor(mMonthTitleBGColor);
            }

            //开始与结束同年
            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 &&
                    mSelectedBeginYear == mSelectedLastYear &&
                    mSelectedBeginYear == mYear) &&
                    (((mMonth == mSelectedBeginMonth && mSelectedLastMonth == mSelectedBeginMonth) &&
                            ((mSelectedBeginDay < mSelectedLastDay && day > mSelectedBeginDay && day < mSelectedLastDay) ||
                                    (mSelectedBeginDay > mSelectedLastDay && day < mSelectedBeginDay && day > mSelectedLastDay))) ||
                            ((mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedBeginMonth && day > mSelectedBeginDay) ||
                                    (mSelectedBeginMonth < mSelectedLastMonth && mMonth == mSelectedLastMonth && day < mSelectedLastDay)) ||
                            ((mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedBeginMonth && day < mSelectedBeginDay) ||
                                    (mSelectedBeginMonth > mSelectedLastMonth && mMonth == mSelectedLastMonth && day > mSelectedLastDay)))) {

                drawRoundRect(false, false,
                        x, y, canvas,
                        false);

                mMonthNumPaint.setColor(mMonthTitleBGColor);
            }

            //开始与结束同年
            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear == mSelectedLastYear && mYear == mSelectedBeginYear) &&
                    ((mMonth > mSelectedBeginMonth && mMonth < mSelectedLastMonth && mSelectedBeginMonth < mSelectedLastMonth) ||
                            (mMonth < mSelectedBeginMonth && mMonth > mSelectedLastMonth && mSelectedBeginMonth > mSelectedLastMonth))) {

                drawRoundRect(false, false,
                        x, y, canvas,
                        false);
                mMonthNumPaint.setColor(mMonthTitleBGColor);

            }


            //开始年份小于结束年份
            if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear != mSelectedLastYear) &&
                    ((mSelectedBeginYear < mSelectedLastYear &&
                            (
                                    (mYear != mSelectedBeginYear && mYear < mSelectedLastYear) ||
                                            (mMonth == mSelectedBeginMonth && mYear == mSelectedBeginYear && day > mSelectedBeginDay) ||
                                            (mMonth == mSelectedLastMonth && mYear == mSelectedLastYear && day < mSelectedLastDay) ||
                                            (mMonth > mSelectedBeginMonth && mYear == mSelectedBeginYear) ||
                                            (mMonth < mSelectedLastMonth && mYear == mSelectedLastYear)

                            ) && (mYear >= mSelectedBeginYear)
                    ))) {

                drawRoundRect(false, false, x, y, canvas,
                        false);

                mMonthNumPaint.setColor(mMonthTitleBGColor);

            }

            if (afterDay(day, today)) {
                mMonthNumPaint.setColor(mAfterDayColor);
            }

            //绘制日期
            canvas.drawText(String.format("%d", day), x, y, mMonthNumPaint);

            dayOffset++;
            if (dayOffset == mNumDays) {
                dayOffset = 0;
                y += mRowHeight;
            }
            day++;
        }
    }

    private void drawRoundRect(Boolean isSelLast, Boolean isBeginDay, int x, int y, Canvas canvas, Boolean isLastDay) {

        int padd = (mPixelWidth / 7) / 2;

        int mRadius = DAY_SELECTED_CIRCLE_SIZE;

        if (isBeginDay) {

            if (isSelLast) {
                canvas.drawRect(x, y - mRadius - MINI_DAY_NUMBER_TEXT_SIZE / 3, x + padd, y + mRadius - MINI_DAY_NUMBER_TEXT_SIZE / 3, mSelectedCirclePaint);
            }

            canvas.drawCircle(x,
                    y - MINI_DAY_NUMBER_TEXT_SIZE / 3,
                    mRadius,
                    mSelectedCirclePaint);

        } else if (isLastDay) {
            canvas.drawRect(x - padd, y - mRadius - MINI_DAY_NUMBER_TEXT_SIZE / 3, x, y + mRadius - MINI_DAY_NUMBER_TEXT_SIZE / 3, mSelectedCirclePaint);
            canvas.drawCircle(x, y - MINI_DAY_NUMBER_TEXT_SIZE / 3, mRadius, mSelectedCirclePaint);
        } else {
            canvas.drawRect(x - padd, y - mRadius - MINI_DAY_NUMBER_TEXT_SIZE / 3, x + padd, y + mRadius - MINI_DAY_NUMBER_TEXT_SIZE / 3, mSelectedCirclePaint);
        }


    }

    private void drawMonthInfo(Canvas canvas) {
        int x = (mWidth + 2 * mPadding) / 2;
        int y = (int) (mRowHeight * 3.2);
        canvas.drawText(getMonthAndYearString(), x, y, mMonthInfoPaint);
    }

    public SimpleMonthAdapter.CalendarDay getDayFromLocation(float x, float y) {
        int padding = mPadding;
        if ((x < padding) || (x > mWidth - mPadding)) {
            return null;
        }

        int yDay = (int) (y - MONTH_HEADER_SIZE) / mRowHeight;
        int day = 1 + ((int) ((x - padding) * mNumDays / (mWidth - padding - mPadding)) - findDayOffset()) + yDay * mNumDays;

        if (mMonth > 11 || mMonth < 0 || CalendarUtils.getDaysInMonth(mMonth, mYear) < day || day < 1) {
            return null;
        }

        return new SimpleMonthAdapter.CalendarDay(mYear, mMonth, day);
    }

    protected void initView() {

        mMonthTitlePaint = new Paint();
        mMonthTitlePaint.setFakeBoldText(true);
        mMonthTitlePaint.setAntiAlias(true);
        mMonthTitlePaint.setTextSize(MONTH_LABEL_TEXT_SIZE);
        mMonthTitlePaint.setTypeface(Typeface.create(mMonthTitleTypeface, Typeface.NORMAL));
        mMonthTitlePaint.setColor(mMonthTextColor);
        mMonthTitlePaint.setTextAlign(Align.CENTER);
        mMonthTitlePaint.setStyle(Style.FILL);

        mMonthInfoPaint = new Paint();
        mMonthInfoPaint.setFakeBoldText(true);
        mMonthInfoPaint.setAntiAlias(true);
        mMonthInfoPaint.setTextSize(MONTH_INFO_TEXT_SIZE);
        mMonthInfoPaint.setTypeface(Typeface.create(mMonthTitleTypeface, Typeface.NORMAL));
        mMonthInfoPaint.setColor(mDayNumColor);
        mMonthInfoPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mMonthInfoPaint.setTextAlign(Align.CENTER);
        mMonthInfoPaint.setAlpha(0);
        mMonthInfoPaint.setStyle(Style.FILL);

        mSelectedCirclePaint = new Paint();
        mSelectedCirclePaint.setFakeBoldText(true);
        mSelectedCirclePaint.setAntiAlias(true);
        mSelectedCirclePaint.setColor(mSelectedDaysColor);
        mSelectedCirclePaint.setTextAlign(Align.CENTER);
        mSelectedCirclePaint.setStyle(Style.FILL);

        mCurrentCirclePaint = new Paint();
        mCurrentCirclePaint.setFakeBoldText(true);
        mCurrentCirclePaint.setAntiAlias(true);
        mCurrentCirclePaint.setColor(mCurrentDayColor);
        mCurrentCirclePaint.setTextAlign(Align.CENTER);
        mCurrentCirclePaint.setStyle(Style.FILL);

        mMonthDayLabelPaint = new Paint();
        mMonthDayLabelPaint.setAntiAlias(true);
        mMonthDayLabelPaint.setTextSize(MONTH_DAY_LABEL_TEXT_SIZE);
        mMonthDayLabelPaint.setColor(mMonthDayLabelTextColor);
        mMonthDayLabelPaint.setTypeface(Typeface.create(mDayOfWeekTypeface, Typeface.NORMAL));
        mMonthDayLabelPaint.setStyle(Style.FILL);
        mMonthDayLabelPaint.setTextAlign(Align.CENTER);
        mMonthDayLabelPaint.setFakeBoldText(true);

        mMonthNumPaint = new Paint();
        mMonthNumPaint.setAntiAlias(true);
        mMonthNumPaint.setTextSize(MINI_DAY_NUMBER_TEXT_SIZE);
        mMonthNumPaint.setStyle(Style.FILL);
        mMonthNumPaint.setTextAlign(Align.CENTER);
        mMonthNumPaint.setFakeBoldText(false);


        mStartEndPaint = new Paint();
        mStartEndPaint.setAntiAlias(true);
        mStartEndPaint.setTextSize(START_END_TEXT_SIZE);
        mStartEndPaint.setStyle(Style.FILL);
        mStartEndPaint.setTextAlign(Align.CENTER);
        mStartEndPaint.setFakeBoldText(false);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawMonthTitle(canvas);
        drawMonthNums_(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                mRowHeight * mNumRows + MONTH_HEADER_SIZE + PADDING_BOTTOM);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            SimpleMonthAdapter.CalendarDay calendarDay = getDayFromLocation(event.getX(), event.getY());
            if (calendarDay != null) {
                onDayClick(calendarDay);
            }
        }
        return true;
    }

    public void reuse() {
        mNumRows = DEFAULT_NUM_ROWS;
        eventSymbols.clear();
        requestLayout();
    }

    public void setMonthParams(HashMap<String, Integer> params) {

        if (!params.containsKey(VIEW_PARAMS_MONTH) && !params.containsKey(VIEW_PARAMS_YEAR)) {
            throw new InvalidParameterException("You must specify month and year for this view");
        }

        setTag(params);

        if (params.containsKey(VIEW_PARAMS_HEIGHT)) {
            mRowHeight = params.get(VIEW_PARAMS_HEIGHT);
            if (mRowHeight < MIN_HEIGHT) {
                mRowHeight = MIN_HEIGHT;
            }
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_DAY)) {
            mSelectedBeginDay = params.get(VIEW_PARAMS_SELECTED_BEGIN_DAY);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_MONTH)) {
            mSelectedBeginMonth = params.get(VIEW_PARAMS_SELECTED_BEGIN_MONTH);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_YEAR)) {
            mSelectedBeginYear = params.get(VIEW_PARAMS_SELECTED_BEGIN_YEAR);
        }

        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_DAY)) {
            mSelectedLastDay = params.get(VIEW_PARAMS_SELECTED_LAST_DAY);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_MONTH)) {
            mSelectedLastMonth = params.get(VIEW_PARAMS_SELECTED_LAST_MONTH);
        }
        if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_YEAR)) {
            mSelectedLastYear = params.get(VIEW_PARAMS_SELECTED_LAST_YEAR);
        }

        mMonth = params.get(VIEW_PARAMS_MONTH);
        mYear = params.get(VIEW_PARAMS_YEAR);

        mHasToday = false;
        mToday = -1;

        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mDayOfWeekStart = mCalendar.get(Calendar.DAY_OF_WEEK);

        if (params.containsKey(VIEW_PARAMS_WEEK_START)) {
            mWeekStart = params.get(VIEW_PARAMS_WEEK_START);
        } else {
            mWeekStart = mCalendar.getFirstDayOfWeek();
        }

        mNumCells = CalendarUtils.getDaysInMonth(mMonth, mYear);
        for (int i = 0; i < mNumCells; i++) {
            final int day = i + 1;
            if (sameDay(day, today)) {
                mHasToday = true;
                mToday = day;
            }

            mIsPrev = prevDay(day, today);
        }

        mNumRows = calculateNumRows();
    }

    public void showMothInfo(boolean show) {
        if (shouldShowMonthInfo != show) {
            shouldShowMonthInfo = show;
            alphaStartTime = System.currentTimeMillis();
        }
    }

    private void calculateAlpha() {
        long elapsedTime = System.currentTimeMillis() - alphaStartTime;
        int alphaChange = (int) ((NORMAL_ALPHA - 0) * elapsedTime / ALPHA_DURATION);

        currentDraggingAlpha = NORMAL_ALPHA - alphaChange;
        if (currentDraggingAlpha < 0 || alphaStartTime == -1) {
            currentDraggingAlpha = 0;
        }
        currentNormalAlpha = alphaChange;
        if (currentNormalAlpha > NORMAL_ALPHA) {
            currentNormalAlpha = NORMAL_ALPHA;
        }
        if (shouldShowMonthInfo) {
            mMonthInfoPaint.setAlpha(currentNormalAlpha);
            mMonthTitlePaint.setAlpha(DRAGING_ALPHA);
            mCurrentCirclePaint.setAlpha(DRAGING_ALPHA);
            mMonthDayLabelPaint.setAlpha(DRAGING_ALPHA);
        } else {
            mMonthInfoPaint.setAlpha(currentDraggingAlpha);
            mMonthTitlePaint.setAlpha(NORMAL_ALPHA);
            mCurrentCirclePaint.setAlpha(NORMAL_ALPHA);
            mMonthDayLabelPaint.setAlpha(NORMAL_ALPHA);
        }

        if (elapsedTime < ALPHA_DURATION) {
            this.postInvalidateDelayed(1000 / FRAMES_PER_SECOND);
        }
    }

    public void setEventSymbols(HashMap<SimpleMonthAdapter.CalendarDay, Integer> symbols) {
        eventSymbols.clear();
        for (HashMap.Entry<SimpleMonthAdapter.CalendarDay, Integer> entry : symbols.entrySet()) {
            eventSymbols.put(entry.getKey().getDay(), entry.getValue());
        }
    }

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        mOnDayClickListener = onDayClickListener;
    }

    public static abstract interface OnDayClickListener {
        public abstract void onDayClick(SimpleMonthView simpleMonthView, SimpleMonthAdapter.CalendarDay calendarDay);
    }


    /**
     * 在使用drawText方法时文字不能根据y坐标居中，所以重新计算y坐标
     *
     * @param paint
     * @param y
     * @return
     */
    private float getTextYCenter(Paint paint, int y) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;
        float offY = fontTotalHeight / 2 - fontMetrics.bottom;
        return y + offY;
    }
}