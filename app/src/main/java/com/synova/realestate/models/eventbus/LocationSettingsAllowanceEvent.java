
package com.synova.realestate.models.eventbus;

/**
 * Created by ducth on 10/12/15.
 */
public class LocationSettingsAllowanceEvent {

    public boolean isLocationEnabled = false;

    public LocationSettingsAllowanceEvent(boolean isLocationEnabled) {
        this.isLocationEnabled = isLocationEnabled;
    }
}
