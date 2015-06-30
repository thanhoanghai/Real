
package com.synova.realestate.network.model;

import com.synova.realestate.base.Constants;
import com.synova.realestate.base.RealEstateApplication;

/**
 * Created by ducth on 6/23/15.
 */
public class AdsInfoEnt {

    public String cellPhoneId = RealEstateApplication.deviceId;
    public double xLocalisation = 48.8184681922801;
    public double yLocalisation = 2.4196212907603;
    public String polygon;
    public int adminId;
    public int offsetS;
    public Constants.PropertyType propertyTypeS = Constants.PropertyType.APPARTEMENT;
    public String rentSaleS;
    public String businessTypeS;
    public String surfaceMinS;
    public String surfaceMaxS;
    public String priceMinS;
    public String priceMaxS;
    public String codePostalS;
    public String roomNumberS;
    public String keyWordS;
    public Constants.FilterOrderType orderByS = Constants.FilterOrderType.DISTANCE;

}
