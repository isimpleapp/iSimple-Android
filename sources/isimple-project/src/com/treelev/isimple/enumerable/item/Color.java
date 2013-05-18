package com.treelev.isimple.enumerable.item;

import java.io.Serializable;

public enum Color implements Serializable {
    WHITE("Белое"), RED("Красное"), ROSE("Розовое"),
    PORTO_WHITE("Белый"), PORTO_RED("Красный"),
    OTHER("Другое");

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
            if (color.description != null && color.description.equalsIgnoreCase(colorId)) {
                return color;
            }
        }
        return OTHER;
    }

    public static Color[] getWineColor() {
        return new Color[] { WHITE, RED, ROSE };
    }

    public static Color[] getChampagneColor() {
        return new Color[] { WHITE, RED, ROSE };
    }

    public static Color[] getPortoColor() {
        return new Color[] { PORTO_WHITE, PORTO_RED };
    }
}
