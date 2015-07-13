
package com.synova.realestate.network.model;

import com.synova.realestate.base.Constants;
import com.synova.realestate.base.RealEstateApplication;
import com.synova.realestate.utils.PrefUtil;

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
    public String rentSaleS = PrefUtil.getAchatLocation().name();
    public String businessTypeS;
    public String surfaceMinS = PrefUtil.getSurfaceMinMax().split("-")[0];
    public String surfaceMaxS = PrefUtil.getSurfaceMinMax().split("-")[1];
    public String priceMinS = PrefUtil.getPrixMinMax().split("-")[0];
    public String priceMaxS = PrefUtil.getPrixMinMax().split("-")[1];
    public String codePostalS;
    public String roomNumberS;
    public String keyWordS = PrefUtil.getMotsCles();
    public Constants.FilterOrderType orderByS = Constants.FilterOrderType.DISTANCE;

}
