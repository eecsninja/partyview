package com.sms.partyview.helpers;

import com.sms.partyview.models.Event;

/**
 * Created by sque on 7/12/14.
 */
public interface EventSaverInterface {
    public void saveNewEvent(Event event, String invitesString);
}
