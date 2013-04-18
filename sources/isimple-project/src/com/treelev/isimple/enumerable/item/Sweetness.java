package com.treelev.isimple.enumerable.item;

public enum Sweetness {
    DRY("сухое"), MEDIUM_DRY("Полусухое"), MEDIUM("Полусладкое"), SWEET("Сладкое"), UNKNOWN;

    private String description;

    private Sweetness() {
        this(null);
    }

    private Sweetness(String description) {
        this.description = description;
    }

    public static Sweetness getSweetness(String sweetnessId) {
        for (Sweetness sweetness : values()) {
            if (sweetness.description != null && sweetness.description.equals(sweetnessId)) {
                return sweetness;
            }
        }
        return UNKNOWN;
    }
}
