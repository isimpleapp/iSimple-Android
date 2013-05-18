package com.treelev.isimple.enumerable.item;

import java.io.Serializable;

public enum Sweetness implements Serializable {
    DRY("Cухое"), MEDIUM_DRY("Полусухое"),
    MEDIUM_SWEET("Полусладкое"), SWEET("Сладкое"),
    CHAMPAGNE_BRUT("Брют"), CHAMPAGNE_XBRUT("Экстра брют"),
    PORTO_DRY("Сухой"), PORTO_SWEET("Сладкий"),
    OTHER("Другое");

    private String description;

    private Sweetness() {
        this(null);
    }

    private Sweetness(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Sweetness getSweetness(String sweetnessId) {
        for (Sweetness sweetness : values()) {
            if (sweetness.description != null && sweetness.description.equalsIgnoreCase(sweetnessId)) {
                return sweetness;
            }
        }
        return OTHER;
    }

    public static Sweetness[] getWineSweetness() {
        return new Sweetness[] { DRY, MEDIUM_DRY, MEDIUM_SWEET, SWEET };
    }

    public static Sweetness[] getChampagneSweetness() {
        return new Sweetness[] { CHAMPAGNE_BRUT, CHAMPAGNE_XBRUT, DRY, MEDIUM_DRY, MEDIUM_SWEET, SWEET };
    }

    public static Sweetness[] getPortoSweetness() {
        return new Sweetness[] { PORTO_DRY, PORTO_SWEET };
    }
}
