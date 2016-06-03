package com.treelev.isimple.enumerable.chain;

public enum ChainType {
    RESTAURANT ("Ресторан"),
    SUPERMARKET ("Супермаркет"),
    UNKNOWN;

    private String name;

    private ChainType() {
    }

    private ChainType(String name) {
        this.name = name;
    }

    public static ChainType getChainType(String name) {
        for (ChainType type : values()) {
            if (type.name != null && type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public static ChainType getChainType(Integer ordinal) {
        return ordinal != null && ordinal >= 0 && ordinal < values().length
                ? values()[ordinal] : UNKNOWN;
    }
}
