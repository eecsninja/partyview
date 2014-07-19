package com.sms.partyview.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ui.ParseLoginConfig;
import com.sms.partyview.R;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int DEFAULT_MIN_PASSWORD_LENGTH = 6;
    private static final String TAG = ProfileFragment.class.getSimpleName() + "_DEBUG";

    private EditText mEtNewPassword;
    private EditText mEtVerifyPassword;
    private Button mBtnChangePassword;

    private ParseLoginConfig config;
    private int minPasswordLength;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        minPasswordLength = DEFAULT_MIN_PASSWORD_LENGTH;

        setUpViews(view);

        return view;
    }

    private void setUpViews(View view) {
        mEtNewPassword = (EditText) view.findViewById(R.id.etNewPassword);
        mEtVerifyPassword = (EditText) view.findViewById(R.id.etVerifyPassword);
        mBtnChangePassword = (Button) view.findViewById(R.id.btnChangePassword);
        mBtnChangePassword.setOnClickListener(this);

        mEtNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mEtNewPassword.getText().toString().length() > 0) {
                    mBtnChangePassword.setEnabled(true);
                } else {
                    mBtnChangePassword.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChangePassword:
                changePassword();
            default:
                Log.d(TAG, "SOMETHING IS WRONG");
        }
    }

    private void changePassword() {
        String password = mEtNewPassword.getText().toString();
        String passwordAgain = mEtVerifyPassword.getText().toString();

        if (password.length() < minPasswordLength) {
            showToast(getResources().getQuantityString(
                    R.plurals.com_parse_ui_password_too_short_toast,
                    minPasswordLength, minPasswordLength));
        } else if (passwordAgain.length() == 0) {
            showToast(R.string.com_parse_ui_reenter_password_toast);
        } else if (!password.equals(passwordAgain)) {
            showToast(R.string.com_parse_ui_mismatch_confirm_password_toast);
            mEtVerifyPassword.selectAll();
            mEtVerifyPassword.requestFocus();
        } else {
            ParseUser user = ParseUser.getCurrentUser();
            user.setPassword(password);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.d(TAG, "successfully change password");
                    showToast(R.string.password_changed);

                    getFragmentManager().beginTransaction().remove(getTargetFragment()).commit();
                }
            });
        }
    }

    private void showToast(int id) {
        showToast(getString(id));
    }

    private void showToast(CharSequence text) {
        Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

}
