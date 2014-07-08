package com.sms.partyview;

/**
 * Created by sandra on 7/7/14.
 */
public enum AttendanceStatus {
    INVITED(0),
    ACCEPTED(1),
    DECLINED(2),
    PRESENT(3);

    private int value;

    private AttendanceStatus(int value) {
        this.value = value;
    }

}
