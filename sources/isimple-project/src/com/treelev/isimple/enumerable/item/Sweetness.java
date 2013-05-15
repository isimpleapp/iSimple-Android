package com.treelev.isimple.enumerable.item;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public enum Sweetness implements Serializable {
    DRY("Cухое"), MEDIUM_DRY("Полусухое"), MEDIUM("Полусладкое"), SWEET("Сладкое"), UNKNOWN;

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
            if (sweetness.description != null && sweetness.description.equals(sweetnessId)) {
                return sweetness;
            }
        }
        return UNKNOWN;
    }
}
