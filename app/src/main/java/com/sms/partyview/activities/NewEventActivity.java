package com.sms.partyview.activities;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.sms.partyview.R;

import org.joda.time.DateTime;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

public class NewEventActivity extends FragmentActivity
        implements CalendarDatePickerDialog.OnDateSetListener,
        RadialTimePickerDialog.OnTimeSetListener {

    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";

    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";

    private TextView mTvDate;
    private TextView mTvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        setUpViews();
    }

    private void setUpViews() {
        mTvDate = (TextView) findViewById(R.id.tvDate);
        mTvTime = (TextView) findViewById(R.id.tvTime);
    }

    public void showDatePicker(View view) {
        FragmentManager fm = getSupportFragmentManager();
        DateTime now = DateTime.now();
        CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                .newInstance(this, now.getYear(), now.getMonthOfYear() - 1,
                        now.getDayOfMonth());
        calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear,
            int dayOfMonth) {
        mTvDate.setText(monthOfYear + "/" + dayOfMonth + "/" + year);
    }

    public void showTimePicker(View view) {
        FragmentManager fm = getSupportFragmentManager();
        DateTime now = DateTime.now();
        RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                .newInstance(this, now.getHourOfDay(), now.getMinuteOfHour(),
                        DateFormat.is24HourFormat(this));
        timePickerDialog.show(fm, FRAG_TAG_TIME_PICKER);
    }
//
//    @Override
//    public void onTimeSet(RadialTimePickerDialog dialog, int hourOfDay, int minute) {
//        mTvTime.setText("" + hourOfDay + ":" + minute);
//    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
        mTvTime.setText("" + hourOfDay + ":" + minute);
    }
}
