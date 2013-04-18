package com.treelev.isimple.domain.db;

import android.location.Location;
import com.treelev.isimple.enumerable.location.LocationType;

public class Shop {

    private String locationId;
    private String locationName;
    private String locationAddress;
    private Float longitude;
    private Float latitude;
    private Integer workingHours;
    private String phoneNumber;
    private String chainId;
    private LocationType locationType;
    private Integer presencePercentage;

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
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

    public Integer getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(Integer workingHours) {
        this.workingHours = workingHours;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public LocationType getLocationType() {
        if (locationType!=null) {
            return locationType;
        } else {
            return LocationType.UNKNOWN;
        }
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public Integer getPresencePercentage() {
        return presencePercentage;
    }

    public void setPresencePercentage(Integer presencePercentage) {
        this.presencePercentage = presencePercentage;
    }

    public Location createShopLocation() {
        Location location = new Location("shop_location");
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        return location;
    }
}
