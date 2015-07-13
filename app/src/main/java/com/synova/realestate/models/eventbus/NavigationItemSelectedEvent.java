
package com.synova.realestate.models.eventbus;

import com.synova.realestate.base.Constants;

/**
 * Created by ducth on 6/27/15.
 */
public class NavigationItemSelectedEvent {

    public Constants.ElementType type;
    public boolean isChecked;

    public NavigationItemSelectedEvent(Constants.ElementType type, boolean isChecked) {
        this.type = type;
        this.isChecked = isChecked;
    }
}
