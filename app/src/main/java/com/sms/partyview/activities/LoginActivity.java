package com.sms.partyview.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginDispatchActivity;
import com.sms.partyview.R;

// This class uses the ParseLoginUI module to generate sign-up and login activities.
public class LoginActivity extends ParseLoginDispatchActivity {
    @Override
    protected Class<?> getTargetClass() {
        return HomeActivity.class;
    }
}
