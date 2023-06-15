package com.mpi.exam_sample.model.api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GeoLocation implements Serializable {
    @SerializedName("lat")
    private String lat;
    @SerializedName("lng")
    private String longitude;

    public GeoLocation(String lat, String longitude) {
        this.lat = lat;
        this.longitude = longitude;
    }

    public String getLat() {
        return lat;
    }

    public String getLongitude() {
        return longitude;
    }
}
