package com.sms.partyview.fragments;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sms.partyview.R;
import com.sms.partyview.helpers.EventSaverInterface;
import com.sms.partyview.helpers.Utils;
import com.sms.partyview.models.Event;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.Period;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.TextUtils.isEmpty;
import static com.sms.partyview.helpers.Utils.*;

public abstract class EditEventFragment extends Fragment
        implements CalendarDatePickerDialog.OnDateSetListener,
        RadialTimePickerDialog.OnTimeSetListener,
        TextView.OnClickListener {

    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";

    private static final Splitter INVITEES_SPLITTER = Splitter.on(',').omitEmptyStrings()
            .trimResults();
    private static final Joiner JOINER = Joiner.on(',');
    public static final String TAG = EditEventFragment.class.getSimpleName() + "_DEBUG";

    // TODO(My): find a more efficient way to retrieve and store this data
    private List<String> mUserNames = new ArrayList<String>();
    private Map<String, ParseUser> mUserNameToUser = new HashMap<String, ParseUser>();

    private ArrayAdapter<String> mAdapterInvitesAutoComplete;
    protected TextView mTvStartDate;
    protected TextView mTvStartTime;
    protected TextView mTvEndDate;
    protected TextView mTvEndTime;
    protected EditText mEtTitle;
    protected EditText mEtAddress;
    protected EditText mEtDescription;
    protected Button mBtnSubmit;
    protected MultiAutoCompleteTextView mAutoTvInvites;
    private MutableDateTime mStartDateTime;
    private MutableDateTime mEndDateTime;

    private EventSaverInterface mEventSaver;

    private String picker;
    private String TAG_START_PICKER = "start";
    private String TAG_END_PICKER = "end";

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

        populateUserNames();
        cacheAppUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);
        setUpViews(view);
        return view;
    }

    private void setUpViews(View view) {
        initializedDateTime();
        findViews(view);
        populateViews();
        setUpClickListeners();
    }

    private void initializedDateTime() {
        // get current time
        LocalDateTime start = DateTime.now().toLocalDateTime().hourOfDay().roundCeilingCopy();
        LocalDateTime end = start.plusHours(1);

        mStartDateTime = new MutableDateTime(start.getYear(), start.getMonthOfYear(),
                start.getDayOfMonth(), start.getHourOfDay(),
                start.getMinuteOfHour(), 0, 0);

        mEndDateTime = new MutableDateTime(end.getYear(), end.getMonthOfYear(),
                end.getDayOfMonth(), end.getHourOfDay(),
                end.getMinuteOfHour(), 0, 0);
    }

    private void setUpClickListeners() {
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
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

    private void populateViews() {
        mTvStartDate.setText(mStartDateTime.toString(DISPLAY_DATE_FORMATTER));
        mTvStartTime.setText(mStartDateTime.toString(DISPLAY_TIME_FORMATTER));

        mTvEndDate.setText(mEndDateTime.toString(DISPLAY_DATE_FORMATTER));
        mTvEndTime.setText(mEndDateTime.toString(DISPLAY_TIME_FORMATTER));
    }

    private void findViews(View view) {
        mTvStartDate = (TextView) view.findViewById(R.id.tvStartDate);
        mTvStartTime = (TextView) view.findViewById(R.id.tvStartTime);
        mTvEndDate = (TextView) view.findViewById(R.id.tvEndDate);
        mTvEndTime = (TextView) view.findViewById(R.id.tvEndTime);
        mEtTitle = (EditText) view.findViewById(R.id.etEventName);
        mEtAddress = (EditText) view.findViewById(R.id.etEventLocation);
        mEtDescription = (EditText) view.findViewById(R.id.etEventDescription);
        mBtnSubmit = (Button) view.findViewById(R.id.btnSubmitEvent);
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

        boolean validationError = false;
        StringBuilder validationErrorMessage =
                new StringBuilder(getResources().getString(R.string.error_intro));

        String title = mEtTitle.getText().toString();
        String address = mEtAddress.getText().toString();
        String invitees = mAutoTvInvites.getText().toString();

        if (isEmpty(title)) {
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_blank_title));
        }

        if (isEmpty(address)) {
            if (validationError) {
                validationErrorMessage.append(getResources().getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_blank_location));
        }

        if (isEmpty(invitees)) {
            if (validationError) {
                validationErrorMessage.append(getResources().getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_blank_invitees));
        }

        Iterable<String> tokens = INVITEES_SPLITTER.split(invitees);
        StringBuilder invalidInviteesErrorMessage = new StringBuilder();
        boolean inviteesValidationError = false;
        List<String> incorrectUserNames = new ArrayList<String>();
        for (String userName : tokens) {
            if (mUserNameToUser.get(userName) == null) {
                inviteesValidationError = true;
                incorrectUserNames.add(userName);
            }
        }

        if (inviteesValidationError) {

            invalidInviteesErrorMessage.append(JOINER.join(incorrectUserNames));

            if (incorrectUserNames.size() > 1) {
                invalidInviteesErrorMessage
                        .append(getString(R.string.error_invalid_invitees_plural_end));
            } else {
                invalidInviteesErrorMessage
                        .append(getString(R.string.error_invalid_invitees_singular_end));
            }

            if (validationError) {
                validationErrorMessage.append(getResources().getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage
                    .append(getString(R.string.error_invalid_invitees_begin));

            validationErrorMessage
                    .append(invalidInviteesErrorMessage);
        }

        validationErrorMessage.append(getResources().getString(R.string.error_end));

        if (mEndDateTime.isBefore(mStartDateTime)) {
            validationError = true;
            validationErrorMessage.append(getResources().getString(R.string.error_invalid_date));
        }

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(getActivity(), validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        final Event event = getEventFromInputData();
        mEventSaver.saveNewEvent(event, mAutoTvInvites.getText().toString());
    }

    // Returns an event object containing field data.
    public abstract Event getEventFromInputData();

    // Reads input fields and populates an event object with the fields' contents.
    protected void readEventInfoFromInputFields(Event event) {
        event.setTitle(mEtTitle.getText().toString());
        event.setDescription(mEtDescription.getText().toString());
        event.setStartDate(mStartDateTime.toDate());
        event.setEndDate(mEndDateTime.toDate());
        event.setHost(ParseUser.getCurrentUser());
        event.setAddress(mEtAddress.getText().toString());
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog,
            int year,
            int monthOfYear,
            int dayOfMonth) {
        if (picker.equals(TAG_START_PICKER)) {
            MutableDateTime oldStartDateTime = new MutableDateTime(mStartDateTime);

            mStartDateTime.set(DateTimeFieldType.year(), year);
            mStartDateTime.set(DateTimeFieldType.monthOfYear(), monthOfYear + 1);
            mStartDateTime.set(DateTimeFieldType.dayOfMonth(), dayOfMonth);
            mTvStartDate.setText(mStartDateTime.toString(DISPLAY_DATE_FORMATTER));

            updateEndDate(oldStartDateTime);
        } else if (picker.equals(TAG_END_PICKER)) {
            mEndDateTime.set(DateTimeFieldType.year(), year);
            mEndDateTime.set(DateTimeFieldType.monthOfYear(), monthOfYear + 1);
            mEndDateTime.set(DateTimeFieldType.dayOfMonth(), dayOfMonth);

            mTvEndDate.setText(mEndDateTime.toString(DISPLAY_DATE_FORMATTER));
        } else {
            Log.d(TAG, "SOMETHING IS WRONG");
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout,
            int hourOfDay,
            int minute) {
        if (picker.equals(TAG_START_PICKER)) {
            MutableDateTime oldStartDateTime = new MutableDateTime(mStartDateTime);

            mStartDateTime.set(DateTimeFieldType.hourOfDay(), hourOfDay);
            mStartDateTime.set(DateTimeFieldType.minuteOfHour(), minute);
            mTvStartTime.setText(mStartDateTime.toString(DISPLAY_TIME_FORMATTER));

            updateEndTime(oldStartDateTime);
        } else if (picker.equals(TAG_END_PICKER)) {
            mEndDateTime.set(DateTimeFieldType.hourOfDay(), hourOfDay);
            mEndDateTime.set(DateTimeFieldType.minuteOfHour(), minute);

            mTvEndTime.setText(mEndDateTime.toString(DISPLAY_TIME_FORMATTER));
        } else {
            Log.d(TAG, "SOMETHING IS WRONG");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvStartDate:
                picker = TAG_START_PICKER;
                showDatePicker(mStartDateTime);
                break;
            case R.id.tvEndDate:
                picker = TAG_END_PICKER;
                showDatePicker(mEndDateTime);
                break;
            case R.id.tvStartTime:
                picker = TAG_START_PICKER;
                showTimePicker(mStartDateTime);
                break;
            case R.id.tvEndTime:
                picker = TAG_END_PICKER;
                showTimePicker(mEndDateTime);
                break;
            default:
                Log.d(TAG, "SOMETHING IS WRONG");
        }
    }

    private void showDatePicker(MutableDateTime dateTime)
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();

        // The month was set (0-11) for compatibility with {@link java.util.Calendar}.
        CalendarDatePickerDialog datePicker = CalendarDatePickerDialog
                .newInstance(this, dateTime.getYear(), dateTime.getMonthOfYear() - 1,
                        dateTime.getDayOfMonth());
        datePicker.show(fm, FRAG_TAG_DATE_PICKER);
    }

    private void showTimePicker(MutableDateTime dateTime)
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        RadialTimePickerDialog timePicker = RadialTimePickerDialog
                .newInstance(this, dateTime.getHourOfDay(), dateTime.getMinuteOfHour(),
                        DateFormat.is24HourFormat(getActivity()));

        timePicker.show(fm, FRAG_TAG_TIME_PICKER);
    }

    private void populateUserNames() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.fromLocalDatastore();

        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(final List<ParseUser> users, ParseException e) {
                ParseUser currentUser = ParseUser.getCurrentUser();

                for (ParseUser user : users) {

                    // do not include host as suggestion for invitees
                    if (!user.equals(currentUser)) {
                        mUserNames.add(user.getUsername());
                        mUserNameToUser.put(user.getUsername(), user);
                    }
                }
                mAdapterInvitesAutoComplete.notifyDataSetChanged();

                Log.d(getClass().getSimpleName() + "_DEBUG", "got all usernames");
                Log.d(getClass().getSimpleName() + "_DEBUG", mUserNames.toString());
            }
        });
    }

    public List<ParseUser> getAttendeeList(String inviteesString) {
        Iterable<String> tokens = INVITEES_SPLITTER.split(inviteesString);

        List<ParseUser> attendeeList = new ArrayList<ParseUser>();
        for (String userName : tokens) {
            attendeeList.add(mUserNameToUser.get(userName));
        }

        return attendeeList;
    }

    private void cacheAppUsers() {

        FindCallback<ParseUser> callback = new FindCallback<ParseUser>() {
            public void done(final List<ParseUser> users, ParseException e) {

                Log.d(TAG, "got user info");
                Log.d(TAG, users.toString());

                // Remove the previously cached results.
                ParseObject.unpinAllInBackground("users", new DeleteCallback() {
                    public void done(ParseException e) {
                        // Cache the new results.
                        ParseObject.pinAllInBackground("users", users);
                        Log.d(TAG, "pin all user info");

                        populateUserNames();
                    }
                });
            }
        };

        Utils.cacheAppUsers(callback);
    }

    private void updateEndDate(MutableDateTime oldStartDateTime) {
        updateEndDateTime(oldStartDateTime);
        mTvEndDate.setText(mEndDateTime.toString(DISPLAY_DATE_FORMATTER));
    }

    private void updateEndTime(MutableDateTime oldStartDateTime) {
        updateEndDateTime(oldStartDateTime);
        mTvEndTime.setText(mEndDateTime.toString(DISPLAY_TIME_FORMATTER));
    }

    private void updateEndDateTime(MutableDateTime oldStartDateTime) {
        Period period = new Period(oldStartDateTime, mStartDateTime);
        mEndDateTime.add(period);
    }
}
