package com.treelev.isimple.enumerable.location;

public enum LocationType {
    RESTAURANT, SUPERMARKET, UNKNOWN;

    public static LocationType getLocationType(String locationTypeId) {
        try {
            return values()[Integer.parseInt(locationTypeId)];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UNKNOWN;
    }
}
