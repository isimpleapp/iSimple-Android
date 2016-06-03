package com.treelev.isimple.enumerable.location;

public enum LocationType {

    RESTAURANT ("Ресторан"),
    SUPERMARKET ("Супермаркет"),
    UNKNOWN;

    private String name;

    private LocationType() {
    }

    private LocationType(String name) {
        this.name = name;
    }

    public static LocationType getLocationType(Integer ordinal) {
        return ordinal != null && ordinal >= 0 && ordinal < values().length
                ? values()[ordinal] : UNKNOWN;
    }

    public static LocationType getLocationType(String name) {
        for (LocationType type : values()) {
            if (type.name != null && type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
