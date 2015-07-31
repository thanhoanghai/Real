package com.synova.realestate.models.eventbus;

/**
 * Created by ducth on 31/07/2015.
 */
public class ChangeDialogFilterValuesEvent {

    public long timestamp;

    public ChangeDialogFilterValuesEvent(long timestamp) {
        this.timestamp = timestamp;
    }
}
