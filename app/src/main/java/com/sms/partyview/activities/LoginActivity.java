package com.sms.partyview.activities;

import com.parse.ui.ParseLoginDispatchActivity;

// This class uses the ParseLoginUI module to generate sign-up and login activities.
public class LoginActivity extends ParseLoginDispatchActivity {
    @Override
    protected Class<?> getTargetClass() {
        return HomeActivity.class;
    }
}
