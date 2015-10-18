
package com.synova.realestate.models;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Ignore;
import com.orm.dsl.Table;
import com.orm.query.Condition;
import com.orm.query.Select;

/**
 * Created by ducth on 30/06/2015.
 */
@Table(name = "AdsInfo")
public class AdsInfoResponseEnt extends SugarRecord {

    private static final long LIMIT_RECORD_SAVE_TIME = (long) 90 * 86400 * 1000;

    @Ignore
    public boolean isSale;
    @Ignore
    public String codePostal;
    @Ignore
    public boolean isFavorite;
    @Ignore
    public String surface;
    @Ignore
    public String distance;
    @Ignore
    public String imageUrl;
    @Ignore
    public String businessType;
    @Ignore
    public String propertyType = "";
    @Ignore
    public int roomNumber;
    @Ignore
    public String title;
    @Ignore
    public String mminMaxPrice;
    @Ignore
    public String rentSale;

    /** Database field purpose only */
    @Column(name = "read_timestamp")
    public long readTimestamp;

    public static void deleteOldReadAds() {
        deleteAll(AdsInfoResponseEnt.class, "read_timestamp <= ?", ""
                + (System.currentTimeMillis() - LIMIT_RECORD_SAVE_TIME));
    }

    public static boolean isRead(long id) {
        AdsInfoResponseEnt readAds = Select.from(AdsInfoResponseEnt.class)
                .where(Condition.prop("id").eq(id)).first();
        return readAds != null;
    }

    @Override
    public long save() {
        readTimestamp = System.currentTimeMillis();
        return super.save();
    }
}
