package com.sms.partyview.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.SignUpCallback;
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
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmit();
            }
        });
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

    // Sends new account info to server.
    private void onSubmit() {
        String userName = userNameField.getText().toString();
        String password = passwordField.getText().toString();
        String email = emailField.getText().toString();
        // User must provide valid signup info.
        String missingField = null;
        if (userName.isEmpty()) {
            missingField = "user name";
        } else if (password.isEmpty()) {
            missingField = "password";
        } else if (email.isEmpty()) {
            missingField = "e-mail";
        }
        if (missingField != null) {
            Toast.makeText(getApplicationContext(),
                    "Must provide a valid " + missingField + ".",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ParseUser user = new ParseUser();
        user.setUsername(userName);
        user.setPassword(password);
        user.setEmail(email);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    // Show a toast to indicate that a user was created.
                    Toast.makeText(getApplicationContext(),
                                   "Successfully created user: " +
                                       userNameField.getText().toString(),
                                   Toast.LENGTH_SHORT).show();
                    // TODO: Handle the next step after signup.
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    System.err.println(e.getMessage());
                    Toast.makeText(getApplicationContext(), "Could not create user.",
                                   Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupViews() {
        userNameField = (EditText) findViewById(R.id.etSignupUserName);
        emailField = (EditText) findViewById(R.id.etSignupEmail);
        passwordField = (EditText) findViewById(R.id.etSignupPassword);
        signupButton = (Button) findViewById(R.id.btSignupCreateAccount);
    }
}
