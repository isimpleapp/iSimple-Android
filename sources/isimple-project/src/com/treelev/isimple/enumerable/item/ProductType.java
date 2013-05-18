package com.treelev.isimple.enumerable.item;

import java.io.Serializable;

public enum ProductType implements Serializable {
    SPARKLING("ВИНО_ИГР", "Игристое"),
    WINE("ВИНО_ВИНО", "Вино"),
    CHAMPAGNE("ВИНО_ШАМП", "Шампанское"),
    HERES("ВИНО_ХЕРЕС", "Херес"),
    WHISKY("ВИНО_ВИСКИ", "Виски"),
    PORTO("ВИНО_ПОРТО", "Порто"),
    SAKE_AUT("СТ_АВТ", "Саке авторское"),
    SAKE("СТ_КЛАСС", "Саке классическое"),
    COGNAC("КСН_КОНЬЯК", "Коньяк"),
    TEQUILA("КСН_ТЕКИЛА", "Текила"),
    ARMAGNAC("КСН_АРМАН", "Арманьяк"),
    RUM("КСН_РОМ", "Ром"),
    BITTER("КСН_БИТТЕР", "Биттер"),
    CASHASA("КСН_КАШАСА", "Кашаса"),
    LIQUOR("ЛИКЕР_ЛИК", "Ликер"),
    ENERGY("ПРОД_БАНАП", "Энергетики"),
    WATER("ПРОД_ВОДА", "Вода"),
    JUICE("ПРОД_СОКИ", "Соки"),
    SYRUP("ПРОД_СИРОП", "Сиропы"),
    GRAPPA("ГРАППА_ГРА", "Граппа"),
    OTHER("ДРУГОЕ", "Другое"),
    UNKNOWN;

    private String name;
    private String description;

    private ProductType() {
    }

    private ProductType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static ProductType getProductType(String typeId) {
        for (ProductType productType : values()) {
            if (productType.name != null && productType.name.equalsIgnoreCase(typeId)) {
                return productType;
            }
        }
        return OTHER;
    }

    public static String[] GetAvailableClassifications(ProductType productType) {
        if (productType == SPARKLING || productType == WINE || productType == CHAMPAGNE)
            return new String[0];
        else {
            return null;
        }
    }
}
