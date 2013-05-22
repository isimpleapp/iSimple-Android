package com.treelev.isimple.enumerable.item;

/**
 * Created with IntelliJ IDEA.
 * User: mhviedchenia
 * Date: 22.05.13
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */
public enum WineType {
    WHITE("белое",  "#FDEFC0"),
    RED("красное",  "#C21018"),
    PINK("розовое",  "#F388A6"),
    OTHER("Other" , "#000000");

    private String description;
    private String color;

    private WineType() {
    }

    private WineType( String description, String color) {
        this.description = description;
        this.color = color;
    }


    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public static WineType getWineType(String wineType) {
        for (WineType type : values()) {
            if (type.description != null && type.description.equalsIgnoreCase(wineType)) {
                return type;
            }
        }
        return OTHER;
    }

}
