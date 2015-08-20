
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
    // public String isSale = PrefUtil.getAchatLocation().getParamName();
    public String rentSaleS = PrefUtil.getAchatLocation().getParamName();
    public String businessTypeS;
    public String surfaceMinS = PrefUtil.getSurfaceMinMax().split("-")[0];
    public String surfaceMaxS = PrefUtil.getSurfaceMinMax().split("-")[1];
    public String priceMinS = PrefUtil.getPrixMinMax().split("-")[0];
    public String priceMaxS = PrefUtil.getPrixMinMax().split("-")[1];
    public String codePostalS;
    public String roomNumberS;
    public String keyWordS = PrefUtil.getMotsCles();

    public PublisherPropertyEnt() {
//        List<Constants.PropertyType> types = PrefUtil.getTypeDeBiens();
//        if (types.contains(Constants.PropertyType.ALL)) {
//            for (int i = 2; i < Constants.PropertyType.values().length; i++) {
//                propertyTypeS += Constants.PropertyType.values()[i].getName() + ",";
//            }
//        } else {
//            for (Constants.PropertyType type : types) {
//                propertyTypeS += type.getName() + ",";
//            }
//        }
//        propertyTypeS = propertyTypeS.length() > 0 ? propertyTypeS.substring(0,
//                propertyTypeS.length() - 1) : "";
    }
}
