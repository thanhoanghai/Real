package com.synova.realestate.network.model;

import com.synova.realestate.base.RealEstateApplication;
import com.synova.realestate.utils.PrefUtil;

/**
 * Created by ducth on 23/06/2015.
 */
public class MapRequestEnt {

    public double xMin;
    public double yMin;
    public double xMax;
    public double yMax;
    public String surfaceMinS = PrefUtil.getSurfaceMinMax().split("-")[0].trim();
    public String surfaceMaxS = PrefUtil.getSurfaceMinMax().split("-")[1].trim();
    public String deviceId = RealEstateApplication.deviceId;
//    public String propertyTypeS = Constants.PropertyType.APPARTEMENT.getName();
//    public String rentSaleS = PrefUtil.getAchatLocation().getParamName();
//    public String businessTypeS = "";
//    public String priceMinS = PrefUtil.getPrixMinMax().split("-")[0].trim();
//    public String priceMaxS = PrefUtil.getPrixMinMax().split("-")[1].trim();
//    public String codePostalS = "";
//    public String roomNumberS = "" + PrefUtil.getRoomNumbers();
//    public String keyWordS = PrefUtil.getMotsCles();

}
