package com.treelev.isimple.enumerable.item;

public enum WineType {
    WHITE("Белое",  "#FDEFC0"),
    RED("Красное",  "#C21018"),
    PINK("Розовое",  "#F388A6"),
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
