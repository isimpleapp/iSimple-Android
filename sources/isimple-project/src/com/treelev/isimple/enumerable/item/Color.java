package com.treelev.isimple.enumerable.item;

import java.io.Serializable;

public enum Color implements Serializable {
    WHITE("белое"), RED("красное"), ROSE("Розовое"), UNKNOWN;

    private String description;

    private Color() {
        this(null);
    }

    private Color(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Color getColor(String colorId) {
        for (Color color : values()) {
            if (color.description != null && color.description.equals(colorId)) {
                return color;
            }
        }
        return UNKNOWN;
    }
}
