package com.synova.realestate.network.model;

import com.synova.realestate.utils.PrefUtil;

/**
 * Created by ducth on 23/06/2015.
 */
public class MapRequestEnt {

    public double xMin;
    public double yMin;
    public double xMax;
    public double yMax;
    public int adsOffset;
    public String surfaceMinS = PrefUtil.getSurfaceMinMax().split("-")[0].trim();
    public String surfaceMaxS = PrefUtil.getSurfaceMinMax().split("-")[1].trim();
    public String deviceId;

}
