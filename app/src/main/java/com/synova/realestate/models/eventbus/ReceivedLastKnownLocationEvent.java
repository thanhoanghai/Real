package com.synova.realestate.models.eventbus;

import android.location.Location;

/**
 * Created by ducth on 10/11/15.
 */
public class ReceivedLastKnownLocationEvent {

    public Location lastKnownLocation;

    public ReceivedLastKnownLocationEvent(Location lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }
}
