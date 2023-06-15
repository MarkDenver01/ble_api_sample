package com.mpi.exam_sample.model.api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Address implements Serializable {
    @SerializedName("address")
    private String street;
    @SerializedName("suite")
    private String suite;
    @SerializedName("city")
    private String city;
    @SerializedName("zipcode")
    private String zipCode;
    @SerializedName("geo")
    private GeoLocation geoLocation;

    public Address(String street, String suite, String city, String zipCode, GeoLocation geoLocation) {
        this.street = street;
        this.suite = suite;
        this.city = city;
        this.zipCode = zipCode;
        this.geoLocation = geoLocation;
    }

    public String getStreet() {
        return street;
    }

    public String getSuite() {
        return suite;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }
}
