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
import com.sms.partyview.R;

public class LoginActivity extends Activity {
    // Handles to views.
    EditText userNameField;
    EditText passwordField;
    Button loginButton;
    TextView signupLink;
    TextView forgotPasswordLink;

    // For accessing shared preferences.
    private static final String PARTYVIEW_SHARED_PREF_KEY = "CurrentUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If there is saved login info, use it.
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            launchHomeActivity(currentUser);
        }
        setContentView(R.layout.activity_login);
        setupViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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

    // Launch the Signup activity.
    private void doSignup() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    // Submit login info to Parse server.
    private void doLogin() {
        String userName = userNameField.getText().toString();
        String password = passwordField.getText().toString();
        // User must provide valid login info.
        String missingField = null;
        if (userName.isEmpty()) {
            missingField = "user name";
        } else if (password.isEmpty()) {
            missingField = "password";
        }
        if (missingField != null) {
            Toast.makeText(getApplicationContext(),
                    "Must provide a valid " + missingField + ".",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ParseUser.logInInBackground(userName, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Toast.makeText(getApplicationContext(),
                                   "Successfully signed in!",
                                   Toast.LENGTH_SHORT).show();
                    launchHomeActivity(user);
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    Toast.makeText(getApplicationContext(),
                            "Login failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    System.err.println(e.getMessage());
                }
            }
        });
    }

    private void launchHomeActivity(ParseUser user) {
        Intent intent = new Intent(this, HomeActivity.class);
        if (user != null) {
            intent.putExtra(HomeActivity.INTENT_USER_NAME, user.getUsername());
            intent.putExtra(HomeActivity.INTENT_EMAIL, user.getEmail());
        }
        startActivity(intent);
        // Do not let user press back and return to login screen.
        finish();
    }

    private void setupViews() {
        userNameField = (EditText) findViewById(R.id.etLoginUserName);
        passwordField = (EditText) findViewById(R.id.etLoginPassword);
        loginButton = (Button) findViewById(R.id.btLogin);
        signupLink = (TextView) findViewById(R.id.tvLoginSignUp);
        forgotPasswordLink = (TextView) findViewById(R.id.tvForgotPassword);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSignup();
            }
        });
        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Launch forgot password activity.
                System.err.println("Forgot password.");
            }
        });
    }
}
