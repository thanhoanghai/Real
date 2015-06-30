
package com.synova.realestate.models.eventbus;

/**
 * Created by ducth on 6/27/15.
 */
public class NavigationItemSelectedEvent {

    public int checkedId;

    public NavigationItemSelectedEvent(int checkedId) {
        this.checkedId = checkedId;
    }
}
