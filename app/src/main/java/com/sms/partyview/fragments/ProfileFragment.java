package com.sms.partyview.fragments;

import com.parse.ParseUser;
import com.parse.ui.ParseLoginConfig;
import com.sms.partyview.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int DEFAULT_MIN_PASSWORD_LENGTH = 6;
    private static final String TAG = ProfileFragment.class.getSimpleName() + "_DEBUG";

    private TextView mTvUsername;
    private TextView mTvEmail;
    private Button mBtnChangePassword;

    private ParseLoginConfig config;
    private int minPasswordLength;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
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
        mTvUsername = (TextView) view.findViewById(R.id.tvUsername);
        mTvEmail = (TextView) view.findViewById(R.id.tvEmail);
        mBtnChangePassword = (Button) view.findViewById(R.id.btnChangePassword);
        mBtnChangePassword.setOnClickListener(this);

        ParseUser user = ParseUser.getCurrentUser();
        mTvEmail.setText(user.getEmail());
        mTvUsername.setText(user.getUsername());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChangePassword:
                displayChangePasswordDialog();
            default:
                Log.d(TAG, "SOMETHING IS WRONG");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    public void displayChangePasswordDialog() {
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        ChangePasswordDialog changePasswordDialog = ChangePasswordDialog.newInstance();
        changePasswordDialog.show(fm, "fragment_change_password");
    }
}
