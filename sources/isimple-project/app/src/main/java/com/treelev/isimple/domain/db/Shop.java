package com.treelev.isimple.domain.db;

import android.location.Location;

import com.treelev.isimple.enumerable.location.LocationType;

import java.io.Serializable;

public class Shop implements Serializable {

    private String locationID;
    private String locationName;
    private String locationAddress;
    private Float longitude;
    private Float latitude;
    private String workingHours;
    private String phoneNumber;
    private String chainID;
    private LocationType locationType;
    private Float presencePercentage;

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getChainID() {
        return chainID;
    }

    public void setChainID(String chainID) {
        this.chainID = chainID;
    }

    public LocationType getLocationType() {
        if (locationType != null) {
            return locationType;
        } else {
            return LocationType.UNKNOWN;
        }
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public Float getPresencePercentage() {
        return presencePercentage;
    }

    public void setPresencePercentage(Float presencePercentage) {
        this.presencePercentage = presencePercentage;
    }

    public Location createShopLocation() {
        Location location = new Location("shop_location");
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        return location;
    }
}
