package com.sms.partyview.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.sms.partyview.R;

public class SignupActivity extends Activity {
    // Handles to views.
    EditText userNameField;
    EditText emailField;
    EditText passwordField;
    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setupViews();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

    private void setupViews() {
        userNameField = (EditText) findViewById(R.id.etSignupUserName);
        emailField = (EditText) findViewById(R.id.etSignupEmail);
        passwordField = (EditText) findViewById(R.id.etSignupPassword);
        signupButton = (Button) findViewById(R.id.btSignupCreateAccount);
    }
}
