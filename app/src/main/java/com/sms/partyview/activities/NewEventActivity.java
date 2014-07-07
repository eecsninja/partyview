package com.sms.partyview.activities;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sms.partyview.R;
import com.sms.partyview.models.Event;
import com.sms.partyview.models.Invites;

import org.joda.time.DateTime;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.GregorianCalendar;

import static java.lang.String.format;

public class NewEventActivity extends FragmentActivity
        implements CalendarDatePickerDialog.OnDateSetListener,
        RadialTimePickerDialog.OnTimeSetListener {

    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";

    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";

    private int mEventYear;

    private int mEventMonth;

    private int mEventDay;

    private int mEventHour;

    private int mEventMinute;

    private TextView mTvDate;

    private TextView mTvTime;

    private EditText mEtTitle;

    private EditText mEtAddress;

    private EditText mEtDescription;

    private EditText mEtInvites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        setUpViews();
    }

    private void setUpViews() {
        mTvDate = (TextView) findViewById(R.id.tvDate);
        mTvTime = (TextView) findViewById(R.id.tvTime);
        mEtTitle = (EditText) findViewById(R.id.etEventName);
        mEtAddress = (EditText) findViewById(R.id.etEventLocation);
        mEtDescription = (EditText) findViewById(R.id.etEventDescription);
        mEtInvites = (EditText) findViewById(R.id.etEventInvites);
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
        // The month was set (0-11) for compatibility with {@link java.util.Calendar}.
        mTvDate.setText(format("%d/%d/%d", monthOfYear + 1, dayOfMonth, year));

        mEventYear = year;
        mEventMonth = monthOfYear + 1;
        mEventDay = dayOfMonth;
        Log.d("DEBUG", format("y:%d; m:%d, d:%d", mEventYear, mEventMonth, mEventDay));
    }

    public void showTimePicker(View view) {
        FragmentManager fm = getSupportFragmentManager();
        DateTime now = DateTime.now();
        RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
                .newInstance(this, now.getHourOfDay(), now.getMinuteOfHour(),
                        DateFormat.is24HourFormat(this));
        timePickerDialog.show(fm, FRAG_TAG_TIME_PICKER);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
        mTvTime.setText(format("%d:%02d", hourOfDay, minute));

        mEventHour = hourOfDay;
        mEventMinute = minute;
    }

    public void createEvent(View view) {
        // TODO:
        // replace MyEvent with Event
        final Event event = new Event();

        // TODO:
        // handle user error, missing inputs etc.

        event.setTitle(mEtTitle.getText().toString());
        event.setAddress(mEtAddress.getText().toString());
        event.setDescription(mEtDescription.getText().toString());

        GregorianCalendar calendar = new GregorianCalendar(mEventYear, mEventMonth, mEventDay,
                mEventHour, mEventMinute);
        event.setDate(calendar.getTime());

        event.setHost(ParseUser.getCurrentUser());

        // TODO:
        // add logic for inviting people
        // determine best way to store invites
        Invites invites = new Invites();
        invites.setInvites(mEtInvites.getText().toString());
        event.setInvites(invites);

        //TODO: discuss with team on best way to save:
        //      saveInBackground
        //      saveEventually: offline protection

        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("DEBUG", "saved event");
                Log.d("DEBUG", event.getObjectId().toString());
            }
        });

        finish();
    }
}
