package com.sms.partyview.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sms.partyview.R;
import com.sms.partyview.fragments.EventListFragment;

public class HomeActivity extends FragmentActivity {
    TextView userNameLabel;
    TextView emailLabel;

    // Event list fragment.
    // TODO: Create tabs for events + invites.
    EventListFragment eventListFragment;

    // Keys for passing in data in an intent.
    public static final String INTENT_USER_NAME = "user_name";
    public static final String INTENT_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupViews();
        // Display the username and email.
        String userName = getIntent().getStringExtra(INTENT_USER_NAME);
        if (userName != null) {
            userNameLabel.setText(userName);
        }
        String email = getIntent().getStringExtra(INTENT_EMAIL);
        if (email != null) {
            emailLabel.setText(email);
        }
        // TODO: Replace all this with actual home screen contents.

        // Create the event list fragment dynamically.
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        // Replace the container with fragment.
        eventListFragment = new EventListFragment();
        transaction.replace(R.id.flHomeScreenEvents, eventListFragment);
        // Execute the changes specified
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViews() {
        userNameLabel = (TextView) findViewById(R.id.tvHomeUserName);
        emailLabel = (TextView) findViewById(R.id.tvHomeEmail);
    }
}
