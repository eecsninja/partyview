package com.sms.partyview.fragments;

import com.google.common.base.Splitter;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.R;
import com.sms.partyview.helpers.EventSaverInterface;
import com.sms.partyview.models.Event;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditEventFragment extends Fragment
        implements CalendarDatePickerDialog.OnDateSetListener,
        RadialTimePickerDialog.OnTimeSetListener,
        TextView.OnClickListener {

    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormat
            .forPattern("E, MMM d");
    private static final DateTimeFormatter DISPLAY_TIME_FORMATTER = DateTimeFormat
            .forPattern("h:mm a");

    // TODO(My): find a more efficient way to retrieve and store this data
    private List<String> mUserNames = new ArrayList<String>();
    private Map<String, ParseUser> mUserNameToUser = new HashMap<String, ParseUser>();

    private ArrayAdapter<String> mAdapterInvitesAutoComplete;
    private TextView mTvStartDate;
    private TextView mTvStartTime;
    private TextView mTvEndDate;
    private TextView mTvEndTime;
    private EditText mEtTitle;
    private EditText mEtAddress;
    private EditText mEtDescription;
    private Button mBtnCreate;
    private MultiAutoCompleteTextView mAutoTvInvites;
    private LocalDateTime mNow;
    private LocalDateTime mNowPlusOne;
    private CalendarDatePickerDialog mStartDatePicker;
    private CalendarDatePickerDialog mEndDatePicker;
    private RadialTimePickerDialog mStartTimePicker;
    private RadialTimePickerDialog mEndTimePicker;
    private MutableDateTime mStartDateTime;
    private MutableDateTime mEndDateTime;

    private EventSaverInterface mEventSaver;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof EventSaverInterface) {
            mEventSaver = (EventSaverInterface) activity;
        } else {
            throw new ClassCastException(activity.toString() +
                    " must implement EventSaverInterface.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getUserNames();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);
        setUpViews(view);
        return view;
    }

    private void setUpViews(View view) {
        findViews(view);
        populateViews();
        setUpClickListeners();
        setUpPickerListeners();
    }

    private void setUpClickListeners() {
        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent();
            }
        });
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
                        DateFormat.is24HourFormat(getActivity()));

        mStartDateTime = new MutableDateTime(mNow.getYear(), mNow.getMonthOfYear(),
                mNow.getDayOfMonth(), mNow.getHourOfDay(),
                mNow.getMinuteOfHour(), 0, 0);

        // end date time setup
        mEndDatePicker = CalendarDatePickerDialog
                .newInstance(this, mNowPlusOne.getYear(), mNowPlusOne.getMonthOfYear() - 1,
                        mNowPlusOne.getDayOfMonth());

        mEndTimePicker = RadialTimePickerDialog
                .newInstance(this, mNowPlusOne.getHourOfDay(), mNowPlusOne.getMinuteOfHour(),
                        DateFormat.is24HourFormat(getActivity()));

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

    private void findViews(View view) {
        mTvStartDate = (TextView) view.findViewById(R.id.tvStartDate);
        mTvStartTime = (TextView) view.findViewById(R.id.tvStartTime);
        mTvEndDate = (TextView) view.findViewById(R.id.tvEndDate);
        mTvEndTime = (TextView) view.findViewById(R.id.tvEndTime);
        mEtTitle = (EditText) view.findViewById(R.id.etEventName);
        mEtAddress = (EditText) view.findViewById(R.id.etEventLocation);
        mEtDescription = (EditText) view.findViewById(R.id.etEventDescription);
        mBtnCreate = (Button) view.findViewById(R.id.btnCreateEvent);
        mAutoTvInvites = (MultiAutoCompleteTextView) view.findViewById(R.id.autoTvInvites);

        // set up autocomplete for invites
        Log.d("DEBUG", "getActivity: " + getActivity());
        mAdapterInvitesAutoComplete = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, mUserNames);
        Log.d("DEBUG", "mAdapterInvitesAutoComplete: " + mAdapterInvitesAutoComplete);
        mAutoTvInvites.setAdapter(mAdapterInvitesAutoComplete);
        mAutoTvInvites.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }

    public void createEvent() {
        final Event event = new Event();

        // TODO:
        // handle user error, missing inputs etc.

        event.setTitle(mEtTitle.getText().toString());
        event.setDescription(mEtDescription.getText().toString());
        event.setStartDate(mStartDateTime.toDate());
        event.setEndDate(mEndDateTime.toDate());
        event.setHost(ParseUser.getCurrentUser());
        event.setAddress(mEtAddress.getText().toString());

        final String invitesString = mAutoTvInvites.getText().toString();
        mEventSaver.saveNewEvent(event, invitesString);
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
        FragmentManager fm = getActivity().getSupportFragmentManager();

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

    private void getUserNames() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.fromLocalDatastore();

        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(final List<ParseUser> users, ParseException e) {
                for (ParseUser user : users) {
                    mUserNames.add(user.getUsername());
                    mUserNameToUser.put(user.getUsername(), user);
                }
                mAdapterInvitesAutoComplete.notifyDataSetChanged();

                Log.d(getClass().getSimpleName() + "_DEBUG", "got all usernames");
                Log.d(getClass().getSimpleName() + "_DEBUG", mUserNames.toString());
            }
        });
    }

    public List<ParseUser> getAttendeeList(String invitesString) {
        Iterable<String> tokens = Splitter.on(',').omitEmptyStrings().trimResults()
                .split(invitesString);

        List<ParseUser> attendeeList = new ArrayList<ParseUser>();
        for (String userName : tokens) {
            attendeeList.add(mUserNameToUser.get(userName));
        }

        return attendeeList;
    }

    // Loads an event for editing. Fills the fields with the event parameters.
    public void loadEvent(Event event) {
        mEtTitle.setText(event.getTitle());
        mEtAddress.setText(event.getAddress());
        mEtDescription.setText(event.getDescription());

        DateTime startDate = new DateTime(event.getStartDate());
        mTvStartDate.setText(DISPLAY_DATE_FORMATTER.print(startDate));
        mTvStartTime.setText(DISPLAY_TIME_FORMATTER.print(startDate));

        DateTime endDate = new DateTime(event.getEndDate());
        mTvEndDate.setText(DISPLAY_DATE_FORMATTER.print(endDate));
        mTvEndTime.setText(DISPLAY_TIME_FORMATTER.print(endDate));
    }
}
