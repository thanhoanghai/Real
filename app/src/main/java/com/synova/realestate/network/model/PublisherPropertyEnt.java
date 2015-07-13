
package com.synova.realestate.network.model;

import com.synova.realestate.utils.PrefUtil;

/**
 * Created by ducth on 6/23/15.
 */
public class PublisherPropertyEnt {

    public String cellPhoneIdI;
    public int publisherIdI;
    public double xLocalisation;
    public double yLocalisation;
    public String polygon;
    public int adminId;
    public int offsetS;
    public String propertyTypeS;
    public String rentSaleS = PrefUtil.getAchatLocation().name();
    public String businessTypeS;
    public String surfaceMinS = PrefUtil.getSurfaceMinMax().split("-")[0];
    public String surfaceMaxS = PrefUtil.getSurfaceMinMax().split("-")[1];
    public String priceMinS = PrefUtil.getPrixMinMax().split("-")[0];
    public String priceMaxS = PrefUtil.getPrixMinMax().split("-")[1];
    public String codePostalS;
    public String roomNumberS;
    public String keyWordS = PrefUtil.getMotsCles();
}
