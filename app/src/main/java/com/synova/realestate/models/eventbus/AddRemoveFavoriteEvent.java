
package com.synova.realestate.models.eventbus;

/**
 * Created by ducth on 10/8/15.
 */
public class AddRemoveFavoriteEvent {
    public long timestamp;

    public AddRemoveFavoriteEvent() {
        this.timestamp = System.currentTimeMillis();
    }
}
