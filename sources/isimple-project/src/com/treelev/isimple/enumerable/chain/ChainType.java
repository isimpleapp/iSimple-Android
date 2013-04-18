package com.treelev.isimple.enumerable.chain;

public enum ChainType {
    RESTAURANT, SUPERMARKET, UNKNOWN;

    public static ChainType getChainType(String chainTypeId) {
        try {
            return values()[Integer.parseInt(chainTypeId)];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UNKNOWN;
    }
}
