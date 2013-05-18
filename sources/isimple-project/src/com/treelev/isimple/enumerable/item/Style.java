package com.treelev.isimple.enumerable.item;

import java.io.Serializable;

public enum Style implements Serializable {
    FIZZY("Шипучее"), STILL("Тихое"), SPARKLING("Игристое"),
    SPECIAL("Специальное"), FORTIFIED("Крепленое"),
    OTHER("Другое");

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
            if (style.description != null && style.description.equalsIgnoreCase(styleId)) {
                return style;
            }
        }
        return OTHER;
    }
}
