package com.sms.partyview.activities;

import com.sms.partyview.fragments.CreateEventFragment;
import com.sms.partyview.fragments.EditEventFragment;

public class NewEventActivity extends EditEventActivity {
    // TODO: Refactor EditEventActivity, move NewEventActivity-specific code into here.

    // Create a fragment for creating a new event.
    @Override
    protected EditEventFragment createFragment() {
        return new CreateEventFragment();
    }
}
