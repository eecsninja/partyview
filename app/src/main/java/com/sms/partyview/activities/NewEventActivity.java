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
import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class NewEventActivity extends FragmentActivity
        implements CalendarDatePickerDialog.OnDateSetListener,
        RadialTimePickerDialog.OnTimeSetListener,
        TextView.OnClickListener {

    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormat
            .forPattern("E, MMM d");
    private static final DateTimeFormatter DISPLAY_TIME_FORMATTER = DateTimeFormat
            .forPattern("h:mm a");

    private TextView mTvStartDate;
    private TextView mTvStartTime;
    private TextView mTvEndDate;
    private TextView mTvEndTime;
    private EditText mEtTitle;
    private EditText mEtAddress;
    private EditText mEtDescription;
    private EditText mEtInvites;

    private LocalDateTime mNow;
    private LocalDateTime mNowPlusOne;
    private CalendarDatePickerDialog mStartDatePicker;
    private CalendarDatePickerDialog mEndDatePicker;
    private RadialTimePickerDialog mStartTimePicker;
    private RadialTimePickerDialog mEndTimePicker;

    private MutableDateTime mStartDateTime;
    private MutableDateTime mEndDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        setUpViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpViews() {
        findViews();
        populateViews();
        setUpClickListeners();
        setUpPickerListeners();
    }

    private void setUpClickListeners() {
        mTvStartDate.setOnClickListener(this);
        mTvStartTime.setOnClickListener(this);
        mTvEndDate.setOnClickListener(this);
        mTvEndTime.setOnClickListener(this);
    }

    private void setUpPickerListeners() {
        initializePickers();

        // DatePicker uses (0-11) for month, so have to add one back
        mStartDatePicker.setOnDateSetListener(new CalendarDatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year,
                    int monthOfYear,
                    int dayOfMonth) {

                mStartDateTime.set(DateTimeFieldType.year(), year);
                mStartDateTime.set(DateTimeFieldType.monthOfYear(), monthOfYear + 1);
                mStartDateTime.set(DateTimeFieldType.dayOfMonth(), dayOfMonth);

                mTvStartDate.setText(mStartDateTime.toString(DISPLAY_DATE_FORMATTER));
            }
        });

        mStartTimePicker.setOnTimeSetListener(new RadialTimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay,
                    int minute) {
                mStartDateTime.set(DateTimeFieldType.hourOfDay(), hourOfDay);
                mStartDateTime.set(DateTimeFieldType.minuteOfHour(), minute);

                mTvStartTime.setText(mStartDateTime.toString(DISPLAY_TIME_FORMATTER));
            }
        });

        //TODO: add error handling for when users chose an end datetime earlier than start
        mEndDatePicker.setOnDateSetListener(new CalendarDatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year,
                    int monthOfYear,
                    int dayOfMonth) {

                mEndDateTime.set(DateTimeFieldType.year(), year);
                mEndDateTime.set(DateTimeFieldType.monthOfYear(), monthOfYear + 1);
                mEndDateTime.set(DateTimeFieldType.dayOfMonth(), dayOfMonth);

                mTvEndDate.setText(mEndDateTime.toString(DISPLAY_DATE_FORMATTER));
            }
        });

        mEndTimePicker.setOnTimeSetListener(new RadialTimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay,
                    int minute) {
                mEndDateTime.set(DateTimeFieldType.hourOfDay(), hourOfDay);
                mEndDateTime.set(DateTimeFieldType.minuteOfHour(), minute);

                mTvEndTime.setText(mEndDateTime.toString(DISPLAY_TIME_FORMATTER));
            }
        });
    }

    private void initializePickers() {
        // start date time setup
        Log.d("DEBUG", "mnow: " + mNow.toString());

        // The month was set (0-11) for compatibility with {@link java.util.Calendar}.
        mStartDatePicker = CalendarDatePickerDialog
                .newInstance(this, mNow.getYear(), mNow.getMonthOfYear() - 1,
                        mNow.getDayOfMonth());

        mStartTimePicker = RadialTimePickerDialog
                .newInstance(this, mNow.getHourOfDay(), mNow.getMinuteOfHour(),
                        DateFormat.is24HourFormat(this));

        mStartDateTime = new MutableDateTime(mNow.getYear(), mNow.getMonthOfYear(),
                mNow.getDayOfMonth(), mNow.getHourOfDay(),
                mNow.getMinuteOfHour(), 0, 0);

        // end date time setup
        mEndDatePicker = CalendarDatePickerDialog
                .newInstance(this, mNowPlusOne.getYear(), mNowPlusOne.getMonthOfYear() - 1,
                        mNowPlusOne.getDayOfMonth());

        mEndTimePicker = RadialTimePickerDialog
                .newInstance(this, mNowPlusOne.getHourOfDay(), mNowPlusOne.getMinuteOfHour(),
                        DateFormat.is24HourFormat(this));

        mEndDateTime = new MutableDateTime(mNowPlusOne.getYear(), mNowPlusOne.getMonthOfYear(),
                mNowPlusOne.getDayOfMonth(), mNowPlusOne.getHourOfDay(),
                mNowPlusOne.getMinuteOfHour(), 0, 0);
    }

    private void populateViews() {
        // get current time
        mNow = DateTime.now().toLocalDateTime().hourOfDay().roundCeilingCopy();
        mNowPlusOne = mNow.plusHours(1);

        mTvStartDate.setText(mNow.toString(DISPLAY_DATE_FORMATTER));
        mTvStartTime.setText(mNow.toString(DISPLAY_TIME_FORMATTER));

        mTvEndDate.setText(mNowPlusOne.toString(DISPLAY_DATE_FORMATTER));
        mTvEndTime.setText(mNowPlusOne.toString(DISPLAY_TIME_FORMATTER));
    }

    private void findViews() {
        mTvStartDate = (TextView) findViewById(R.id.tvStartDate);
        mTvStartTime = (TextView) findViewById(R.id.tvStartTime);
        mTvEndDate = (TextView) findViewById(R.id.tvEndDate);
        mTvEndTime = (TextView) findViewById(R.id.tvEndTime);
        mEtTitle = (EditText) findViewById(R.id.etEventName);
        mEtAddress = (EditText) findViewById(R.id.etEventLocation);
        mEtDescription = (EditText) findViewById(R.id.etEventDescription);
        mEtInvites = (EditText) findViewById(R.id.etEventInvites);
    }

    public void createEvent(View view) {
        final Event event = new Event();

        // TODO:
        // handle user error, missing inputs etc.

        event.setTitle(mEtTitle.getText().toString());
        event.setAddress(mEtAddress.getText().toString());
        event.setDescription(mEtDescription.getText().toString());
        event.setDate(mStartDateTime.toDate());
        //TODO: save end datetime
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

                Intent data = new Intent();
                data.putExtra("eventId", event.getObjectId());
                setResult(RESULT_OK, data);

                Log.d("DEBUG", "saved event");
                Log.d("DEBUG", event.getObjectId().toString());

                finish();
            }
        });
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int i, int i2,
            int i3) {

    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i2) {

    }

    @Override
    public void onClick(View v) {
        showPicker(v);
    }

    private void showPicker(View view) {
        FragmentManager fm = getSupportFragmentManager();

        switch (view.getId()) {
            case R.id.tvStartDate:
                mStartDatePicker.show(fm, FRAG_TAG_DATE_PICKER);
                break;
            case R.id.tvStartTime:
                mStartTimePicker.show(fm, FRAG_TAG_TIME_PICKER);
                break;
            case R.id.tvEndDate:
                mEndDatePicker.show(fm, FRAG_TAG_DATE_PICKER);
                break;
            case R.id.tvEndTime:
                mEndTimePicker.show(fm, FRAG_TAG_TIME_PICKER);
                break;
            default:
                Log.d("DEBUG", "SOMETHING IS WRONG");
        }
    }
}
