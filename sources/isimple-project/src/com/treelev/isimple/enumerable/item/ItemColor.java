package com.treelev.isimple.enumerable.item;

import java.io.Serializable;

public enum ItemColor implements Serializable {

    WHITE("Белое",  "#FDEFC0"),
    RED("Красное",  "#C21018"),
    PINK("Розовое",  "#F388A6"),
    PORTO_WHITE("Белый", "#FDEFC0"),
    PORTO_RED("Красный", "#C21018"),
    OTHER("Другое", "#000000");

    private String name;
    private String code;

    private ItemColor() {
    }

    private ItemColor(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public static ItemColor getColor(String colorId) {
        for (ItemColor color : values()) {
            if (color.name != null && color.name.equalsIgnoreCase(colorId)) {
                return color;
            }
        }
        return OTHER;
    }

    public static ItemColor getColor(Integer ordinal) {
        return (ordinal != null && ordinal >= 0 && ordinal < ItemColor.values().length) ?
                ItemColor.values()[ordinal] : OTHER;
    }

    public static ItemColor[] getWineColor() {
        return new ItemColor[] { WHITE, RED, PINK };
    }

    public static ItemColor[] getChampagneColor() {
        return new ItemColor[] { WHITE, RED, PINK };
    }

    public static ItemColor[] getPortoColor() {
        return new ItemColor[] { PORTO_WHITE, PORTO_RED };
    }
}
