package com.treelev.isimple.enumerable.item;

import java.io.Serializable;

public enum Style implements Serializable {
    STILL("тихое"), SPARKLING("игристое"), SPECIAL("специальное"), FORTIFIED("крепленое"), UNKNOWN;

    private String description;

    private Style() {
        this(null);
    }

    private Style(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Style getStyle(String styleId) {
        for (Style style : values()) {
            if (style.description != null && style.description.equals(styleId)) {
                return style;
            }
        }
        return UNKNOWN;
    }
}
