package com.treelev.isimple.enumerable.item;

import com.treelev.isimple.domain.ui.Presentable;

import java.io.Serializable;

public enum ProductType implements Serializable, Presentable {
    SPARKLING("ВИНО_ИГР", "Игристое", null),
    WINE("ВИНО_ВИНО", "Вино", null),
    CHAMPAGNE("ВИНО_ШАМП", "Шампанское", null),
    PORTO("ВИНО_ПОРТО", "Портвейн", null),
    HERES("ВИНО_ХЕРЕС", "Херес", "#FDEFC0"),
    WHISKY("ВИНО_ВИСКИ", "Виски", "#E75202"),
    SAKE_AUT("СТ_АВТ", "Саке авторское", "#E2E2E2"),
    SAKE("СТ_КЛАСС", "Саке классическое", "#E2E2E2"),
    COGNAC("КСН_КОНЬЯК", "Коньяк", "#DB5B1B"),
    ARMAGNAC("КСН_АРМАН", "Арманьяк", "#D88C3D"),
    RUM("КСН_РОМ", "Ром", "#CC4228"),
    CASHASA("КСН_КАШАСА", "Кашаса", "#F1D224"),
    LIQUOR("ЛИКЕР_ЛИК", "Ликер", "#484354"),
    ENERGY("ПРОД_БАНАП", "Энергетики", "#000000"),
    WATER("ПРОД_ВОДА", "Вода", "#A6E4E4"),
    JUICE("ПРОД_СОКИ", "Соки", "#E1E234"),
    SYRUP("ПРОД_СИРОП", "Сиропы", "#E1E234"),
    GRAPPA("ГРАППА_ГРА", "Граппа", "#DBBE61"),
    OTHER("ДРУГОЕ", "Другое", "#000000"),

    TEQUILA("КСН_ТЕКИЛА", "Текила", "#000000"),
    BITTER("КСН_БИТТЕР", "Биттер", "#000000"),

    UNKNOWN("UNKNOWN", "", "#000000");

    private String name;
    private String description;
    private String color;

    private ProductType() {
    }

    private ProductType(String name, String description, String color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public static Presentable[] getSpiritsTypes() {
        return new ProductType[]{COGNAC, WHISKY, ARMAGNAC, GRAPPA, LIQUOR, RUM, CASHASA};
    }

    public static Presentable[] getPortoHeresTypes() {
        return new ProductType[]{PORTO, HERES};
    }

    public static Presentable[] getWaterTypes() {
        return new ProductType[]{WATER, JUICE, ENERGY, SYRUP};
    }

    public static ProductType getProductType(String typeId) {
        for (ProductType productType : values()) {
            if (productType.name != null && productType.name.equalsIgnoreCase(typeId)) {
                return productType;
            }
        }

        return OTHER;
    }


    public static ProductType getProductType(Integer typeOrdinal) {
        return (typeOrdinal != null && typeOrdinal >= 0 && typeOrdinal < ProductType.values().length) ?
                ProductType.values()[typeOrdinal] : OTHER;
    }


    public static String[] GetAvailableClassifications(ProductType productType) {
        if (productType == SPARKLING || productType == WINE || productType == CHAMPAGNE)
            return new String[0];
        else {
            return null;
        }
    }

    @Override
    public String getLabel() {
        return getDescription();
    }
}
